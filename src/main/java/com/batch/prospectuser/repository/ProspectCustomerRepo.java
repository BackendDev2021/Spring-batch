package com.dolphinskart.prospectuser.repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.dolphinskart.prospectuser.model.ProspectCustomer;

@Repository
public interface ProspectCustomerRepo extends MongoRepository<ProspectCustomer, String> {

	Optional<ProspectCustomer> findByEmailId(String email);

	ProspectCustomer findByPhoneNumber(String number);

	Page<ProspectCustomer> findAllByIsActive(Boolean isActive, Pageable paging);

//	Set<ProspectCustomer> findAllByIsActiveAndIsSubscriber(Boolean isActive, Boolean isSubscriber);

	Page<ProspectCustomer> findAllByIsActiveAndIsSubscriber(Boolean isActive, Boolean isSubscriber, Pageable paging);

//	@Query(value = "{ 'email_id' : {'$in' : ?0 } }")
//	Set<ProspectCustomer> findAllByEmailId(Set<String> mailIds);

	Optional<ProspectCustomer> findByIsSubscriberAndEmailId(Boolean isSubscriber, String emailId);

	Boolean existsByPhoneNumberAndIsActive(String mobile, Boolean isActive);

	Boolean existsByEmailIdAndIsActive(String emailId, Boolean isActive);

	@Query(value = "{is_active:?0,'moved_to_register_customer':{$gte:?1,$lte:?2 } }", count = true)
	Long findAllByIsActive(Boolean isActive, Date fromDate, Date toDate);

	@Query(value = "{is_registered_by_referral:?0,'moved_to_register_customer':{$gte:?1,$lte:?2 } }", count = true)
	Long findAllByIsRegisteredByReferral(Boolean isRegisteredByReferral, Date fromDate, Date toDate);

	@Query(value = "{is_active:?0,'created_prospect_customer':{$gte:?1,$lte:?2}}", count = true)
	Long findAllByCreatedDateAsPropectCustomerAndIsActive(Boolean isActive, Date fromDate, Date toDate);

	@Query(value = "{'email_id':{'$in' : ?0}}")
	List<ProspectCustomer> findAllByEmailId(Set<String> emailIds);

	@Query(value = "{'phone_number':{'$in' : ?0}}")
	List<ProspectCustomer> findAllByPhoneNumber(Set<String> phoneNumbers);
}
