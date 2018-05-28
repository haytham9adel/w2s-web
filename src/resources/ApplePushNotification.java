package resources;

import java.io.InputStream;
import java.util.Date;

import javax.net.ssl.SSLContext;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsNotification;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.EnhancedApnsNotification;
import com.notnoop.apns.PayloadBuilder;
 

public class ApplePushNotification {
	
	public void pushMessageNew(String token,String msg) {
		ApnsService service = null;
        try {
           
        	service = APNS
        			.newService()
        			.withCert("D:\\companies\\we4it\\maak\\FinalProject\\Tracking_bus\\src\\resources\\sandboxParentapp.p12", "Rudiment123")
        			.withSandboxDestination().build();
            service.start();
        	
        	  String payload = APNS.newPayload().alertBody(msg).build();


        	 int now =  (int)(new Date().getTime()/1000);

        	 EnhancedApnsNotification notification = new EnhancedApnsNotification
        			 (EnhancedApnsNotification.INCREMENT_ID() /* Next ID */,
        	     now + 60 * 60 /* Expire in one hour */,
        	     token /* Device Token */,
        	     payload);

        	 service.push(notification);
        	 
        	 System.out.println("> done <") ;
        	
        } catch (Exception ex) {
            // more logging
        	ex.printStackTrace();
        } finally {
            // check if the service was successfull initialized and stop it here, if it was
            if (service != null) {
            	 
                service.stop();
            }
 
        }
    }
	
// Method for send pushNotification for Apple IOS
	
	public void pushMessage(String device_token,String msg,String msg_show,int sound_sett) {
        ApnsService service = null;
        try {
        	 // get the certificate
            InputStream certStream = this.getClass().getClassLoader().getResourceAsStream("resources/ParentApp_dev.p12");
        	//service = APNS.newService().withCert(certStream, "Rudiment123").withSandboxDestination().build();
        	service = APNS.newService().withCert("D:\\companies\\we4it\\maak\\FinalProject\\Tracking_bus\\src\\resources\\sandboxParentapp.p12", "Rudiment123").withSandboxDestination().build();
            service.start();
            
                try {
                	PayloadBuilder payloadBuilder = APNS.newPayload();
                	//System.out.println("Sound"+sound_sett);
                	if(sound_sett==0) {
                		System.out.println("no");
                		payloadBuilder = payloadBuilder.badge(1).alertBody(msg_show).customField("msg",msg).sound("default");	
                	}else {
                		System.out.println("yes");
                		payloadBuilder = payloadBuilder.badge(1).alertBody(msg_show).customField("msg",msg);
                	}
                  //  payloadBuilder = payloadBuilder.badge(1).alertBody(msg_show).customField("msg",msg).sound("default");
                    if (payloadBuilder.isTooLong()) {
                        payloadBuilder = payloadBuilder.shrinkBody();
                    }
                    System.out.println(payloadBuilder);
                    String payload = payloadBuilder.build();
                    String token=device_token;
                   // String token = "d3d41770d7a145ab4250be9381e6d33ddbe32f2b467705331cb6feef86d5a003";
                   ApnsNotification n = service.push(token, payload);
                   
                } catch (Exception ex) {
                	ex.printStackTrace() ;
                }
            //}
        } catch (Exception ex) {
            // more logging
        	ex.printStackTrace() ;
        } finally {
            // check if the service was successfull initialized and stop it here, if it was
            if (service != null) {
            	 
                service.stop();
            }
 
        }
    }
 	 public static void main(String args[])
	 {
 		// System.out.println("ABC");
 		// String a="d3d41770d7a145ab4250be9381e6d33ddbe32f2b467705331cb6feef86d5a003";
 		// System.out.println(a.length());
	    ApplePushNotification ap=new ApplePushNotification();
	     ap.pushMessage("432925849ce6395b59a8b33294be323c6d3d921da9f52ab09d9e0974050639b9","Ravi set "," lol ",  1 );
		 ap.pushMessageNew("432925849ce6395b59a8b33294be32 3c6d3d921da9f52ab09d9e09740506 39b9","new Ravi apns ");
	 }
}
