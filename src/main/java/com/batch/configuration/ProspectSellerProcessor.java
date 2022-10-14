package com.dolphinskart.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.dolphinskart.prospectuser.constants.ProspectUserConstants;
import com.dolphinskart.prospectuser.model.ProspectSeller;
import com.dolphinskart.prospectuser.notification.utils.NotificationConfig;

public class ProspectSellerProcessor implements ItemProcessor<ProspectSeller, ProspectSeller> {

	private static final Logger logger = LoggerFactory.getLogger(ProspectSellerProcessor.class);

	@Autowired
	private NotificationConfig notificationConfig;

	@Override
	public ProspectSeller process(ProspectSeller seller) throws Exception {
		if (StringUtils.isNotBlank(seller.getEmailId())) {
			if (seller.getIsSubscriber() && seller.getIsActive()) {
				if (seller.getIsEmailValid()) {
					Map<String, Object> notifyToUser = new HashMap<>();
					notifyToUser.put(ProspectUserConstants.IMAGE, ProspectUserConstants.PS_IMAGE_SRC);
					notifyToUser.put(ProspectUserConstants.EMAIL, ProspectUserConstants.DOLPHINS_SUPPORT);
					notificationConfig.sendEmail(seller.getEmailId(), notifyToUser, ProspectUserConstants.PC_TYPE,
							ProspectUserConstants.PS_SUB_TYPE);
				}
			}
		}

		if (StringUtils.isNotBlank(seller.getPhoneNumber())) {
			if (seller.getIsSubscriber() && seller.getIsActive()) {
				if (seller.getIsMobileValid()) {
					String prospectCustMsgContent = new StringBuilder().append(ProspectUserConstants.PS_MSG).toString();
					notificationConfig.sendSms(List.of(seller.getPhoneNumber()), prospectCustMsgContent);
				}
			}
		}
		return seller;
	}

}
