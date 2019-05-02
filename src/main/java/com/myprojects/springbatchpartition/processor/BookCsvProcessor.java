package com.myprojects.springbatchpartition.processor;

import org.springframework.batch.item.ItemProcessor;

import com.myprojects.springbatchpartition.domain.BookDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookCsvProcessor implements ItemProcessor<BookDTO, BookDTO>{

	@Override
	public BookDTO process(BookDTO bookDTO) throws Exception {
		log.debug("bookDTO in processor:"+bookDTO.toString());
		return bookDTO;
	}

}
