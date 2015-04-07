package org.stormdev.translator.yandex.errors;

public class YandexUnsupportedLanguageException extends YandexException {
	private static final long serialVersionUID = 1L;

	@Override
	public int getErrorCode() {
		return 501;
	}

	@Override
	public String getErrorMsg() {
		return "The specified translation direction is not supported.";
	}
	
}
