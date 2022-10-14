package com.dolphinskart.prospectuser.serviceImpl;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dolphinskart.globalexceptionhandlingutil.model.BusinessException;
import com.dolphinskart.prospectuser.constants.ProspectUserConstants;
import com.dolphinskart.prospectuser.dto.ProspectCustomerAnalyticsResponse;
import com.dolphinskart.prospectuser.dto.ProspectSellerAnalyticsResponse;
import com.dolphinskart.prospectuser.dto.RequestDates;
import com.dolphinskart.prospectuser.errorcodes.ProspectUserErrorCodes;
import com.dolphinskart.prospectuser.model.NotificationHistory;
import com.dolphinskart.prospectuser.model.ProspectCustomer;
import com.dolphinskart.prospectuser.model.ProspectSeller;
import com.dolphinskart.prospectuser.repository.NotificationHistoryRepo;
import com.dolphinskart.prospectuser.repository.ProspectCustomerRepo;
import com.dolphinskart.prospectuser.repository.ProspectSellerRepo;
import com.dolphinskart.prospectuser.service.ProspectUserService;

@Service
public class ProspectUserServiceImpl implements ProspectUserService {

	private Logger logger = LoggerFactory.getLogger(ProspectUserServiceImpl.class);

	private static final String FILE_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

//	@Autowired
//	CustomerRepository customerRepository;
	
	@Autowired
	ProspectCustomerRepo prospectCustomerRepo;
	
	@Autowired
	ProspectSellerRepo prospectSellerRepo;
	
	@Autowired
	NotificationHistoryRepo notificationHistoryRepo;

//	@Autowired
//	NotificationBean bean;
//
//	@Autowired
//	NotificationMobileBean notificationBeanForMobile;

	@Autowired
	UploadProspectUserDatas extractBean;
	
	
	@Autowired
	MongoTemplate mongoTemplate;

