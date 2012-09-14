package com.example.beratungskonfigurator;

import harmony.java.awt.Color;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseBooleanArray;

import com.example.beratungskonfigurator.dialog.RaumauswahlDialog;
import com.example.beratungskonfigurator.server.ServerInterface;
import com.example.beratungskonfigurator.server.ServerInterfaceListener;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.*;

public class ExportPdfExample {

	static String name = "", vorname = "", strasse = "", hausnummer = "", plz = "", ort = "";
	static String telefon = "", mobil = "", fax = "", email = "";
	static String geburtsdatum = "", familienstand = "", konfession = "", pflegestufe = "";
	static String kostentraeger = "", versicherungsart = "", krankenkasse = "", leistungsart = "", vermoegen = "";
	static String aArt = "", aName = "", aVorname = "", aStrasse = "", aHausnummer = "", aPlz = "", aOrt = "", aTelefon = "", aMobil = "", aFax = "",
			aEmail = "";

	static String stockwerke = "", wohnflaeche = "";
	static List<String> wohnsituationList = new ArrayList<String>();
	static List<String> wohnformList = new ArrayList<String>();
	static List<String> wohnumfeldList = new ArrayList<String>();
	static List<String> wohnraeumeList = new ArrayList<String>();
	static List<String> wohnbarrierenList = new ArrayList<String>();

	static List<String> erkrankungenList = new ArrayList<String>();
	static List<String> pflegeList = new ArrayList<String>();
	static List<String> alltagskompetenzenList = new ArrayList<String>();
	static List<String> beobachtungenList = new ArrayList<String>();

	static List<String> szenarienList = new ArrayList<String>();
	static List<String> raeumeList = new ArrayList<String>();
	static List<String> geraeteeinstellungenList = new ArrayList<String>();
	static List<String> lichtartList = new ArrayList<String>();
	static List<String> lichtfarbeList = new ArrayList<String>();
	static List<String> lichtstaerkeList = new ArrayList<String>();
	static List<String> dienstleisterList = new ArrayList<String>();
	static List<String> exportdatenList = new ArrayList<String>();

	private static String file;
	private static int count = 0;
	private static int lastCount;

	private static int mKundeId;
	private static int mAngehoerigerId;

	private static Document document;

