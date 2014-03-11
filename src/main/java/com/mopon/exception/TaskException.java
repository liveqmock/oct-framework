package com.mopon.exception;

import com.mopon.entity.logs.ErrorMsg;

public class TaskException extends BaseException {

	private static final long serialVersionUID = 1L;

	public TaskException(ErrorMsg errorMsg) {
		super(errorMsg);
	}
	
	public TaskException(String message, ErrorMsg errorMsg) {
		super(message, errorMsg);
	}
	
	public TaskException(Throwable cause, ErrorMsg errorMsg) {
		super(cause, errorMsg);
	}
	
	public TaskException(String message, Throwable cause, ErrorMsg errorMsg) {
		super(message, cause, errorMsg);
	}

}
