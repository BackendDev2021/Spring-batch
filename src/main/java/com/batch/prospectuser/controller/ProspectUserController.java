package com.dolphinskart.prospectuser.controller;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.validation.constraints.NotBlank;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dolphinskart.globalexceptionhandlingutil.model.BusinessException;
import com.dolphinskart.prospectuser.dto.ProspectCustomerAnalyticsResponse;
import com.dolphinskart.prospectuser.dto.ProspectSellerAnalyticsResponse;
import com.dolphinskart.prospectuser.dto.RequestDates;
import com.dolphinskart.prospectuser.errorcodes.ProspectUserErrorCodes;
import com.dolphinskart.prospectuser.model.NotificationHistory;
import com.dolphinskart.prospectuser.service.ProspectUserService;

/**
 * This feature is for inviting the mulitple customer who are not registered
 * with any of our portal , We will send the marketing emails and sms to invite
 * them to become our portal user
 * 
 * @author Mohan
 */

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/customer-registration-service")
@Validated
public class ProspectUserController {

	@Autowired
	private ProspectUserService prospectUserService;
	
	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job emailJob;
	
//	@Autowired
//	private JobRepository jobRepository;
//	
//	@Value("${upload.path}")
//    private String uploadPath;

	/**
	 * Controller to extract the excel data and persist to repository
	 * 
	 * @param dataFile
	 * @return
	 * @throws IOException
	 */
	@PostMapping(value = "/prospect-customers/save/workbook")
	public ResponseEntity<String> uploadProspectCustomerDatas(@RequestParam MultipartFile dataFile,
			@RequestParam(required = true) Boolean isCustomer) throws IOException {
		if (!prospectUserService.isFileEmpty(dataFile)) {
			throw new BusinessException(ProspectUserErrorCodes.INVALID_WORKBOOK.getErrorCode(),
					ProspectUserErrorCodes.INVALID_WORKBOOK.getMessage(), HttpStatus.NOT_ACCEPTABLE);
		}
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(prospectUserService.uploadProspectCustomerDatas(dataFile,isCustomer));
	}
	
	

//	@PostMapping(value = "/prospect-customers/save")
//	public ResponseEntity<String> uploadProspectCustomerData(@RequestParam MultipartFile dataFile,
//			@RequestParam(required = true) Boolean isCustomer) throws IOException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
//		//Save multipartFile file in a temporary physical folder
//		//it's assumed you have a folder called tmpuploads in the resources folder
//		java.nio.file.Files.createDirectories(Paths.get(uploadPath));
//		 Path root = Paths.get(uploadPath);
////		java.nio.file.Files.copy(dataFile.getInputStream(), root.resolve(dataFile.getOriginalFilename()));
//        Resource resource = new UrlResource(root.toUri());
//        String path = resource.getURL().getPath();
//        File fileToImport = new File(path + dataFile.getOriginalFilename());
//		java.nio.file.Files.copy(dataFile.getInputStream(), root.resolve(dataFile.getOriginalFilename()));
//       JobExecution jobExecution = jobLauncher.run(importUserJob, new JobParametersBuilder()
//				.addString("fullPathFileName", fileToImport.getAbsolutePath()).toJobParameters());
//		return ResponseEntity.ok("Done..!");        
//	} 
	
