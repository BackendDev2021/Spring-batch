package com.dolphinskart.prospectuser.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Document(collection = "prospect_sellers")
public class ProspectSeller {
	
	@Id
	private String id;
	
	@Field(value = "seller_name")
	private String sellerName;

	@Field(value = "email_id")
	private String emailId;
	
	@Field(value = "is_email_id")
	private Boolean isEmailValid = true;

	@Field(value = "phone_number")
	private String phoneNumber;
	
	@Field(value = "is_phone_number")
	private Boolean isMobileValid = true;

	@Field(value = "is_subscriber")
	private Boolean isSubscriber = true;

	@Field(value = "is_active")
	private Boolean isActive = true;
	
	private LocalDate lastEmailSentAt;
	
	private Boolean isNotificationSent;
	
//	@Field(value = "is_registered_by_referral")
//	private Boolean isRegisteredByReferral = false;
	
	@Field(value = "created_prospect_seller")
	@JsonFormat(shape = Shape.STRING, pattern = "dd-MM-yyyy")
	private LocalDate createdAsProspectSeller;
	
	@Field(value = "moved_to_register_seller")
	@JsonFormat(shape = Shape.STRING, pattern = "dd-MM-yyyy")
	private LocalDate movedToRegisterSeller;

}
