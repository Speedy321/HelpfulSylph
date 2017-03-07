package christo.ffxivbot.ffxiv.fc;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class FreeCompagnie {

	public static String JAMAPI = "https://www.jamapi.xyz/";
	
	static String requestUrl = "http://na.finalfantasyxiv.com/lodestone/freecompany/";
	static String requestUrl_2 = "/member/";
	static String requestData = "{ \"members\":[{\"elem\":\".name_box a\", \"name\":\"text\", \"link\":\"href\"}] }";
	static String requestPageCount = "{\"data\":[{ \"elem\": \".total\", \"count\": \"text\" }] }";
	
	public static JSONObject getMembers(String fcID){

		JSONObject fcMemembers = new JSONObject();
		try {
			HttpResponse<JsonNode> requestpages = Unirest.post(JAMAPI)
					.field("url", requestUrl+fcID+requestUrl_2)
					.field("json_data", requestPageCount)
					.asJson();
			
			JsonNode jnode = requestpages.getBody();
			int pages = (int)Math.ceil((double)(Integer.parseInt(jnode.getObject().getJSONArray("data").getJSONObject(0).getString("count")))/50); 
			
			for(int i = 1; i < pages+1; i++){
				HttpResponse<JsonNode> membersFragment = Unirest.post(JAMAPI)
						.field("url", requestUrl+fcID+requestUrl_2+"?page="+i)
						.field("json_data", requestData)
						.asJson();
				
				
				
			}
			
		} catch (UnirestException e) { e.printStackTrace(); }
		
		
		
		return fcMemembers;
	}
	
}
