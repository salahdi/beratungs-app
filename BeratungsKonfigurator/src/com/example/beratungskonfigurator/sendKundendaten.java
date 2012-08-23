package com.example.beratungskonfigurator;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;

public class sendKundendaten {
	
	try {

		// ----------------------------------------------------------------------------------//
		// updateKundeAdresse
		// ----------------------------------------------------------------------------------//

		JSONObject params = new JSONObject();
		ServerInterface si;

		pDialogUpdate = new ProgressDialog(getActivity());
		pDialogUpdate.setMessage("Lade Daten!");
		pDialogUpdate.setIndeterminate(false);
		pDialogUpdate.setCancelable(false);
		pDialogUpdate.show();
		
		params.put("kundeId", kundeId);

		// z.B. Lass Swirl jetzt rotieren
		pDialogUpdate.show();

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					Log.i("VOR DIALOG: ", "in SERVER SUCCESS HANDLER");
					pDialogUpdate.dismiss();
					
					Log.i("msg", result.getString("msg"));

					
				}public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method stub
				}
			});
			si.call("updateKundeAdresse", params);
		}
}
