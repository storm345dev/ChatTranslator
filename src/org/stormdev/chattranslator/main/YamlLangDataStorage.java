package org.stormdev.chattranslator.main;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.stormdev.chattranslator.api.DataStorage;
import org.stormdev.chattranslator.api.Lang;

public class YamlLangDataStorage implements DataStorage<Lang> {
	private File conFile;
	private YamlConfiguration con;
	
	public YamlLangDataStorage(File conFile, YamlConfiguration con){
		this.conFile = conFile;
		this.con = con;
	}
	
	private String toUUIDString(UUID uuid){
		return uuid.toString().replaceAll(Pattern.quote("-"), "");
	}
	
	private void asyncSave(){
		Bukkit.getScheduler().runTaskAsynchronously(ChatTranslator.plugin, new Runnable(){

			@Override
			public void run() {
				try {
					con.save(conFile);
				} catch (IOException e) {
					// OH NO
					e.printStackTrace();
				}
				return;
			}});
	}
	
	@Override
	public Lang getValue(UUID uuid) {
		try {
			String langRaw = con.getString(toUUIDString(uuid));
			if(langRaw == null){
				return ChatTranslator.DEFAULT_LANGUAGE;
			}
			return Lang.forShortString(langRaw);
		} catch (Exception e) {
			return ChatTranslator.DEFAULT_LANGUAGE;
		}
	}

	@Override
	public void setValue(UUID uuid, Lang value) {
		con.set(toUUIDString(uuid), value.getShortLangName());
		asyncSave();
	}

	@Override
	public boolean hasValueSet(UUID uuid) {
		return con.contains(toUUIDString(uuid));
	}

}
