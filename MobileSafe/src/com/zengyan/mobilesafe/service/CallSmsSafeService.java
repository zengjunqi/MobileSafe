package com.zengyan.mobilesafe.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.zengyan.mobilesafe.db.BlackNumberDao;

import android.R.integer;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallSmsSafeService extends Service {
	public static final String TAG = "ZENG";
	private InnerSmsReceiver receiver;
	private BlackNumberDao dao;
	private TelephonyManager tm;
	private MyListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class InnerSmsReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "�ڲ��㲥�����ߣ� ���ŵ�����");
			// ��鷢�����Ƿ��Ǻ��������룬���ö�������ȫ�����ء�
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for (Object obj : objs) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
				// �õ����ŷ�����
				String sender = smsMessage.getOriginatingAddress();
				String result = dao.findMode(sender);
				if ("2".equals(result) || "3".equals(result)) {
					Log.i(TAG, "���ض�����");
					abortBroadcast();
				}
				// ��ʾ���롣
				String body = smsMessage.getMessageBody();
				if (body.contains("fapiao")) {
					// ���ͷ��Ʊ���ĺ� ���Էִʼ�����
					Log.i(TAG, "���ط�Ʊ����");
					abortBroadcast();
				}
			}
		}
	}

	@Override
	public void onCreate() {
		Log.i("ZENG", "CallSmsSafeService onCreate");
		dao = new BlackNumberDao(this);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		receiver = new InnerSmsReceiver();

		// ����ע��㲥������
		IntentFilter filter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(receiver, filter);
		super.onCreate();

		Log.i("ZENG", "CallSmsSafeService Start");
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		receiver = null;
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		super.onDestroy();
	}

	private class MyListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// ����״̬��
				String result = dao.findMode(incomingNumber);
				Log.i(TAG, incomingNumber + "==" + result);
				if ("1".equals(result) || "3".equals(result)) {
					Log.i(TAG, "�Ҷϵ绰��������");
					Uri uri = Uri.parse("content://call_log/calls");
					getContentResolver().registerContentObserver(uri, true,
							new CallLogObserver(new Handler(),incomingNumber));
					endCall();// Զ�̷���ִ��,����ͬһ����, ���������ݹ۲��߹۲�Call log�������ݱ仯 
					// ɾ�����м�¼
					// deleteCallLog(incomingNumber);
				
				}
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

	private class CallLogObserver extends ContentObserver {

		private String incomingNumber;
		public CallLogObserver(Handler handler,String incomingNumber) {
			super(handler);
			// TODO Auto-generated constructor stub
			this.incomingNumber=incomingNumber;
		}

		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			getContentResolver().unregisterContentObserver(this);
			deleteCallLog(incomingNumber);
			super.onChange(selfChange);
		}

	}

	public void endCall() {
		// IBinder iBinder = ServiceManager.getService(TELEPHONY_SERVICE);
		try {
			// ����servicemanager���ֽ���
			Class clazz = CallSmsSafeService.class.getClassLoader().loadClass(
					"android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class);
			IBinder ibinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
			ITelephony.Stub.asInterface(ibinder).endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteCallLog(String incomingNumber) {
		// TODO Auto-generated method stub
		ContentResolver resolver = getContentResolver();
		Uri uri = Uri.parse("content://call_log/calls");
		resolver.delete(uri, "number=?", new String[] { incomingNumber });
	}
}