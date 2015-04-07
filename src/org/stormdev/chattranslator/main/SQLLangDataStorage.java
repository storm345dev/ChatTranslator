package org.stormdev.chattranslator.main;

import java.sql.SQLException;
import java.util.UUID;
import java.util.regex.Pattern;

import org.stormdev.chattranslator.api.DataStorage;
import org.stormdev.chattranslator.api.Lang;

public class SQLLangDataStorage implements DataStorage<Lang> {
	public static String TABLE = "Languages";
	public static String ID = "id";
	public static String LANG = "language";
	
	public SQLLangDataStorage(){
		ChatTranslator.SQL.createTable(TABLE, new String[]{ID, LANG}, new String[]{"varchar(255) NOT NULL PRIMARY KEY", "varchar(32)"});
	}
	
	private String toUUIDString(UUID uuid){
		return uuid.toString().replaceAll(Pattern.quote("-"), "");
	}

	@Override
	public Lang getValue(UUID uuid) {
		try {
			return Lang.forShortString(ChatTranslator.SQL.searchTable(TABLE, ID, toUUIDString(uuid), LANG).toString().trim());
		} catch (Exception e) {
			return ChatTranslator.DEFAULT_LANGUAGE;
		}
	}

	@Override
	public void setValue(UUID uuid, Lang value) {
		try {
			ChatTranslator.SQL.setInTable(TABLE, ID, toUUIDString(uuid), LANG, value.getShortLangName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean hasValueSet(UUID uuid) {
		Object o;
		try {
			o = ChatTranslator.SQL.searchTable(TABLE, ID, toUUIDString(uuid), LANG);
		} catch (SQLException e) {
			return false;
		}
		boolean b =  o != null && o.toString().length() > 0 && !o.toString().equals("null");
		if(!b){
			return false;
		}
		
		try {
			Lang.forShortString(o.toString());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
