package com.zengyan.mobilesafe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zengyan.mobilesafe.utils.SmsUtils;
import com.zengyan.mobilesafe.utils.SmsUtils.BackUpCallBack;
import com.zengyan.mobilesafe.utils.SmsUtils.RestoreBack;

public class AtoolsActivity extends Activity {
	ProgressDialog pbBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}

	/**
	 * 点击事件，进入号码归属地查询的页面
	 * 
	 * @param view
	 */
	public void numberQuery(View view) {
		Intent intentv = new Intent(this, NumberAddressQueryActivity.class);
		startActivity(intentv);

	}

	public void smsBackup(View view) {

		pbBar=new ProgressDialog(this);
		pbBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pbBar.setMessage("正在备份中...");
		pbBar.show();
		new Thread() {

			public void run() {
				try {
					SmsUtils.backupSms(AtoolsActivity.this,new BackUpCallBack() {
						
						@Override
						public void onSmsBackup(int process) {
							// TODO Auto-generated method stub
							pbBar.setProgress(process);
						}
						
						@Override
						public void beforeBackup(int max) {
							// TODO Auto-generated method stub
							pbBar.setMax(max);
						}
					});
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AtoolsActivity.this, "备份成功", 0).show();
						}
					});
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AtoolsActivity.this, "备份失败", 0).show();
						}
					});
				}finally{
					pbBar.dismiss();
				}
			}

		}.start();

		
	}
	
	public void smsResotore(View view) {

		pbBar=new ProgressDialog(this);
		pbBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pbBar.setMessage("正在还原中...");
		pbBar.show();
		new Thread() {

			public void run() {
				try {
					SmsUtils.restoreSms(AtoolsActivity.this,true,new RestoreBack() {

						@Override
						public void beforeRestore(int max) {
							// TODO Auto-generated method stub
							pbBar.setMax(max);
						}

						@Override
						public void onSmsRestore(int process) {
							// TODO Auto-generated method stub
							pbBar.setProgress(process);
						}
						
						
					});
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AtoolsActivity.this, "还原成功", 0).show();
						}
					});
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(AtoolsActivity.this, "还原失败", 0).show();
						}
					});
				}finally{
					pbBar.dismiss();
				}
			}

		}.start();
		

		
	}

}
