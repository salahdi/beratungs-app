package com.example.beratungskonfigurator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class DBconnection extends Activity {
	
	TextView tv;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db_main);
        
        if (android.os.Build.VERSION.SDK_INT > 9) {
        	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        	StrictMode.setThreadPolicy(policy);
        }
        
        
        
        try {
        	JSONObject params = new JSONObject();
        	params.put( "klientId", 1 );
			
        	ServerInterface si = new ServerInterface();
        	//si.setVerbose( true );
        	si.addListener( new ServerInterfaceListener() {
				
				public void serverSuccessHandler(JSONObject result) throws JSONException {
					
					// z.B. Swirl jetzt verstecken und Grid mit Daten füllen
					
					Log.i( "vorname", result.getJSONObject("data").getString( "vorname" ) );
					Log.i( "name", result.getJSONObject("data").getString( "name" ) );
					Log.i( "fs", result.getJSONObject("data").getString( "familienstand" ) );
					Log.i( "msg", result.getString("msg") );
				}
				
				public void serverErrorHandler(Exception e) {
					
					// z.B. Fehler Dialog aufploppen lassen
					
					Log.e( "error", "called" );
				}
			});
        	si.call( "klientRequest", params );
        	
        	// z.B. Lass Swirl jetzt rotieren
        	
        	
        	////////////////////////////////////////////////////////
        	
        	si = new ServerInterface();
        	si.addListener( new ServerInterfaceListener() {
				
				public void serverSuccessHandler(JSONObject result) throws JSONException {
					// TODO Auto-generated method stub
					JSONArray fs = result.getJSONArray("data");
					Log.i( "data", fs.toString() );
					Log.i( "msg", result.getString("msg") );
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method stub
					
				}
			});
        	params = new JSONObject();
        	si.call("gibFamilienstaende", params);
        	
		} catch (JSONException e) {
		}
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    
    public JSONObject postData( String method, JSONObject params ) {
    	JSONObject result = null;
    	String jsonString = "";
    	
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://jd.mazebert.com/dbService.php");
		//HttpPost httppost = new HttpPost("http://jaydee85.ja.funpic.de/app/dbService.php");
		//HttpPost httppost = new HttpPost("http://jaydee.bplaced.net/app/dbService.php");
		//HttpPost httppost = new HttpPost("http://jaydee.site11.com/app/dbService.php");
		
		try {			
			params.put( "method", method );
			
			// Create a NameValuePair out of the JSONObject + a name
            List<NameValuePair> nVP = new ArrayList<NameValuePair>();  
            nVP.add(new BasicNameValuePair("jsonPost", params.toString()));  

            // Hand the NVP to the POST
            httppost.setEntity(new UrlEncodedFormEntity(nVP));
            Log.i("main", "TestPOST - nVP = "+nVP.toString());

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			Log.e("HTTPresponse", response.toString());

			// for JSON:
			if(response != null)
			{
				Log.e("RESPONSE", "RESPONSE not NULL");
				InputStream is = response.getEntity().getContent();
				Log.e("InputSTREAM is", is.toString());

				jsonString = convertStreamToString(is);
			}
			Log.e("ausgabe", jsonString);
			//tv.setText(text);
			Log.e("TEXT View", "ENDE");

		}catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
    		// TODO Auto-generated catch block
    	} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// try parse the string to a JSON object
        try {
        	result = new JSONObject( jsonString );
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        
        return result;
	}
	
	
	public static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    
}