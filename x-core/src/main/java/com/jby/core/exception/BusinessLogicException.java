package com.jby.core.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于业务逻辑主动抛出与业务相关的异常时使用（如登录时账号、密码不匹配），取代原有的new Exception写法
 */
@Slf4j
@Getter
public class BusinessLogicException extends RuntimeException {

	private static final long serialVersionUID = -9030799817318892152L;
	//错误码
	private Integer code;

	//重定向
	private String message;
	
	public BusinessLogicException(String message) {
		this.message = message;
		log.error("BusinessLogicException:" + message);
	}

	public BusinessLogicException(Integer code, String message) {
		this.message = message;
		this.code = code;
		log.error("BusinessLogicException:" + message);
	}



}
