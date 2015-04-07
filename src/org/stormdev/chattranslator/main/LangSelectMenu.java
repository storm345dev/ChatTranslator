package org.stormdev.chattranslator.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.stormdev.chattranslator.api.Lang;
import org.stormdev.openapi.gui.PagedMenu;
import org.stormdev.openapi.gui.PagedMenu.MenuDetails;
import org.stormdev.translator.yandex.errors.YandexUnsupportedLanguageException;

public class LangSelectMenu implements MenuDetails {
	protected PagedMenu menu;
	protected List<MenuItem> items = new ArrayList<MenuItem>();
	
	public LangSelectMenu(){
		//Calculate and populate menus
		List<String> orderedLangNames = new ArrayList<String>();
		for(Lang l:Lang.values()){
			orderedLangNames.add(l.getLanguageName());
		}
		Collections.sort(orderedLangNames);
		for(String sn:orderedLangNames){
			Lang l;
			try {
				l = Lang.forSimilarLangName(sn);
			} catch (YandexUnsupportedLanguageException e) {
				//Should never happen
				e.printStackTrace();
				continue;
			}
			items.add(getMenuItem(l));
		}
		
		this.menu = new PagedMenu(this);
	}
	
	public void show(Player player){
		player.closeInventory();
		this.menu.open(player);
	}

	@Override
	public List<MenuItem> getDisplayItems(Player player) {
		return items;
	}
	
	private MenuItem getMenuItem(final Lang lang){
		MenuItem mi = new MenuItem(){

			@Override
			public ItemStack getDisplayItem() {
				return new ItemStack(Material.WOOL);
			}

			@Override
			public String getColouredTitle() {
				return ChatColor.BLUE+lang.getLanguageName();
			}

			@Override
			public String[] getColouredLore() {
				return new String[]{ChatColor.WHITE+"Click to set as your language!"};
			}

			@Override
			public void onClick(Player player) {
				ChatTranslator.languageManager.setLanguage(player, lang);
				String msg = "Successfully set your language to "+lang.getLanguageName();
				String s;
				try {
					s = ChatTranslator.getTranslator().translate(Lang.ENGLISH, lang, msg);
				} catch (Exception e) {
					player.sendMessage(ChatColor.RED+"Error: "+e.getMessage());
					return;
				}
				player.sendMessage(ChatColor.GREEN+s);
				return;
			}};
		return mi;
	}

	@Override
	public String getColouredMenuTitle(Player player) {
		return ChatColor.BLUE+"Select a language:";
	}

	@Override
	public int getPageSize() {
		return 54;
	}

	@Override
	public String noDisplayItemMessage() {
		return "Nothing to see here...";
	}
}
