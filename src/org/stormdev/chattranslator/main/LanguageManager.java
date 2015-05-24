package org.stormdev.chattranslator.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.stormdev.chattranslator.api.DataStorage;
import org.stormdev.chattranslator.api.Lang;
import org.stormdev.openapi.gui.MetadataValue;

public class LanguageManager implements org.stormdev.chattranslator.api.LanguageManager {
	public static String META = "lang.meta";
	public static DataStorage<Lang> data = null;
	
	public LanguageManager(DataStorage<Lang> data){
		LanguageManager.data = data;
	}
	
	public void setDataStorage(DataStorage<Lang> data){
		LanguageManager.data = data;
	}
	
	@Override
	public List<Player> getOnlinePlayersByLanguage(Lang lang){
		List<Player> players = new ArrayList<Player>();
		for(Player pl:Bukkit.getOnlinePlayers()){
			if(!pl.hasMetadata(META)){
				continue; //Don't have a lang set
			}
			Lang l = getLanguage(pl);
			if(l.getShortLangName().equals(lang.getShortLangName())){
				players.add(pl);
			}
		}
		return players;
	}
	
	@Override
	public List<Lang> getOnlinePlayerLanguages(){
		List<Lang> langs = new ArrayList<Lang>();
		for(Player pl:Bukkit.getOnlinePlayers()){
			if(!pl.hasMetadata(META)){
				continue; //Don't have a lang set
			}
			Lang l = getLanguage(pl);
			if(!langs.contains(l)){
				langs.add(l);
			}
		}
		return langs;
	}
	
	@Override
	public List<Lang> getOnlinePlayerLanguagesExcDefault(){
		List<Lang> langs = new ArrayList<Lang>();
		for(Player pl:Bukkit.getOnlinePlayers()){
			if(!pl.hasMetadata(META)){
				continue; //Don't have a lang set
			}
			Lang l = getLanguage(pl);
			if(l.getShortLangName().equals(ChatTranslator.DEFAULT_LANGUAGE.getShortLangName())){
				continue; //It's the default lang
			}
			if(!langs.contains(l)){
				langs.add(l);
			}
		}
		return langs;
	}
	
	@Override
	public List<Player> getPlayersByLanguage(List<Player> pls, Lang lang){
		List<Player> players = new ArrayList<Player>();
		for(Player pl:pls){
			if(!pl.hasMetadata(META)){
				continue; //Don't have a lang set
			}
			Lang l = getLanguage(pl);
			if(l.getShortLangName().equals(lang.getShortLangName())){
				players.add(pl);
			}
		}
		return players;
	}
	
	@Override
	public List<Lang> getPlayerLanguages(List<Player> pls){
		List<Lang> langs = new ArrayList<Lang>();
		for(Player pl:pls){
			if(!pl.hasMetadata(META)){
				continue; //Don't have a lang set
			}
			Lang l = getLanguage(pl);
			if(!langs.contains(l)){
				langs.add(l);
			}
		}
		return langs;
	}
	
	@Override
	public List<Lang> getPlayerLanguagesExcDefault(List<Player> pls){
		List<Lang> langs = new ArrayList<Lang>();
		for(Player pl:pls){
			if(!pl.hasMetadata(META)){
				continue; //Don't have a lang set
			}
			Lang l = getLanguage(pl);
			if(l.getShortLangName().equals(ChatTranslator.DEFAULT_LANGUAGE.getShortLangName())){
				continue; //It's the default lang
			}
			if(!langs.contains(l)){
				langs.add(l);
			}
		}
		return langs;
	}
	
	@Override
	public List<Lang> getPlayerLanguagesExc(List<Player> pls, Lang... exc){
		List<Lang> excd = Arrays.asList(exc);
		List<Lang> langs = new ArrayList<Lang>();
		for(Player pl:pls){
			if(!pl.hasMetadata(META)){
				continue; //Don't have a lang set
			}
			Lang l = getLanguage(pl);
			if(excd.contains(l)){
				continue; //It's the default lang
			}
			if(!langs.contains(l)){
				langs.add(l);
			}
		}
		return langs;
	}
	
	@Override
	public boolean hasSetLang(Player player){
		if(!player.hasMetadata(META)){
			return false;
		}
		else {
			return true;
		}
	}
	
	public boolean hasSetLang(UUID uuid){
		if(data == null){
			ChatTranslator.plugin.getLogger().warning("Chat Translator cannot function without a method of data storage enabled either in the config or by another plugin! No such data storage method is enabled!");
			return false;
		}
		return data.hasValueSet(uuid);
	}
	
	@Override
	public Lang getLanguage(Player player){
		if(player.hasMetadata(META)){
			try {
				return (Lang) player.getMetadata(META).get(0).value();
			} catch (Exception e) {
				//Oh well
				player.removeMetadata(META, ChatTranslator.plugin);
			}
		}
		UUID uuid = player.getUniqueId();
		return getLanguage(uuid);
	}
	
	public Lang getLanguage(UUID uuid){
		if(data == null){
			ChatTranslator.plugin.getLogger().warning("Chat Translator cannot function without a method of data storage enabled either in the config or by another plugin! No such data storage method is enabled!");
			return ChatTranslator.DEFAULT_LANGUAGE;
		}
		return data.getValue(uuid);
	}
	
	@Override
	public void setLanguage(Player player, Lang lang){
		if(data == null){
			ChatTranslator.plugin.getLogger().warning("Chat Translator cannot function without a method of data storage enabled either in the config or by another plugin! No such data storage method is enabled!");
			return;
		}
		setLanguage(player.getUniqueId(), lang);
		player.removeMetadata(META, ChatTranslator.plugin);
		player.setMetadata(META, new MetadataValue(lang, ChatTranslator.plugin));
	}
	
	public void setLanguage(UUID uuid, Lang lang){
		if(data == null){
			ChatTranslator.plugin.getLogger().warning("Chat Translator cannot function without a method of data storage enabled either in the config or by another plugin! No such data storage method is enabled!");
			return;
		}
		data.setValue(uuid, lang);
	}
	
	@Override
	public boolean hasOfflinePlayerSetLang(UUID uuid){
		return hasSetLang(uuid);
	}
	
	@Override
	public Lang getOfflinePlayerLanguage(UUID uuid){
		return getLanguage(uuid);
	}
	
	@Override
	public void setOfflinePlayerLanguage(UUID uuid, Lang lang){
		setLanguage(uuid, lang);
	}
}
