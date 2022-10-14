package com.dolphinskart.prospectuser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProspectSellerAnalyticsResponse {

	private Long countOfPresentProspectSellerCount;

	private String percentageOfPresentProspectSeller;

	private Long countOfProspectSellerToRegisteredSeller;

	private String percentageOfProspectSellerToRegisteredSeller;

//	private Long countOfProspectCustomersRegisteredByReferral;
//
//	private String prospectCustomerToRegisteredCustomerByReferralPercentage;

	private Long countOfDirectRegisteredSeller;

	private String percentageOfdirectRegisteredSeller;
}
