package org.stormdev.chattranslator.main;

import org.bukkit.entity.Player;
import org.stormdev.chattranslator.api.MessageHandler;

public class DefaultMessageHandler implements MessageHandler {

	@Override
	public String formatMsg(Player chatted, String msg) {
		return "<"+chatted.getDisplayName()+"> "+msg;
	}

	@Override
	public void sendMessage(String formattedMsg, Player... recipients) {
		for(Player pl:recipients){
			pl.sendMessage(formattedMsg);
		}
	}

	@Override
	public boolean overrideChatEvent() {
		return ChatTranslator.TRANSLATE_CHAT;
	}

}
