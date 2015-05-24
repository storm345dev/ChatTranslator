package org.stormdev.translator.yandex.errors;

public class YandexBlockedKeyException extends YandexException {
	private static final long serialVersionUID = 1L;

	@Override
	public int getErrorCode() {
		return 401;
	}

	@Override
	public String getErrorMsg() {
		return "(Yandex error, is the API key correct?) Error: Invalid API Key";
	}
	
}
