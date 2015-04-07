package org.stormdev.chattranslator.api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.bukkit.entity.Player;
import org.stormdev.chattranslator.main.ChatListener;
import org.stormdev.chattranslator.main.ChatTranslator;
import org.stormdev.translator.yandex.errors.YandexException;
import org.stormdev.translator.yandex.errors.YandexUnsupportedLanguageException;

public class TranslatorToolkit {
	public static TranslatorToolkit getToolkit(){
		return ChatTranslator.TOOLKIT;
	}
	
	public LanguageManager getLanguageManager(){
		return ChatTranslator.languageManager;
	}
	
	public String getAPIKey(){
		return ChatTranslator.API_KEY;
	}
	
	public void setAPIKey(String key){
		ChatTranslator.API_KEY = key;
	}
	
	public void setLanguageDataStorage(DataStorage<Lang> dataStorage){
		ChatTranslator.languageManager.setDataStorage(dataStorage);
	}
	
	public void openLangSelectMenu(Player player){
		ChatTranslator.selectMenu.show(player);
	}
	
	public Lang detectLanguage(String text) throws YandexUnsupportedLanguageException, YandexException, MalformedURLException, IOException{
		return ChatTranslator.getTranslator().getLang(text);
	}
	
	public String translate(Lang from, Lang to, String text) throws YandexException, MalformedURLException, IOException{
		return ChatTranslator.getTranslator().translate(from, to, text);
	}
	
	public void setMessageHandler(MessageHandler messageHandler){
		ChatListener.messageHandler = messageHandler;
	}
	
	public void handleAsIfChatEvent(List<Player> recipients, Player chatted, String msg){
		ChatListener.handleAsChatEvent(recipients, chatted, msg);
	}
	
	public void handleAsIfChatEvent(List<Player> recipients, Player chatted, String msgPrefix, String msg){
		ChatListener.handleAsChatEvent(recipients, chatted, msgPrefix, msg);
	}
}
