package com.dolphinskart.prospectuser.dto;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.Data;

@Data
public class RequestDates {

	@JsonFormat(shape = Shape.STRING, pattern = "dd-MM-yyyy")
	private Date fromDate;
	
	@JsonFormat(shape = Shape.STRING, pattern = "dd-MM-yyyy")
	private Date toDate;
}
