package christo.ffxivbot.ffxiv.xivdbAPI;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

public class XIVBD {
	
	public static String SEARCH_URL = "http://api.xivdb.com/search?one=characters&string=";
	public static String CHARACTER_URL = "http://api.xivdb.com/character/";
	
	public static int getCharID(String name, String server){
		
		int id = 0;
		
	    InputStream searchRep;
		try {
			searchRep = new URL(XIVBD.SEARCH_URL+name).openStream();
		
			try(Scanner scan = new Scanner(searchRep)){
		   		String searchBody = scan.nextLine();
		   	
		   		JSONArray searchRes = new JSONObject(searchBody).getJSONObject("characters").getJSONArray("results");
		   	
		   		for(int i = 0; i < searchRes.length(); i++){
		   			if(searchRes.getJSONObject(i).getString("server").equalsIgnoreCase(server)) {
				   		id = searchRes.getJSONObject(i).getInt("id");
				   		break;
		   			}
		   		}
		   		
		   		searchRep.close();
		   	}
		} catch (MalformedURLException e) {	e.printStackTrace();
		} catch (IOException e) { e.printStackTrace(); }
	        
		return id;
		
	}
	
}
