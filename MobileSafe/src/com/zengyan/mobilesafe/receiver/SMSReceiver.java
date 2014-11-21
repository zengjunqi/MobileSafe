package com.zengyan.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.zengyan.mobilesafe.R;
import com.zengyan.mobilesafe.service.GPSService;

public class SMSReceiver extends BroadcastReceiver {

	private static final String TAG = "ZENG";
	private SharedPreferences sp;
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		Object[] objs = (Object[]) arg1.getExtras().get("pdus");
		sp = arg0.getSharedPreferences("config", Context.MODE_PRIVATE);
		for (Object object : objs) {
			SmsMessage sms =SmsMessage.createFromPdu((byte[]) object);
			//发送者
			String sender = sms.getOriginatingAddress();//15555555556
			String safenumber = sp.getString("phone", "");//5556
			//5556
			///1559999995556
//			Toast.makeText(context, sender, 1).show();
			Log.i(TAG, "====sender=="+sender);
			String body = sms.getMessageBody();
			
			if(sender.contains(safenumber)){
				
				if("#*location*#".equals(body)){
					//得到手机的GPS
					Log.i(TAG, "得到手机的GPS");
				//启动服务
					Intent i = new Intent(arg0,GPSService.class);
					arg0.startService(i);
					SharedPreferences sp = arg0.getSharedPreferences("config", Context.MODE_PRIVATE);
					String lastlocation = sp.getString("lastlocation", null);
					if(TextUtils.isEmpty(lastlocation)){
						//位置没有得到
						SmsManager.getDefault().sendTextMessage(sender, null, "geting loaction.....", null, null);
					}else{
						SmsManager.getDefault().sendTextMessage(sender, null, lastlocation, null, null);
					}
					
					
					//把这个广播终止掉
					abortBroadcast();
				}else if("#*alarm*#".equals(body)){
					//播放报警影音
					Log.i(TAG, "播放报警影音");
					MediaPlayer player = MediaPlayer.create(arg0, R.raw.ylzs);
					player.setLooping(false);//
					player.setVolume(1.0f, 1.0f);
					player.start();
					
					abortBroadcast();
				}
				else if("#*wipedata*#".equals(body)){
					//远程清除数据
					Log.i(TAG, "远程清除数据");
					abortBroadcast();
				}
				else if("#*lockscreen*#".equals(body)){
					//远程锁屏
					Log.i(TAG, "远程锁屏");
					abortBroadcast();
				}
			}
			

		}
	}

}
