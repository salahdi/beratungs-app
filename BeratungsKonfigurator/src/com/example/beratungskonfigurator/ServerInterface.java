package com.example.beratungskonfigurator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class ServerInterface 
{
	public ServerInterface() 
	{
		m_action = new CallAction();
	}
	
	public void call( String method, JSONObject params ) 
	{
		if( m_action.wasCalled() == false )
		{
			m_action.prepareForCall( method, params );
			m_action.execute( 0 );
		}
		else
		{
			throw new RuntimeException( "A ServerInterface must only be used once! Create a new instance!" );
		}
	}
	
	public void setVerbose( boolean verbose )
	{
		m_action.setVerbose( verbose );
	}
	
	public void addListener( ServerInterfaceListener l )
	{
		m_action.addListener( l );
	}
	
	public void removeListener( ServerInterfaceListener l )
	{
		m_action.removeListener( l );
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	class CallAction extends AsyncTask<Integer, Integer, JSONObject>
	{
		public CallAction() 
		{
			m_called = false;
			m_listeners = new LinkedList<ServerInterfaceListener>();
			m_exception = null;
		}
		
		public boolean wasCalled()
		{
			return m_called;
		}
		
		public void setVerbose( boolean verbose )
		{
			m_verbose = verbose;
		}
		
		public void prepareForCall( String method, JSONObject params )
		{
			m_called = true;
			m_params = params;
			try 
			{
				m_params.put( "method", method );
				m_params.put( "millis", System.currentTimeMillis() );
			} 
			catch (JSONException e) 
			{
			}
		}
		
		public void addListener( ServerInterfaceListener l )
		{
			m_listeners.addLast( l );
		}
		
		public void removeListener( ServerInterfaceListener l )
		{
			m_listeners.remove( l );
		}

		@Override
		protected JSONObject doInBackground( Integer... params )
		{
			JSONObject result = null;
			
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost( "http://jd.mazebert.com/dbService.php" );
			
			try 
			{
				// Create a NameValuePair out of the JSONObject + a name
	            List<NameValuePair> nVP = new ArrayList<NameValuePair>();  
	            nVP.add(new BasicNameValuePair("jsonPost", m_params.toString()));  

	            // Hand the NVP to the POST
	            httppost.setEntity(new UrlEncodedFormEntity(nVP));

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);

				// for JSON:
				if( response == null )
				{
					// dispatch error in main thread!
					dispatchError( new NullPointerException( "response must not be null" ) );
				}
				
				// decode response
				InputStream is = response.getEntity().getContent();
				m_serverResponse = convertStreamToString(is);
				
				if( m_verbose )
				{
					Log.i( "Server response is", m_serverResponse );
				}
				
				result = new JSONObject( m_serverResponse );
			}
			catch( ClientProtocolException e ) 
			{
				// dispatch error in main thread!
				dispatchError( e );
			} 
			catch( IOException e ) 
			{
				// dispatch error in main thread!
				dispatchError( e );
	    	} 
			catch (JSONException e ) 
			{
	    		// dispatch error in main thread!
				dispatchError( e );
			}
			
			return result;
		}
				
		protected void dispatchError( Exception e )
		{
			Log.e( "Error receiving JSON data from server", "" );
			Log.e( "-Response from server is", m_serverResponse );
			Log.e( "-Error from client", e.getMessage() );
			
			// save error for later use
			m_exception = e;
		}
		
		@Override
		protected void onPostExecute( JSONObject result ) 
		{
			if( result != null )
			{
				// all is good
				for( ListIterator<ServerInterfaceListener> it = m_listeners.listIterator(); it.hasNext(); )
				{
					try
					{
						it.next().serverSuccessHandler( result );
					}
					catch( JSONException e )
					{
						Log.e( "JSON exception", e.getMessage() );
					}
				}
			}
			else
			{
				// error occurred
				for( ListIterator<ServerInterfaceListener> it = m_listeners.listIterator(); it.hasNext(); )
				{
					it.next().serverErrorHandler( m_exception );
				}
			}
		}
		
		private JSONObject m_params;
		private LinkedList<ServerInterfaceListener> m_listeners;
		private boolean m_called;
		private Exception m_exception;
		private String m_serverResponse;
		private boolean m_verbose = false;
	}
	
	private String convertStreamToString(InputStream is) 
	{
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
	
	private CallAction m_action;
}
