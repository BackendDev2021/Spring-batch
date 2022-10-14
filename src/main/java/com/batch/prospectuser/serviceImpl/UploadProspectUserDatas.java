package com.dolphinskart.prospectuser.serviceImpl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.dolphinskart.prospectuser.constants.ProspectUserConstants;
import com.dolphinskart.prospectuser.dto.AddSubscriptionType;
import com.dolphinskart.prospectuser.dto.ProspectCustomerDto;
import com.dolphinskart.prospectuser.model.ProspectCustomer;
import com.dolphinskart.prospectuser.model.ProspectSeller;
import com.dolphinskart.prospectuser.repository.ProspectCustomerRepo;
import com.dolphinskart.prospectuser.repository.ProspectSellerRepo;
import com.mongodb.client.DistinctIterable;

@Component
public class UploadProspectUserDatas {

	private Logger logger = LoggerFactory.getLogger(UploadProspectUserDatas.class);

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	ProspectCustomerRepo prospectCustomerRepo;

	@Autowired
	ProspectSellerRepo prospectSellerRepo;
	
	@Value("${customer-care.subscription.url}")
	private String customerCareSubscriptionUrl;
	
	@Autowired
	private RestTemplate restTemplate;

	public static FormulaEvaluator evaluator = null;

	/**
	 * Method to read / extract values from excel
	 * 
	 * @param workbook
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Boolean readDatasFromExcel(XSSFWorkbook workbook, Boolean isCustomer) {
		List<ProspectSeller> saveListOfProspectSeller = new ArrayList<>();
		Set<ProspectSeller> prospectSellerData = new HashSet<>();
		List<ProspectCustomer> saveListOfProspectCustomer = new ArrayList<>();
		Set<ProspectCustomer> prospectCustomersData = new HashSet<>();
		List<?> unqiueRecordsForSavingToDb = new ArrayList<>();
		Boolean savedToDb = true;
		evaluator = workbook.getCreationHelper().createFormulaEvaluator();
		try {
			XSSFSheet sheet = workbook.getSheetAt(0);

			for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {

				Row headerRow = sheet.getRow(0);
				ProspectCustomerDto prospectCustomerDto = new ProspectCustomerDto();
				List<String> addEmails = new ArrayList<String>();
				List<String> addPhoneNos = new ArrayList<String>();
				Row currentRow = sheet.getRow(i);

				if (ObjectUtils.isNotEmpty(currentRow)) {
					for (int j = currentRow.getFirstCellNum(); j < headerRow.getLastCellNum(); j++) {

						String headerValue = headerRow.getCell(j).getStringCellValue();
						Cell ce = currentRow.getCell(j);

						if (StringUtils.equalsIgnoreCase(headerValue, ProspectUserConstants.CUSTOMER_NAME)) {
							if (stringNotBlankValidation(ce)) {
								prospectCustomerDto.setName(ce.getStringCellValue());
							}
						} else if (StringUtils.equalsIgnoreCase(headerValue, ProspectUserConstants.EMAIL_ID)) {
							if (stringNotBlankValidation(ce)) {
								if (ce.getStringCellValue().contains(ProspectUserConstants.DELIMETER_COMMA)) {
									StringTokenizer extractEmailValuesFromCell = new StringTokenizer(
											ce.getStringCellValue(), ProspectUserConstants.DELIMETER_COMMA);
									while (extractEmailValuesFromCell.hasMoreTokens()) {
										String email = removeWhiteSpace(extractEmailValuesFromCell.nextToken());
										String emailNonSpace = email.strip();
										addEmails.add(emailNonSpace);
									}
									prospectCustomerDto.setEmailIds(addEmails);
								} else if (!ce.getStringCellValue()
										.contains(ProspectUserConstants.DELIMETER_COMMA)) {
									String email = removeWhiteSpace(ce.getStringCellValue());
									if (StringUtils.isNotBlank(email)) {
										String emailNonSpace = email.strip();
										addEmails.add(emailNonSpace);
										prospectCustomerDto.setEmailIds(addEmails);
									}
								}
							}
						} else if (StringUtils.equalsIgnoreCase(headerValue, ProspectUserConstants.MOBILE)) {
							try {
								if (checkNumericValue(ce) != null) {
									if (checkNumericValue(ce).contains(ProspectUserConstants.DELIMETER_COMMA)) {
										String mobile = ce.getStringCellValue().replaceAll("\\s", "");
										StringTokenizer extractEmailValuesFromCell = new StringTokenizer(mobile,
												ProspectUserConstants.DELIMETER_COMMA);
										while (extractEmailValuesFromCell.hasMoreTokens()) {
											Number number = makeAsNumberBySingleValue(
													extractEmailValuesFromCell.nextToken());
											String contactNo = ObjectUtils.isNotEmpty(number)
													? mobileNumberValidation(number)
													: "";
											if (StringUtils.isNotBlank(contactNo)) {
												String contactNoSpaceTrim = removeWhiteSpace(contactNo);
												String contactNoSpaceStrip = contactNoSpaceTrim.strip();
												addPhoneNos.add(contactNoSpaceStrip);
											}
										}
										prospectCustomerDto.setPhoneDetails(addPhoneNos);
									} else if (!checkNumericValue(ce)
											.contains(ProspectUserConstants.DELIMETER_COMMA)) {
										Number number = makeAsNumber(ce);
										String contactNo = ObjectUtils.isNotEmpty(number)
												? mobileNumberValidation(number)
												: "";
										if (StringUtils.isNotBlank(contactNo)) {
											String contactNoSpaceTrim = removeWhiteSpace(contactNo);
											String contactNoSpaceStrip = contactNoSpaceTrim.strip();
											addPhoneNos.add(contactNoSpaceStrip);
											prospectCustomerDto.setPhoneDetails(addPhoneNos);

										} else {
											logger.warn("Invalid mobile No:{}", number);
										}
									}
								}
							} catch (Exception ex) {
								logger.error("Exception occured while reading mobile value from excel:{}",
										ex.getLocalizedMessage());
							}
						}
					}
				}
				if (isCustomer) {
					if (ObjectUtils.isNotEmpty(prospectCustomerDto)) {
						prospectCustomersData = (Set<ProspectCustomer>) this
								.createObjectsBasedOnContactSources(prospectCustomerDto, isCustomer);
						saveListOfProspectCustomer.addAll(prospectCustomersData);
					}
					List<ProspectCustomer> customersRecordWithoutDuplicates = null;
					if(CollectionUtils.isNotEmpty(saveListOfProspectCustomer)) {
						customersRecordWithoutDuplicates = (List<ProspectCustomer>) this
								.removedDuplicatesRecordsFromExtractedDataList(saveListOfProspectCustomer, true);
						logger.info("Removing duplicate from prospect customer excel data method exe completed");
					}
					if(CollectionUtils.isNotEmpty(customersRecordWithoutDuplicates)) {
						unqiueRecordsForSavingToDb = (List<ProspectCustomer>) this
								.removeDuplicateRecordsFromListIfThatRecordExistInDb(customersRecordWithoutDuplicates,
										true);	
					}
				} else {
					if (ObjectUtils.isNotEmpty(prospectCustomerDto)) {
						prospectSellerData = (Set<ProspectSeller>) this
								.createObjectsBasedOnContactSources(prospectCustomerDto, isCustomer);
						saveListOfProspectSeller.addAll(prospectSellerData);
					}
					List<ProspectSeller> sellersRecordWithoutDuplicates = null;
					if (CollectionUtils.isNotEmpty(saveListOfProspectSeller)) {
						sellersRecordWithoutDuplicates = (List<ProspectSeller>) this
								.removedDuplicatesRecordsFromExtractedDataList(saveListOfProspectSeller, false);
						logger.info("Removing duplicate from prospect seller excel data method exe completed");
					}
					if (CollectionUtils.isNotEmpty(sellersRecordWithoutDuplicates)) {
						unqiueRecordsForSavingToDb = (List<ProspectSeller>) this
								.removeDuplicateRecordsFromListIfThatRecordExistInDb(sellersRecordWithoutDuplicates,
										false);
					}
				}
			}
			try {
				if (isCustomer) {
					this.persistAllDataToDb(unqiueRecordsForSavingToDb, true);
				} else {
					this.persistAllDataToDb(unqiueRecordsForSavingToDb, false);
				}
			} catch (Exception ex) {
				logger.error("Exception occured while doing batch persist:{}", ex.getLocalizedMessage());
			}
			logger.info("Saved all the prospect customer / prospect seller to db");
			return savedToDb;
		} catch (Exception ex) {
			logger.error("Exception occured while reading values from excel:{}", ex.getLocalizedMessage());
			return false;
		}
	}

	/*
	 * Tiny method to remove whitespaces
	 */
	public String removeWhiteSpace(String stringValue) {
		return stringValue.replaceAll("\\s", "");
	}

