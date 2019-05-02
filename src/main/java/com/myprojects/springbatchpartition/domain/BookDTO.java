package com.myprojects.springbatchpartition.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BookDTO {
	private Long id;
	private String title;
	private Long authorId; // change this to object
	private LocalDate published_date;
	private int isbn;
	private LocalDateTime createdDateTime;
	private String createdBy;
	private LocalDateTime modifiedDateTime;
	private String modifiedBy;	
}
