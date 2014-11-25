package com.zengyan.mobilesafe.receiver;
import com.zengyan.mobilesafe.service.UpdateWidgetService;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
public class MyWidget extends AppWidgetProvider {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context,UpdateWidgetService.class);
		context.startService(i);
		super.onReceive(context, intent);
	}
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	@Override
	public void onEnabled(Context context) {
		Intent intent = new Intent(context,UpdateWidgetService.class);
		context.stopService(intent);
		super.onEnabled(context);
	}
	@Override
	public void onDisabled(Context context) {
		Intent intent = new Intent(context,UpdateWidgetService.class);
		context.stopService(intent);
		Log.i("ZENG", "onDisabled");
		super.onDisabled(context);
	}
}





