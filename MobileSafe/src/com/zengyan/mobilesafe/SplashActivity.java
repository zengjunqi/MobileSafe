package com.zengyan.mobilesafe;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SplashActivity extends ActionBarActivity {

	private TextView tvVersion;
	private PackageManager packageManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_activity);
		tvVersion=(TextView) findViewById(R.id.tv_version);
		tvVersion.setText("°æ±¾£º"+getVersion());
	}
	private String getVersion() {
		packageManager=getPackageManager();
		try {
		PackageInfo info=	packageManager.getPackageInfo(getPackageName(), 0);
		return info.versionName;
			
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	

}
