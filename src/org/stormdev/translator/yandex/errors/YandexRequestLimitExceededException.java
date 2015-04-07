package org.stormdev.translator.yandex.errors;

public class YandexRequestLimitExceededException extends YandexException {
	private static final long serialVersionUID = 1L;

	@Override
	public int getErrorCode() {
		return 403;
	}

	@Override
	public String getErrorMsg() {
		return "You have reached the daily limit for requests (including calls of the detect method).";
	}
	
}
