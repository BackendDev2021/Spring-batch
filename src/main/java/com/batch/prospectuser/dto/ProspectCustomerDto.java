package com.dolphinskart.prospectuser.dto;

import java.util.List;

import lombok.Data;

@Data
public class ProspectCustomerDto {

	private String name;
	
	private List<String> emailIds;
	
	private List<String> phoneDetails;
}
