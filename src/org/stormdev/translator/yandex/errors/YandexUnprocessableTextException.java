package org.stormdev.translator.yandex.errors;

public class YandexUnprocessableTextException extends YandexException {
	private static final long serialVersionUID = 1L;

	@Override
	public int getErrorCode() {
		return 422;
	}

	@Override
	public String getErrorMsg() {
		return "The text could not be translated.";
	}
	
}
