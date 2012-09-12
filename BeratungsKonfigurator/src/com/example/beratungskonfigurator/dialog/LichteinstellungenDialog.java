package com.example.beratungskonfigurator.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TabHost;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beratungskonfigurator.ListViewButtonAdapter;
import com.example.beratungskonfigurator.NumberPic;
import com.example.beratungskonfigurator.R;
import com.example.beratungskonfigurator.server.ServerInterface;
import com.example.beratungskonfigurator.server.ServerInterfaceListener;

public class LichteinstellungenDialog extends Dialog {

	private ProgressDialog pDialog;
	private ListView raeumeList;
	private ListView lichtartList;
	private ListView lichtfarbeList;
	private ListView lichtstaerkeList;

	private int raumId;

	private JSONArray raumIdArray = new JSONArray();

	ListViewButtonAdapter listAdapter;
	private int mKundeId;
	private int mSzenarioId;
	private Context mContext;

	SimpleAdapter dataAdapterRaeume;

	ArrayList<Integer> selSzenarioList = new ArrayList<Integer>();

	// Constructor
	public LichteinstellungenDialog(final Context context, int kundeId, int szenarioId) {
		super(context);

		mContext = context;
		pDialog = new ProgressDialog(mContext);
		pDialog.setMessage("Lade Daten!");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);

