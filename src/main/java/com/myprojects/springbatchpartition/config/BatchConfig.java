package com.myprojects.springbatchpartition.config;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.step.tasklet.SystemCommandTasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.myprojects.springbatchpartition.domain.BookDTO;
import com.myprojects.springbatchpartition.processor.BookCsvProcessor;
import com.myprojects.springbatchpartition.reader.BookCsvReader;
import com.myprojects.springbatchpartition.writer.BookItemWriter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class BatchConfig extends JobExecutionListenerSupport{
	
	@Value("${csv.new.file}")
	private Resource newCsvFile;

	@Value("${csv.partitions.file.path}")
	private String csvPartitionsFilePath;
	
	@Value("${csv.new.file.dir}")
	private String csvNewFileDir;
	
	@Value("${csv.temp.file.dir}")
	private String csvTempFileDir;
	
	@Autowired  //(required = true)
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
    @Autowired
    private DataSource dataSource;
	
	@Bean(name = "loadBooksJob")
	public Job loadBooksJob() throws MalformedURLException {
		
		Step splitCsvFilesStep = stepBuilderFactory.get("splitCsvFiles-step")
													.tasklet(splitBookCSVFileTasklet())
													//.listener(splitBookCSVFileTasklet())
													.build();
		
		Step convertSplitFilesToCSVFilesTaskletStep = stepBuilderFactory.get("convertSplitFilesToCSVFiles-step")
				.tasklet(convertSplitFilesToCSVFilesTasklet())
				//.listener(convertSplitFilesToCSVFilesTasklet())
				.build();
		
		Step loadBooksStep = stepBuilderFactory.get("loadBooks-step")
							 .<BookDTO,BookDTO>chunk(1)
							 .reader(bookCsvReader(null))
							 .processor(bookCsvProcessor())
							 .writer(bookItemWriter(dataSource))
							 .build();
		
		Step masterStep =  stepBuilderFactory.get("masterStep")
								.partitioner("loadBooksStep", partitioner())
								.step(loadBooksStep)
								.taskExecutor(taskExecutor())
								.build();
		
		return jobBuilderFactory.get("loadbooks-job")
									.incrementer(new RunIdIncrementer())
									.listener(this)
									.start(splitCsvFilesStep)
															//.start(loadBooksStep)
									//.start(convertSplitFilesToCSVFilesTaskletStep)
									//.start(masterStep)
									//.next(convertSplitFilesToCSVFilesTaskletStep)
									.next(masterStep)
									.build();
		
	}
	
	@Bean
	@StepScope
	public SystemCommandTasklet splitBookCSVFileTasklet() {
	    SystemCommandTasklet tasklet = new SystemCommandTasklet();
	    String fileName = newCsvFile.getFilename();
	    System.out.println("csv File : " + fileName);
	    log.debug("csv File : " + fileName);
	    //tasklet.setCommand(new String("gsplit -d -b 100 " + fileName + " " + fileName.substring(0, fileName.length() - 3)));
	    //tasklet.setCommand(new String("cp " + fileName + " " + fileName.substring(0, fileName.length() - 4)));
	    //tasklet.setCommand(new String("split -b1m books.csv book"));
	    //tasklet.setCommand(new String("split -C -b1m " + fileName + " " + csvPartitionsFilePath+fileName.substring(0, fileName.length() - 3)));
	    tasklet.setCommand(new String("split -l 10000 " + fileName + " " + csvTempFileDir+fileName.substring(0, fileName.length() - 3)));
	    tasklet.setWorkingDirectory(csvNewFileDir); //("/Users/ranga/myprojects/batchjob_files/partitions"); //csvNewFileDir csvPartitionsFilePath
	    tasklet.setTimeout(600000l);
	    return tasklet;
	} //for i in *; do mv "$i" "$i.csv"; done
	
	@Bean
	@StepScope
	public SystemCommandTasklet convertSplitFilesToCSVFilesTasklet() {
	    SystemCommandTasklet tasklet = new SystemCommandTasklet();
	    //tasklet.setCommand(new String("gsplit -d -b 100 " + fileName + " " + fileName.substring(0, fileName.length() - 3)));
	    //tasklet.setCommand(new String("cp " + fileName + " " + fileName.substring(0, fileName.length() - 4)));
	    //tasklet.setCommand(new String("split -b1m books.csv book"));
	    //tasklet.setCommand(new String("for i in *; do mv \"$i\" \"$i.csv\"; done"));
	    tasklet.setCommand(new String("for file in booksdata.*; do mv \"$file\" \"$file.txt\"; done"));
	    tasklet.setWorkingDirectory(csvPartitionsFilePath); //("/Users/ranga/myprojects/batchjob_files/partitions"); //csvNewFileDir csvPartitionsFilePath
	    tasklet.setTimeout(600000l);
	    return tasklet;
	}

	
	
	//public FlatFileItemReader<Person> personItemReader(@Value("#{stepExecutionContext['fileName']}") String filename) throws MalformedURLException {

	@Bean
	@StepScope
	@Qualifier("bookCsvReader")
	public BookCsvReader bookCsvReader(@Value("#{stepExecutionContext['fileName']}") String filename) throws MalformedURLException {
		return new BookCsvReader(filename);
	}
	
	@Bean
	public BookCsvProcessor bookCsvProcessor() {
		return new BookCsvProcessor();
	}
	
	@Bean
	public BookItemWriter bookItemWriter(DataSource dataSource) {
		return new BookItemWriter(dataSource);
	}
	
	// partitioner logic
	
	  @Bean("partitioner")
	  @StepScope
	  public Partitioner partitioner() {
	  	log.info("In Partitioner");
	  	MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
	  	ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
	  	Resource[] resources = null;
	  	try {
	  		resources = resolver.getResources("file:/Users/ranga/myprojects/batchjob_files/partitions/booksdata.*"); ///Users/ranga/myprojects/batchjob_files
	  		log.info("resources:"+resources);
	  	} catch (IOException e) {
	  		e.printStackTrace();
	  	}
	  	partitioner.setResources(resources);
	  	partitioner.partition(10);
	  	return partitioner;
	  }
	  
	    @Bean
		public ThreadPoolTaskExecutor taskExecutor() {
			ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
			taskExecutor.setMaxPoolSize(10);
			taskExecutor.setCorePoolSize(10);
			taskExecutor.setQueueCapacity(10);
			taskExecutor.afterPropertiesSet();
			return taskExecutor;
		}
	  
/*	  @Bean
		@Qualifier("masterStep")
		public Step masterStep() {
			return stepBuilderFactory.get("masterStep")
					.partitioner("step1", partitioner())
					.step(step1())
					.taskExecutor(taskExecutor())
					.build();
		}*/


	    
/*	    @Bean
	    public Step step1() {
	    	
		return stepBuilderFactory.get("step1")
							 .<BookDTO,BookDTO>chunk(1)
							 //.reader(bookCsvReader())
							 .processor(bookCsvProcessor())
							 .writer(bookItemWriter(dataSource))
							 .reader(bookCsvReader)
							 .build();
	    }*/
	  
	
}
