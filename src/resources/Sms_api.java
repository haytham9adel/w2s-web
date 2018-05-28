package resources;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import resources.Assets;
public class Sms_api {



	
	
	public void sendMsg(String message,String mobile ) {
		  String url = "http://www.hisms.ws/api.php" ;

		  
		  try {
			  String encodedMsg =  URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8.toString()) ;

			  String query = "?send_sms&username=966544757057&password=MustafaZidan"
			  		       + "&numbers="+mobile+"&sender=M3aak&message=" +encodedMsg
				          ;
       		  
		         URL u = new URL(url +query);
		         URLConnection uc = u.openConnection();
		         uc.setDoOutput(true);
		         uc.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		         PrintWriter pw = new PrintWriter(uc.getOutputStream());
		         pw.println("");
		         pw.close();
		         BufferedReader in = new BufferedReader(
		    	 new InputStreamReader(uc.getInputStream()));
		         //get the input from the request
			        
		         String res = in.readLine();
		         System.out.println("response from sms gateway >> " +res );	 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        

	         
    }
	
}
