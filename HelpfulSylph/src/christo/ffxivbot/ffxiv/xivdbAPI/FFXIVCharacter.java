package christo.ffxivbot.ffxiv.xivdbAPI;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.annotation.XmlValue;

import org.json.JSONException;
import org.json.JSONObject;

public class FFXIVCharacter {

	public int id; 
	public String discordID;
	
	public String name, server, race, clan, gender, nameday, title, guardian, city, grandCompany, gcRank;
	
	public String icon, image;
	
	public List<ClassJob> classjobs;
	
	public String fcRank;
 	
	public FFXIVCharacter(int id, String discordID) {
		this.id = id;
		this.discordID = discordID;
		try {
			InputStream charRep = new URL(XIVBD.CHARACTER_URL+id).openStream();
			try(Scanner scan2 = new Scanner(charRep)){
				String charBody = scan2.nextLine();
			
				JSONObject charObj = new JSONObject(charBody);
				
				JSONObject charData = charObj.getJSONObject("data");
				JSONObject charClassJobs = charData.getJSONObject("classjobs");
			
				name = charData.getString("name");
				server = charData.getString("server");
				race = charData.getString("race");
				clan = charData.getString("clan");
				gender = charData.getString("gender");
				nameday = charData.getString("nameday");
				if(!charData.get("title").equals(null)) 
					title = charData.getString("title");
				else title = " ";
				guardian = charData.getJSONObject("guardian").getString("name");
				city = charData.getJSONObject("city").getString("name");
				if(!charData.get("grand_company").equals(null)){
					grandCompany = charData.getJSONObject("grand_company").getString("name");
					gcRank = charData.getJSONObject("grand_company").getString("rank");
				} else {
					grandCompany = "N/A";
					gcRank = "N/A";
				}
				
				icon = charData.getString("avatar");
				image = charData.getString("portrait");
				
			} catch (JSONException e){ e.printStackTrace(); }
			
			charRep.close();
			
		} catch (IOException e1) { e1.printStackTrace(); }

	}
	
}
