package com.example.beratungskonfigurator;

import com.example.beratungskonfigurator.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
	}

	public void onButtonClick(View view) {
		switch (view.getId()) {
		case R.id.btn_nberatung:
			startActivity(new Intent(this, TabActivity.class));
			break;
		case R.id.btn_fberatung:
			startActivity(new Intent(this, TabActivity.class));
			break;
		}
	}
}
