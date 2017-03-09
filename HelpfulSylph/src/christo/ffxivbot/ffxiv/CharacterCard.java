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
		// eBuilder.setImage(character.image);
		eBuilder.setThumbnail(character.icon);
		eBuilder.setDescription("\n"+"```"+character.name+" is a "+character.gender+" "+character.race+
								" who is playing on "+character.server+" for the "+character.grandCompany+
								" and has reached the rank of "+character.gcRank+"```");
		
		eBuilder.addField("Disciples of War", getDoWar(), true); 
		eBuilder.addField("Disciples of Magic", getDoMagic(), true);
		eBuilder.addField("Disciples of the Hand", getDotHand(), true);
		eBuilder.addField("Disciples of the Land", getDotLand(), true); 
		
		membd = eBuilder.build();
		
		
	}
	
	public String getDoWar(){
		return  ClassJobEmotes.ICON_GLA+"  "+character.classjobs.get(0).name+": "+character.classjobs.get(0).level+"\n"+
				ClassJobEmotes.ICON_PGL+"  "+character.classjobs.get(1).name+": "+character.classjobs.get(1).level+"\n"+
				ClassJobEmotes.ICON_MRD+"  "+character.classjobs.get(2).name+": "+character.classjobs.get(2).level+"\n"+
				ClassJobEmotes.ICON_LNC+"  "+character.classjobs.get(3).name+": "+character.classjobs.get(3).level+"\n"+
				ClassJobEmotes.ICON_ARC+"  "+character.classjobs.get(4).name+": "+character.classjobs.get(4).level+"\n"+
				ClassJobEmotes.ICON_ROG+"  "+character.classjobs.get(5).name+": "+character.classjobs.get(5).level+"\n"+
				ClassJobEmotes.ICON_CNJ+"  "+character.classjobs.get(6).name+": "+character.classjobs.get(6).level+"\n"+
				ClassJobEmotes.ICON_THM+"  "+character.classjobs.get(7).name+": "+character.classjobs.get(7).level+"\n"+
				ClassJobEmotes.ICON_ACN+"  "+character.classjobs.get(18).name+": "+character.classjobs.get(18).level+"\n";
	}
	
	String getDoMagic(){
		return  ClassJobEmotes.ICON_AST+"  "+character.classjobs.get(20).name+": "+character.classjobs.get(20).level+"\n"+
				ClassJobEmotes.ICON_DRK+"  "+character.classjobs.get(21).name+": "+character.classjobs.get(21).level+"\n"+
				ClassJobEmotes.ICON_MCH+"  "+character.classjobs.get(22).name+": "+character.classjobs.get(22).level+"\n";
	}
	
	String getDotHand(){
		return  ClassJobEmotes.ICON_CRP+"  "+character.classjobs.get(8).name+": "+character.classjobs.get(8).level+"\n"+
				ClassJobEmotes.ICON_BSM+"  "+character.classjobs.get(9).name+": "+character.classjobs.get(9).level+"\n"+
				ClassJobEmotes.ICON_ARM+"  "+character.classjobs.get(10).name+": "+character.classjobs.get(10).level+"\n"+
				ClassJobEmotes.ICON_GSM+"  "+character.classjobs.get(11).name+": "+character.classjobs.get(11).level+"\n"+
				ClassJobEmotes.ICON_LTW+"  "+character.classjobs.get(12).name+": "+character.classjobs.get(12).level+"\n"+
				ClassJobEmotes.ICON_WVR+"  "+character.classjobs.get(13).name+": "+character.classjobs.get(13).level+"\n"+
				ClassJobEmotes.ICON_ALC+"  "+character.classjobs.get(14).name+": "+character.classjobs.get(14).level+"\n"+
				ClassJobEmotes.ICON_CUL+"  "+character.classjobs.get(15).name+": "+character.classjobs.get(15).level+"\n";
	}          
	
	String getDotLand(){
		return 
				ClassJobEmotes.ICON_MIN+"  "+character.classjobs.get(16).name+": "+character.classjobs.get(16).level+"\n"+
				ClassJobEmotes.ICON_BTN+"  "+character.classjobs.get(17).name+": "+character.classjobs.get(17).level+"\n"+
				ClassJobEmotes.ICON_FSH+"  "+character.classjobs.get(18).name+": "+character.classjobs.get(18).level+"\n";			
	}
	public MessageEmbed getCard(){
		return membd;
	}
}
