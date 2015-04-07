package org.stormdev.chattranslator.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.stormdev.chattranslator.api.Lang;
import org.stormdev.chattranslator.api.MessageHandler;
import org.stormdev.openapi.gui.MetadataValue;

public class ChatListener implements Listener {
	public static MessageHandler messageHandler = null;
	
	public static void handleAsChatEvent(List<Player> recipients, Player chatted, String msg){
		handleAsChatEvent(recipients, chatted, "", msg);
	}
	
	public static void handleAsChatEvent(List<Player> recipients, Player chatted, String msgPrefix, String msg){
		Lang originalLang = ChatTranslator.languageManager.getLanguage(chatted);
		if(!ChatTranslator.languageManager.hasSetLang(chatted)){
			try {
				originalLang = ChatTranslator.getTranslator().getLang(msg);
				if(!originalLang.getShortLangName().equals(ChatTranslator.DEFAULT_LANGUAGE.getShortLangName())){
					String s = ChatColor.RED+"Detected you spoke in "+originalLang.getLanguageName()+", which is different to the your currently set language of "+ChatTranslator.DEFAULT_LANGUAGE.getLanguageName()+"! Perhaps consider changing your currently set language to match?";
					chatted.sendMessage(ChatTranslator.getTranslator().translate(Lang.ENGLISH, originalLang, s));
				}
			} catch (Exception e) {
				//Don't worry, stay with default
			}
		}
		List<Lang> languages = ChatTranslator.languageManager.getPlayerLanguagesExc(recipients, originalLang);
		List<Player> sentTo = new ArrayList<Player>(recipients);
		for(Lang l:languages){
			try {
				String translated = ChatTranslator.getTranslator().translate(originalLang, l, msg);
				List<Player> toSend = ChatTranslator.languageManager.getPlayersByLanguage(recipients, l);
				String formattedMsg = formatMsg(chatted, msgPrefix + translated);
				for(Player p:toSend){
					messageHandler.sendMessage(formattedMsg, p);
					sentTo.remove(p);
				}
			} catch (Exception e) {
				e.printStackTrace();
				String formattedMsg = formatMsg(chatted, msgPrefix + msg);
				for(Player player:recipients){
					messageHandler.sendMessage(formattedMsg, player);
					return;
				}
			}
		}
		messageHandler.sendMessage(formatMsg(chatted, msgPrefix + msg), sentTo.toArray(new Player[]{})); //Same lang as chatter
	}
	
	public ChatListener(){
		messageHandler = new DefaultMessageHandler();
		Bukkit.getPluginManager().registerEvents(this, ChatTranslator.plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	void onChat(AsyncPlayerChatEvent event){
		if(!messageHandler.overrideChatEvent() || event.isCancelled()){
			return;
		}
		event.setCancelled(true);
		String msg = event.getMessage();
		List<Player> recipients = new ArrayList<Player>(event.getRecipients());
		Lang originalLang = ChatTranslator.languageManager.getLanguage(event.getPlayer());
		if(!ChatTranslator.languageManager.hasSetLang(event.getPlayer())){
			try {
				originalLang = ChatTranslator.getTranslator().getLang(msg);
				if(!originalLang.getShortLangName().equals(ChatTranslator.DEFAULT_LANGUAGE.getShortLangName())){
					String s = ChatColor.RED+"Detected you spoke in "+originalLang.getLanguageName()+", which is different to the your currently set language of "+ChatTranslator.DEFAULT_LANGUAGE.getLanguageName()+"! Perhaps consider changing your currently set language to match?";
					event.getPlayer().sendMessage(ChatTranslator.getTranslator().translate(Lang.ENGLISH, originalLang, s));
				}
			} catch (Exception e) {
				//Don't worry, stay with default
			}
		}
		Player chatted = event.getPlayer();
		List<Lang> languages = ChatTranslator.languageManager.getPlayerLanguagesExc(recipients, originalLang);
		List<Player> sentTo = new ArrayList<Player>(recipients);
		for(Lang l:languages){
			try {
				String translated = ChatTranslator.getTranslator().translate(originalLang, l, msg);
				List<Player> toSend = ChatTranslator.languageManager.getPlayersByLanguage(recipients, l);
				String formattedMsg = formatMsg(chatted, translated);
				for(Player p:toSend){
					messageHandler.sendMessage(formattedMsg, p);
					sentTo.remove(p);
				}
			} catch (Exception e) {
				e.printStackTrace();
				String formattedMsg = formatMsg(chatted, msg);
				for(Player player:recipients){
					messageHandler.sendMessage(formattedMsg, player);
					return;
				}
			}
		}
		messageHandler.sendMessage(formatMsg(chatted, msg), sentTo.toArray(new Player[]{})); //Same lang as chatter
	}
	
	private static String formatMsg(Player chatted, String msg){
		return messageHandler.formatMsg(chatted, msg);
	}
	
	@EventHandler
	void onJoin(PlayerJoinEvent event){
		final Player player = event.getPlayer();
		Bukkit.getScheduler().runTaskAsynchronously(ChatTranslator.plugin, new Runnable(){

			@Override
			public void run() {
				player.removeMetadata(LanguageManager.META, ChatTranslator.plugin);
				if(ChatTranslator.languageManager.hasSetLang(player.getUniqueId())){
					player.setMetadata(LanguageManager.META, new MetadataValue(ChatTranslator.languageManager.getLanguage(player), ChatTranslator.plugin));
				}
				return;
			}});
	}
}
