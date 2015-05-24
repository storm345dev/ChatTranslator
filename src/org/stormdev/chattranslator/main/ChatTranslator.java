package org.stormdev.chattranslator.main;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.stormdev.chattranslator.api.DataStorage;
import org.stormdev.chattranslator.api.Lang;
import org.stormdev.chattranslator.api.TranslatorToolkit;
import org.stormdev.openapi.storm.SQL.MySQL;
import org.stormdev.openapi.storm.SQL.SQLManager;
import org.stormdev.translator.yandex.errors.YandexUnsupportedLanguageException;
import org.stormdev.translator.yandex.translation.YandexConnection;

public class ChatTranslator extends JavaPlugin {
	
	public static FileConfiguration config;
	
	public static String API_KEY = "";
	public static boolean TRANSLATE_CHAT = false;
	public static boolean ENABLE_TRANSLATION = true;
	public static String DEFAULT_LANG = "en";
	public static Lang DEFAULT_LANGUAGE = Lang.ENGLISH;
	public static TranslatorToolkit TOOLKIT = null;
	public static MySQL mySQL = null;
	public static SQLManager SQL = null;
	public static ChatTranslator plugin;
	
	public static LanguageManager languageManager = null;
	public static LangSelectMenu selectMenu = null;
	private static YandexConnection translateCon = null;
	
	@Override
	public void onEnable(){
		plugin = this;
		config = getConfig();
		if(!config.contains("api_key")){
			config.set("api_key", "Get an API key FREE from https://tech.yandex.com/keys/get/?service=trnsl");
		}
		if(!config.contains("translateChat")){
			config.set("translateChat", true);
		}
		if(!config.contains("enableTranslator")){
			config.set("enableTranslator", true);
		}
		if(!config.contains("defaultLang")){
			config.set("defaultLang", "en");
		}
		if(!config.contains("YAML.enable")){
			config.set("YAML.enable", true);
		}
		if(!config.contains("SQL.enable")){
			config.set("SQL.enable", false);
		}
		if(!config.contains("SQL.jdbcurl")){
			config.set("SQL.jdbcurl", "jdbc:mysql://localhost/siteName");
		}
		if(!config.contains("SQL.user")){
			config.set("SQL.user", "username");
		}
		if(!config.contains("SQL.pass")){
			config.set("SQL.pass", "password");
		}
		saveConfig();
		
		API_KEY = config.getString("api_key");
		TRANSLATE_CHAT = config.getBoolean("translateChat");
		DEFAULT_LANG = config.getString("defaultLang");
		ENABLE_TRANSLATION = config.getBoolean("enableTranslator");
		
		try {
			DEFAULT_LANGUAGE = Lang.forShortString(DEFAULT_LANG);
		} catch (YandexUnsupportedLanguageException e1) {
			getLogger().log(Level.SEVERE, "Unknown default language! Using English instead!");
			DEFAULT_LANGUAGE = Lang.ENGLISH;
		}
		
		if(API_KEY.equalsIgnoreCase("Get an API key FREE from http://api.yandex.com/key/form.xml?service=trnsl")
				|| API_KEY.equalsIgnoreCase("Get an API key FREE from https://tech.yandex.com/keys/get/?service=trnsl")){
			getLogger().log(Level.SEVERE, "IMPORTANT: No API KEY is set! Get an API key FREE from https://tech.yandex.com/keys/get/?service=trnsl and enter it in the CONFIG!");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		else {
			if(!ENABLE_TRANSLATION){
				getLogger().warning("Translator is disabled in the config! ChatTranslator won't be able to translate any text!");
			}
			translateCon = new YandexConnection(API_KEY);
		}
		
		DataStorage<Lang> ds = null;
		if(config.getBoolean("SQL.enable")){
			mySQL = new MySQL(this, config.getString("SQL.jdbcurl"), config.getString("SQL.user"), config.getString("SQL.pass"));
			SQL = new SQLManager(mySQL, this);
			if(mySQL.isConnected()){
				ds = new SQLLangDataStorage();
			}
			else {
				getLogger().warning("SQL connection failed! This will likely cause issues!");
			}
		}
		else if(config.getBoolean("YAML.enable")){
			File yamlFile = new File(getDataFolder()+File.separator+"languages.yml");
			yamlFile.getParentFile().mkdirs();
			if(!yamlFile.exists()){
				try {
					yamlFile.createNewFile();
				} catch (IOException e) {
					//Oh crap
					e.printStackTrace();
				}
			}
			
			YamlConfiguration con = YamlConfiguration.loadConfiguration(yamlFile);
			ds = new YamlLangDataStorage(yamlFile, con);
		}
		
		if(ds == null){
			getLogger().log(Level.WARNING, "NO data storage interface could be found! The plugin will NOT function properly unless another plugin is providing it!");
		}
		languageManager = new LanguageManager(ds);
		selectMenu = new LangSelectMenu();
		new ChatListener();
		getCommand("language").setExecutor(new LangCommand());
		
		TOOLKIT = new TranslatorToolkit();
		
		getLogger().info("Chat Translator enabled!");
	}
	
	public static YandexConnection getTranslator(){
		if(translateCon == null){
			plugin.getLogger().log(Level.SEVERE, "IMPORTANT: No API KEY is set! Get an API key FREE from https://tech.yandex.com/keys/get/?service=trnsl and enter it in the CONFIG!");
			throw new RuntimeException("No API KEY!");
		}
		return translateCon;
	}
	
	@Override
	public void onDisable(){
		getLogger().info("Chat Translator disabled!");
	}
}
