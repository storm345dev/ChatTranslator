package org.stormdev.translator.yandex.errors;

import java.io.IOException;

public abstract class YandexException extends IOException {
	private static final long serialVersionUID = 1L;
	
	public abstract int getErrorCode();
	public abstract String getErrorMsg();
	
	@Override
	public String getMessage(){
		return getErrorCode()+": "+getErrorMsg();
	}
}
