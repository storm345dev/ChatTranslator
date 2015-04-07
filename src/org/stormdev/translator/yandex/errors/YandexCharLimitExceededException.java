package org.stormdev.translator.yandex.errors;

public class YandexCharLimitExceededException extends YandexException {
	private static final long serialVersionUID = 1L;

	@Override
	public int getErrorCode() {
		return 404;
	}

	@Override
	public String getErrorMsg() {
		return "You have reached the daily limit for the volume of translated text (including calls of the detect method).";
	}
	
}
