package org.stormdev.translator.yandex.errors;

public class YandexTextToolongException extends YandexException {
	private static final long serialVersionUID = 1L;

	@Override
	public int getErrorCode() {
		return 413;
	}

	@Override
	public String getErrorMsg() {
		return "The text size exceeds the maximum.";
	}
	
}
