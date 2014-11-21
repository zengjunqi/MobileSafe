package com.zengyan.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class BootCompleteReceiver extends BroadcastReceiver {

	private SharedPreferences sp;
	private TelephonyManager tManager;

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		// 读取之前保存SIM信息与当前SIM卡信息,比较是否一样
		sp = arg0.getSharedPreferences("config", Context.MODE_PRIVATE);
		boolean protecting = sp.getBoolean("protecting", false);
		if (protecting) {
			String saveSimString = sp.getString("sim", "");
			tManager = (TelephonyManager) arg0
					.getSystemService(Context.TELEPHONY_SERVICE);

			String realSimString = tManager.getSimSerialNumber();
			if (!saveSimString.equals(realSimString)) {// 如果SIM卡不同,发送短信给安全号码
				SmsManager.getDefault().sendTextMessage(
						sp.getString("phone", ""), null, "sim changed.....",
						null, null);
			}
		}
	}

}
