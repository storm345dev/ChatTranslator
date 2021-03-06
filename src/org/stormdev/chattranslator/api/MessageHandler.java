package org.stormdev.chattranslator.api;

import org.bukkit.entity.Player;

public interface MessageHandler {
	public String formatMsg(Player chatted, String whatTheySaid);
	public void sendMessage(String formattedMsg, Player... recipients);
	public boolean overrideChatEvent();
}
