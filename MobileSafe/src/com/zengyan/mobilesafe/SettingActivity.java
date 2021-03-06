package com.zengyan.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.zengyan.mobilesafe.service.AddressService;
import com.zengyan.mobilesafe.service.CallSmsSafeService;
import com.zengyan.mobilesafe.service.WatchDogService;
import com.zengyan.mobilesafe.ui.SettingClickView;
import com.zengyan.mobilesafe.ui.SettingItem;
import com.zengyan.mobilesafe.utils.ServiceUtils;

public class SettingActivity extends Activity {
	private SettingItem siv_update;
	private SharedPreferences sp;

	// 设置是否开启显示归属地
	private SettingItem siv_show_address;
	private Intent showAddress;

	// 黑名单拦截设置
	private SettingItem siv_callsms_safe;
	private Intent callSmsSafeIntent;

	// 黑名单拦截设置
	private SettingItem siv_watchdog;
	private Intent callWatchDog;
	// 设置归属地显示框背景
	private SettingClickView scv_changebg;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	
		boolean isServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this,
				"com.zengyan.mobilesafe.service.AddressService");

		if (isServiceRunning) {
			// 监听来电的服务是开启的
			siv_show_address.setChecked(true);
		} else {
			siv_show_address.setChecked(false);
		}

		boolean iscallSmsServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this,
				"com.zengyan.mobilesafe.service.CallSmsSafeService");
		siv_callsms_safe.setChecked(iscallSmsServiceRunning);

		boolean isWatchDogRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this,
				"com.zengyan.mobilesafe.service.WatchDogService");
		siv_watchdog.setChecked(isWatchDogRunning);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		siv_update = (SettingItem) findViewById(R.id.siv_update);

		boolean update = sp.getBoolean("update", false);
		if (update) {
			// 自动升级已经开启
			siv_update.setChecked(true);
		} else {
			// 自动升级已经关闭
			siv_update.setChecked(false);
		}
		siv_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				// 判断是否有选中
				// 已经打开自动升级了
				if (siv_update.isChecked()) {
					siv_update.setChecked(false);
					editor.putBoolean("update", false);

				} else {
					// 没有打开自动升级
					siv_update.setChecked(true);
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});

		// 设置号码归属地显示空间
		siv_show_address = (SettingItem) findViewById(R.id.siv_show_address);
		showAddress = new Intent(this, AddressService.class);
		siv_show_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_show_address.isChecked()) {
					// 变为非选中状态
					siv_show_address.setChecked(false);
					stopService(showAddress);

				} else {
					// 选择状态
					siv_show_address.setChecked(true);
					startService(showAddress);

				}

			}
		});
		// 黑名单拦截设置
		siv_callsms_safe = (SettingItem) findViewById(R.id.siv_callsms_safe);
		callSmsSafeIntent = new Intent(this, CallSmsSafeService.class);
		siv_callsms_safe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_callsms_safe.isChecked()) {
					// 变为非选中状态
					siv_callsms_safe.setChecked(false);
					stopService(callSmsSafeIntent);
					Log.i("ZENG", "Stop CallSmsSafeService");
				} else {
					// 选择状态
					siv_callsms_safe.setChecked(true);
					Log.i("ZENG", "Start CallSmsSafeService");
					startService(callSmsSafeIntent);
				}

			}
		});
		// 设置号码归属地显示空间
				siv_watchdog = (SettingItem) findViewById(R.id.siv_watchdog);
				callWatchDog = new Intent(this, WatchDogService.class);
				siv_watchdog.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (siv_watchdog.isChecked()) {
							// 变为非选中状态
							siv_watchdog.setChecked(false);
							stopService(callWatchDog);

						} else {
							// 选择状态
							siv_watchdog.setChecked(true);
							startService(callWatchDog);

						}

					}
				});
		// 设置号码归属地的背景
		scv_changebg = (SettingClickView) findViewById(R.id.scv_changebg);
		scv_changebg.setTitle("归属地提示框风格");
		final String[] items = { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };
		int which = sp.getInt("which", 0);
		scv_changebg.setDesc(items[which]);

		scv_changebg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int dd = sp.getInt("which", 0);
				// 弹出一个对话框
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("归属地提示框风格");
				builder.setSingleChoiceItems(items, dd,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								// 保存选择参数
								Editor editor = sp.edit();
								editor.putInt("which", which);
								editor.commit();
								scv_changebg.setDesc(items[which]);

								// 取消对话框
								dialog.dismiss();
							}
						});
				builder.setNegativeButton("cancel", null);
				builder.show();

			}
		});

	}

}
