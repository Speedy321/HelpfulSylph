package christo.ffxivbot.dataBase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import christo.ffxivbot.ffxiv.xivdbAPI.FFXIVCharacter;

public class CharacterDB {
	
	long index = 0;

	public static JSONObject makeDB(JSONObject db){
		
		File dbFile = new File("characterDB.json");
		if(dbFile.exists()){
			try {
				BufferedReader bufR = new BufferedReader(new FileReader(dbFile));
				
				String dbRead = bufR.readLine();
				
				bufR.close();
				
				return new JSONObject(dbRead);
				
			} catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			List<JSONObject> jCharList = new ArrayList<>();
			db.put("list", new JSONArray(jCharList));
			return db;
		}
		return db;				
	}
	
	public static void addToDB(List<FFXIVCharacter> charList, JSONObject db){
		for(int i = 0; i < charList.size(); i++){
			addToDB(charList.get(i), db);
		}
	}
	
	public static void addToDB(FFXIVCharacter charData, JSONObject db){
		for(int i = 0; i < db.getJSONArray("list").length(); i++){
			if(db.getJSONArray("list").getJSONObject(i).getString("discordID") == charData.discordID){
				db.getJSONArray("list").put(i, asJSON(charData));
				return;
			}
		}
		db.getJSONArray("list").put(asJSON(charData));
	}
	
	static JSONObject asJSON(FFXIVCharacter charData){
		JSONObject obj = new JSONObject();

		obj.put("name", charData.name);
		obj.put("id", charData.id);
		obj.put("discordID", charData.discordID);
		
		return obj;
	}	
}
