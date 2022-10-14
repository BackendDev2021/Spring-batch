package com.dolphinskart.prospectuser.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
	
	private Long successCount;
	
	private Long failureCount;
	
	@JsonFormat(shape = Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
	private LocalDateTime lastNotificationTriggeredOn;
	
	private String lastNotificationSentTo;
	
}
