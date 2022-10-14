package com.dolphinskart.prospectuser.service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.dolphinskart.prospectuser.dto.ProspectCustomerAnalyticsResponse;
import com.dolphinskart.prospectuser.dto.ProspectSellerAnalyticsResponse;
import com.dolphinskart.prospectuser.dto.RequestDates;
import com.dolphinskart.prospectuser.model.NotificationHistory;

public interface ProspectUserService {

	Boolean notifyToProspectUsers(String notifyTo,String notificationSentBy) throws InterruptedException, ExecutionException;

	String uploadProspectCustomerDatas(MultipartFile dataFile,Boolean isCustomer) throws IOException;

	Boolean isFileEmpty(MultipartFile xlsFile);

	Page<?> allProspectCustomerDatas(Integer page, Integer offset,Boolean isCustomer);

	Boolean unsubscribeCustomerByEmailId(String emailId);

	ProspectCustomerAnalyticsResponse prospectCustomersAnalyticsAndCounts(RequestDates dates);

	Page<NotificationHistory> notificationHistoryResponse(Integer page, Integer offset);

	ProspectSellerAnalyticsResponse prospectSellersAnalyticsAndCounts(RequestDates dates);

	Boolean updateSellerAsRegisteredSeller(String emailId);

	// Boolean unsubscribeTheCustomers(Set<String> emailIds);

}
