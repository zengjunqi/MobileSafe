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
		//��ȡ֮ǰ����SIM��Ϣ�뵱ǰSIM����Ϣ,�Ƚ��Ƿ�һ��
		sp=arg0.getSharedPreferences("config", Context.MODE_PRIVATE);
		String saveSimString=sp.getString("sim", "");
		tManager=(TelephonyManager) arg0.getSystemService(Context.TELEPHONY_SERVICE);
		
		String realSimString=tManager.getSimSerialNumber();
		if (!saveSimString.equals(realSimString)) {//���SIM����ͬ,���Ͷ��Ÿ���ȫ����
			SmsManager.getDefault().sendTextMessage(sp.getString("phone", ""), null, "sim changed.....", null, null);
		}
	}

}