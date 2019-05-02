package com.myprojects.springbatchpartition.repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.myprojects.springbatchpartition.domain.BookDTO;

@Repository
public class BookRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	
    public int insertBook(BookDTO bookDTO){
        String query = "INSERT INTO ITEM VALUES(?,?,?,?,?,?)";
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

        return jdbcTemplate.update(query,bookDTO.getId(),bookDTO.getTitle(),bookDTO.getPublished_date(),bookDTO.getIsbn(),timestamp,timestamp);
    }
	
	
	
}
