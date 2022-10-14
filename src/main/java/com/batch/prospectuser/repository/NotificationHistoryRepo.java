package com.dolphinskart.prospectuser.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dolphinskart.prospectuser.model.NotificationHistory;

@Repository
public interface NotificationHistoryRepo extends MongoRepository<NotificationHistory, String>{

}
