package com.dolphinskart.prospectuser.notification.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SMSInfo {
	List<String> toNumbers;
	String content;
	String channel;

}
