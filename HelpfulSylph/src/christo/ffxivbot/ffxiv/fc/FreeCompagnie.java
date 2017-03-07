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
	static String test = "{\"url\": \"http://na.finalfantasyxiv.com/lodestone/freecompany/9232238498621161992/\",\"json_data\": {\"members \": [{\"elem \": \".name_box a \",\"name \": \"text \",\"link \": \"href \"}]}}";
	static String requestUrl = "{\"url\":\"http://na.finalfantasyxiv.com/lodestone/freecompany/";
	static String requestData = "/, \"json_data\":\"{\"members\": [{\"elem\": \".name_box a\", \"name\": \"text\", \"link\": \"href\"}]}\"}";
	
	public static JSONObject getMembers(String fcID){

		JSONObject fcMemembers = new JSONObject();
		try {
			
			//JSONObject request = new JSONObject(/*requestUrl+fcID+requestData*/test);
			
			//HttpResponse<JsonNode> jsonNode = Unirest.post(JAMAPI).body(request).asJson();
			HttpURLConnection respCon = (HttpURLConnection) new URL(JAMAPI).openConnection();
			respCon.setDoOutput(true);
			respCon.setRequestMethod("POST");
			respCon.setRequestProperty("url", "http://na.finalfantasyxiv.com/lodestone/freecompany/9232238498621161992/");
			respCon.setRequestProperty("json_data", "{\"members\": [{\"elem\": \".name_box a\", \"name\": \"text\", \"link\": \"href\"}]}");
			respCon.connect();
			
			InputStream is = respCon.getInputStream();
			Scanner scan = new Scanner(is);
			String str = scan.nextLine();
				
			scan.close();
			is.close();
			
			fcMemembers = new JSONObject(str);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return fcMemembers;
	}
	
}
