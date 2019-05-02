package com.myprojects.springbatchpartition.batch;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.myprojects.springbatchpartition.domain.BookDTO;

public class BookCvsFieldSetMapper implements FieldSetMapper<BookDTO> {
	 
    @Override
    public BookDTO mapFieldSet(FieldSet fieldSet) throws BindException {
    	BookDTO bookDTO = new BookDTO();
         
    	bookDTO.setId(fieldSet.readLong("id"));
    	bookDTO.setTitle(fieldSet.readRawString("title"));
    	bookDTO.setAuthorId(fieldSet.readLong("authorId"));
    	Date date = fieldSet.readDate("published_date","yyyy-MM-dd");
    	LocalDate publishedDate = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(date));
    	bookDTO.setPublished_date(publishedDate);
    	bookDTO.setIsbn(fieldSet.readInt("isbn"));
         
        return bookDTO;
    }
}