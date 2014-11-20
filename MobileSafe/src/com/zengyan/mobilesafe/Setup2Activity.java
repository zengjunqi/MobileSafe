package com.zengyan.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.zengyan.mobilesafe.ui.SettingItem;

public class Setup2Activity extends BaseSetupActivity {
	private SettingItem siv_setup2_sim;
	private TelephonyManager tmManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		siv_setup2_sim = (SettingItem) findViewById(R.id.siv_setup2_sim);
		tmManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

		String simno = sp.getString("sim", null);
		if (TextUtils.isEmpty(simno)) {
			siv_setup2_sim.setChecked(false);
		} else {
			siv_setup2_sim.setChecked(true);
		}

		siv_setup2_sim.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String sim = tmManager.getSimSerialNumber();
				Editor editor = sp.edit();
				if (siv_setup2_sim.isChecked()) {
					editor.putString("sim", null);
					siv_setup2_sim.setChecked(false);
				} else {
					editor.putString("sim", sim);
					siv_setup2_sim.setChecked(true);
				}
				editor.commit();
			}
		});
	}

	@Override
	public void showNext() {
		String sim=sp.getString("sim", null);
		if (TextUtils.isEmpty(sim)) {
			Toast.makeText(this, "Sim¿¨Ã»ÓÐ°ó¶¨.", 0).show();
			return;
		}
		
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);

	}

	@Override
	public void showPre() {
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);

	}

}