	/**
	 * Method to create new objects according to the contact source and persist to
	 * db
	 * 
	 * @param prospectCustomer
	 * @return
	 */
	private Set<?> createObjectsBasedOnContactSources(ProspectCustomerDto prospectCustomer, Boolean isCustomer) {
		ProspectCustomer saveCustomer = null;
		ProspectSeller saveSeller = null;
		Set<ProspectCustomer> listOfProspectCustomer = new HashSet<>();
		Set<ProspectSeller> listOfProspectSeller = new HashSet<>();
		int emailSourcesize = 0, phoneSourcesize = 0;
		if (CollectionUtils.isNotEmpty(prospectCustomer.getEmailIds())) {
			emailSourcesize = prospectCustomer.getEmailIds().size();
		}
		if (CollectionUtils.isNotEmpty(prospectCustomer.getPhoneDetails())) {
			phoneSourcesize = prospectCustomer.getPhoneDetails().size();
		}
		if (emailSourcesize >= phoneSourcesize) {

			for (int i = 0; i < emailSourcesize; i++) {
				if (isCustomer) {
					saveCustomer = new ProspectCustomer();
					saveCustomer.setIsActive(true);
					saveCustomer.setId(UUID.randomUUID().toString());
//					saveCustomer.setIsCustomer(isCustomer);
					saveCustomer.setName(prospectCustomer.getName());
					saveCustomer.setIsSubscriber(true);
					saveCustomer.setCreatedAsProspectCustomer(LocalDate.now());
					saveCustomer.setIsNotificationSent(false);
					saveCustomer.setMovedToRegisterCustomer(null);
					String email = removeWhiteSpace(prospectCustomer.getEmailIds().get(i));
					saveCustomer.setEmailId(email);
					if (phoneSourcesize > i) {
						saveCustomer.setPhoneNumber(prospectCustomer.getPhoneDetails().get(i));
					}
					listOfProspectCustomer.add(saveCustomer);
				} else {
					saveSeller = new ProspectSeller();
					saveSeller.setIsActive(true);
					saveSeller.setId(UUID.randomUUID().toString());
					saveSeller.setSellerName(prospectCustomer.getName());
					saveSeller.setIsSubscriber(true);
					saveSeller.setCreatedAsProspectSeller(LocalDate.now());
					saveSeller.setIsNotificationSent(false);
					saveSeller.setMovedToRegisterSeller(null);
					String email = removeWhiteSpace(prospectCustomer.getEmailIds().get(i));
					saveSeller.setEmailId(email);
					if (phoneSourcesize > i) {
						saveSeller.setPhoneNumber(prospectCustomer.getPhoneDetails().get(i));
					}
					listOfProspectSeller.add(saveSeller);
				}
			}
		} else {
			for (int i = 0; i < phoneSourcesize; i++) {
				if (isCustomer) {
					saveCustomer = new ProspectCustomer();
					saveCustomer.setIsActive(true);
					saveCustomer.setId(UUID.randomUUID().toString());
//					saveCustomer.setIsCustomer(isCustomer);
					saveCustomer.setName(prospectCustomer.getName());
					saveCustomer.setIsSubscriber(true);
					saveCustomer.setCreatedAsProspectCustomer(LocalDate.now());
					saveCustomer.setMovedToRegisterCustomer(null);
					String email = removeWhiteSpace(prospectCustomer.getEmailIds().get(i));
					saveCustomer.setEmailId(email);
					if (phoneSourcesize > i) {
						saveCustomer.setPhoneNumber(prospectCustomer.getPhoneDetails().get(i));
					}
					listOfProspectCustomer.add(saveCustomer);
				} else {
					saveSeller = new ProspectSeller();
					saveSeller.setIsActive(true);
					saveSeller.setId(UUID.randomUUID().toString());
					saveSeller.setSellerName(prospectCustomer.getName());
					saveSeller.setIsSubscriber(true);
					saveSeller.setCreatedAsProspectSeller(LocalDate.now());
					saveSeller.setMovedToRegisterSeller(null);
					String email = removeWhiteSpace(prospectCustomer.getEmailIds().get(i));
					saveSeller.setEmailId(email);
					if (phoneSourcesize > i) {
						saveSeller.setPhoneNumber(prospectCustomer.getPhoneDetails().get(i));
					}
					listOfProspectSeller.add(saveSeller);
				}
			}
		}
		if (isCustomer) {
			return listOfProspectCustomer;
		} else {
			return listOfProspectSeller;
		}
	}

