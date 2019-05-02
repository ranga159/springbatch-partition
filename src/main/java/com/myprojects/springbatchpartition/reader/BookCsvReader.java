package com.myprojects.springbatchpartition.reader;

import java.net.MalformedURLException;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.UrlResource;

import com.myprojects.springbatchpartition.batch.BookCvsFieldSetMapper;
import com.myprojects.springbatchpartition.domain.BookDTO;

public class BookCsvReader extends FlatFileItemReader<BookDTO>{
	
	private static final String DELIMITER_COMMA = ",";

	public  BookCsvReader(String fileName) throws MalformedURLException {
		setResource(new UrlResource(fileName));
		DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
		delimitedLineTokenizer.setNames(new String[] {"id", "title", "authorId", "published_date", "isbn"});
		delimitedLineTokenizer.setDelimiter(DELIMITER_COMMA);
		delimitedLineTokenizer.setStrict(false);
		
	/*	BeanWrapperFieldSetMapper<BookDTO> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(BookDTO.class);
		fieldSetMapper.setDistanceLimit(0);*/
		
		BookCvsFieldSetMapper bookCvsFieldSetMapper = new BookCvsFieldSetMapper();
		
		DefaultLineMapper<BookDTO> defaultLineMapper = new DefaultLineMapper<>();
		defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
		defaultLineMapper.setFieldSetMapper(bookCvsFieldSetMapper);
		setLineMapper(defaultLineMapper);
		
	}

}
