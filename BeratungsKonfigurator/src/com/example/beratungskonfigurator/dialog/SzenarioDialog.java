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

import com.example.beratungskonfigurator.R;
import com.example.beratungskonfigurator.server.ServerInterface;
import com.example.beratungskonfigurator.server.ServerInterfaceListener;


public class SzenarioDialog extends Dialog {

	private ProgressDialog pDialog;
	private ListView szenarioList;

	private int szenarioId;
	private int mKundeId;
	private int mAnwendungsfallId;
	private String mAnwendungsfallName;
	private TextView tBeschreibung;
	private TextView dialogTitel;
	private JSONArray szId;
	private JSONArray la;

	ArrayList<Integer> selSzenarioList = new ArrayList<Integer>();

	// Constructor
	public SzenarioDialog(final Context context, int anwendungsfallId, int kundeId, String anwendungsfallName) {
		super(context);

		pDialog = new ProgressDialog(context);
		pDialog.setMessage("Lade Daten!");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);

		mKundeId = kundeId;
		mAnwendungsfallId = anwendungsfallId;
		mAnwendungsfallName = anwendungsfallName;

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

		setContentView(R.layout.szenario_dialog_layout);

		dialogTitel = (TextView) findViewById(R.id.dialogTitel);
		dialogTitel.setText(mAnwendungsfallName);
		
		tBeschreibung = (TextView) findViewById(R.id.tBeschreibung);
		tBeschreibung.setMovementMethod(new ScrollingMovementMethod());
		Button bClose = (Button) findViewById(R.id.bClose);
		bClose.setBackgroundResource(R.drawable.button_close);
		bClose.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// ----------------------------------------------------------------------------------//
				// insertSzenario
				// ----------------------------------------------------------------------------------//