	/**
	 * Service implementation for triggering the notifications to our dolphins
	 * portal prospect users
	 */
	@Override
	@SuppressWarnings({ "unchecked" })
	public Boolean notifyToProspectUsers(String notifyTo,String notificationSentBy) throws InterruptedException, ExecutionException {
//		NotificationHistoryDto finalResponse = new NotificationHistoryDto();
		Boolean finalResponse = false;
		Set<String> activeCustomeremailIds = new HashSet<>();
		Set<String> activeCustomerContactNos = new HashSet<>();
		if (StringUtils.equalsIgnoreCase(ProspectUserConstants.CUSTOMER, notifyTo)) {
			List<ProspectCustomer> prospectCustomersContactSources = (List<ProspectCustomer>) this
					.collectionsByQuery(ProspectCustomer.class);
			activeCustomeremailIds = prospectCustomersContactSources.stream()
					.filter(cust -> StringUtils.isNotBlank(cust.getEmailId()) && cust.getIsEmailValid() == true)
					.map(ProspectCustomer::getEmailId).collect(Collectors.toSet());
			activeCustomerContactNos = prospectCustomersContactSources.stream()
					.filter(cust -> StringUtils.isNotBlank(cust.getPhoneNumber()) && cust.getIsMobileValid() == true)
					.map(ProspectCustomer::getPhoneNumber).collect(Collectors.toSet());
		} else if (StringUtils.equalsIgnoreCase(ProspectUserConstants.SELLER, notifyTo)) {
			List<ProspectSeller> prospectSellersContactSources = (List<ProspectSeller>) this
					.collectionsByQuery(ProspectSeller.class);
			activeCustomeremailIds = prospectSellersContactSources.stream()
					.filter(cust -> StringUtils.isNotBlank(cust.getEmailId()) && cust.getIsEmailValid() == true)
					.map(ProspectSeller::getEmailId).collect(Collectors.toSet());
			activeCustomerContactNos = prospectSellersContactSources.stream()
					.filter(cust -> StringUtils.isNotBlank(cust.getPhoneNumber()) && cust.getIsMobileValid() == true)
					.map(ProspectSeller::getPhoneNumber).collect(Collectors.toSet());
		} else if (StringUtils.equalsIgnoreCase(ProspectUserConstants.BOTH, notifyTo)) {
			List<ProspectCustomer> prospectCustomers = (List<ProspectCustomer>) this
					.collectionsByQuery(ProspectCustomer.class);
			List<ProspectSeller> prospectSellers = (List<ProspectSeller>) this.collectionsByQuery(ProspectSeller.class);
			Set<String> emailIds = prospectCustomers.stream()
					.filter(cust -> StringUtils.isNotBlank(cust.getEmailId()) && cust.getIsEmailValid() == true)
					.map(ProspectCustomer::getEmailId).collect(Collectors.toSet());
			Set<String> emailIds1 = prospectSellers.stream()
					.filter(cust -> StringUtils.isNotBlank(cust.getEmailId()) && cust.getIsEmailValid() == true)
					.map(ProspectSeller::getEmailId).collect(Collectors.toSet());
			activeCustomeremailIds.addAll(emailIds);
			activeCustomeremailIds.addAll(emailIds1);
			Set<String> phone = prospectCustomers.stream()
					.filter(cust -> StringUtils.isNotBlank(cust.getPhoneNumber()) && cust.getIsMobileValid() == true)
					.map(ProspectCustomer::getPhoneNumber).collect(Collectors.toSet());
			Set<String> phone1 = prospectSellers.stream()
					.filter(cust -> StringUtils.isNotBlank(cust.getPhoneNumber()) && cust.getIsMobileValid() == true)
					.map(ProspectSeller::getPhoneNumber).collect(Collectors.toSet());
			activeCustomerContactNos.addAll(phone);
			activeCustomerContactNos.addAll(phone1);
		}
//		finalResponse = notifyUtils.gatewayToSendNotifications(new ArrayList<>(activeCustomeremailIds),
//				new ArrayList<>(activeCustomerContactNos), notifyTo);
//		logger.info("notifications ended:{}", notifyTo, "Completed by time - " + formattedISTDateTime(LocalDateTime.now()));
//		finalResponse = this.buildNotificationResponse(response, notifyTo,notificationSentBy);
//		logger.info("notifications execution completed  for :{},:{},:{}", notifyTo,
//				"response" + finalResponse + "Completed by time:{} - "+ LocalDateTime.now() );
		return (finalResponse);
	}
	
	private List<?> collectionsByQuery(Class<?> object) {
		Query query = new Query();
		query.fields().include(ProspectUserConstants.TABLE_MAIL_ID);
		query.fields().include(ProspectUserConstants.TABLE_PHONE_NUMBER);
		List<?> collections = mongoTemplate.find(query, object);
		return collections;
	}
	
	/**
	 * Building generic notification reponse having counts and timestamp,tracking
	 * details
	 * 
	 * @param response
	 * @param notifyTo
	 * @return
	 */
//	private NotificationHistoryDto buildNotificationResponse(Map<String, Boolean> notificationResponse, String notifyTo,
//			String notificationSentBy) {
//		NotificationHistoryDto notificationResponseDto =  new NotificationHistoryDto();
//		if(MapUtils.isNotEmpty(notificationResponse)) {
//			Long invalidContactCounts = notificationResponse.entrySet().stream().filter(a -> ObjectUtils.isNotEmpty(a))
//					.filter(a -> a.getValue() == false).count();
//			Long validContactCounts = notificationResponse.entrySet().stream().filter(a -> ObjectUtils.isNotEmpty(a))
//					.filter(a -> a.getValue() == true).count();
//			if (!(invalidContactCounts == 0) || !(validContactCounts == 0)) {
//				NotificationHistory historyResponse = new NotificationHistory(UUID.randomUUID().toString(),
//						validContactCounts, invalidContactCounts, notifyTo, notificationSentBy,
//						DateAndTimeUtility.getISTLocalDateTime());
//				BeanUtils.copyProperties(historyResponse, notificationResponseDto);
//				notificationHistoryRepo.save(historyResponse);
//				logger.info("notifications saved :{}",
//						notifyTo, LocalDateTime.now() + "Completed by time - " + System.currentTimeMillis());
//			}
//		}
//		return notificationResponseDto;
//	}
	
