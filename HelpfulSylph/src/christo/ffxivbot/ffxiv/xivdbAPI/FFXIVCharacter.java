package christo.ffxivbot.ffxiv.xivdbAPI;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.annotation.XmlValue;

import org.json.JSONArray;
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
			InputStream charRep = new URL(XIVBD.CHARACTER_URL_API+id).openStream();
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
				 
				makeClassJobList(charClassJobs);
				
				icon = charData.getString("avatar");
				image = charData.getString("portrait");
				
			} catch (JSONException e){ e.printStackTrace(); }
			
			charRep.close();
			
		} catch (IOException e1) { e1.printStackTrace(); }

	}
	
	void makeClassJobList(JSONObject classJobList){
		classjobs = new ArrayList<ClassJob>();
		for(int i = 0; i < 18; i++){
			classjobs.add(i, new ClassJob(classJobList.getJSONObject(""+(i+1)).getString("name"), classJobList.getJSONObject(""+(i+1)).getInt("level")));	
		}
		classjobs.add(18, new ClassJob(classJobList.getJSONObject(""+26).getString("name"), classJobList.getJSONObject(""+26).getInt("level")));	
		classjobs.add(19, new ClassJob(classJobList.getJSONObject(""+29).getString("name"), classJobList.getJSONObject(""+29).getInt("level")));	
		for(int i = 20; i < 23; i++){
			classjobs.add(i, new ClassJob(classJobList.getJSONObject(""+(i+11)).getString("name"), classJobList.getJSONObject(""+(i+11)).getInt("level")));	
		}
	}
	
}
