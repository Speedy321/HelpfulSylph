package christo.ffxivbot.ffxiv.fc;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import christo.ffxivbot.ffxiv.xivdbAPI.FFXIVCharacter;

public class FreeCompagnie {

	//TODO: convert href into id
	
	public static String JAMAPI = "https://www.jamapi.xyz/";
	
	static String requestUrl = "http://na.finalfantasyxiv.com/lodestone/freecompany/";
	static String requestUrl_2 = "/member/";
	static String requestData = "{ \"members\":[{\"elem\":\".name_box a\", \"name\":\"text\", \"link\":\"href\"}] }";
	static String requestPageCount = "{\"data\":[{ \"elem\": \".total\", \"count\": \"text\" }] }";
	
	public static int toID(JSONObject jo){
		String hrefParts[] = jo.getString("link").split("/");
		return Integer.parseInt(hrefParts[3]);//TODO check for right part
	}
	
	public static JSONObject getMembers(String fcID){

		JSONObject fcMemembers = new JSONObject();
		fcMemembers.put("list", new JSONArray());
		try {
			HttpResponse<JsonNode> requestpages = Unirest.post(JAMAPI)
					.field("url", requestUrl+fcID+requestUrl_2)
					.field("json_data", requestPageCount)
					.asJson();
			
			JsonNode jnode = requestpages.getBody();
			int pages = (int)Math.ceil((double)(Integer.parseInt(jnode.getObject().getJSONArray("data").getJSONObject(0).getString("count")))/50); 
			
			for(int i = 1; i < pages+1; i++){
				
				System.out.println("Getting fc members page "+i+" ...");
				
				HttpResponse<JsonNode> membersFragment = Unirest.post(JAMAPI)
						.field("url", requestUrl+fcID+requestUrl_2+"?page="+i)
						.field("json_data", requestData)
						.asJson();
				
				JsonNode memFragJNode = membersFragment.getBody();
				
				for(int j = 0; j < memFragJNode.getObject().getJSONArray("members").length();j++){
					memFragJNode.getObject().getJSONArray("members").getJSONObject(j).put("id", toID(memFragJNode.getObject().getJSONArray("members").getJSONObject(j)));
					fcMemembers.getJSONArray("list").put((j+(50*(i-1))),memFragJNode.getObject().getJSONArray("members").get(j));
				}
				
			}
			
		} catch (UnirestException e) { e.printStackTrace(); }
		
		return fcMemembers;
	}

	public static List<FFXIVCharacter> toCharList(JSONObject fcMembers) {
		
		List<FFXIVCharacter> charList = new ArrayList<>();
		
		for(int i = 0; i < fcMembers.getJSONArray("list").length(); i++){
			charList.add(new FFXIVCharacter(fcMembers.getJSONArray("list").getJSONObject(i).getInt("id"), ""));
			if(i%20==0) System.out.println("Adding char #"+i+" to the list...");
		}
		
		return charList;
	}
	
}
