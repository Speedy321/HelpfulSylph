package christo.ffxivbot.ffxiv;

import java.awt.Color;

import christo.ffxivbot.ffxiv.xivdbAPI.FFXIVCharacter;
import christo.ffxivbot.ffxiv.xivdbAPI.XIVBD;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

public class CharacterCard {
	
	FFXIVCharacter character;
	MessageEmbed membd;
	
	public CharacterCard(FFXIVCharacter chara) {
		character = chara;
		
		EmbedBuilder eBuilder = new EmbedBuilder();
		
		eBuilder.setColor(new Color(character.id));
		eBuilder.setAuthor(character.name, XIVBD.CHARACTER_URL+character.id, character.icon);
		eBuilder.setImage(character.image);
		eBuilder.setThumbnail(character.icon);
		eBuilder.setDescription("\n"+"```"+character.name+" is a "+character.gender+" "+character.race+
								" who is playing on "+character.server+" for the "+character.grandCompany+
								" and has reached the rank of "+character.gcRank+"```");
		
		membd = eBuilder.build();
	}
	
	public MessageEmbed getCard(){
		return membd;
	}
}