	// Method for string and empty validation
	private boolean stringNotBlankValidation(Cell ce) {
		return ObjectUtils.isNotEmpty(ce) && StringUtils.isNotBlank(ce.getStringCellValue());
	}

	/**
	 * Method to optimise mobile number values
	 * 
	 * @param number
	 * @return
	 */
	public String mobileNumberValidation(Number number) {
		String value = "";
		if (ObjectUtils.isNotEmpty(number)) {
			String mobile = String.valueOf(number);
			Pattern numericPattern = Pattern.compile(ProspectUserConstants.REGEX);
			Matcher numericValueMatcher = numericPattern.matcher(mobile);
			Boolean isNumericValue = numericValueMatcher.find() && numericValueMatcher.group().equals(mobile);
			Pattern specialCharPattern = Pattern.compile(ProspectUserConstants.SPECIAL_CHAR_PATTERN);
			Boolean isNumericValueHasSpecialChar = specialCharPattern.matcher(mobile).find();
			if (!isNumericValueHasSpecialChar) {
				if (isNumericValue) {
					String contactNo = String.valueOf(mobile);
					if (contactNo.length() == 10) {
						value = ProspectUserConstants.COUNTRY_CODE + contactNo;
					} else if (contactNo.length() == 12) {
						value = ProspectUserConstants.OPERATOR + contactNo;
					} else if (contactNo.length() == 13) {
						value = contactNo;
					}
				}
			} else {
				logger.warn("Invalid mobile value:{}", mobile);
			}
		}
		return value;
	}