	/*
	 * Method to get the expected IST time and date format
	 */
//	public LocalDateTime formattedISTDateTime(LocalDateTime localDateTime) {
//		LocalDateTime timestamp = localDateTime.atZone(ZoneId.of(TimeZoneUtility.IST.getValue())).toLocalDateTime();
//		LocalDateTime lastNotificationSentAt = LocalDateTime.parse(
//				timestamp.format(DateTimeFormatter.ofPattern(ProspectUserConstants.DATE_TIME_FORMAT)),
//				DateTimeFormatter.ofPattern(ProspectUserConstants.DATE_TIME_FORMAT));
//		return lastNotificationSentAt;
//	}
	
	/**
	 * Service implementation of validating , reading the data uploaded by our
	 * admin/user
	 */
	@Override
	public String uploadProspectCustomerDatas(MultipartFile dataFile,Boolean isCustomer) throws IOException {
		Boolean isDatasUploaded = null;
		XSSFWorkbook workbook = new XSSFWorkbook(dataFile.getInputStream());
		Boolean isValidWorkbook = this.validateUploadingWorkbook(workbook);
		Boolean workbookHasRelevantDatas = extractBean.checkWorkbookHasRelevantData(workbook);
		if (isValidWorkbook) {
			if (workbookHasRelevantDatas) {
				if(isCustomer) {
					logger.info("reading datas of customer/seller from excel method started");
					isDatasUploaded = extractBean.readDatasFromExcel(workbook,true);
					logger.info("reading/extracting datas from excel method completed");	
				}else {
					logger.info("reading datas of customer/seller from excel method started");
					isDatasUploaded = extractBean.readDatasFromExcel(workbook,false);
					logger.info("reading/extracting datas from excel method completed");	
				}
				if (isDatasUploaded) {
					return ProspectUserConstants.SAVED;
				} else {
					throw new BusinessException(ProspectUserErrorCodes.SOMETHING_WENT_WRONG.getErrorCode(),
							ProspectUserErrorCodes.SOMETHING_WENT_WRONG.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				throw new BusinessException(ProspectUserErrorCodes.INVALID_WORKBOOK.getErrorCode(),
						ProspectUserErrorCodes.INVALID_WORKBOOK.getMessage(), HttpStatus.NOT_ACCEPTABLE);
			}
		} else {
			throw new BusinessException(ProspectUserErrorCodes.EMPTY_FILE.getErrorCode(),
					ProspectUserErrorCodes.EMPTY_FILE.getMessage(), HttpStatus.NOT_ACCEPTABLE);
		}
	}

	/**
	 * Method to validate uploading data workbook
	 * 
	 * @param workbook
	 * @return
	 */
	private Boolean validateUploadingWorkbook(XSSFWorkbook workbook) {
		XSSFSheet sheet = workbook.getSheetAt(0);
		if (sheet.getLastRowNum() == -1 || sheet.getLastRowNum() < 1) {
			logger.warn("invalid workbook uploaded");
			return false;
		}
		return true;
	}
	
	/*
	 * Method to find valid/in-valid workbook
	 */
	@Override
	public Boolean isFileEmpty(MultipartFile xlsFile) {
		if (xlsFile.isEmpty()) {
			throw new BusinessException();
		}
		if (StringUtils.equalsAny(xlsFile.getContentType(), FILE_TYPE)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Page<?> allProspectCustomerDatas(Integer page, Integer offset, Boolean isCustomer) {
		Pageable paging = PageRequest.of(page, offset);
		if (isCustomer) {
			Page<ProspectCustomer> prospectCustomers = prospectCustomerRepo.findAll(paging);
			return prospectCustomers;
		} else {
			Page<ProspectSeller> prospectSellers = prospectSellerRepo.findAll(paging);
			return prospectSellers;
		}
	}

	/**
	 * Service implementation of Un-subscribe api by user's emailId
	 */
	@Override
	public Boolean unsubscribeCustomerByEmailId(String emailId) {
		Optional<ProspectCustomer> customerByEmail = prospectCustomerRepo.findByIsSubscriberAndEmailId(true,emailId);
		if (customerByEmail.isPresent()) {
			customerByEmail.get().setIsSubscriber(false);
			prospectCustomerRepo.save(customerByEmail.get());
			return true;
		} else {
			throw new BusinessException(ProspectUserErrorCodes.NO_CUSTOMER_EXIST.getErrorCode(),
					ProspectUserErrorCodes.NO_CUSTOMER_EXIST.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Analysis and counts of our prospect customer's registered status
	 */
	@Override
	public ProspectCustomerAnalyticsResponse prospectCustomersAnalyticsAndCounts(RequestDates date) {
		RequestDates dates = new RequestDates();
		if (ObjectUtils.isEmpty(date.getFromDate()) && ObjectUtils.isEmpty(date.getToDate())) {
			dates.setFromDate(Date.valueOf(LocalDate.now().minusYears(ProspectUserConstants.YEARS_COUNT)));
			dates.setToDate(Date.valueOf(LocalDate.now()));
		} else {
			dates = date;
		}

		Long countOfPresentProspectCustomersCount = prospectCustomerRepo
				.findAllByCreatedDateAsPropectCustomerAndIsActive(true, dates.getFromDate(), dates.getToDate());

		Long countOfProspectCustomerMovedAsRegisteredCustomer = prospectCustomerRepo.findAllByIsActive(false,
				dates.getFromDate(), dates.getToDate());

		Long countOfCustomerMovedAsRegisteredCustomerByReferral = prospectCustomerRepo
				.findAllByIsRegisteredByReferral(true, dates.getFromDate(), dates.getToDate());

		Long countOfDirectRegisteredCustomer = null;
//				customerRepository.findAllIsRegisteredFromProspectCustomer(true,
//				dates.getFromDate(), dates.getToDate());

		String percentageOfProspectCustomer = percentageCalculator(countOfPresentProspectCustomersCount,
				countOfPresentProspectCustomersCount);
		logger.info("percentage and count of prospect customer:{},:{}", countOfPresentProspectCustomersCount,
				percentageOfProspectCustomer);

		String percentageOfProspectCustomerMovedAsRegisteredCustomer = percentageCalculator(
				countOfPresentProspectCustomersCount, countOfProspectCustomerMovedAsRegisteredCustomer);
		logger.info("percentage and count of prospect customer who moved as registered customer:{},:{}",
				countOfProspectCustomerMovedAsRegisteredCustomer,
				percentageOfProspectCustomerMovedAsRegisteredCustomer);

		String percentageOfCustomerMovedAsRegisteredCustomerByReferral = percentageCalculator(
				countOfPresentProspectCustomersCount, countOfCustomerMovedAsRegisteredCustomerByReferral);
		logger.info("percentage and count of prospect customer who moved as registered customer by referral:{}",
				countOfCustomerMovedAsRegisteredCustomerByReferral,
				percentageOfCustomerMovedAsRegisteredCustomerByReferral);

		String percentageOfDirectRegisteredCustomer = percentageCalculator(countOfPresentProspectCustomersCount,
				countOfDirectRegisteredCustomer);
		logger.info("percentage and count of customers who registered as customer directly:{}",
				countOfDirectRegisteredCustomer, percentageOfDirectRegisteredCustomer);

		return new ProspectCustomerAnalyticsResponse(countOfPresentProspectCustomersCount, percentageOfProspectCustomer,
				countOfProspectCustomerMovedAsRegisteredCustomer, percentageOfProspectCustomerMovedAsRegisteredCustomer,
				countOfCustomerMovedAsRegisteredCustomerByReferral,
				percentageOfCustomerMovedAsRegisteredCustomerByReferral, countOfDirectRegisteredCustomer,
				percentageOfDirectRegisteredCustomer);
	}

	/**
	 * Generic method for percentage calculation
	 * 
	 * @param totalValue
	 * @param value
	 * @return
	 */
	private String percentageCalculator(Long totalValue, Long value) {
		Double calculation = (Double.valueOf(value) / Double.valueOf(totalValue) * 100);
		Double roundOff = Math.round(calculation * 100.0) / 100.0;
		return String.valueOf(roundOff + ProspectUserConstants.PERCENTAGE);
	}

	/**
	 * Notification history from the table by paginated response
	 */
	@Override
	public Page<NotificationHistory> notificationHistoryResponse(Integer page, Integer offset) {
		Pageable paging = PageRequest.of(page, offset,
				Sort.by(Direction.DESC, ProspectUserConstants.LAST_NOTIFICATION_SENT_AT));
		Page<NotificationHistory> notificationRecords = notificationHistoryRepo.findAll(paging);
		return notificationRecords;
	}

	@Override
	public ProspectSellerAnalyticsResponse prospectSellersAnalyticsAndCounts(RequestDates date) {
		RequestDates dates = new RequestDates();
		if (ObjectUtils.isEmpty(date.getFromDate()) && ObjectUtils.isEmpty(date.getToDate())) {
			dates.setFromDate(Date.valueOf(LocalDate.now().minusYears(ProspectUserConstants.YEARS_COUNT)));
			dates.setToDate(Date.valueOf(LocalDate.now()));
		} else {
			dates = date;
		}
		
		Long countOfProspectSeller = prospectSellerRepo.findAllByCreatedDateAsPropectSellerAndIsActive(true,
				dates.getFromDate(), dates.getToDate());
		String percentageOfPresentProspectSeller = percentageCalculator(countOfProspectSeller, countOfProspectSeller);
		logger.info("percentage and count of present prospect seller:{},:{}", countOfProspectSeller,
				percentageOfPresentProspectSeller);
		
		Long countOfProspectSellerMovedToRegisteredSeller = prospectSellerRepo
				.findAllByMovedToRegisterSellerAndIsActive(false, dates.getFromDate(), dates.getToDate());
		String percentageOfProspectSellerMovedToRegisteredSeller = percentageCalculator(countOfProspectSeller,
				countOfProspectSellerMovedToRegisteredSeller);
		logger.info("percentage and count of prospect seller who moved to registered seller:{},:{}",
				countOfProspectSellerMovedToRegisteredSeller, percentageOfProspectSellerMovedToRegisteredSeller);
		
		//Dependency with Seller registration service
		Long countOfDirectRegisteredSeller = Long.valueOf(0);
		String percentageOfDirectRegisteredSeller = "0%";
		
		return new ProspectSellerAnalyticsResponse(countOfProspectSeller, percentageOfPresentProspectSeller,
				countOfProspectSellerMovedToRegisteredSeller, percentageOfProspectSellerMovedToRegisteredSeller,
				countOfDirectRegisteredSeller, percentageOfDirectRegisteredSeller);
	}

	/**
	 * Service implementation for update the prospect seller as in-active and set
	 * the timestamp when they moved as registered seller
	 */
	@Override
	public Boolean updateSellerAsRegisteredSeller(String emailId) {
		Optional<ProspectSeller> existByEmail = prospectSellerRepo.findByEmailId(emailId);
		if (existByEmail.isPresent()) {
			existByEmail.get().setMovedToRegisterSeller(LocalDate.now());
			existByEmail.get().setIsActive(false);
			prospectSellerRepo.save(existByEmail.get());
			return true;
		} else {
			return false;
		}
	}


//	@Override
//	public Boolean unsubscribeTheCustomers(Set<String> emailIds) {
//		logger.info("un-subscribe method started");
//		return toUnsubscribe(emailIds);
//	}
//
//	private Boolean toUnsubscribe(Set<String> mailIds) {
//		if (CollectionUtils.isNotEmpty(mailIds)) {
//			Set<ProspectCustomer> activeCustomersFromDb = prospectCustomerRepo.findAllByEmailId(mailIds);
//			activeCustomersFromDb.stream().forEach(cust -> {
//					if (StringUtils.isNotBlank(cust.getEmailId())) {
//							cust.setSubscriber(false);
//					}
//			});
//			prospectCustomerRepo.saveAll(activeCustomersFromDb);
//			logger.info("un-subscribe method exe completed");
//			return true;
//		} else {
//			return false;
//		}
//	}

}
