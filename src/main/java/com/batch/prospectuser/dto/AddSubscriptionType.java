package com.dolphinskart.prospectuser.dto;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddSubscriptionType {
	
	@Email(message = "Email Id  should be valid")
	@NotBlank(message = "Email Id  should'nt be empty")
	private String emailId;

	@NotEmpty(message = "Subscription type cant be empty")
	private List<String> subscriptionType;
}