				try {
					if (selSzenarioList.isEmpty()) {
						selSzenarioList.add(0);
					}
					String selected = TextUtils.join(".", selSzenarioList);

					JSONObject updateParams = new JSONObject();
					updateParams.put("szenario", selected);
					updateParams.put("kundeId", mKundeId);

					Log.i("kundeId", "kundeId: " + mKundeId + " Szenario als String: " + selected);

					ServerInterface si = new ServerInterface();
					si.addListener(new ServerInterfaceListener() {
						public void serverSuccessHandler(JSONObject result) throws JSONException {
							Log.i("INSERT Szenario: ", result.getString("msg"));
						}

						public void serverErrorHandler(Exception e) {
							// TODO Auto-generated method
							// stub
						}
					});
					si.call("insertSzenario", updateParams);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				dismiss();

			}
		});

		// ----------------------------------------------------------------------------------//
		// gibSzenario
		// ----------------------------------------------------------------------------------//

		try {
			JSONObject params = new JSONObject();
			ServerInterface si;
			pDialog.show();

			params.put("anwendungsfallId", anwendungsfallId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					pDialog.dismiss();

					List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

					la = result.getJSONArray("data");
					szId = result.getJSONArray("szenarioId");

					for (int i = 0; i < la.length(); i++) {
						HashMap<String, String> hm = new HashMap<String, String>();
						hm.put("name", la.getString(i));
						hm.put("icon", String.valueOf(R.drawable.unchecked));
						list.add(hm);
					}
					Log.i("data", la.toString());

					SimpleAdapter adapterMainList = new SimpleAdapter(context, list, R.layout.listview_kategorie, new String[] { "icon", "name" },
							new int[] { R.id.icon, R.id.name });
					szenarioList = (ListView) findViewById(R.id.szenarioList);
					szenarioList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
					szenarioList.setAdapter(adapterMainList);
					szenarioList.setItemChecked(0, true);

					try {
						szenarioId = szId.getInt(0);
						Log.i("szenarioLIST:", "szenarioId: " + szenarioId);

						// ----------------------------------------------------------------------------------//
						// gibSzenarioDetails
						// ----------------------------------------------------------------------------------//

						JSONObject params = new JSONObject();
						ServerInterface si;

						params.put("szenarioId", szenarioId);

						si = new ServerInterface();
						si.addListener(new ServerInterfaceListener() {

							public void serverSuccessHandler(JSONObject result) throws JSONException {

								tBeschreibung.setText(result.getJSONObject("data").getString("beschreibung"));
							}

							public void serverErrorHandler(Exception e) {
								// z.B. Fehler Dialog aufploppen lassen
								Log.e("error", "called");
							}
						});
						si.call("gibSzenarioDetails", params);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					szenarioList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
							try {
								szenarioId = szId.getInt(position);
								Log.i("szenarioLIST:", "szenarioId: " + szenarioId + " Position: " + position);

								Log.i("gibSzenarioDetails", "START gibSzenarioDetails");

								// ----------------------------------------------------------------------------------//
								// gibSzenarioDetails
								// ----------------------------------------------------------------------------------//

								JSONObject params = new JSONObject();
								ServerInterface si;

								params.put("szenarioId", szenarioId);

								si = new ServerInterface();
								si.addListener(new ServerInterfaceListener() {

									public void serverSuccessHandler(JSONObject result) throws JSONException {

										tBeschreibung.setText(result.getJSONObject("data").getString("beschreibung"));
									}

									public void serverErrorHandler(Exception e) {
										// z.B. Fehler Dialog aufploppen lassen
										Log.e("error", "called");
									}
								});
								si.call("gibSzenarioDetails", params);

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					});

					szenarioList.setOnItemLongClickListener(new OnItemLongClickListener() {
						public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {

							try {
								int szenarioIdLong = szId.getInt(pos);
								Log.i("Szenario ID", "SZENARIO ID: " + szenarioIdLong);
								if (selSzenarioList.contains(szenarioIdLong)) {

									for (Iterator<Integer> nameIter = selSzenarioList.iterator(); nameIter.hasNext();) {
										Integer name = nameIter.next();
										if (name == szenarioIdLong) {
											nameIter.remove();
											Log.i("REMOVE", "name REMOVE: " + name);
										}
									}
								} else {
									selSzenarioList.add(szenarioIdLong);
								}

								Log.i("LISTE", "SZENARIO ID List: " + selSzenarioList);

								List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
								for (int i = 0; i < la.length(); i++) {
									HashMap<String, String> hm = new HashMap<String, String>();
									hm.put("name", la.getString(i));
									if (selSzenarioList.contains(szId.getInt(i))) {
										hm.put("icon", String.valueOf(R.drawable.checked));
									} else {
										hm.put("icon", String.valueOf(R.drawable.unchecked));
									}
									list.add(hm);
								}
								SimpleAdapter adapterMainList = new SimpleAdapter(context, list, R.layout.listview_kategorie, new String[] { "icon",
										"name" }, new int[] { R.id.icon, R.id.name });
								szenarioList = (ListView) findViewById(R.id.szenarioList);
								szenarioList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
								szenarioList.setAdapter(adapterMainList);
								szenarioList.setItemChecked(pos, true);
								Log.i("long clicked", "pos" + " " + pos);

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							return true;
						}
					});

				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibSzenario", params);

			// ----------------------------------------------------------------------------------//
			// gibKundeSzenario
			// ----------------------------------------------------------------------------------//

			Log.i("gibKundeSzenario", "START gibKundeSzenario");

			params = new JSONObject();
			pDialog.show();

			params.put("kundeId", mKundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					pDialog.dismiss();

					JSONArray selectedSId = result.getJSONArray("data");

					Log.i("JSONArray Result", "JSONArray Result: " + selectedSId);

					for (int i = 0; i < selectedSId.length(); i++) {
						selSzenarioList.add(selectedSId.getInt(i));
					}

					List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
					for (int i = 0; i < la.length(); i++) {
						HashMap<String, String> hm = new HashMap<String, String>();
						hm.put("name", la.getString(i));
						if (selSzenarioList.contains(szId.getInt(i))) {
							hm.put("icon", String.valueOf(R.drawable.checked));
						} else {
							hm.put("icon", String.valueOf(R.drawable.unchecked));
						}
						list.add(hm);
					}
					SimpleAdapter adapterMainList = new SimpleAdapter(context, list, R.layout.listview_kategorie, new String[] { "icon", "name" },
							new int[] { R.id.icon, R.id.name });
					szenarioList = (ListView) findViewById(R.id.szenarioList);
					szenarioList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
					szenarioList.setAdapter(adapterMainList);
					szenarioList.setItemChecked(0, true);
				}

				public void serverErrorHandler(Exception e) {
					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			si.call("gibKundeSzenario", params);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// get our tabHost from the xml
		TabHost tabs = (TabHost) findViewById(R.id.TabHost01);
		tabs.setup();

		// create tab 1
		TabHost.TabSpec tab1 = tabs.newTabSpec("Beschreibung");
		tab1.setContent(R.id.tBeschreibung);
		tab1.setIndicator("Beschreibung");
		tabs.addTab(tab1);

		// create tab 2
		TabHost.TabSpec tab2 = tabs.newTabSpec("Bilder");
		tab2.setContent(R.id.gBilder);
		tab2.setIndicator("Bilder");
		tabs.addTab(tab2);

		// create tab 2
		TabHost.TabSpec tab3 = tabs.newTabSpec("Video");
		tab3.setContent(R.id.vVideo);
		tab3.setIndicator("Video");
		tabs.addTab(tab3);

	}

	static final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

}
