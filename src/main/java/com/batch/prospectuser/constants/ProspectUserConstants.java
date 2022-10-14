package com.dolphinskart.prospectuser.constants;

public interface ProspectUserConstants {

	public static final String SAVED = "Uploaded successfully";
	public static final String CUSTOMER_NAME = "Name";
	public static final String CUSTOMER = "customer";
	public static final String SELLER = "seller";
	public static final String BOTH = "both";
	public static final String EMAIL_ID = "Email ID";
	public static final String MOBILE = "Phone Number";
	public static final String TABLE_MAIL_ID = "email_id";
	public static final String TABLE_PHONE_NUMBER = "phone_number";
	public static final String PC_TYPE = "marketing";
	public static final String PC_SUB_TYPE = "prospect-customer";
	public static final String PS_SUB_TYPE ="prospect-seller";
	public static final String PC_IMAGE_SRC = "https://storage.googleapis.com/dk-qa-media-storage/dk-qa-media-storage/images/banners/1661840472877customer_mailer_modified_11zon.jpg";
	public static final String PS_IMAGE_SRC = "https://storage.googleapis.com/dk-qa-media-storage/dk-qa-media-storage/images/banners/1663083213545Dolphins_kart.jpg";
	public static final String IMAGE = "image";
	public static final String REGEX = "(0|91)?[6-9][0-9]{9}";
	public static final String EMAIL = "emailId";
	public static final String DOLPHINS_SUPPORT = "support@dolphinskart.com";
	public static final String LAST_NOTIFICATION_SENT_AT = "last_notification_sent_at";
	public static final String DELIMETER_COMMA = ",";
	public static final String DECIMAL_FORMAT = "#,#,#,#,#,#,#,#,#,#";
	public static final String MOBILE_PATTERN = ".*\\d.*";
	public static final String OPERATOR = "+";
	public static final String COUNTRY_CODE = "+91";
	public static final String NAME = "name";
	public static final String SPECIAL_CHAR_PATTERN = "[^a-zA-Z0-9]";
	public static final String DATE_FORMAT_PC = "dd-MM-yyyy";
	public static final String DATE_TIME_FORMAT = "dd-MM-yyyy hh:mm a";
	public static final String PERCENTAGE = "%";
	public static final long YEARS_COUNT = 3;
	public static final String SUBSCRIPTION_TYPE = "Marketing Email";
	
	// Sms contents
	public static final String PC_MSG = "Register with dolphinskart.com" + System.lineSeparator()
			+ "today and get a giftcard worth Rs 100 shop for fashion, kitchen, etc. Register, Refer, Reward # RRR";
	public static final String PS_MSG = "Are you looking for more growth in your business?" + System.lineSeparator()
			+ "Start Selling your Fashion, Electronics, and any other products at seller.dolphinskart.com and have 5X Sales.";
}
