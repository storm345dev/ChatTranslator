package org.stormdev.chattranslator.api;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

public interface LanguageManager {
	public List<Player> getOnlinePlayersByLanguage(Lang lang);
	public List<Lang> getOnlinePlayerLanguages();
	public List<Lang> getOnlinePlayerLanguagesExcDefault();
	public List<Player> getPlayersByLanguage(List<Player> players, Lang lang);
	public List<Lang> getPlayerLanguages(List<Player> players);
	public List<Lang> getPlayerLanguagesExcDefault(List<Player> players);
	public List<Lang> getPlayerLanguagesExc(List<Player> players, Lang... exc);
	public boolean hasSetLang(Player player);
	public boolean hasOfflinePlayerSetLang(UUID uuid);
	public Lang getLanguage(Player player);
	public Lang getOfflinePlayerLanguage(UUID uuid);
	public void setLanguage(Player player, Lang lang);
	public void setOfflinePlayerLanguage(UUID uuid, Lang lang);
}
