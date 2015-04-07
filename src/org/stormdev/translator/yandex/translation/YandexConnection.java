package org.stormdev.translator.yandex.translation;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import org.stormdev.chattranslator.api.Lang;
import org.stormdev.translator.yandex.errors.YandexBlockedKeyException;
import org.stormdev.translator.yandex.errors.YandexCharLimitExceededException;
import org.stormdev.translator.yandex.errors.YandexException;
import org.stormdev.translator.yandex.errors.YandexInvalidKeyException;
import org.stormdev.translator.yandex.errors.YandexRequestLimitExceededException;
import org.stormdev.translator.yandex.errors.YandexTextToolongException;
import org.stormdev.translator.yandex.errors.YandexUnknownException;
import org.stormdev.translator.yandex.errors.YandexUnprocessableTextException;
import org.stormdev.translator.yandex.errors.YandexUnsupportedLanguageException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class YandexConnection {
	public String API_KEY = "NULL";
	
	public YandexConnection(String API_KEY){
		this.API_KEY = API_KEY;
	}
	
	public static void main(String[] args){
		YandexConnection con = new YandexConnection("NULL");
		for(Lang to:Lang.values()){
			try {
				String out = con.translate(Lang.ENGLISH, to, "Auto web translation in java");
				System.out.println(to.getLanguageName()+": "+out);
			} catch (Exception e) {
				System.out.println(to.getLanguageName()+" is NOT SUPPORTED!");
			}
		}
	}
	
	private void throwException(int code) throws YandexException{
		if(code == 401){
			throw new YandexInvalidKeyException();
		}
		else if(code == 402){
			throw new YandexBlockedKeyException();
		}
		else if(code == 403){
			throw new YandexRequestLimitExceededException();
		}
		else if(code == 404){
			throw new YandexCharLimitExceededException();
		}
		else if(code == 413){
			throw new YandexTextToolongException();
		}
		else if(code == 422){
			throw new YandexUnprocessableTextException();
		}
		else if(code == 501 || code == 400){
			throw new YandexUnsupportedLanguageException();
		}
		throw new YandexUnknownException(code);
	}
	
	public Lang getLang(String text) throws YandexUnsupportedLanguageException, YandexException, MalformedURLException, IOException{
		JsonElement resp = getJSONResponse(getDetectUrl(text));
		if(resp == null){
			throw new YandexUnknownException();
		}
		
		JsonObject ob = resp.getAsJsonObject();
		if(!ob.has("code")){
			throw new YandexUnknownException();
		}
		int code = ob.get("code").getAsInt();
		
		if(code == 200){
			//Successfully translated
			if(!ob.has("lang")){
				throw new YandexUnknownException();
			}
			return Lang.forShortString(ob.get("lang").getAsString());
		}
		else {
			throwException(code);
			return Lang.ENGLISH;
		}
	}
	
	public String translate(Lang from, Lang to, String text) throws YandexException, MalformedURLException, IOException{
		JsonElement resp = getJSONResponse(getTranslateUrl(from, to, text));
		if(resp == null){
			throw new YandexUnknownException();
		}
		
		JsonObject ob = resp.getAsJsonObject();
		if(!ob.has("code")){
			throw new YandexUnknownException();
		}
		int code = ob.get("code").getAsInt();
		if(code == 200){
			//Successfully translated
			if(!ob.has("text")){
				throw new YandexUnknownException();
			}
			return ob.get("text").getAsString();
		}
		else {
			throwException(code);
			return "";
		}
	}
	
	public JsonElement getJSONResponse(String url) throws MalformedURLException, IOException{
		String s = getResponse(url);
		return new JsonParser().parse(s);
	}
	
	private String convertStreamToString(java.io.InputStream is) {
	    Scanner s = new Scanner(is, "UTF-8");
	    s.useDelimiter("\\A");
	    try {
			String st = s.hasNext() ? s.next() : "";
			s.close();
			return st;
		} catch (Exception e) {
			s.close();
			return "";
		}
	}
	
	public String getResponse(String url) throws MalformedURLException, IOException, YandexException{
		URLConnection con = new URL(url).openConnection();
		con.setUseCaches(true); //For similar phrases
		con.setConnectTimeout(10000);
		con.setReadTimeout(10000);
		con.connect();
		if(con instanceof HttpURLConnection){
			HttpURLConnection huc = (HttpURLConnection) con;
			int code = huc.getResponseCode();
			if(code != 200){
				throwException(code);
			}
		}
		if(con instanceof HttpsURLConnection){
			HttpsURLConnection huc = (HttpsURLConnection) con;
			int code = huc.getResponseCode();
			if(code != 200){
				throwException(code);
			}
		}
		InputStream in = con.getInputStream();
		String response = convertStreamToString(in);
		in.close();
		return response;
	}
	
	private String urlEncode(String s){
		String urlEncodedMsg;
		try {
			urlEncodedMsg = URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("Unsupported URL encoding for this platform: UTF-8");
			return null;
		}
		//return urlEncodedMsg;
		return urlEncodedMsg.replace("%20", "+");
	}
	
	private String getDetectUrl(String toTranslate){
		//https://translate.yandex.net/api/v1.5/tr.json/detect?key=APIkey&text=Hello+world
		StringBuilder base = new StringBuilder("https://translate.yandex.net/api/v1.5/tr.json/detect?key=");
		base.append(urlEncode(API_KEY));
		base.append("&text=");
		base.append(urlEncode(toTranslate));
		return base.toString();
	}
	
	private String getTranslateUrl(Lang from, Lang to, String toTranslate){
		return getTranslateUrl(Lang.getTranslateString(from, to), toTranslate);
	}
	
	private String getTranslateUrl(String langString, String toTranslate){
		//https://translate.yandex.net/api/v1.5/tr.json/translate?key=APIkey&lang=en-ru&text=To+be,+or+not+to+be%3F&text=That+is+the+question.
		StringBuilder base = new StringBuilder("https://translate.yandex.net/api/v1.5/tr.json/translate?key=");
		base.append(urlEncode(API_KEY));
		base.append("&lang=");
		base.append(urlEncode(langString));
		base.append("&text=");
		base.append(urlEncode(toTranslate));
		return base.toString();
	}
}
