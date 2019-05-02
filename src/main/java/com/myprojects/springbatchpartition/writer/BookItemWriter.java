package com.myprojects.springbatchpartition.writer;

import javax.sql.DataSource;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;

import com.myprojects.springbatchpartition.domain.BookDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookItemWriter extends JdbcBatchItemWriter<BookDTO> implements StepExecutionListener{

	String sql = "INSERT INTO inventory_schema.book_partition (id, title,author_id,  published_date, isbn) VALUES(:id, :title, :authorId, :published_date, :isbn)";
	
    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("beforeStep BookItemWriter");
    }
    
	public BookItemWriter(DataSource dataSource) {
		log.debug("BookItemWriter in writer:"+sql);
		this.setDataSource(dataSource);
		this.setSql(sql);
		this.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<BookDTO>());
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		// TODO Auto-generated method stub
		return null;
	}   

}
