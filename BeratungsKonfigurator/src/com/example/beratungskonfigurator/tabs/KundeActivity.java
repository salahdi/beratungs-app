package com.example.beratungskonfigurator.tabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.beratungskonfigurator.R;
import com.example.beratungskonfigurator.server.ServerInterface;
import com.example.beratungskonfigurator.server.ServerInterfaceListener;

@SuppressLint("ParserError")
public class KundeActivity extends Fragment implements View.OnKeyListener {

	private ProgressDialog pDialog;
	private ProgressDialog pDialogUpdate;

	private int currentSelected = 0;
	private int kundeId;
	private int angehoerigerId;

	private static final String ADRESSE = "Adresse";
	private static final String KONTAKT = "Kontakt";
	private static final String INFORMATION = "Information";
	private static final String VERSORGUNG = "Versorgung";
	private static final String ANGEHOERIGER = "Angehöriger";

	private int kFamilienstandId = 0;
	private int kKonfessionId = 0;
	private int kPflegestufeId = 0;
	private int kVersicherungsartId = 0;
	private int kVermoegenId = 0;
	private int kLeistungsartId = 0;
	private int kKrankenkasseId = 0;
	private int kKostentraegerId = 0;
	private int aArtId = 0;

	private ListView lv;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		kundeId = this.getArguments().getInt("sendKundeId");
		angehoerigerId = this.getArguments().getInt("sendAngehoerigerId");

