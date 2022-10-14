package com.dolphinskart.prospectuser.notification.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageInputs {

	private List<String> toNumbers;
	
	private String content;
	
	private String channel;
}
