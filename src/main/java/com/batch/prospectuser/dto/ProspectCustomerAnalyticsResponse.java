package com.dolphinskart.prospectuser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProspectCustomerAnalyticsResponse {

	private Long countOfPresentProspectCustomersCount;

	private String prospectCustomerPercentage;

	private Long countOfProspectCustomerToRegisteredCustomer;

	private String prospectCustomerToRegisteredCustomerPercentage;

	private Long countOfProspectCustomersRegisteredByReferral;

	private String prospectCustomerToRegisteredCustomerByReferralPercentage;

	private Long countOfDirectRegisteredCustomer;

	private String directRegisteredCustomerPercentage;
}
