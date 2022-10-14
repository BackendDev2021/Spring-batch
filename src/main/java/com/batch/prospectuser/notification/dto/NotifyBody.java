package com.dolphinskart.prospectuser.notification.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class NotifyBody {
	
	PubSubMessage message;
	String subscription;
}
