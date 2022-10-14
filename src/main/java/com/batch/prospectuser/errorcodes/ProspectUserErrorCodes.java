package com.dolphinskart.prospectuser.errorcodes;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ProspectUserErrorCodes {

	EMPTY_FILE("PC01", "Empty file can't be Accepted!"),
	INVALID_WORKBOOK("PC02","Please upload the valid excel workbook and data"),
	SOMETHING_WENT_WRONG("PC03", "Something went wrong while uploading the file"),
	NO_CUSTOMER_EXIST("PC04", "There is no subscriber with the provided email id");

	public String errorCode;
	public String message;

	public String getErrorCode() {
		return errorCode;
	}

	public String getMessage() {
		return message;
	}
}