		mKundeId = kundeId;
		mSzenarioId = szenarioId;

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
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.lichteinstellungen_dialog_layout);

		Button bCloseLichteinstellungen = (Button) findViewById(R.id.bCloseLichteinstellungen);
		bCloseLichteinstellungen.setBackgroundResource(R.drawable.button_close);
		bCloseLichteinstellungen.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				insertLichtart();
				insertLichtfarbe();
				insertLichtstaerke();
				dismiss();
			}
		});

		try {

			// ----------------------------------------------------------------------------------//
			// gibKundeRaumauswahlName
			// ----------------------------------------------------------------------------------//

			pDialog.show();

			JSONObject params = new JSONObject();

			params.put("kundeId", mKundeId);
			params.put("szenarioId", mSzenarioId);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
					JSONArray la = result.getJSONArray("data");
					raumIdArray = result.getJSONArray("raumId");

					if (la.isNull(0)) {
						AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
						builder.setTitle("Kein Räume ausgewählt!");
						builder.setIcon(R.drawable.icon_kategorie1);
						builder.setMessage(
								"Sie haben noch keine Räume für Ihre persönliche Lichteinstellungen ausgewählt! Wählen Sie zuerst unter der Rubrik Raumauswahl Ihre Räume für dieses Szenario aus!")
								.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
									}
								});
						AlertDialog alert = builder.create();
						alert.show();
						pDialog.dismiss();
						dismiss();
					} else {

						for (int i = 0; i < la.length(); i++) {
							HashMap<String, String> hm = new HashMap<String, String>();
							hm.put("name", la.getString(i));
							hm.put("icon", String.valueOf(R.drawable.listitem_refresh));
							list.add(hm);
						}
						Log.i("data", la.toString());
						Log.i("msg", result.getString("msg"));

						dataAdapterRaeume = new SimpleAdapter(mContext, list, R.layout.listview_raeume_einstellungen,
								new String[] { "name", "icon" }, new int[] { R.id.name, R.id.icon });
						raeumeList = (ListView) findViewById(R.id.raeumeList);
						raeumeList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
						raeumeList.setAdapter(dataAdapterRaeume);
						raeumeList.setItemChecked(0, true);
						pDialog.dismiss();

						gibLichtart();
						gibLichtfarbe();
						gibLichtstaerke();
						gibKundeLichtart(0);
						gibKundeLichtfarbe(0);
						gibKundeLichtstaerke(0);

						raeumeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
								insertLichtart();
								insertLichtfarbe();
								insertLichtstaerke();
								gibLichtart();
								gibLichtfarbe();
								gibLichtstaerke();
								gibKundeLichtart(position);
								gibKundeLichtfarbe(position);
								gibKundeLichtstaerke(position);
							}
						});
					}

				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibKundeRaumauswahlName", params);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// get our tabHost from the xml
		final TabHost tabs = (TabHost) findViewById(R.id.TabHost01);
		tabs.setup();

		// create tab 1
		TabHost.TabSpec tab1 = tabs.newTabSpec("Lichtart");
		tab1.setContent(R.id.layLichtart);
		tab1.setIndicator("Lichtart");
		tabs.addTab(tab1);

		// create tab 2
		TabHost.TabSpec tab2 = tabs.newTabSpec("Lichtfarbe");
		tab2.setContent(R.id.layLichtfarbe);
		tab2.setIndicator("Lichtfarbe");
		tabs.addTab(tab2);

		// create tab 3
		TabHost.TabSpec tab3 = tabs.newTabSpec("Lichtstärke");
		tab3.setContent(R.id.layLichtstaerke);
		tab3.setIndicator("Lichtstärke");
		tabs.addTab(tab3);

	}

	static final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

	public void gibLichtart() {

		// ----------------------------------------------------------------------------------//
		// gibLichtart
		// ----------------------------------------------------------------------------------//

		JSONObject params = new JSONObject();
		ServerInterface si;
		pDialog.show();

		si = new ServerInterface();
		si.addListener(new ServerInterfaceListener() {

			public void serverSuccessHandler(JSONObject result) throws JSONException {

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

				SimpleAdapter dataAdapter = new SimpleAdapter(mContext, list, R.layout.listview_checkable, from, to);
				lichtartList = (ListView) findViewById(R.id.lichtartList);
				lichtartList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				lichtartList.setAdapter(dataAdapter);

				pDialog.dismiss();

				lichtartList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
					}
				});
			}

			public void serverErrorHandler(Exception e) {
				// z.B. Fehler Dialog aufploppen lassen
				Log.e("error", "called");
			}
		});
		params = new JSONObject();
		si.call("gibLichtart", params);
	}

	public void gibLichtfarbe() {

		// ----------------------------------------------------------------------------------//
		// gibLichtfarbe
		// ----------------------------------------------------------------------------------//

		JSONObject params = new JSONObject();
		ServerInterface si;
		pDialog.show();

		si = new ServerInterface();
		si.addListener(new ServerInterfaceListener() {

			public void serverSuccessHandler(JSONObject result) throws JSONException {

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

				SimpleAdapter dataAdapter = new SimpleAdapter(mContext, list, R.layout.listview_checkable, from, to);
				lichtfarbeList = (ListView) findViewById(R.id.lichtfarbeList);
				lichtfarbeList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				lichtfarbeList.setAdapter(dataAdapter);

				pDialog.dismiss();

				lichtfarbeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
					}
				});
			}

			public void serverErrorHandler(Exception e) {
				// z.B. Fehler Dialog aufploppen lassen
				Log.e("error", "called");
			}
		});
		params = new JSONObject();
		si.call("gibLichtfarbe", params);
	}

	public void gibLichtstaerke() {

		// ----------------------------------------------------------------------------------//
		// gibLichtstaerke
		// ----------------------------------------------------------------------------------//

		JSONObject params = new JSONObject();
		ServerInterface si;
		pDialog.show();

		si = new ServerInterface();
		si.addListener(new ServerInterfaceListener() {

			public void serverSuccessHandler(JSONObject result) throws JSONException {

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

				SimpleAdapter dataAdapter = new SimpleAdapter(mContext, list, R.layout.listview_checkable, from, to);
				lichtstaerkeList = (ListView) findViewById(R.id.lichtstaerkeList);
				lichtstaerkeList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				lichtstaerkeList.setAdapter(dataAdapter);

				pDialog.dismiss();

				lichtstaerkeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
					}
				});
			}

			public void serverErrorHandler(Exception e) {
				// z.B. Fehler Dialog aufploppen lassen
				Log.e("error", "called");
			}
		});
		params = new JSONObject();
		si.call("gibLichtstaerke", params);
	}

	public void gibKundeLichtart(int position) {

		// ----------------------------------------------------------------------------------//
		// gibKundeLichtart
		// ----------------------------------------------------------------------------------//

		try {

			raumId = raumIdArray.getInt(position);
			JSONObject params = new JSONObject();
			pDialog.show();

			params.put("kundeId", mKundeId);
			params.put("raumId", raumId);
			params.put("szenarioId", mSzenarioId);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					JSONArray wd = result.getJSONArray("data");

					for (int i = 0; i < wd.length(); i++) {
						lichtartList.setItemChecked((wd.getInt(i)) - 1, true);
					}
					pDialog.dismiss();
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibKundeLichtart", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void gibKundeLichtfarbe(int position) {

		// ----------------------------------------------------------------------------------//
		// gibKundeLichtfarbe
		// ----------------------------------------------------------------------------------//

		try {
			raumId = raumIdArray.getInt(position);
			JSONObject params = new JSONObject();
			pDialog.show();

			params.put("kundeId", mKundeId);
			params.put("raumId", raumId);
			params.put("szenarioId", mSzenarioId);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					JSONArray wd = result.getJSONArray("data");

					for (int i = 0; i < wd.length(); i++) {
						lichtfarbeList.setItemChecked((wd.getInt(i)) - 1, true);
					}

					pDialog.dismiss();
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibKundeLichtfarbe", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void gibKundeLichtstaerke(int position) {

		// ----------------------------------------------------------------------------------//
		// gibKundeLichtstaerke
		// ----------------------------------------------------------------------------------//

		try {
			raumId = raumIdArray.getInt(position);
			JSONObject params = new JSONObject();
			pDialog.show();

			params.put("kundeId", mKundeId);
			params.put("raumId", raumId);
			params.put("szenarioId", mSzenarioId);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					JSONArray wd = result.getJSONArray("data");

					for (int i = 0; i < wd.length(); i++) {
						lichtstaerkeList.setItemChecked((wd.getInt(i)) - 1, true);
					}
					pDialog.dismiss();
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibKundeLichtstaerke", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void insertLichtart() {

		// ----------------------------------------------------------------------------------//
		// insertLichtart
		// ----------------------------------------------------------------------------------//

		try {
			String selected = "";
			int count = 0;
			int cntChoice = lichtartList.getCount();
			SparseBooleanArray sparseBooleanArray = lichtartList.getCheckedItemPositions();

			for (int i = 0; i < cntChoice; i++) {
				if (sparseBooleanArray.get(i)) {
					selected += (lichtartList.getItemIdAtPosition(i) + 1) + ".";
					count++;
				}
			}
			if (count != 0) {
				selected = selected.substring(0, selected.length() - 1);
				Log.i("onPause: ", "Selected nach substring: " + selected + "  Count != 0: " + count);
			}
			JSONObject updateParams = new JSONObject();
			updateParams.put("lichtart", selected);
			updateParams.put("kundeId", mKundeId);
			updateParams.put("countAnz", count);
			updateParams.put("raumId", raumId);
			updateParams.put("szenarioId", mSzenarioId);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {
				public void serverSuccessHandler(JSONObject result) throws JSONException {
					Log.i("INSERT Lichtart: ", result.getString("msg"));
				}

				public void serverErrorHandler(Exception e) {
				}
			});
			si.call("insertLichtart", updateParams);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public void insertLichtfarbe() {

		// ----------------------------------------------------------------------------------//
		// insertLichtfarbe
		// ----------------------------------------------------------------------------------//

		try {
			String selected = "";
			int count = 0;
			int cntChoice = lichtfarbeList.getCount();
			SparseBooleanArray sparseBooleanArray = lichtfarbeList.getCheckedItemPositions();

			for (int i = 0; i < cntChoice; i++) {
				if (sparseBooleanArray.get(i)) {
					selected += (lichtfarbeList.getItemIdAtPosition(i) + 1) + ".";
					count++;
				}
			}
			if (count != 0) {
				selected = selected.substring(0, selected.length() - 1);
				Log.i("onPause: ", "Selected nach substring: " + selected + "  Count != 0: " + count);
			}
			JSONObject updateParams = new JSONObject();
			updateParams.put("lichtfarbe", selected);
			updateParams.put("kundeId", mKundeId);
			updateParams.put("countAnz", count);
			updateParams.put("raumId", raumId);
			updateParams.put("szenarioId", mSzenarioId);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {
				public void serverSuccessHandler(JSONObject result) throws JSONException {
					Log.i("INSERT Lichtfarbe: ", result.getString("msg"));
				}

				public void serverErrorHandler(Exception e) {
				}
			});
			si.call("insertLichtfarbe", updateParams);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public void insertLichtstaerke() {

		// ----------------------------------------------------------------------------------//
		// insertLichtstaerke
		// ----------------------------------------------------------------------------------//

		try {
			String selected = "";
			int count = 0;
			int cntChoice = lichtstaerkeList.getCount();
			SparseBooleanArray sparseBooleanArray = lichtstaerkeList.getCheckedItemPositions();

			for (int i = 0; i < cntChoice; i++) {
				if (sparseBooleanArray.get(i)) {
					selected += (lichtstaerkeList.getItemIdAtPosition(i) + 1) + ".";
					count++;
				}
			}
			if (count != 0) {
				selected = selected.substring(0, selected.length() - 1);
				Log.i("onPause: ", "Selected nach substring: " + selected + "  Count != 0: " + count);
			}
			JSONObject updateParams = new JSONObject();
			updateParams.put("lichtstaerke", selected);
			updateParams.put("kundeId", mKundeId);
			updateParams.put("countAnz", count);
			updateParams.put("raumId", raumId);
			updateParams.put("szenarioId", mSzenarioId);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {
				public void serverSuccessHandler(JSONObject result) throws JSONException {
					Log.i("INSERT Lichtstärke: ", result.getString("msg"));
				}

				public void serverErrorHandler(Exception e) {
				}
			});
			si.call("insertLichtstaerke", updateParams);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}