		pDialog = new ProgressDialog(getActivity());
		pDialog.setMessage("Lade Daten!");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);

		pDialogUpdate = new ProgressDialog(getActivity());
		pDialogUpdate.setMessage("Aktualisiere Daten!");
		pDialogUpdate.setIndeterminate(false);
		pDialogUpdate.setCancelable(false);

		final View kundeView = (View) inflater.inflate(R.layout.tab_kunde_layout, container, false);
		final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		currentSelected = 0;

		final LinearLayout layAdresse = (LinearLayout) kundeView.findViewById(R.id.layAdresse);
		final LinearLayout layKontakt = (LinearLayout) kundeView.findViewById(R.id.layKontakt);
		final LinearLayout layInformation = (LinearLayout) kundeView.findViewById(R.id.layInformation);
		final LinearLayout layVersorgung = (LinearLayout) kundeView.findViewById(R.id.layVersorgung);
		final LinearLayout layAngehoeriger = (LinearLayout) kundeView.findViewById(R.id.layAngehoeriger);

		layKontakt.setVisibility(View.GONE);
		layInformation.setVisibility(View.GONE);
		layVersorgung.setVisibility(View.GONE);
		layAngehoeriger.setVisibility(View.GONE);

		final ToggleButton toggleEditAdresse = (ToggleButton) kundeView.findViewById(R.id.toggleEditAdresse);
		final ToggleButton toggleEditKontakt = (ToggleButton) kundeView.findViewById(R.id.toggleEditKontakt);
		final ToggleButton toggleEditInformation = (ToggleButton) kundeView.findViewById(R.id.toggleEditInformation);
		final ToggleButton toggleEditVersorgung = (ToggleButton) kundeView.findViewById(R.id.toggleEditVersorgung);
		final ToggleButton toggleEditAngehoeriger = (ToggleButton) kundeView.findViewById(R.id.toggleEditAngehoeriger);
		toggleEditAdresse.setBackgroundResource(R.drawable.button_edit);
		toggleEditKontakt.setBackgroundResource(R.drawable.button_edit);
		toggleEditInformation.setBackgroundResource(R.drawable.button_edit);
		toggleEditVersorgung.setBackgroundResource(R.drawable.button_edit);
		toggleEditAngehoeriger.setBackgroundResource(R.drawable.button_edit);

		final TextView titelAdresse = (TextView) kundeView.findViewById(R.id.titelAdresse);
		titelAdresse.setText(ADRESSE);
		final EditText kName = (EditText) kundeView.findViewById(R.id.kName);
		final EditText kVorname = (EditText) kundeView.findViewById(R.id.kVorname);
		final EditText kStrasse = (EditText) kundeView.findViewById(R.id.kStrasse);
		final EditText kHausnummer = (EditText) kundeView.findViewById(R.id.kHausnummer);
		final EditText kPlz = (EditText) kundeView.findViewById(R.id.kPLZ);
		final EditText kOrt = (EditText) kundeView.findViewById(R.id.kOrt);

		final TextView titelKontakt = (TextView) kundeView.findViewById(R.id.titelKontakt);
		titelKontakt.setText(KONTAKT);
		final EditText kTelefon = (EditText) kundeView.findViewById(R.id.kTelefon);
		final EditText kMobil = (EditText) kundeView.findViewById(R.id.kMobil);
		final EditText kFax = (EditText) kundeView.findViewById(R.id.kFax);
		final EditText kEmail = (EditText) kundeView.findViewById(R.id.kMail);

		final TextView titelInformation = (TextView) kundeView.findViewById(R.id.titelInformation);
		titelInformation.setText(INFORMATION);
		final EditText kGeburtsdatum = (EditText) kundeView.findViewById(R.id.kGeburtsdatum);
		final Spinner kFamilienstand = (Spinner) kundeView.findViewById(R.id.kFamilienstand);
		final Spinner kKonfession = (Spinner) kundeView.findViewById(R.id.kKonfession);
		final Spinner kPflegestufe = (Spinner) kundeView.findViewById(R.id.kPflegestufe);

		final TextView titelVersorgung = (TextView) kundeView.findViewById(R.id.titelVersorgung);
		titelVersorgung.setText(VERSORGUNG);
		final Spinner kVersicherungsart = (Spinner) kundeView.findViewById(R.id.kVersicherungsart);
		final Spinner kLeistungsart = (Spinner) kundeView.findViewById(R.id.kLeistungsart);
		final Spinner kKrankenkasse = (Spinner) kundeView.findViewById(R.id.kKrankenkasse);
		final Spinner kKostentraeger = (Spinner) kundeView.findViewById(R.id.kKostentraeger);
		final Spinner kVermoegen = (Spinner) kundeView.findViewById(R.id.kVermoegen);

		final TextView titelAngehoeriger = (TextView) kundeView.findViewById(R.id.titelAngehoeriger);
		titelAngehoeriger.setText(ANGEHOERIGER);
		final Spinner aArt = (Spinner) kundeView.findViewById(R.id.aArt);
		final EditText aName = (EditText) kundeView.findViewById(R.id.aName);
		final EditText aVorname = (EditText) kundeView.findViewById(R.id.aVorname);
		final EditText aStrasse = (EditText) kundeView.findViewById(R.id.aStrasse);
		final EditText aHausnummer = (EditText) kundeView.findViewById(R.id.aHausnummer);
		final EditText aPlz = (EditText) kundeView.findViewById(R.id.aPLZ);
		final EditText aOrt = (EditText) kundeView.findViewById(R.id.aOrt);
		final EditText aTelefon = (EditText) kundeView.findViewById(R.id.aTelefon);
		final EditText aMobil = (EditText) kundeView.findViewById(R.id.aMobil);
		final EditText aFax = (EditText) kundeView.findViewById(R.id.aFax);
		final EditText aEmail = (EditText) kundeView.findViewById(R.id.aMail);

		// ----------------------------------------------------------------------------------//
		// updateCurrentDate
		// ----------------------------------------------------------------------------------//

		try {
			JSONObject updateParams = new JSONObject();
			updateParams.put("kundeId", kundeId);

			ServerInterface si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {
				public void serverSuccessHandler(JSONObject result) throws JSONException {
					Log.i("UPDATE CurrentDate: ", result.getString("msg"));
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method
					// stub
				}
			});
			si.call("updateCurrentDate", updateParams);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String[] values = new String[] { ADRESSE, KONTAKT, INFORMATION, VERSORGUNG, ANGEHOERIGER };

		SimpleAdapter adapterMainList = new SimpleAdapter(getActivity(), list, R.layout.listview_main, new String[] { "name" },
				new int[] { R.id.name });
		lv = (ListView) kundeView.findViewById(R.id.kundeList);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		list.clear();
		HashMap<String, String> temp = new HashMap<String, String>();
		for (int i = 0; i < values.length; i++) {
			temp.put("name", values[i]);
			list.add(temp);
			temp = new HashMap<String, String>();
		}
		lv.setAdapter(adapterMainList);
		lv.setItemChecked(0, true);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

				switch (position) {
				case 0:
					Log.i("CASE", "IN Case: 0");
					switch (currentSelected) {
					case 0:
						Log.i("Current-CASE", "IN Case: 0");
						layAdresse.setVisibility(View.GONE);
						break;
					case 1:
						Log.i("Current-CASE", "IN Case: 1");
						layKontakt.setVisibility(View.GONE);
						break;
					case 2:
						Log.i("Current-CASE", "IN Case: 2");
						layInformation.setVisibility(View.GONE);
						break;
					case 3:
						Log.i("Current-CASE", "IN Case: 3");
						layVersorgung.setVisibility(View.GONE);
						break;
					case 4:
						Log.i("Current-CASE", "IN Case: 4");
						layAngehoeriger.setVisibility(View.GONE);
						break;
					}
					Log.i("CASE-0", "Out of Case 0!");
					layAdresse.setVisibility(View.VISIBLE);
					currentSelected = position;
					break;
				case 1:
					Log.i("CASE", "IN Case: 1");
					switch (currentSelected) {
					case 0:
						Log.i("Current-CASE", "IN Case: 0");
						layAdresse.setVisibility(View.GONE);
						break;
					case 1:
						Log.i("Current-CASE", "IN Case: 1");
						layKontakt.setVisibility(View.GONE);
						break;
					case 2:
						Log.i("Current-CASE", "IN Case: 2");
						layInformation.setVisibility(View.GONE);
						break;
					case 3:
						Log.i("Current-CASE", "IN Case: 3");
						layVersorgung.setVisibility(View.GONE);
						break;
					case 4:
						Log.i("Current-CASE", "IN Case: 4");
						layAngehoeriger.setVisibility(View.GONE);
						break;
					}
					Log.i("CASE-1", "Out of Case 1!");
					layKontakt.setVisibility(View.VISIBLE);
					currentSelected = position;
					break;
				case 2:
					Log.i("CASE", "IN Case: 2");
					switch (currentSelected) {
					case 0:
						Log.i("Current-CASE", "IN Case: 0");
						layAdresse.setVisibility(View.GONE);
						break;
					case 1:
						Log.i("Current-CASE", "IN Case: 1");
						layKontakt.setVisibility(View.GONE);
						break;
					case 2:
						Log.i("Current-CASE", "IN Case: 2");
						layInformation.setVisibility(View.GONE);
						break;
					case 3:
						Log.i("Current-CASE", "IN Case: 3");
						layVersorgung.setVisibility(View.GONE);
						break;
					case 4:
						Log.i("Current-CASE", "IN Case: 4");
						layAngehoeriger.setVisibility(View.GONE);
						break;
					}
					Log.i("CASE-2", "Out of Case 2!");
					layInformation.setVisibility(View.VISIBLE);
					currentSelected = position;
					break;
				case 3:
					Log.i("CASE", "IN Case: 3");
					switch (currentSelected) {
					case 0:
						Log.i("Current-CASE", "IN Case: 0");
						layAdresse.setVisibility(View.GONE);
						break;
					case 1:
						Log.i("Current-CASE", "IN Case: 1");
						layKontakt.setVisibility(View.GONE);
						break;
					case 2:
						Log.i("Current-CASE", "IN Case: 2");
						layInformation.setVisibility(View.GONE);
						break;
					case 3:
						Log.i("Current-CASE", "IN Case: 3");
						layVersorgung.setVisibility(View.GONE);
						break;
					case 4:
						Log.i("Current-CASE", "IN Case: 4");
						layAngehoeriger.setVisibility(View.GONE);
						break;
					}
					Log.i("CASE-3", "Out of Case 3!");
					layVersorgung.setVisibility(View.VISIBLE);
					currentSelected = position;
					break;
				case 4:
					Log.i("CASE", "IN Case: 4");
					switch (currentSelected) {
					case 0:
						Log.i("Current-CASE", "IN Case: 0");
						layAdresse.setVisibility(View.GONE);
						break;
					case 1:
						Log.i("Current-CASE", "IN Case: 1");
						layKontakt.setVisibility(View.GONE);
						break;
					case 2:
						Log.i("Current-CASE", "IN Case: 2");
						layInformation.setVisibility(View.GONE);
						break;
					case 3:
						Log.i("Current-CASE", "IN Case: 3");
						layVersorgung.setVisibility(View.GONE);
						break;
					case 4:
						Log.i("Current-CASE", "IN Case: 4");
						layAngehoeriger.setVisibility(View.GONE);
						break;
					}
					Log.i("CASE-4", "Out of Case 4!");
					layAngehoeriger.setVisibility(View.VISIBLE);
					currentSelected = position;
					break;
				}
			}
		});

		toggleEditAdresse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					// The toggle is enabled
					kName.setFocusable(true);
					kName.setClickable(true);
					kName.setCursorVisible(true);
					kName.setFocusableInTouchMode(true);
					kName.requestFocus();

					imm.showSoftInput(kName, InputMethodManager.SHOW_IMPLICIT);

					kVorname.setFocusable(true);
					kVorname.setClickable(true);
					kVorname.setCursorVisible(true);
					kVorname.setFocusableInTouchMode(true);

					kStrasse.setFocusable(true);
					kStrasse.setClickable(true);
					kStrasse.setCursorVisible(true);
					kStrasse.setFocusableInTouchMode(true);

					kPlz.setFocusable(true);
					kPlz.setClickable(true);
					kPlz.setCursorVisible(true);
					kPlz.setFocusableInTouchMode(true);

					kHausnummer.setFocusable(true);
					kHausnummer.setClickable(true);
					kHausnummer.setCursorVisible(true);
					kHausnummer.setFocusableInTouchMode(true);

					kOrt.setFocusable(true);
					kOrt.setClickable(true);
					kOrt.setCursorVisible(true);
					kOrt.setFocusableInTouchMode(true);

					// toggleEdit1.setBackgroundColor(Color.rgb(51,182,
					// 234));
				} else {

					// ----------------------------------------------------------------------------------//
					// updateKundeAdresse
					// ----------------------------------------------------------------------------------//
					try {
						JSONObject updateParams = new JSONObject();

						updateParams.put("kundeId", kundeId);
						updateParams.put("name", kName.getText().toString());
						updateParams.put("vorname", kVorname.getText().toString());
						updateParams.put("strasse", kStrasse.getText().toString());
						updateParams.put("hausnummer", kHausnummer.getText().toString());
						updateParams.put("plz", kPlz.getText().toString());
						updateParams.put("ort", kOrt.getText().toString());

						// z.B. Lass Swirl jetzt rotieren
						pDialogUpdate.show();

						ServerInterface si = new ServerInterface();
						si.addListener(new ServerInterfaceListener() {

							public void serverSuccessHandler(JSONObject result) throws JSONException {

								pDialogUpdate.dismiss();
								Log.i("UPDATE Kunde Adresse: ", result.getString("msg"));
							}

							public void serverErrorHandler(Exception e) {
								// TODO Auto-generated method
								// stub
							}
						});
						si.call("updateKundeAdresse", updateParams);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					// The toggle is disabled
					kName.setFocusable(false);
					kName.setClickable(false);
					kName.setCursorVisible(false);
					kName.setFocusableInTouchMode(false);

					kVorname.setFocusable(false);
					kVorname.setClickable(false);
					kVorname.setCursorVisible(false);
					kVorname.setFocusableInTouchMode(false);

					kStrasse.setFocusable(false);
					kStrasse.setClickable(false);
					kStrasse.setCursorVisible(false);
					kStrasse.setFocusableInTouchMode(false);

					kPlz.setFocusable(false);
					kPlz.setClickable(false);
					kPlz.setCursorVisible(false);
					kPlz.setFocusableInTouchMode(false);

					kHausnummer.setFocusable(false);
					kHausnummer.setClickable(false);
					kHausnummer.setCursorVisible(false);
					kHausnummer.setFocusableInTouchMode(false);

					kOrt.setFocusable(false);
					kOrt.setClickable(false);
					kOrt.setCursorVisible(false);
					kOrt.setFocusableInTouchMode(false);

					imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
					// toggleEdit1.setBackgroundColor(Color.LTGRAY);
				}
			}
		});

		toggleEditKontakt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					// The toggle is enabled
					kTelefon.setFocusable(true);
					kTelefon.setClickable(true);
					kTelefon.setCursorVisible(true);
					kTelefon.setFocusableInTouchMode(true);
					kTelefon.requestFocus();

					imm.showSoftInput(kTelefon, InputMethodManager.SHOW_IMPLICIT);

					kMobil.setFocusable(true);
					kMobil.setClickable(true);
					kMobil.setCursorVisible(true);
					kMobil.setFocusableInTouchMode(true);

					kFax.setFocusable(true);
					kFax.setClickable(true);
					kFax.setCursorVisible(true);
					kFax.setFocusableInTouchMode(true);

					kEmail.setFocusable(true);
					kEmail.setClickable(true);
					kEmail.setCursorVisible(true);
					kEmail.setFocusableInTouchMode(true);

				} else {

					// ----------------------------------------------------------------------------------//
					// updateKundeKontakt
					// ----------------------------------------------------------------------------------//
					try {

						JSONObject updateParams = new JSONObject();

						updateParams.put("kundeId", kundeId);
						updateParams.put("telefon", kTelefon.getText().toString());
						updateParams.put("mobil", kMobil.getText().toString());
						updateParams.put("fax", kFax.getText().toString());
						updateParams.put("email", kEmail.getText().toString());

						// z.B. Lass Swirl jetzt rotieren
						pDialogUpdate.show();

						ServerInterface si = new ServerInterface();
						si.addListener(new ServerInterfaceListener() {

							public void serverSuccessHandler(JSONObject result) throws JSONException {

								pDialogUpdate.dismiss();
								Log.i("UPDATE Kunde Kontakt: ", result.getString("msg"));
							}

							public void serverErrorHandler(Exception e) {
								// TODO Auto-generated method
								// stub
							}
						});
						si.call("updateKundeKontakt", updateParams);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					// The toggle is disabled
					kTelefon.setFocusable(false);
					kTelefon.setClickable(false);
					kTelefon.setCursorVisible(false);
					kTelefon.setFocusableInTouchMode(false);
					kTelefon.requestFocus();

					kMobil.setFocusable(false);
					kMobil.setClickable(false);
					kMobil.setCursorVisible(false);
					kMobil.setFocusableInTouchMode(false);

					kFax.setFocusable(false);
					kFax.setClickable(false);
					kFax.setCursorVisible(false);
					kFax.setFocusableInTouchMode(false);

					kEmail.setFocusable(false);
					kEmail.setClickable(false);
					kEmail.setCursorVisible(false);
					kEmail.setFocusableInTouchMode(false);

					imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
				}
			}
		});

		toggleEditInformation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					// The toggle is enabled
					kGeburtsdatum.setFocusable(true);
					kGeburtsdatum.setClickable(true);
					kGeburtsdatum.setCursorVisible(true);
					kGeburtsdatum.setFocusableInTouchMode(true);
					kGeburtsdatum.requestFocus();

					kFamilienstand.setEnabled(true);
					kKonfession.setEnabled(true);
					kPflegestufe.setEnabled(true);

					imm.showSoftInput(kGeburtsdatum, InputMethodManager.SHOW_IMPLICIT);

				} else {

					// ----------------------------------------------------------------------------------//
					// updateKundeInformation
					// ----------------------------------------------------------------------------------//
					try {

						JSONObject updateParams = new JSONObject();

						updateParams.put("kundeId", kundeId);
						updateParams.put("geburtsdatum", kGeburtsdatum.getText().toString());
						updateParams.put("familienstand", (kFamilienstand.getSelectedItemPosition() + 1));
						updateParams.put("konfession", (kKonfession.getSelectedItemPosition() + 1));
						updateParams.put("pflegestufe", (kPflegestufe.getSelectedItemPosition() + 1));

						// z.B. Lass Swirl jetzt rotieren
						pDialogUpdate.show();

						ServerInterface si = new ServerInterface();
						si.addListener(new ServerInterfaceListener() {

							public void serverSuccessHandler(JSONObject result) throws JSONException {

								pDialogUpdate.dismiss();
								Log.i("UPDATE Kunde Information: ", result.getString("msg"));
							}

							public void serverErrorHandler(Exception e) {
								// TODO Auto-generated method
								// stub
							}
						});
						si.call("updateKundeInformation", updateParams);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					// The toggle is disabled
					kGeburtsdatum.setFocusable(false);
					kGeburtsdatum.setClickable(false);
					kGeburtsdatum.setCursorVisible(false);
					kGeburtsdatum.setFocusableInTouchMode(false);

					kFamilienstand.setEnabled(false);
					kKonfession.setEnabled(false);
					kPflegestufe.setEnabled(false);

					imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
				}
			}
		});

		toggleEditVersorgung.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					// The toggle is enabled
					kVersicherungsart.setEnabled(true);
					kLeistungsart.setEnabled(true);
					kKrankenkasse.setEnabled(true);
					kKostentraeger.setEnabled(true);
					kVermoegen.setEnabled(true);
				} else {

					// ----------------------------------------------------------------------------------//
					// updateKundeVersorgung
					// ----------------------------------------------------------------------------------//
					try {

						JSONObject updateParams = new JSONObject();

						updateParams.put("kundeId", kundeId);
						updateParams.put("kostentraeger", (kKostentraeger.getSelectedItemPosition() + 1));
						updateParams.put("versicherungsart", (kVersicherungsart.getSelectedItemPosition() + 1));
						updateParams.put("leistungsart", (kLeistungsart.getSelectedItemPosition() + 1));
						updateParams.put("krankenkasse", (kKrankenkasse.getSelectedItemPosition() + 1));
						updateParams.put("vermoegen", (kVermoegen.getSelectedItemPosition() + 1));

						// z.B. Lass Swirl jetzt rotieren
						pDialogUpdate.show();

						ServerInterface si = new ServerInterface();
						si.addListener(new ServerInterfaceListener() {

							public void serverSuccessHandler(JSONObject result) throws JSONException {

								pDialogUpdate.dismiss();
								Log.i("UPDATE Kunde Information: ", result.getString("msg"));
							}

							public void serverErrorHandler(Exception e) {
								// TODO Auto-generated method
								// stub
							}
						});
						si.call("updateKundeVersorgung", updateParams);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					// The toggle is disabled
					kVersicherungsart.setEnabled(false);
					kLeistungsart.setEnabled(false);
					kKrankenkasse.setEnabled(false);
					kKostentraeger.setEnabled(false);
					kVermoegen.setEnabled(false);
				}
			}
		});

		toggleEditAngehoeriger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					// The toggle is enabled
					aArt.setEnabled(true);

					aName.setFocusable(true);
					aName.setClickable(true);
					aName.setCursorVisible(true);
					aName.setFocusableInTouchMode(true);
					aName.requestFocus();

					imm.showSoftInput(aName, InputMethodManager.SHOW_IMPLICIT);

					aVorname.setFocusable(true);
					aVorname.setClickable(true);
					aVorname.setCursorVisible(true);
					aVorname.setFocusableInTouchMode(true);

					aStrasse.setFocusable(true);
					aStrasse.setClickable(true);
					aStrasse.setCursorVisible(true);
					aStrasse.setFocusableInTouchMode(true);

					aPlz.setFocusable(true);
					aPlz.setClickable(true);
					aPlz.setCursorVisible(true);
					aPlz.setFocusableInTouchMode(true);

					aHausnummer.setFocusable(true);
					aHausnummer.setClickable(true);
					aHausnummer.setCursorVisible(true);
					aHausnummer.setFocusableInTouchMode(true);

					aOrt.setFocusable(true);
					aOrt.setClickable(true);
					aOrt.setCursorVisible(true);
					aOrt.setFocusableInTouchMode(true);

					aTelefon.setFocusable(true);
					aTelefon.setClickable(true);
					aTelefon.setCursorVisible(true);
					aTelefon.setFocusableInTouchMode(true);

					aMobil.setFocusable(true);
					aMobil.setClickable(true);
					aMobil.setCursorVisible(true);
					aMobil.setFocusableInTouchMode(true);

					aFax.setFocusable(true);
					aFax.setClickable(true);
					aFax.setCursorVisible(true);
					aFax.setFocusableInTouchMode(true);

					aEmail.setFocusable(true);
					aEmail.setClickable(true);
					aEmail.setCursorVisible(true);
					aEmail.setFocusableInTouchMode(true);

				} else {

					// ----------------------------------------------------------------------------------//
					// updateAngehoeriger
					// ----------------------------------------------------------------------------------//
					try {
						JSONObject updateParams = new JSONObject();

						updateParams.put("kundeId", kundeId);
						updateParams.put("angehoerigerId", angehoerigerId);
						updateParams.put("aArt", (aArt.getSelectedItemPosition() + 1));
						updateParams.put("aName", aName.getText().toString());
						updateParams.put("aVorname", aVorname.getText().toString());
						updateParams.put("aStrasse", aStrasse.getText().toString());
						updateParams.put("aHausnummer", aHausnummer.getText().toString());
						updateParams.put("aPlz", aPlz.getText().toString());
						updateParams.put("aOrt", aOrt.getText().toString());
						updateParams.put("aTelefon", aTelefon.getText().toString());
						updateParams.put("aMobil", aMobil.getText().toString());
						updateParams.put("aFax", aFax.getText().toString());
						updateParams.put("aEmail", aEmail.getText().toString());

						// z.B. Lass Swirl jetzt rotieren
						pDialogUpdate.show();

						ServerInterface si = new ServerInterface();
						si.addListener(new ServerInterfaceListener() {

							public void serverSuccessHandler(JSONObject result) throws JSONException {

								pDialogUpdate.dismiss();
								Log.i("UPDATE Kunde Adresse: ", result.getString("msg"));
							}

							public void serverErrorHandler(Exception e) {
								// TODO Auto-generated method
								// stub
							}
						});
						si.call("updateAngehoeriger", updateParams);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					// The toggle is disabled
					aArt.setEnabled(false);

					aName.setFocusable(false);
					aName.setClickable(false);
					aName.setCursorVisible(false);
					aName.setFocusableInTouchMode(false);

					aVorname.setFocusable(false);
					aVorname.setClickable(false);
					aVorname.setCursorVisible(false);
					aVorname.setFocusableInTouchMode(false);

					aStrasse.setFocusable(false);
					aStrasse.setClickable(false);
					aStrasse.setCursorVisible(false);
					aStrasse.setFocusableInTouchMode(false);

					aPlz.setFocusable(false);
					aPlz.setClickable(false);
					aPlz.setCursorVisible(false);
					aPlz.setFocusableInTouchMode(false);

					aHausnummer.setFocusable(false);
					aHausnummer.setClickable(false);
					aHausnummer.setCursorVisible(false);
					aHausnummer.setFocusableInTouchMode(false);

					aOrt.setFocusable(false);
					aOrt.setClickable(false);
					aOrt.setCursorVisible(false);
					aOrt.setFocusableInTouchMode(false);

					aTelefon.setFocusable(false);
					aTelefon.setClickable(false);
					aTelefon.setCursorVisible(false);
					aTelefon.setFocusableInTouchMode(false);

					aMobil.setFocusable(false);
					aMobil.setClickable(false);
					aMobil.setCursorVisible(false);
					aMobil.setFocusableInTouchMode(false);

					aFax.setFocusable(false);
					aFax.setClickable(false);
					aFax.setCursorVisible(false);
					aFax.setFocusableInTouchMode(false);

					aEmail.setFocusable(false);
					aEmail.setClickable(false);
					aEmail.setCursorVisible(false);
					aEmail.setFocusableInTouchMode(false);

					imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
				}
			}
		});

		try {

			// ----------------------------------------------------------------------------------//
			// gibKundendaten
			// ----------------------------------------------------------------------------------//

			JSONObject params = new JSONObject();
			ServerInterface si;
			pDialog.show();

			params.put("kundeId", kundeId);

			Log.i("VOR DIALOG: ", "KundeID: " + kundeId);

			// z.B. Lass Swirl jetzt rotieren
			pDialog.show();

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					Log.i("VOR DIALOG: ", "in SERVER SUCCESS HANDLER");
					pDialog.dismiss();

					kName.setText(result.getJSONObject("data").getString("name"));
					kVorname.setText(result.getJSONObject("data").getString("vorname"));
					kStrasse.setText(result.getJSONObject("data").getString("strasse"));
					kHausnummer.setText(result.getJSONObject("data").getString("hausnummer"));
					kPlz.setText(result.getJSONObject("data").getString("plz"));
					kOrt.setText(result.getJSONObject("data").getString("ort"));

					kTelefon.setText(result.getJSONObject("data").getString("telefon"));
					kMobil.setText(result.getJSONObject("data").getString("mobil"));
					kFax.setText(result.getJSONObject("data").getString("fax"));
					kEmail.setText(result.getJSONObject("data").getString("email"));

					kGeburtsdatum.setText(result.getJSONObject("data").getString("geburtsdatum"));

					kFamilienstandId = result.getJSONObject("data").getInt("familienstandId");
					kKonfessionId = result.getJSONObject("data").getInt("konfessionId");
					kPflegestufeId = result.getJSONObject("data").getInt("pflegestufeId");
					kLeistungsartId = result.getJSONObject("data").getInt("leistungsartId");
					kKrankenkasseId = result.getJSONObject("data").getInt("krankenkasseId");
					kKostentraegerId = result.getJSONObject("data").getInt("kostentraegerId");
					kVersicherungsartId = result.getJSONObject("data").getInt("versicherungsartId");
					kVermoegenId = result.getJSONObject("data").getInt("vermoegenId");

					aArtId = result.getJSONObject("data").getInt("artId");
					angehoerigerId = result.getJSONObject("data").getInt("angehoerigerId");
					aName.setText(result.getJSONObject("data").getString("aName"));
					aVorname.setText(result.getJSONObject("data").getString("aVorname"));
					aStrasse.setText(result.getJSONObject("data").getString("aStrasse"));
					aHausnummer.setText(result.getJSONObject("data").getString("aHausnummer"));
					aPlz.setText(result.getJSONObject("data").getString("aPlz"));
					aOrt.setText(result.getJSONObject("data").getString("aOrt"));

					aTelefon.setText(result.getJSONObject("data").getString("aTelefon"));
					aMobil.setText(result.getJSONObject("data").getString("aMobil"));
					aFax.setText(result.getJSONObject("data").getString("aFax"));
					aEmail.setText(result.getJSONObject("data").getString("aEmail"));

					Log.i("ALLLEEEEEEE DATEN", result.toString());
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method stub
				}
			});
			si.call("gibKundendaten", params);

			// ----------------------------------------------------------------------------------//
			// gibFamilienstaende
			// ----------------------------------------------------------------------------------//

			si = new ServerInterface();
			// si.setVerbose( true );
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					// z.B. Swirl jetzt verstecken und Grid mit Daten füllen
					pDialog.dismiss();

					List<String> list = new ArrayList<String>();

					JSONArray fs = result.getJSONArray("data");

					for (int i = 0; i < fs.length(); i++) {
						list.add(fs.getString(i));
					}
					Log.i("data", fs.toString());
					Log.i("msg", result.getString("msg"));

					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					kFamilienstand.setEnabled(false);
					kFamilienstand.setAdapter(dataAdapter);
					kFamilienstand.setSelection(kFamilienstandId - 1);

				}

				public void serverErrorHandler(Exception e) {

					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibFamilienstaende", params);

			// ----------------------------------------------------------------------------------//
			// gibKonfession
			// ----------------------------------------------------------------------------------//

			pDialog.show();

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					// z.B. Swirl jetzt verstecken und Grid mit Daten füllen
					pDialog.dismiss();

					List<String> list = new ArrayList<String>();

					JSONArray kf = result.getJSONArray("data");

					for (int i = 0; i < kf.length(); i++) {
						list.add(kf.getString(i));
					}
					Log.i("data", kf.toString());
					Log.i("msg", result.getString("msg"));

					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					kKonfession.setEnabled(false);
					kKonfession.setAdapter(dataAdapter);
					kKonfession.setSelection(kKonfessionId - 1);

				}

				public void serverErrorHandler(Exception e) {

					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibKonfession", params);

			// ----------------------------------------------------------------------------------//
			// gibPflegestufe
			// ----------------------------------------------------------------------------------//

			pDialog.show();

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					// z.B. Swirl jetzt verstecken und Grid mit Daten füllen
					pDialog.dismiss();

					List<String> list = new ArrayList<String>();

					JSONArray pfs = result.getJSONArray("data");

					for (int i = 0; i < pfs.length(); i++) {
						list.add(pfs.getString(i));
					}
					Log.i("data", pfs.toString());
					Log.i("msg", result.getString("msg"));

					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					kPflegestufe.setEnabled(false);
					kPflegestufe.setAdapter(dataAdapter);
					kPflegestufe.setSelection(kPflegestufeId - 1);

				}

				public void serverErrorHandler(Exception e) {

					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibPflegestufe", params);

			// ----------------------------------------------------------------------------------//
			// gibKostentraeger
			// ----------------------------------------------------------------------------------//

			pDialog.show();

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					// z.B. Swirl jetzt verstecken und Grid mit Daten füllen
					pDialog.dismiss();

					List<String> list = new ArrayList<String>();

					JSONArray kt = result.getJSONArray("data");

					for (int i = 0; i < kt.length(); i++) {
						list.add(kt.getString(i));
					}
					Log.i("data", kt.toString());
					Log.i("msg", result.getString("msg"));

					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					kKostentraeger.setEnabled(false);
					kKostentraeger.setAdapter(dataAdapter);
					kKostentraeger.setSelection(kKostentraegerId - 1);

				}

				public void serverErrorHandler(Exception e) {

					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibKostentraeger", params);

			// ----------------------------------------------------------------------------------//
			// gibVersicherungsart
			// ----------------------------------------------------------------------------------//

			pDialog.show();

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					// z.B. Swirl jetzt verstecken und Grid mit Daten füllen
					pDialog.dismiss();

					List<String> list = new ArrayList<String>();

					JSONArray va = result.getJSONArray("data");

					for (int i = 0; i < va.length(); i++) {
						list.add(va.getString(i));
					}
					Log.i("data", va.toString());
					Log.i("msg", result.getString("msg"));

					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					kVersicherungsart.setEnabled(false);
					kVersicherungsart.setAdapter(dataAdapter);
					kVersicherungsart.setSelection(kVersicherungsartId - 1);

				}

				public void serverErrorHandler(Exception e) {

					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibVersicherungsart", params);

			// ----------------------------------------------------------------------------------//
			// gibKrankenkasse
			// ----------------------------------------------------------------------------------//

			pDialog.show();

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					// z.B. Swirl jetzt verstecken und Grid mit Daten füllen
					pDialog.dismiss();

					List<String> list = new ArrayList<String>();

					JSONArray kk = result.getJSONArray("data");

					for (int i = 0; i < kk.length(); i++) {
						list.add(kk.getString(i));
					}
					Log.i("data", kk.toString());
					Log.i("msg", result.getString("msg"));

					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					kKrankenkasse.setEnabled(false);
					kKrankenkasse.setAdapter(dataAdapter);
					kKrankenkasse.setSelection(kKrankenkasseId - 1);

				}

				public void serverErrorHandler(Exception e) {

					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibKrankenkasse", params);

			// ----------------------------------------------------------------------------------//
			// gibVermoegen
			// ----------------------------------------------------------------------------------//

			pDialog.show();

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					// z.B. Swirl jetzt verstecken und Grid mit Daten füllen
					pDialog.dismiss();

					List<String> list = new ArrayList<String>();

					JSONArray kk = result.getJSONArray("data");

					for (int i = 0; i < kk.length(); i++) {
						list.add(kk.getString(i));
					}
					Log.i("data", kk.toString());
					Log.i("msg", result.getString("msg"));

					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					kVermoegen.setEnabled(false);
					kVermoegen.setAdapter(dataAdapter);
					kVermoegen.setSelection(kVermoegenId - 1);

				}

				public void serverErrorHandler(Exception e) {

					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibVermoegen", params);

			// ----------------------------------------------------------------------------------//
			// gibLeistungsart
			// ----------------------------------------------------------------------------------//

			pDialog.show();

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					// z.B. Swirl jetzt verstecken und Grid mit Daten füllen
					pDialog.dismiss();

					List<String> list = new ArrayList<String>();

					JSONArray la = result.getJSONArray("data");

					for (int i = 0; i < la.length(); i++) {
						list.add(la.getString(i));
					}
					Log.i("data", la.toString());
					Log.i("msg", result.getString("msg"));

					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					kLeistungsart.setEnabled(false);
					kLeistungsart.setAdapter(dataAdapter);
					kLeistungsart.setSelection(kLeistungsartId - 1);

				}

				public void serverErrorHandler(Exception e) {

					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibLeistungsart", params);

			// ----------------------------------------------------------------------------------//
			// gibAngehoerigerArt
			// ----------------------------------------------------------------------------------//

			pDialog.show();

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					// z.B. Swirl jetzt verstecken und Grid mit Daten füllen
					pDialog.dismiss();

					List<String> list = new ArrayList<String>();

					JSONArray aa = result.getJSONArray("data");

					for (int i = 0; i < aa.length(); i++) {
						list.add(aa.getString(i));
					}
					Log.i("data", aa.toString());
					Log.i("msg", result.getString("msg"));

					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					aArt.setEnabled(false);
					aArt.setAdapter(dataAdapter);
					aArt.setSelection(aArtId - 1);

				}

				public void serverErrorHandler(Exception e) {

					// z.B. Fehler Dialog aufploppen lassen
					Log.e("error", "called");
				}
			});
			params = new JSONObject();
			si.call("gibAngehoerigerArt", params);

		} catch (JSONException e) {
		}
		return kundeView;
	}

	@Override
	public void onPause() {
		lv.setItemChecked(0, true);
		super.onPause();
	}

	static final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
}