	/**
	 * Generic method to extract original mobile value
	 * 
	 * @param cell
	 * @return
	 */
	private Number makeAsNumber(Cell cell) {
		Number number = null;
		if (checkNumericValue(cell) != null) {
			String value = removeWhiteSpace(checkNumericValue(cell));
			Double num = Double.valueOf(value);
			DecimalFormat pattern = new DecimalFormat(ProspectUserConstants.DECIMAL_FORMAT);
			NumberFormat testNumberFormat = NumberFormat.getNumberInstance();
			String mob = testNumberFormat.format(num);
			try {
				number = pattern.parse(mob);
			} catch (ParseException e) {
				logger.error("Exception occured while converting mobile number values from excel:{}",
						e.getLocalizedMessage());
			}
		}
		return number;
	}

	/**
	 * Generic method to extract original mobile value
	 * 
	 * @param cell
	 * @return
	 */
	private Number makeAsNumberBySingleValue(String ce) {
		Number number = null;
		if (ce != null) {
			Double num = Double.valueOf(ce);
			DecimalFormat pattern = new DecimalFormat(ProspectUserConstants.DECIMAL_FORMAT);
			NumberFormat testNumberFormat = NumberFormat.getNumberInstance();
			String mob = testNumberFormat.format(num);
			try {
				number = pattern.parse(mob);
			} catch (ParseException e) {
				logger.error("Exception occured while converting mobile number values from excel:{}",
						e.getLocalizedMessage());
			}
		}
		return number;
	}

