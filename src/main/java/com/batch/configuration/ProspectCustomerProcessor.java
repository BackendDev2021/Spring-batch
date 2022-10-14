package com.dolphinskart.configuration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.dolphinskart.prospectuser.constants.ProspectUserConstants;
import com.dolphinskart.prospectuser.model.ProspectCustomer;
import com.dolphinskart.prospectuser.notification.utils.NotificationConfig;
import com.dolphinskart.prospectuser.repository.ProspectCustomerRepo;

public class ProspectCustomerProcessor implements ItemProcessor<ProspectCustomer, ProspectCustomer> {

	private static final Logger logger = LoggerFactory.getLogger(ProspectCustomerProcessor.class);

	@Autowired
	private NotificationConfig notificationConfig;

	@Autowired
	private ProspectCustomerRepo prospectCustomerRepo;

	@Override
	public ProspectCustomer process(ProspectCustomer customer) throws Exception {
		List<ProspectCustomer> countOfCustomers = new ArrayList<ProspectCustomer>();
		Boolean isNotificationDateAvailable = false;
		if (countOfCustomers.size() <= 100) {
			if (StringUtils.isNotBlank(customer.getEmailId())) {
				if (customer.getIsNotificationSent().equals(false)) {
					if (ObjectUtils.isNotEmpty(customer.getLastNotificationSentAt())) {
						if (!customer.getLastNotificationSentAt().equals(LocalDate.now())) {
							isNotificationDateAvailable = true;
						}
					}
					if (customer.getIsSubscriber() && customer.getIsActive()) {
						if (customer.getIsEmailValid() && isNotificationDateAvailable) {
							try {
								this.sendEmail(customer);
							} catch (Exception ex) {
								logger.error("Exception occured with this email :{}", ex.getLocalizedMessage());
								customer.setIsMobileValid(false);
							}
							System.out.println("Email sent to -" + customer.getEmailId());
							if (StringUtils.isNotBlank(customer.getPhoneNumber())) {
								if (customer.getIsMobileValid()) {
									if (customer.getIsSubscriber() && customer.getIsActive()) {
										try {
											this.sendSms(customer);
										} catch (Exception ex) {
											logger.error("Exception occured with this mobile :{}",
													ex.getLocalizedMessage());
											customer.setIsMobileValid(false);
										}
										System.out.println(
												"Sms sent to -" + customer.getPhoneNumber() + countOfCustomers.size());
									}
								}
							}
						} else if (customer.getIsEmailValid()) {
							this.sendEmail(customer);
							System.out.println("Email sent to -" + customer.getEmailId());
							if (StringUtils.isNotBlank(customer.getPhoneNumber())) {
								if (customer.getIsMobileValid()) {
									if (customer.getIsSubscriber() && customer.getIsActive()) {
										this.sendSms(customer);
										System.out.println(
												"Sms sent to -" + customer.getPhoneNumber() + countOfCustomers.size());
									}
								}
							}
						}
					}
				}
				customer.setIsNotificationSent(true);
				customer.setLastNotificationSentAt(LocalDate.now());
				prospectCustomerRepo.save(customer);
				countOfCustomers.add(customer);
			}
		}
		return customer;
	}

	private void sendEmail(ProspectCustomer customer) {
		Map<String, Object> notifyToUser = new HashMap<>();
		notifyToUser.put(ProspectUserConstants.IMAGE, ProspectUserConstants.PC_IMAGE_SRC);
		notifyToUser.put(ProspectUserConstants.EMAIL, ProspectUserConstants.DOLPHINS_SUPPORT);
		notificationConfig.sendEmail(customer.getEmailId(), notifyToUser, ProspectUserConstants.PC_TYPE,
				ProspectUserConstants.PC_SUB_TYPE);
	}

	private void sendSms(ProspectCustomer customer) {
		String prospectCustMsgContent = new StringBuilder().append(ProspectUserConstants.PC_MSG).toString();
		notificationConfig.sendSms(List.of(customer.getPhoneNumber()), prospectCustMsgContent);
	}
}