	/**
	 * Notification apis to send all of our prospect customer
	 * 
	 * @return
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws JobParametersInvalidException 
	 * @throws JobInstanceAlreadyCompleteException 
	 * @throws JobRestartException 
	 * @throws JobExecutionAlreadyRunningException 
	 */
	@GetMapping(value = "/notify-prospect-customers")
	public ResponseEntity<Boolean> notifyToProspectCustomer(@RequestParam(required = true) String notifyTo,
			@RequestParam(required = true) String notificationSentBy)
			throws InterruptedException, ExecutionException, JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {
//		return ResponseEntity.status(HttpStatus.OK)
//				.body(prospectCustomerService.notifyToProspectUsers(notifyTo, notificationSentBy));
		JobExecution jobExecution = jobLauncher.run(emailJob, new JobParameters());
		System.out.println("Completed job thread name - " + jobExecution.getJobInstance().getJobName());
		System.out.println("Executed Job status - " + jobExecution.getStatus());
		return ResponseEntity.ok(true);
	}
	
//	@GetMapping(value = "/notify-prospect-sellers")
//	public ResponseEntity<Boolean> notifyToProspectSeller(@RequestParam(required = true) String notifyTo,
//			@RequestParam(required = true) String notificationSentBy)
//			throws InterruptedException, ExecutionException, JobExecutionAlreadyRunningException, JobRestartException,
//			JobInstanceAlreadyCompleteException, JobParametersInvalidException {
////		return ResponseEntity.status(HttpStatus.OK)
////				.body(prospectCustomerService.notifyToProspectUsers(notifyTo, notificationSentBy));
//		JobExecution jobExecution = jobLauncher.run(emailJob, new JobParameters());
//		System.out.println("Completed job thread name - " + jobExecution.getJobInstance().getJobName());
//		System.out.println("Executed Job status - " + jobExecution.getStatus());
//		return ResponseEntity.ok(true);
//	}

//	/**
//	 * Method to un-subscribe the customers by given email Ids from our portal 
//	 * 
//	 * @param emailIds
//	 * @return
//	 */
//	@PutMapping(value = "/prospect-customers/un-subscription")
//	public ResponseEntity<Boolean> unsubscribeTheCustomers(@RequestBody Set<String> emailIds) {
//		return ResponseEntity.status(HttpStatus.ACCEPTED)
//				.body(prospectCustomerService.unsubscribeTheCustomers(emailIds));
//	}

	/**
	 * Method to un-subscribe the single person by emailId
	 * 
	 * @param emailId
	 * @return
	 */
	@PutMapping(value = "/prospect-customers/un-subscribe")
	public ResponseEntity<Boolean> unsubscribeCustomerByEmailId(
			@RequestHeader @NotBlank(message = "Email Id is required!") String emailId) {
		return ResponseEntity.status(HttpStatus.ACCEPTED)
				.body(prospectUserService.unsubscribeCustomerByEmailId(emailId));
	}

	/**
	 * Fetch all the datas from prospect customer /prospect seller table with pagination
	 * 
	 * @param page
	 * @param offset
	 * @return
	 */
	@GetMapping(value = "/prospect-customers/datas")
	public ResponseEntity<Page<?>> allProspectCustomerDatas(
			@RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "10") Integer offset,Boolean isCustomer) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(prospectUserService.allProspectCustomerDatas(page, offset,isCustomer));
	}
	
	/**
	 * 
	 * Return the prospect customers counts and percentage by analyzing the three
	 * tables based on input dates
	 * 
	 * @param dates
	 * @return
	 */
	@PostMapping(value = "/prospect-customers/analytics/count")
	public ResponseEntity<ProspectCustomerAnalyticsResponse> prospectCustomersAnalyticsAndCounts(@RequestBody RequestDates dates) {
		return ResponseEntity.status(HttpStatus.OK).body(prospectUserService.prospectCustomersAnalyticsAndCounts(dates));
	}
	
	/**
	 * All notifications history from the table
	 * 
	 * @param page
	 * @param offset
	 * @return
	 */
	@GetMapping(value = "/prospect-customers/notification-history")
	public ResponseEntity<Page<NotificationHistory>> notificationHistoryResponse(
			@RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "10") Integer offset) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(prospectUserService.notificationHistoryResponse(page, offset));
	}
	
	/**
	 * Return the prospect seller counts and percentage by analyzing the three
	 * tables based on input dates
	 * 
	 * @param dates
	 * @return
	 */
	@PostMapping(value = "/prospect-sellers/analytics/count")
	public ResponseEntity<ProspectSellerAnalyticsResponse> prospectSellersAnalyticsAndCounts(
			@RequestBody RequestDates dates) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(prospectUserService.prospectSellersAnalyticsAndCounts(dates));
	}
	
	/**
	 * Update the seller whether seller is going to be as registered seller api for
	 * seller registration service
	 * 
	 * @param emailId
	 * @return
	 */
	@PutMapping(value = "/prospect-seller")
	public ResponseEntity<Boolean> updateSellerAsRegisteredSeller(@RequestHeader(required = true) String emailId) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(prospectUserService.updateSellerAsRegisteredSeller(emailId));
	}
}