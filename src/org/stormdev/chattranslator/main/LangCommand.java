package org.stormdev.chattranslator.main;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.stormdev.chattranslator.api.Lang;
import org.stormdev.translator.yandex.errors.YandexUnsupportedLanguageException;

public class LangCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias,
			String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("Players only!");
			return true;
		}
		Player player = (Player) sender;
		
		if(args.length > 0){ //They specified a lang, phew...
			String input = args[0];
			Lang lang = Lang.ENGLISH;
			try {
				lang = Lang.forSimilarShortString(input);
			} catch (YandexUnsupportedLanguageException e) {
				try {
					lang = Lang.forSimilarLangName(input);
				} catch (YandexUnsupportedLanguageException e1) {
					player.sendMessage(ChatColor.RED+"Unknown Language! Use /lang for a list of supported languages!");
					return true;
				}
			}
			
			if(lang == null){
				player.sendMessage(ChatColor.RED+"Unknown Language! Use /lang for a list of supported languages!");
				return true;
			}
			
			ChatTranslator.languageManager.setLanguage(player, lang);
			String msg = "Successfully set your language to "+lang.getLanguageName();
			String s;
			try {
				s = ChatTranslator.getTranslator().translate(Lang.ENGLISH, lang, msg);
			} catch (Exception e) {
				player.sendMessage(ChatColor.RED+"Error: "+e.getMessage());
				return true;
			}
			sender.sendMessage(ChatColor.GREEN+s);
			return true;
		}
		//Show the GUI
		ChatTranslator.selectMenu.show(player);
		return true;
	}

}