	/*
	 * Get the value from cell without losing data and null checks
	 */
	public String checkNumericValue(Cell ce) {
		return ObjectUtils.isNotEmpty(ce)
				? (getCellTypeOwn(ce).equals(CellType.NUMERIC) ? String.valueOf(ce.getNumericCellValue())
						: ce.getStringCellValue())
				: "";
	}

	/*
	 * Method to extract the cell type from formula cell
	 */
	public CellType getCellTypeOwn(Cell ce) {
		if (ce.getCellType().equals(CellType.FORMULA)) {
			if (evaluator.evaluateFormulaCell(ce).equals(CellType.NUMERIC)) {
				return CellType.NUMERIC;
			} else if (evaluator.evaluateFormulaCell(ce).equals(CellType.STRING)) {
				return CellType.STRING;
			}
		}
		return ce.getCellType();
	}

	/**
	 * Method to check the workbook has the relevant data to further oprerations
	 * 
	 * @param workbook
	 * @return
	 */
	public Boolean checkWorkbookHasRelevantData(XSSFWorkbook workbook) {
		Boolean isRelevant = false;
		XSSFSheet sheet = workbook.getSheetAt(0);
		for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
			Row headerRow = sheet.getRow(0);
			Row currentRow = sheet.getRow(i);
			if (ObjectUtils.isNotEmpty(currentRow)) {
				for (int j = currentRow.getFirstCellNum(); j < headerRow.getLastCellNum(); j++) {
					String headerValue = headerRow.getCell(j).getStringCellValue();
					if (StringUtils.equalsIgnoreCase(headerValue, ProspectUserConstants.CUSTOMER_NAME)) {
						isRelevant = true;
					} else if (StringUtils.equalsIgnoreCase(headerValue, ProspectUserConstants.EMAIL_ID)) {
						isRelevant = true;
					} else if (StringUtils.equalsIgnoreCase(headerValue, ProspectUserConstants.MOBILE)) {
						isRelevant = true;
					}
				}
			}
		}
		return isRelevant;
	}

	/**
	 * Method executing persist operation to save list of datas
	 * 
	 * @param customerDatas
	 * @return
	 */
	private boolean persistAllDataToDb(List<?> customerDatas, Boolean isCustomer) {
		Boolean subscriptionResult = false;
		if (CollectionUtils.isNotEmpty(customerDatas)) {
			if (isCustomer) {
				@SuppressWarnings("unchecked")
				List<ProspectCustomer> prospectCustomers = (List<ProspectCustomer>) customerDatas;
				List<ProspectCustomer> savedProspectCustomers =prospectCustomerRepo.saveAll(prospectCustomers);
				logger.info("Customer datas saved to db");
				List<String> prospectCustomerEmailLists = savedProspectCustomers.stream().map(ProspectCustomer::getEmailId)
						.collect(Collectors.toList());
				logger.info("subscription api method exe started");
				subscriptionResult = this.subscribeWithSubsType(prospectCustomerEmailLists);
				logger.info("subscription api method exe ended:{}", subscriptionResult);
				return true;
			} else {
				@SuppressWarnings("unchecked")
				List<ProspectSeller> prospectSellers = (List<ProspectSeller>) customerDatas;
				List<ProspectSeller> savedProspectSellers = prospectSellerRepo.saveAll(prospectSellers);
				logger.info("Customer datas saved to db");
				List<String> prospectSellerEmailLists = savedProspectSellers.stream().map(ProspectSeller::getEmailId)
						.collect(Collectors.toList());
				logger.info("subscription api method exe started");
				subscriptionResult = this.subscribeWithSubsType(prospectSellerEmailLists);
				logger.info("subscription api method exe ended:{}", subscriptionResult);
				return true;
			}
		} else {
			logger.info("Customer datas not saved to db");
			return false;
		}
	}

	/**
	 * Method to remove the duplicate/repeated records from uploaded excel
	 * 
	 * @param prospectCustomers
	 * @return
	 */
	private List<?> removedDuplicatesRecordsFromExtractedDataList(List<?> prospectCustomers, Boolean isCustomer) {
		@SuppressWarnings("unchecked")
		List<ProspectCustomer> prospectCustomers2 = (List<ProspectCustomer>) prospectCustomers;
		@SuppressWarnings("unchecked")
		List<ProspectSeller> prospectSellers2 = (List<ProspectSeller>) prospectCustomers;
		if (isCustomer) {
			List<String> emailids = prospectCustomers2.stream().map(ProspectCustomer::getEmailId)
					.collect(Collectors.toList());
			List<String> phoneNos = prospectCustomers2.stream().map(ProspectCustomer::getPhoneNumber)
					.collect(Collectors.toList());
			prospectCustomers2.stream().forEach(cust -> {
				if (StringUtils.isNotBlank(cust.getEmailId())) {
					if (isDuplicateValueFromList(emailids, cust.getEmailId())) {
						emailids.remove(cust.getEmailId());
						cust.setEmailId(null);
					}
				}
				if (StringUtils.isNotBlank(cust.getPhoneNumber())) {
					if (isDuplicateValueFromList(phoneNos, cust.getPhoneNumber())) {
						phoneNos.remove(cust.getPhoneNumber());
						cust.setPhoneNumber(null);
					}
				}
			});
			prospectCustomers2.removeIf(
					cust -> StringUtils.isBlank(cust.getEmailId()) && StringUtils.isBlank(cust.getPhoneNumber()));
		} else {
			List<String> emailids = prospectSellers2.stream().map(ProspectSeller::getEmailId)
					.collect(Collectors.toList());
			List<String> phoneNos = prospectSellers2.stream().map(ProspectSeller::getPhoneNumber)
					.collect(Collectors.toList());
			prospectSellers2.stream().forEach(cust -> {
				if (StringUtils.isNotBlank(cust.getEmailId())) {
					if (isDuplicateValueFromList(emailids, cust.getEmailId())) {
						emailids.remove(cust.getEmailId());
						cust.setEmailId(null);
					}
				}
				if (StringUtils.isNotBlank(cust.getPhoneNumber())) {
					if (isDuplicateValueFromList(phoneNos, cust.getPhoneNumber())) {
						phoneNos.remove(cust.getPhoneNumber());
						cust.setPhoneNumber(null);
					}
				}
			});
			prospectSellers2.removeIf(
					cust -> StringUtils.isBlank(cust.getEmailId()) && StringUtils.isBlank(cust.getPhoneNumber()));
		}
		return isCustomer ? prospectCustomers2 : prospectSellers2;
	}

	/*
	 * Method to check the duplicate record count for excel level
	 */
	private Boolean isDuplicateValueFromList(List<String> values, String value) {
		Boolean isValid = false;
		if (Collections.frequency(values, value) > 1) {
			isValid = true;
		}
		return isValid;
	}

	/*
	 * Method to optimise the duplicate records by checking in DB level
	 */
	private List<?> removeDuplicateRecordsFromListIfThatRecordExistInDb(List<?> prospectCustomers, Boolean isCustomer) {
		@SuppressWarnings("unchecked")
		List<ProspectCustomer> prospectCustomers2 = (List<ProspectCustomer>) prospectCustomers;
		@SuppressWarnings("unchecked")
		List<ProspectSeller> prospectSellers2 = (List<ProspectSeller>) prospectCustomers;
		if (CollectionUtils.isNotEmpty(prospectCustomers)) {
			List<ProspectCustomer> uniqueRecordsOfProspectCustomer = new ArrayList<>();
			List<ProspectSeller> uniqueRecordsOfProspectSeller = new ArrayList<>();
//			List<?> uniquesRecords = new ArrayList<>();
			if (isCustomer) {
				List<String> emailids = this.setOfProspectCustomerStringValues(ProspectUserConstants.TABLE_MAIL_ID);
				List<String> phoneNos = this
						.setOfProspectCustomerStringValues(ProspectUserConstants.TABLE_PHONE_NUMBER);
				uniqueRecordsOfProspectCustomer = new ArrayList<>();
				uniqueRecordsOfProspectCustomer.addAll(prospectCustomers2);
//				uniquesRecords = uniqueRecordsOfProspectCustomer;
				uniqueRecordsOfProspectCustomer.stream().forEach(cust -> {
					if (ObjectUtils.isNotEmpty(cust) && StringUtils.isNotBlank(cust.getEmailId())) {
						if (isDuplicateValueFromDb(emailids, cust.getEmailId())) {
							cust.setEmailId(null);
						//	prospectCustomers.remove(cust);
						}
					}
					if (ObjectUtils.isNotEmpty(cust) && StringUtils.isNotBlank(cust.getPhoneNumber())) {
						if (isDuplicateValueFromDb(phoneNos, cust.getPhoneNumber())) {
							cust.setPhoneNumber(null);
							//	prospectCustomers.remove(cust);
						}
					}
				});
				prospectCustomers2.removeIf(
						cust -> StringUtils.isBlank(cust.getEmailId()) && StringUtils.isBlank(cust.getPhoneNumber()));
			} else {
				List<String> emailids = this.setOfProspectSellerStringValues(ProspectUserConstants.TABLE_MAIL_ID);
				List<String> phoneNos = this
						.setOfProspectSellerStringValues(ProspectUserConstants.TABLE_PHONE_NUMBER);
				uniqueRecordsOfProspectSeller = new ArrayList<>();
				uniqueRecordsOfProspectSeller.addAll(prospectSellers2);
//				uniquesRecords = uniqueRecordsOfProspectSeller;
				uniqueRecordsOfProspectSeller.stream().forEach(cust -> {
					if (ObjectUtils.isNotEmpty(cust) && StringUtils.isNotBlank(cust.getEmailId())) {
						if (isDuplicateValueFromDb(emailids, cust.getEmailId())) {
							cust.setEmailId(null);
//							prospectSellers2.remove(cust);
						}
					}
					if (ObjectUtils.isNotEmpty(cust) && StringUtils.isNotBlank(cust.getPhoneNumber())) {
						if (isDuplicateValueFromDb(phoneNos, cust.getPhoneNumber())) {
							cust.setPhoneNumber(null);
//							prospectSellers2.remove(cust);
						}
					}
				});
				prospectSellers2.removeIf(
						cust -> StringUtils.isBlank(cust.getEmailId()) && StringUtils.isBlank(cust.getPhoneNumber()));
			}
		}
		if (isCustomer) {
			return prospectCustomers2;
		} else {
			return prospectSellers2;
		}
	}

	/*
	 * Method to get the list of email/mobile values from prospect customer table by
	 * DB level
	 */
	public List<String> setOfProspectCustomerStringValues(String value) {
		DistinctIterable<String> distinctIds = mongoTemplate
				.getCollection(mongoTemplate.getCollectionName(ProspectCustomer.class)).distinct(value, String.class);
//		return Lists.newArrayList(distinctIds);
		return List.of();
	}

	/*
	 * Method to get the list of email/mobile values from prospect seller table by
	 * DB level
	 */
	public List<String> setOfProspectSellerStringValues(String value) {
		DistinctIterable<String> distinctIds = mongoTemplate
				.getCollection(mongoTemplate.getCollectionName(ProspectSeller.class)).distinct(value, String.class);
//		return Lists.newArrayList(distinctIds);
		return List.of();
	}

	/*
	 * Method to check the duplicate count in DB level
	 */
	private Boolean isDuplicateValueFromDb(List<String> values, String value) {
		Boolean isValid = false;
		if (Collections.frequency(values, value) == 1) {
			isValid = true;
		}
		return isValid;
	}
	
	/**
	 * Api to send the subscription and un-subscription details
	 * 
	 * @param userDetails
	 * @return
	 */
	private Boolean subscribeWithSubsType(List<String> userContactsSources) {
		List<AddSubscriptionType> userDetails = userContactsSources.stream().map(
				email -> new AddSubscriptionType(email, Arrays.asList(ProspectUserConstants.SUBSCRIPTION_TYPE)))
				.collect(Collectors.toList());
		logger.info("Subscription api url :{}", customerCareSubscriptionUrl);
		Boolean unsubscribeApiResponse = restTemplate
				.exchange(customerCareSubscriptionUrl, HttpMethod.POST,
						new HttpEntity<List<AddSubscriptionType>>(userDetails, new HttpHeaders()), Boolean.class)
				.getBody();
		logger.info("Subscription api response from customer care service:{}", unsubscribeApiResponse);
		return unsubscribeApiResponse;
	}
}
