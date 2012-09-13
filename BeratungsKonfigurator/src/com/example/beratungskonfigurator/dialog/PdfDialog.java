package com.example.beratungskonfigurator.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.beratungskonfigurator.R;

public class PdfDialog extends Dialog {

	private ProgressDialog pDialog;
	private ListView pdfList;
	
	private List<String> fileListName = new ArrayList<String>();
	private List<String> fileListPath = new ArrayList<String>();

	// Constructor
	public PdfDialog(Context context) {
		super(context);
		
		setContentView(R.layout.pdf_dialog_layout);

		pDialog = new ProgressDialog(context);
		pDialog.setMessage("Lade Daten!");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);

		WindowManager.LayoutParams paramsLayout = getWindow().getAttributes();

		paramsLayout.x = 0;
		paramsLayout.y = 0;
		this.getWindow().setAttributes(paramsLayout);

		setCancelable(true);

		String FOLDER = "/pdf";
		File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+FOLDER);
		Log.i("FILE PATH", "Path: "+ root);
		ListDir(root);


		pdfList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

				File selectedFile = new File(fileListPath.get(position));
		        if (selectedFile.exists()) {
		            Uri path = Uri.fromFile(selectedFile);
		            Intent intent = new Intent(Intent.ACTION_VIEW);
		            intent.setDataAndType(path, "application/pdf");
		            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		            getContext().startActivity(intent);
		        }

			}

		});

	}

	void ListDir(File f) {
		File[] files = f.listFiles();
		fileListName.clear();
		fileListPath.clear();
		for (File file : files) {
			fileListName.add(file.getName());
			fileListPath.add(file.getPath());
		}

		ArrayAdapter<String> directoryList = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, fileListName);
		pdfList = (ListView) findViewById(R.id.pdfList);
		pdfList.setAdapter(directoryList);
	}

}
