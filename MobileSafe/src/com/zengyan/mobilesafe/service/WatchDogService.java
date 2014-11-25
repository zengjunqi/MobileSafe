package com.zengyan.mobilesafe.service;

import java.util.List;

import com.zengyan.mobilesafe.EnterPwdActivity;
import com.zengyan.mobilesafe.db.AppLockDao;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class WatchDogService extends Service {

	private ActivityManager activityManager;
	private boolean flag = true;
	private AppLockDao helper;
	private InnerReceiver receiver;
	private String tempStopPackname;
	private ScreenOffReceiver receiveroffOff;
	private ScreenOnReceiver receiveroffon;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private class InnerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			tempStopPackname = intent.getStringExtra("packname");
			System.out.println("==接收到了广播:" + tempStopPackname);
		}
	}
	//屏幕锁屏
	private class ScreenOffReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			tempStopPackname=null;
			flag=false;
			System.out.println("flag:false");
		}
		
	}
	//屏幕锁屏
	private class ScreenOnReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			flag=true;
			monitor();
			System.out.println("flag:true");
		}
		
	}
	@Override
	public void onCreate() {

		receiver = new InnerReceiver();
		registerReceiver(receiver, new IntentFilter(
				"com.zengyan.mobilesafe.tempstop"));
		
		receiveroffOff=new ScreenOffReceiver();
		registerReceiver(receiveroffOff, new IntentFilter(
				Intent.ACTION_SCREEN_OFF));
		
		receiveroffon=new ScreenOnReceiver();
		registerReceiver(receiveroffon, new IntentFilter(
				Intent.ACTION_SCREEN_ON));
		
		activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		helper = new AppLockDao(this);

		monitor();
		super.onCreate();
	}

	private void monitor() {
		new Thread() {
			public void run() {
				while (flag) {
					// 最近打开的任务栈在最前面
					List<RunningTaskInfo> infos = activityManager
							.getRunningTasks(1);
					String packname = infos.get(0).topActivity.getPackageName();
					System.out.println("packname:" + packname);
					if (helper.find(packname)) {
						// 
						if (!packname.equals(tempStopPackname)) {

							Intent intent = new Intent();
							// 服务是没有任务栈信息的,在服务开启activity,要指定这个activity动行的任务栈
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("packname", packname);
							intent.setClass(getApplicationContext(),
									EnterPwdActivity.class);
							startActivity(intent);
						}
					}

					try {
						sleep(100);
					} catch (Exception e) {
						// TODO: handle exception
					}

				}

			};

		}.start();
	}

	@Override
	public void onDestroy() {
		flag = false;
		unregisterReceiver(receiver);
		receiver=null;
		unregisterReceiver(receiveroffOff);
		receiveroffOff=null;
		unregisterReceiver(receiveroffon);
		receiveroffon=null;
		super.onDestroy();
	}

}
