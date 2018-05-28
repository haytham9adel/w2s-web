package resources;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import antlr.StringUtils;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;

public class Gcm {                             
	public final static String AUTH_KEY_FCM = "AIzaSyC_LPZfBerKelp1GWRAykUVw2jgAd1-hsE";
	public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

	/**
	 * gcmRegId is the id which android app will give to server (one time)
	 **/
	
	public void pushNotificationToGCM(String userDeviceIdKey, String message) throws Exception{
			try{
		   String authKey = AUTH_KEY_FCM; // You FCM AUTH key
		   String FMCurl = API_URL_FCM; 

		   URL url = new URL(FMCurl);
		   HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		   conn.setUseCaches(false);
		   conn.setDoInput(true);
		   conn.setDoOutput(true);

		   conn.setRequestMethod("POST");
		   conn.setRequestProperty("Authorization","key="+authKey);
		   conn.setRequestProperty("Content-Type","application/json");

		   JSONObject json = new JSONObject();
		   json.put("to",userDeviceIdKey.trim());
		   JSONObject info = new JSONObject();
		   info.put("title", "Notificatoin Title"); // Notification title
		   info.put("body", ""); // Notification body
		   json.put("data", info);
		   info.put("msg", message);
		//   JSONObject data = new JSONObject();
		   
		    
		    
		   OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		   wr.write(json.toString());
		   wr.flush();
		   conn.getInputStream();
		   System.out.println(json);
			}catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
		}
	
}