	private static Font catFont = new Font(Font.HELVETICA, 18, Font.BOLD);
	private static Font cyanFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.CYAN);
	private static Font dGrayFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.DARK_GRAY);
	private static Font subFont = new Font(Font.HELVETICA, 16, Font.BOLD);
	private static Font smallBold = new Font(Font.HELVETICA, 12, Font.BOLD);

	public static void main(String[] args, int kundeId, int angehoerigerId) {

		mKundeId = kundeId;
		mAngehoerigerId = angehoerigerId;
		
		wohnsituationList.clear();
		wohnformList.clear();
		wohnumfeldList.clear();
		wohnraeumeList.clear();
		wohnbarrierenList.clear();

		erkrankungenList.clear();
		pflegeList.clear();
		alltagskompetenzenList.clear();
		beobachtungenList.clear();

		szenarienList.clear();
		raeumeList.clear();
		geraeteeinstellungenList.clear();
		lichtartList.clear();
		lichtfarbeList.clear();
		lichtstaerkeList.clear();
		dienstleisterList.clear();
		exportdatenList.clear();
		
		exportKundedaten();
		exportWohnungsdaten();
		exportGesundheitsdaten();
		exportSzenariendaten();
		exportExportdaten();
	}

	public static String getPdfPath() {
		return file;
	}

	private static void createDocument() {
		String currentDateTime = DateFormat.getDateTimeInstance().format(new Date());
		System.out.println("Hello World");
		count = lastCount + 1;
		lastCount = count;
		String FOLDER = "/pdf";
		String FILE = "/" + currentDateTime + "_Beratungskonfigurator.pdf";
		file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + FOLDER + FILE;

		// step 1: creation of a document-object
		document = new Document();
		try {
			// step 2:
			// we create a writer that listens to the document
			// and directs a PDF-stream to a file
			PdfWriter.getInstance(document, new FileOutputStream(file));
			document.open();
			addMetaData(document);
			addTitlePage(document);
			Log.i("EXPORT DATEN LIST", "List: " + exportdatenList.toString());
			Log.i("EXPORT DATEN LIST", "List an Pos 0: " + exportdatenList.get(0));
			for (int i = 0; i < exportdatenList.size(); i++) {
				if (exportdatenList.get(i).equals("0")) {
					addKundendaten(document);
				} else if (exportdatenList.get(i).equals("1")) {
					addWohnungsdaten(document);
				} else if (exportdatenList.get(i).equals("2")) {
					addGesundheitsdaten(document);
				} else if (exportdatenList.get(i).equals("3")) {
					addSzenariendaten(document);
				}
			}
		} catch (DocumentException de) {
			System.err.println(de.getMessage());
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
		document.close();
	}

	private static void addMetaData(Document document) {
		document.addTitle("KONGA home Beratung");
		document.addSubject("Beratungsübersicht");
		document.addKeywords("Beratung, Konfiguration, AAL, Gebäudesteuerung, Homeautomation");
		document.addAuthor("Julian Doll");
		document.addCreator("Julian Doll");
	}

	private static void addTitlePage(Document document) throws DocumentException {
		Paragraph preface = new Paragraph();
		addEmptyLine(preface, 1);
		preface.add(new Paragraph("Beratungsübersicht", catFont));
		addEmptyLine(preface, 1);
		preface.add(new Paragraph("Beratung durchgeführt von: Julian Doll   " + new Date(), smallBold));
		addEmptyLine(preface, 3);
		document.add(preface);
		// document.newPage();
	}

	private static void addContent(Document document) throws DocumentException {
		Anchor anchorKundendaten = new Anchor("Kundendaten", catFont);
		anchorKundendaten.setName("Kundendaten");
		Chapter catPart1 = new Chapter(new Paragraph(anchorKundendaten), 1);

		Anchor anchorWohnungsdaten = new Anchor("Wohnungsdaten", catFont);
		anchorWohnungsdaten.setName("Kundendaten");
		Chapter catPart2 = new Chapter(new Paragraph(anchorWohnungsdaten), 2);

		Paragraph subPara = new Paragraph("Subcategory 1", subFont);
		Section subCatPart = catPart1.addSection(subPara);
		subCatPart.add(new Paragraph("Hello"));

		subPara = new Paragraph("Subcategory 2", subFont);
		subCatPart = catPart1.addSection(subPara);
		subCatPart.add(new Paragraph("Paragraph 1"));
		subCatPart.add(new Paragraph("Paragraph 2"));
		subCatPart.add(new Paragraph("Paragraph 3"));

		// Add a list
		// createList(subCatPart);
		Paragraph paragraph = new Paragraph();
		addEmptyLine(paragraph, 5);
		subCatPart.add(paragraph);

		// Now add all this to the document
		document.add(catPart1);

		// Next section
		anchorKundendaten = new Anchor("Second Chapter", catFont);
		anchorKundendaten.setName("Second Chapter");

		// Second parameter is the number of the chapter
		catPart1 = new Chapter(new Paragraph(anchorKundendaten), 1);

		subPara = new Paragraph("Subcategory", subFont);
		subCatPart = catPart1.addSection(subPara);
		subCatPart.add(new Paragraph("This is a very important message"));

		// Now add all this to the document
		document.add(catPart1);

	}

	/*
	 * private static void createList(Section subCatPart) { List list = new
	 * List(true, false, 10); list.add(new ListItem("First point"));
	 * list.add(new ListItem("Second point")); list.add(new
	 * ListItem("Third point")); subCatPart.add(list); }
	 */

	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}

	private static void addKundendaten(final Document document) throws DocumentException {

		Anchor anchorKundendaten = new Anchor("Kundendaten", catFont);
		anchorKundendaten.setName("Kundendaten");
		Chapter catPart1 = new Chapter(new Paragraph(anchorKundendaten), 1);

		Paragraph subPara1 = new Paragraph("Adresse", subFont);
		Section subCatPart1 = catPart1.addSection(subPara1);
		subCatPart1.add(new Paragraph("Name:" + "   " + name));
		subCatPart1.add(new Paragraph("Vorname:" + "   " + vorname));
		subCatPart1.add(new Paragraph("Straße/Nr.:" + "   " + strasse + " " + hausnummer));
		subCatPart1.add(new Paragraph("PLZ/Ort:" + "   " + plz + " " + ort));
		document.add(Chunk.NEWLINE);

		Paragraph subPara2 = new Paragraph("Kontakt", subFont);
		Section subCatPart2 = catPart1.addSection(subPara2);
		subCatPart2.add(new Paragraph("Telefon:" + "   " + telefon));
		subCatPart2.add(new Paragraph("Mobil:" + "   " + mobil));
		subCatPart2.add(new Paragraph("Fax:" + "   " + fax));
		subCatPart2.add(new Paragraph("E-Mail:" + "   " + email));
		document.add(Chunk.NEWLINE);

		Paragraph subPara3 = new Paragraph("Information", subFont);
		Section subCatPart3 = catPart1.addSection(subPara3);
		subCatPart3.add(new Paragraph("Geburtsdatum:" + "   " + geburtsdatum));
		subCatPart3.add(new Paragraph("Familienstand:" + "   " + familienstand));
		subCatPart3.add(new Paragraph("Konfession:" + "   " + konfession));
		subCatPart3.add(new Paragraph("Pflegestufe:" + "   " + pflegestufe));
		document.add(Chunk.NEWLINE);

		Paragraph subPara4 = new Paragraph("Versorgung", subFont);
		Section subCatPart4 = catPart1.addSection(subPara4);
		subCatPart4.add(new Paragraph("Kostenträger:" + "   " + kostentraeger));
		subCatPart4.add(new Paragraph("Versicherungsart:" + "   " + versicherungsart));
		subCatPart4.add(new Paragraph("Krankenkasse:" + "   " + krankenkasse));
		subCatPart4.add(new Paragraph("Leistungsart:" + "   " + leistungsart));
		subCatPart4.add(new Paragraph("Vermögen:" + "   " + vermoegen));
		document.add(Chunk.NEWLINE);

		Paragraph subPara5 = new Paragraph("Angehöriger", subFont);
		Section subCatPart5 = catPart1.addSection(subPara5);
		subCatPart5.add(new Paragraph("Art:" + "   " + aArt));
		subCatPart5.add(new Paragraph("Name:" + "   " + aName));
		subCatPart5.add(new Paragraph("Vorname:" + "   " + aVorname));
		subCatPart5.add(new Paragraph("Straße/Nr.:" + "   " + aStrasse + " " + aHausnummer));
		subCatPart5.add(new Paragraph("PLZ/Ort:" + "   " + aPlz + " " + aOrt));
		subCatPart5.add(new Paragraph("Telefon:" + "   " + aTelefon));
		subCatPart5.add(new Paragraph("Mobil:" + "   " + aMobil));
		subCatPart5.add(new Paragraph("Fax:" + "   " + aFax));
		subCatPart5.add(new Paragraph("E-Mail:" + "   " + aEmail));

		document.add(catPart1);

	}

	private static void addWohnungsdaten(final Document document) throws DocumentException {

		Anchor anchorWohnungsdaten = new Anchor("Wohnungsdaten", catFont);
		anchorWohnungsdaten.setName("Wohnungsdaten");
		Chapter catPart2 = new Chapter(new Paragraph(anchorWohnungsdaten), 2);

		Paragraph subPara1 = new Paragraph("Wohnsituation", subFont);
		Section subCatPart1 = catPart2.addSection(subPara1);

		for (int i = 0; i < wohnsituationList.size(); i++) {
			subCatPart1.add(new Paragraph(wohnsituationList.get(i).toString()));
		}
		document.add(Chunk.NEWLINE);
		Paragraph subPara2 = new Paragraph("Wohnform", subFont);
		Section subCatPart2 = catPart2.addSection(subPara2);

		for (int i = 0; i < wohnformList.size(); i++) {
			subCatPart2.add(new Paragraph(wohnformList.get(i).toString()));
		}
		document.add(Chunk.NEWLINE);
		Paragraph subPara3 = new Paragraph("Wohnumfeld", subFont);
		Section subCatPart3 = catPart2.addSection(subPara3);

		for (int i = 0; i < wohnumfeldList.size(); i++) {
			subCatPart3.add(new Paragraph(wohnumfeldList.get(i).toString()));
		}
		document.add(Chunk.NEWLINE);
		Paragraph subPara4 = new Paragraph("Wohnräume", subFont);
		Section subCatPart4 = catPart2.addSection(subPara4);

		for (int i = 0; i < wohnraeumeList.size(); i++) {
			subCatPart4.add(new Paragraph(wohnraeumeList.get(i).toString()));
		}
		document.add(Chunk.NEWLINE);
		Paragraph subPara5 = new Paragraph("Wohnbarrieren", subFont);
		Section subCatPart5 = catPart2.addSection(subPara5);

		for (int i = 0; i < wohnbarrierenList.size(); i++) {
			subCatPart5.add(new Paragraph(wohnbarrierenList.get(i).toString()));
		}
		Paragraph subPara6 = new Paragraph("Wohninformation", subFont);
		Section subCatPart6 = catPart2.addSection(subPara6);
		subCatPart6.add(new Paragraph("Anzahl der Stockwerke:" + "   " + stockwerke));
		subCatPart6.add(new Paragraph("Wohnfläche in qm:" + "   " + wohnflaeche));

		document.add(catPart2);

	}

	private static void addGesundheitsdaten(final Document document) throws DocumentException {

		Anchor anchorGesundheitsdaten = new Anchor("gesundheitliche Problemstellung", catFont);
		anchorGesundheitsdaten.setName("gesundheitliche Problemstellung");
		Chapter catPart3 = new Chapter(new Paragraph(anchorGesundheitsdaten), 3);

		Paragraph subPara1 = new Paragraph("Erkrankungen", subFont);
		Section subCatPart1 = catPart3.addSection(subPara1);

		for (int i = 0; i < erkrankungenList.size(); i++) {
			subCatPart1.add(new Paragraph(erkrankungenList.get(i).toString()));
		}
		document.add(Chunk.NEWLINE);
		Paragraph subPara2 = new Paragraph("Pflege erfolgt durch", subFont);
		Section subCatPart2 = catPart3.addSection(subPara2);

		for (int i = 0; i < pflegeList.size(); i++) {
			subCatPart2.add(new Paragraph(pflegeList.get(i).toString()));
		}
		document.add(Chunk.NEWLINE);
		Paragraph subPara3 = new Paragraph("Alltagskompetenzen", subFont);
		Section subCatPart3 = catPart3.addSection(subPara3);

		for (int i = 0; i < alltagskompetenzenList.size(); i++) {
			subCatPart3.add(new Paragraph(alltagskompetenzenList.get(i).toString()));
		}
		document.add(Chunk.NEWLINE);
		Paragraph subPara4 = new Paragraph("Beobachtungen", subFont);
		Section subCatPart4 = catPart3.addSection(subPara4);

		for (int i = 0; i < beobachtungenList.size(); i++) {
			subCatPart4.add(new Paragraph(beobachtungenList.get(i).toString()));
		}
		document.add(catPart3);

	}

	private static void addSzenariendaten(final Document document) throws DocumentException {

		Anchor anchorSzenarien = new Anchor("eingerichtete Szenarien Einstellungen", catFont);
		anchorSzenarien.setName("eingerichtete Szenarien Einstellungen");
		Chapter catPart4 = new Chapter(new Paragraph(anchorSzenarien), 4);

		Paragraph subPara1 = new Paragraph("Geräteeinstellungen", subFont);
		Section subCatPart1 = catPart4.addSection(subPara1);
		document.add(Chunk.NEWLINE);
		for (int i = 0; i < geraeteeinstellungenList.size(); i++) {
			subCatPart1.add(new Paragraph(geraeteeinstellungenList.get(i).toString() + ":", dGrayFont));
			subCatPart1.add(new Paragraph(geraeteeinstellungenList.get(i = i + 1).toString() + ":   "
					+ geraeteeinstellungenList.get(i = i + 1).toString() + "x  " + geraeteeinstellungenList.get(i = i + 1).toString() + "  "
					+ geraeteeinstellungenList.get(i = i + 1).toString()));
		}
		document.add(Chunk.NEWLINE);
		Paragraph subPara2 = new Paragraph("Lichteinstellungen", subFont);
		Section subCatPart2 = catPart4.addSection(subPara2);
		document.add(Chunk.NEWLINE);
		subCatPart2.add(new Paragraph("Lichtart", dGrayFont));
		for (int i = 0; i < lichtartList.size(); i++) {
			subCatPart2.add(new Paragraph(lichtartList.get(i).toString()));
		}
		document.add(Chunk.NEWLINE);
		subCatPart2.add(new Paragraph("Lichtfarbe", dGrayFont));
		for (int i = 0; i < lichtfarbeList.size(); i++) {
			subCatPart2.add(new Paragraph(lichtfarbeList.get(i).toString()));
		}
		document.add(Chunk.NEWLINE);
		subCatPart2.add(new Paragraph("Lichtstärke", dGrayFont));
		for (int i = 0; i < lichtstaerkeList.size(); i++) {
			subCatPart2.add(new Paragraph(lichtstaerkeList.get(i).toString()));
		}
		document.add(Chunk.NEWLINE);
		subCatPart2.add(new Paragraph("Dienstleister", dGrayFont));
		for (int i = 0; i < dienstleisterList.size(); i++) {
			subCatPart2.add(new Paragraph(dienstleisterList.get(i).toString()));
		}
		document.add(catPart4);

	}

	private static void exportKundedaten() {

		// ----------------------------------------------------------------------------------//
		// exportKundendaten
		// ----------------------------------------------------------------------------------//

		try {
			JSONObject params = new JSONObject();
			ServerInterface si;

			params.put("kundeId", mKundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					Log.i("Alle Export DATEN", result.toString());

					name = result.getJSONObject("data").getString("name");
					vorname = result.getJSONObject("data").getString("vorname");
					strasse = result.getJSONObject("data").getString("strasse");
					hausnummer = result.getJSONObject("data").getString("hausnummer");
					plz = result.getJSONObject("data").getString("plz");
					ort = result.getJSONObject("data").getString("ort");

					telefon = result.getJSONObject("data").getString("telefon");
					mobil = result.getJSONObject("data").getString("mobil");
					fax = result.getJSONObject("data").getString("fax");
					email = result.getJSONObject("data").getString("email");

					geburtsdatum = result.getJSONObject("data").getString("geburtsdatum");
					familienstand = result.getJSONObject("data").getString("familienstand");
					konfession = result.getJSONObject("data").getString("konfession");
					pflegestufe = result.getJSONObject("data").getString("pflegestufe");

					kostentraeger = result.getJSONObject("data").getString("kostentraeger");
					versicherungsart = result.getJSONObject("data").getString("versicherungsart");
					krankenkasse = result.getJSONObject("data").getString("krankenkasse");
					leistungsart = result.getJSONObject("data").getString("leistungsart");
					vermoegen = result.getJSONObject("data").getString("vermoegen");

					aArt = result.getJSONObject("data").getString("art");
					aName = result.getJSONObject("data").getString("aName");
					aVorname = result.getJSONObject("data").getString("aVorname");
					aStrasse = result.getJSONObject("data").getString("aStrasse");
					aHausnummer = result.getJSONObject("data").getString("aHausnummer");
					aPlz = result.getJSONObject("data").getString("aPlz");
					aOrt = result.getJSONObject("data").getString("aOrt");
					aTelefon = result.getJSONObject("data").getString("aTelefon");
					aMobil = result.getJSONObject("data").getString("aMobil");
					aFax = result.getJSONObject("data").getString("aFax");
					aEmail = result.getJSONObject("data").getString("aEmail");

				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method stub
				}
			});
			si.call("exportKundendaten", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void exportWohnungsdaten() {

		// ----------------------------------------------------------------------------------//
		// exportWohnsituation
		// ----------------------------------------------------------------------------------//
		try {
			JSONObject params = new JSONObject();
			ServerInterface si;

			params.put("kundeId", mKundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					Log.i("Alle Export DATEN", result.toString());

					JSONArray wd = result.getJSONArray("data");

					for (int i = 0; i < wd.length(); i++) {
						wohnsituationList.add(wd.getString(i));
					}
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method stub
				}
			});
			si.call("exportWohnsituation", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ----------------------------------------------------------------------------------//
		// exportWohnform
		// ----------------------------------------------------------------------------------//
		try {
			JSONObject params = new JSONObject();
			ServerInterface si;

			params.put("kundeId", mKundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					Log.i("Alle Export DATEN", result.toString());

					JSONArray wd = result.getJSONArray("data");

					for (int i = 0; i < wd.length(); i++) {
						wohnformList.add(wd.getString(i));
					}
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method stub
				}
			});
			si.call("exportWohnform", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ----------------------------------------------------------------------------------//
		// exportWohninformation
		// ----------------------------------------------------------------------------------//

		try {
			JSONObject params = new JSONObject();
			ServerInterface si;

			params.put("kundeId", mKundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					Log.i("Alle Export DATEN", result.toString());

					stockwerke = result.getJSONObject("data").getString("stockwerke");
					wohnflaeche = result.getJSONObject("data").getString("wohnflaeche");
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method stub
				}
			});
			si.call("exportWohninformation", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ----------------------------------------------------------------------------------//
		// exportWohnumfeld
		// ----------------------------------------------------------------------------------//
		try {
			JSONObject params = new JSONObject();
			ServerInterface si;

			params.put("kundeId", mKundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					Log.i("Alle Export DATEN", result.toString());

					JSONArray wd = result.getJSONArray("data");

					for (int i = 0; i < wd.length(); i++) {
						wohnumfeldList.add(wd.getString(i));
					}
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method stub
				}
			});
			si.call("exportWohnumfeld", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ----------------------------------------------------------------------------------//
		// exportWohnraeume
		// ----------------------------------------------------------------------------------//
		try {
			JSONObject params = new JSONObject();
			ServerInterface si;

			params.put("kundeId", mKundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					Log.i("Alle Export DATEN", result.toString());

					JSONArray wd = result.getJSONArray("data");

					for (int i = 0; i < wd.length(); i++) {
						wohnraeumeList.add(wd.getString(i));
					}
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method stub
				}
			});
			si.call("exportWohnraeume", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ----------------------------------------------------------------------------------//
		// exportWohnbarrieren
		// ----------------------------------------------------------------------------------//
		try {
			JSONObject params = new JSONObject();
			ServerInterface si;

			params.put("kundeId", mKundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					Log.i("Alle Export DATEN", result.toString());

					JSONArray wd = result.getJSONArray("data");

					for (int i = 0; i < wd.length(); i++) {
						wohnbarrierenList.add(wd.getString(i));
					}
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method stub
				}
			});
			si.call("exportWohnbarrieren", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void exportGesundheitsdaten() {

		// ----------------------------------------------------------------------------------//
		// exportErkrankungen
		// ----------------------------------------------------------------------------------//
		try {
			JSONObject params = new JSONObject();
			ServerInterface si;

			params.put("kundeId", mKundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					Log.i("Alle Export DATEN", result.toString());

					JSONArray wd = result.getJSONArray("data");

					for (int i = 0; i < wd.length(); i++) {
						erkrankungenList.add(wd.getString(i));
					}
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method stub
				}
			});
			si.call("exportErkrankungen", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ----------------------------------------------------------------------------------//
		// exportPflege
		// ----------------------------------------------------------------------------------//
		try {
			JSONObject params = new JSONObject();
			ServerInterface si;

			params.put("kundeId", mKundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					Log.i("Alle Export DATEN", result.toString());

					JSONArray wd = result.getJSONArray("data");

					for (int i = 0; i < wd.length(); i++) {
						pflegeList.add(wd.getString(i));
					}
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method stub
				}
			});
			si.call("exportPflege", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ----------------------------------------------------------------------------------//
		// exportAlltagskompetenzen
		// ----------------------------------------------------------------------------------//
		try {
			JSONObject params = new JSONObject();
			ServerInterface si;

			params.put("kundeId", mKundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					Log.i("Alle Export DATEN", result.toString());

					JSONArray wd = result.getJSONArray("data");

					for (int i = 0; i < wd.length(); i++) {
						alltagskompetenzenList.add(wd.getString(i));
					}
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method stub
				}
			});
			si.call("exportAlltagskompetenzen", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ----------------------------------------------------------------------------------//
		// exportBeobachtungen
		// ----------------------------------------------------------------------------------//
		try {
			JSONObject params = new JSONObject();
			ServerInterface si;

			params.put("kundeId", mKundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					Log.i("Alle Export DATEN", result.toString());

					JSONArray wd = result.getJSONArray("data");

					for (int i = 0; i < wd.length(); i++) {
						beobachtungenList.add(wd.getString(i));
					}
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method stub
				}
			});
			si.call("exportBeobachtungen", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void exportSzenariendaten() {

		// ----------------------------------------------------------------------------------//
		// exportGeraeteeinstellungen
		// ----------------------------------------------------------------------------------//
		try {
			JSONObject params = new JSONObject();
			ServerInterface si;

			params.put("kundeId", mKundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					Log.i("Alle Geraete DATEN", result.toString());

					JSONArray wd = result.getJSONArray("data");

					for (int i = 0; i < wd.length(); i++) {
						geraeteeinstellungenList.add(wd.getString(i));
					}
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method stub
				}
			});
			si.call("exportGeraeteeinstellungen", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ----------------------------------------------------------------------------------//
		// exportLichtart
		// ----------------------------------------------------------------------------------//
		try {
			JSONObject params = new JSONObject();
			ServerInterface si;

			params.put("kundeId", mKundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					Log.i("Alle Lichtart DATEN", result.toString());

					JSONArray wd = result.getJSONArray("data");

					for (int i = 0; i < wd.length(); i++) {
						lichtartList.add(wd.getString(i));
					}
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method stub
				}
			});
			si.call("exportLichtart", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ----------------------------------------------------------------------------------//
		// exportLichtfarbe
		// ----------------------------------------------------------------------------------//
		try {
			JSONObject params = new JSONObject();
			ServerInterface si;

			params.put("kundeId", mKundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					Log.i("Alle Lichtfarbe DATEN", result.toString());

					JSONArray wd = result.getJSONArray("data");

					for (int i = 0; i < wd.length(); i++) {
						lichtfarbeList.add(wd.getString(i));
					}
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method stub
				}
			});
			si.call("exportLichtfarbe", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ----------------------------------------------------------------------------------//
		// exportLichtstaerke
		// ----------------------------------------------------------------------------------//
		try {
			JSONObject params = new JSONObject();
			ServerInterface si;

			params.put("kundeId", mKundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					Log.i("Alle Lichtstaerke DATEN", result.toString());

					JSONArray wd = result.getJSONArray("data");

					for (int i = 0; i < wd.length(); i++) {
						lichtstaerkeList.add(wd.getString(i));
					}
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method stub
				}
			});
			si.call("exportLichtstaerke", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ----------------------------------------------------------------------------------//
		// exportDienstleister
		// ----------------------------------------------------------------------------------//
		try {
			JSONObject params = new JSONObject();
			ServerInterface si;

			params.put("kundeId", mKundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					Log.i("Alle Dienstleister DATEN", result.toString());

					JSONArray wd = result.getJSONArray("data");

					for (int i = 0; i < wd.length(); i++) {
						dienstleisterList.add(wd.getString(i));
					}
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method stub
				}
			});
			si.call("exportDienstleister", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void exportExportdaten() {

		// ----------------------------------------------------------------------------------//
		// gibKundeExportdaten
		// ----------------------------------------------------------------------------------//
		try {
			JSONObject params = new JSONObject();
			ServerInterface si;

			params.put("kundeId", mKundeId);

			si = new ServerInterface();
			si.addListener(new ServerInterfaceListener() {

				public void serverSuccessHandler(JSONObject result) throws JSONException {

					Log.i("Alle Export DATEN", result.toString());

					JSONArray wd = result.getJSONArray("data");

					for (int i = 0; i < wd.length(); i++) {
						exportdatenList.add(wd.getString(i));
					}
					createDocument();
				}

				public void serverErrorHandler(Exception e) {
					// TODO Auto-generated method stub
				}
			});
			si.call("gibKundeExportdaten", params);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
