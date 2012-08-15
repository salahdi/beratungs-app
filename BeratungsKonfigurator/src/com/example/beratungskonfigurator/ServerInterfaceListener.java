package com.example.beratungskonfigurator;

import org.json.JSONException;
import org.json.JSONObject;

public interface ServerInterfaceListener 
{
	public void serverSuccessHandler( JSONObject result ) throws JSONException;
	
	public void serverErrorHandler( Exception e );
}
