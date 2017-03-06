package christo.ffxivbot.ffxiv.fc;

import org.json.JSONObject;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class FreeCompagnie {

	public static String JAMAPI = "https://www.jamapi.xyz/";
	String request = "{\"members\": [{\"elem\": \".name_box a\", \"name\": \"text\", \"link\": \"href\"}]}";
	
	public JSONObject getMembers(String fcID){
		JSONObject fcMemembers = new JSONObject();
		Unirest.post(JAMAPI).body(request);
		
		
		return fcMemembers;
	}
	
}
