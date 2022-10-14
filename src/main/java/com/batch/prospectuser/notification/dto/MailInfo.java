package com.dolphinskart.prospectuser.notification.dto;

import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MailInfo {
	String from;
	String toList;
	Map<String,Object> content;
	String type;
	String subType;

}



