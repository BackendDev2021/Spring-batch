package com.dolphinskart.prospectuser.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "notification_history")
public class NotificationHistory {

	@Id
	private String id;
	
	@Field(value = "success_count")
	private Long successCount;
	
	@Field(value = "failure_count")
	private Long failureCount;
	
	@Field(value = "last_notification_sent_to")
	private String lastNotificationSentTo;
	
	@Field(value = "notification_sent_by")
	private String notificationSentBy;
	
	@Field(value = "last_notification_sent_at")
	@JsonFormat(shape = Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
	private LocalDateTime lastNotificationSentAt;
}
