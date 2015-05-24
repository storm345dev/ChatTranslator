package org.stormdev.translator.yandex.errors;

public class YandexInvalidKeyException extends YandexException {
	private static final long serialVersionUID = 1L;

	@Override
	public int getErrorCode() {
		return 402;
	}

	@Override
	public String getErrorMsg() {
		return "(Yandex error, is the API key correct?) Error: API Key blocked";
	}
	
}
