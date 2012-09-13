package com.example.beratungskonfigurator.tabs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.example.beratungskonfigurator.ExportPdfExample;
import com.example.beratungskonfigurator.MainActivity;
import com.example.beratungskonfigurator.R;
import com.example.beratungskonfigurator.dialog.PdfDialog;
import com.example.beratungskonfigurator.dialog.ExportDatenDialog;

public class ExportActivity extends Fragment {

	Button datenPdf;
	Button createPdf;
	Button sendPdf;
	Button openPdf;
	Spinner selectPdf;
	EditText emailAdress;
	private int kundeId;
	private int angehoerigerId;
	
	private List<String> fileListName = new ArrayList<String>();
	private List<String> fileListPath = new ArrayList<String>();

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final View exportView = (View) inflater.inflate(R.layout.tab_export_layout, container, false);
		
		kundeId = this.getArguments().getInt("sendKundeId");
		angehoerigerId = this.getArguments().getInt("sendAngehoerigerId");
		
		datenPdf = (Button) exportView.findViewById(R.id.datenPdf);
		createPdf = (Button) exportView.findViewById(R.id.createPdf);
		sendPdf = (Button) exportView.findViewById(R.id.sendPdf);
		openPdf = (Button) exportView.findViewById(R.id.openPdf);
		selectPdf = (Spinner) exportView.findViewById(R.id.spinnerPdf);
		emailAdress = (EditText) exportView.findViewById(R.id.editMail);
		
		String FOLDER = "/pdf";
		File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+FOLDER);
		Log.i("FILE PATH", "Path: "+ root);
		ListDir(root);
		
		datenPdf.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				ExportDatenDialog customDatenDialog = new ExportDatenDialog(getActivity());
				customDatenDialog.setTitle("Daten auswählen");
				customDatenDialog.show();
			}
		});

		createPdf.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				ExportPdfExample exportPDF = new ExportPdfExample();
				exportPDF.main(null, kundeId, angehoerigerId);
			}
		});

		openPdf.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				PdfDialog customPdfDialog = new PdfDialog(getActivity());
				customPdfDialog.setTitle("PDF auswählen");
				customPdfDialog.show();

			}
		});

		sendPdf.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				if(isEmailValid(emailAdress.getText().toString())){
					String pathtoPdf = fileListPath.get(selectPdf.getSelectedItemPosition());
					
					final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

					emailIntent.setType("plain/text");

					emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {emailAdress.getText().toString()});

					emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "[KONGA home] Beratungsübersicht");
					
					emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+ pathtoPdf));

					startActivity(Intent.createChooser(emailIntent, "E-Mail versenden!"));
					
				}else{
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setMessage(
							"Bitte geben Sie eine valide E-Mail Adresse an!")
							.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.dismiss();
								}
							});
					AlertDialog alert = builder.create();
					alert.show();
				}
				
		

			}
		});

		return exportView;
	}
	
	void ListDir(File f) {
		File[] files = f.listFiles();
		fileListName.clear();
		fileListPath.clear();
		for (File file : files) {
			fileListName.add(file.getName());
			fileListPath.add(file.getPath());
		}

		ArrayAdapter<String> directoryList = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, fileListName);
		directoryList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		selectPdf.setAdapter(directoryList);
	}
	
	public static boolean isEmailValid(String email) {
	    boolean isValid = false;

	    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	    CharSequence inputStr = email;

	    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(inputStr);
	    if (matcher.matches()) {
	        isValid = true;
	    }
	    return isValid;
	}


}
