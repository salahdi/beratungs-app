package com.example.beratungskonfigurator.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beratungskonfigurator.R;
import com.example.beratungskonfigurator.server.ServerInterface;
import com.example.beratungskonfigurator.server.ServerInterfaceListener;

public class RaumauswahlDialog extends Dialog {

	private ProgressDialog pDialog;
	private ListView raumauswahlList;

	private int mKundeId;

	ArrayList<Integer> selSzenarioList = new ArrayList<Integer>();

	// Constructor
	public RaumauswahlDialog(final Context context, int kundeId) {
		super(context);

		pDialog = new ProgressDialog(context);
		pDialog.setMessage("Lade Daten!");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);

		mKundeId = kundeId;

		// get this window's layout parameters so we can change the position
		WindowManager.LayoutParams paramsLayout = getWindow().getAttributes();

		// change the position. 0,0 is center
		paramsLayout.x = 0;
		paramsLayout.y = 0;
		paramsLayout.height = WindowManager.LayoutParams.FILL_PARENT;
		paramsLayout.width = WindowManager.LayoutParams.FILL_PARENT;
		this.getWindow().setAttributes(paramsLayout);

		setCancelable(false);

		// no title on this dialog
		// requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.raumauswahl_dialog_layout);

		Button bClose = (Button) findViewById(R.id.closeButton);
		bClose.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// ----------------------------------------------------------------------------------//
				// insertRaumauswahl
				// ----------------------------------------------------------------------------------//

				try {
					String selected = "";
					int count = 0;
					int cntChoice = raumauswahlList.getCount();
					SparseBooleanArray sparseBooleanArray = raumauswahlList.getCheckedItemPositions();

					for (int i = 0; i < cntChoice; i++) {
						if (sparseBooleanArray.get(i)) {
							selected += (raumauswahlList.getItemIdAtPosition(i) + 1) + ".";
							count++;
						}
					}
					if (count != 0) {
						selected = selected.substring(0, selected.length() - 1);
						Log.i("Button OK: ", "Selected nach entfernen: " + selected + "  Count != 0: " + count);
					}

					JSONObject updateParams = new JSONObject();
					updateParams.put("raumauswahl", selected);
					updateParams.put("kundeId", mKundeId);
					updateParams.put("countAnz", count);

					ServerInterface si = new ServerInterface();
					si.addListener(new ServerInterfaceListener() {
						public void serverSuccessHandler(JSONObject result) throws JSONException {
							Log.i("INSERT Raumauswahl: ", result.getString("msg"));
						}

						public void serverErrorHandler(Exception e) {
							// TODO Auto-generated method
							// stub
						}
					});
					si.call("insertRaumauswahl", updateParams);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				dismiss();
			}
		});

		try {

			// ----------------------------------------------------------------------------------//
			// gibRaumauswahl
			// ----------------------------------------------------------------------------------//

			pDialog.show();

			JSONObject params = new JSONObject();
			Log.i("KundeID", "KundeID: " + mKundeId);
			params.put("kundeId", mKundeId);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					pDialog.dismiss();

					List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
					JSONArray la = result.getJSONArray("data");

					for (int i = 0; i < la.length(); i++) {
						HashMap<String, String> hm = new HashMap<String, String>();
						hm.put("txt", la.getString(i));
						list.add(hm);
					}
					Log.i("data", la.toString());
					Log.i("msg", result.getString("msg"));

					String[] from = { "txt" };
					int[] to = { R.id.txt };

					SimpleAdapter dataAdapter = new SimpleAdapter(context, list, R.layout.listview_checkable, from, to);
					raumauswahlList = (ListView) findViewById(R.id.raumauswahlList);
					raumauswahlList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					raumauswahlList.setAdapter(dataAdapter);
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibRaumauswahl", params);

			// ----------------------------------------------------------------------------------//
			// gibKundeRaumauswahl
			// ----------------------------------------------------------------------------------//

			params = new JSONObject();
			pDialog.show();

			params.put("kundeId", kundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					pDialog.dismiss();

					JSONArray wd = result.getJSONArray("data");

					for (int i = 0; i < wd.length(); i++) {
						raumauswahlList.setItemChecked((wd.getInt(i)) - 1, true);
					}
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibKundeRaumauswahl", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	static final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

}
