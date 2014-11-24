package com.zengyan.mobilesafe.service;
import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.zengyan.mobilesafe.R;
import com.zengyan.mobilesafe.receiver.MyWidget;
import com.zengyan.mobilesafe.utils.SystemInfoUtils;
/**
 * 锁屏服务
 *
 */
public class UpdateWidgetService extends Service {
	private ScreenOffReceiver offreceiver;
	private ScreenOnReceiver onreceiver;
	private Timer timer;
	private TimerTask task;
	//wedget的管理器
	private AppWidgetManager awm;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	//锁屏
	private class ScreenOffReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			stopTimer();
		}
	}
	//解锁
	private class ScreenOnReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			startTimer();
		}
	}
	@Override
	public void onCreate() {
		onreceiver = new ScreenOnReceiver();
		offreceiver = new ScreenOffReceiver();
		registerReceiver(onreceiver,new IntentFilter(Intent.ACTION_SCREEN_ON));
		registerReceiver(offreceiver,new IntentFilter(Intent.ACTION_SCREEN_OFF));
		awm = AppWidgetManager.getInstance(this);
		startTimer();
		Log.i("ZENG", "onCreate==");
		super.onCreate();
	}
	//开启
	private void startTimer(){
		if(timer == null && task == null){
			timer = new Timer();
			task = new TimerTask() {
				@Override
				public void run() {
					Log.i("ZENG", "更新widget");
					//设置更新的组件
					ComponentName provider = new ComponentName(UpdateWidgetService.this,MyWidget.class);
					RemoteViews views = new RemoteViews(getPackageName(),R.layout.process_widget);
					views.setTextViewText(R.id.process_count,"正在运行的进程:"+SystemInfoUtils.getRunningProcessCount(getApplicationContext())+"个");
					views.setTextViewText(R.id.process_memory,"可用内存:"+(SystemInfoUtils.getAvailRam(getApplicationContext())/1024/1024)+"MB");
					// 描述一个动作,这个动作是由另外的一个应用程序执行的.
					// 自定义一个广播事件,杀死后台进度的事件
					Intent intent = new Intent();
					intent.setAction("com.zengyan.mobilesafe.killall");
					PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
					views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
					awm.updateAppWidget(provider, views);
				}
			};
			timer.schedule(task,0,3000);
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(offreceiver);
		unregisterReceiver(onreceiver);
		offreceiver = null;
		onreceiver = null;
		stopTimer();
	}
	
	private void stopTimer() {
		if(timer != null && task != null){
			timer.cancel();
			task.cancel();
			timer = null;
			task = null;
		}
	}
}




















