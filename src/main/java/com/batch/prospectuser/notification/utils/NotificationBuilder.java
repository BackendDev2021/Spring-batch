//package com.dolphinskart.prospectuser.notification.utils;
//
//import java.util.Base64;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import com.dolphinskart.prospectuser.notification.dto.MailInfo;
//import com.dolphinskart.prospectuser.notification.dto.MessageInputs;
//import com.dolphinskart.prospectuser.notification.dto.NotifyBody;
//import com.dolphinskart.prospectuser.notification.dto.PubSubAttributes;
//import com.dolphinskart.prospectuser.notification.dto.PubSubMessage;
//import com.google.gson.Gson;
//
//@Component
//public class NotificationBuilder {
//
//	private static final String subscription = "projects/myproject/subscriptions/mysubscription";
//
//	private static final String key = "value";
//
//	private static final String channel = "sms";
//	
//	@Value("${notification.service.from.mail}")
//	private String notificationFromEmail;
//
//	public NotifyBody buildMessage(List<String> toNumbers, String content) {
//		MessageInputs messageInfo = new MessageInputs(
//				toNumbers, content, channel);
//		Gson gson = new Gson();
//		String messageJson = gson.toJson(messageInfo);
//		NotifyBody notifyBody = new NotifyBody();
//		PubSubMessage message = new PubSubMessage();
//		PubSubAttributes attributes = new PubSubAttributes();
//		attributes.setKey(key);
//		message.setData(new String(Base64.getEncoder().encode(messageJson.getBytes())));
//		message.setAttributes(attributes);
//		notifyBody.setMessage(message);
//		notifyBody.setSubscription(subscription);
//		return notifyBody;
//	}
//
//	public NotifyBody buildEmail(String toEmail, Map<String, Object> inputs, String type,
//			String subType) {
//		MailInfo mailInfo = new MailInfo();
//		mailInfo.setFrom(notificationFromEmail);
//		mailInfo.setSubType(subType);
//		mailInfo.setToList(toEmail);
//		mailInfo.setType(type);
//		mailInfo.setContent(inputs);
//		Gson gson = new Gson();
//		String mailJson = gson.toJson(mailInfo);
//		NotifyBody notifyBody = new NotifyBody();
//		PubSubMessage message = new PubSubMessage();
//		message.setData(new String(Base64.getEncoder().encode(mailJson.getBytes())));
//		notifyBody.setMessage(message);
//		return notifyBody;
//	}
//}
