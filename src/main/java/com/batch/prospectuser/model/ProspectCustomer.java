package com.dolphinskart.prospectuser.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("prospect_customers")
public class ProspectCustomer {

	@Id
	private String id;
	
	@Field(value = "customer_name")
	private String name;

	@Field(value = "email_id")
	//@Indexed(unique = true)
	private String emailId;
	
	@Field(value = "is_email_id")
	private Boolean isEmailValid = true;

	@Field(value = "phone_number")
	//@Indexed(unique = true)
	private String phoneNumber;
	
	@Field(value = "is_phone_number")
	private Boolean isMobileValid = true;

	@Field(value = "is_subscriber")
	private Boolean isSubscriber = true;
	
	private Boolean isNotificationSent;
	
	private LocalDate lastNotificationSentAt;

	@Field(value = "is_active")
	private Boolean isActive = true;
	
//	@Field(value = "is_customer")
//	private Boolean isCustomer = false;
	
	@Field(value = "is_registered_by_referral")
	private Boolean isRegisteredByReferral = false;
	
	@Field(value = "created_prospect_customer")
	@JsonFormat(shape = Shape.STRING, pattern = "dd-MM-yyyy")
	private LocalDate createdAsProspectCustomer;
	
	@Field(value = "moved_to_register_customer")
	@JsonFormat(shape = Shape.STRING, pattern = "dd-MM-yyyy")
	private LocalDate movedToRegisterCustomer;
}
