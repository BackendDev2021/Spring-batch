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

import com.dolphinskart.prospectuser.model.ProspectSeller;

@Repository
public interface ProspectSellerRepo extends MongoRepository<ProspectSeller, String>{

	Page<ProspectSeller> findAllByIsActiveAndIsSubscriber(Boolean isActive, Boolean isSubscriber, Pageable paging);

	Set<ProspectSeller> findAllByIsActiveAndIsSubscriber(Boolean isActive, Boolean isSubscriber);

	@Query(value = "{'email_id':{'$in' : ?0}}")
	List<ProspectSeller> findAllByEmailId(Set<String> emailIds);

	@Query(value = "{'phone_number':{'$in' : ?0}}")
	List<ProspectSeller> findAllByPhoneNumber(Set<String> phoneNumbers);

	Optional<ProspectSeller> findByEmailId(String emailId);
	
	@Query(value = "{is_active:?0,'created_prospect_seller':{$gte:?1,$lte:?2 } }", count = true)
	Long findAllByCreatedDateAsPropectSellerAndIsActive(Boolean isActive, Date fromDate, Date toDate);

	@Query(value = "{is_active:?0,'moved_to_register_seller':{$gte:?1,$lte:?2} }",count = true)
	Long findAllByMovedToRegisterSellerAndIsActive(Boolean isActive, Date fromDate, Date toDate);

}
