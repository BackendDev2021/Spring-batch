package com.dolphinskart.prospectuser.notification.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationHistoryDto {
	
	private String id;
	
	private Long successCount;
	
	private Long failureCount;
	
	private String lastNotificationSentTo;
	
	private String notificationSentBy;
	
	@JsonFormat(shape = Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
	private LocalDateTime lastNotificationSentAt;
	
}
