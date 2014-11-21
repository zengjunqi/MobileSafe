package com.zengyan.mobilesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.R.integer;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

public class SmsUtils {

	public interface BackUpCallBack {
		public void beforeBackup(int max);

		public void onSmsBackup(int process);
	}

	public static void backupSms(Context context, BackUpCallBack backUpCallBack)
			throws Exception {
		ContentResolver resolver = context.getContentResolver();
		File file = new File(Environment.getExternalStorageDirectory(),
				"backup.xml");
		if (file.exists()) {
			file.delete();
		}
		FileOutputStream fos = new FileOutputStream(file);

		XmlSerializer serializer = Xml.newSerializer();// 序列化器
		serializer.setOutput(fos, "utf-8");

		serializer.startDocument("utf-8", true);

		serializer.startTag(null, "smss");

		Uri uri = Uri.parse("content://sms/");

		Cursor cursor = resolver.query(uri, new String[] { "body", "address",
				"type", "date" }, null, null, null);
		// pd.setMax(cursor.getCount());
		int max = cursor.getCount();
		backUpCallBack.beforeBackup(max);
		serializer.attribute(null, "max", max + "");
		int process = 0;
		while (cursor.moveToNext()) {
			String body = cursor.getString(0);
			String address = cursor.getString(1);
			String type = cursor.getString(2);
			String date = cursor.getString(3);

			serializer.startTag(null, "sms");
			serializer.startTag(null, "body");
			serializer.text(body);
			serializer.endTag(null, "body");

			serializer.startTag(null, "address");
			serializer.text(address);
			serializer.endTag(null, "address");

			serializer.startTag(null, "type");
			serializer.text(type);
			serializer.endTag(null, "type");

			serializer.startTag(null, "date");
			serializer.text(date);
			serializer.endTag(null, "date");

			serializer.endTag(null, "sms");
			process++;
			// pd.setProgress(process);
			backUpCallBack.onSmsBackup(process);
			// Thread.sleep(500);
		}

		serializer.endTag(null, "smss");
		serializer.endDocument();
		fos.close();
		cursor.close();
	}

	public interface RestoreBack {
		public void beforeRestore(int max);

		public void onSmsRestore(int process);
	}

	public static void restoreSms(Context context, boolean flag,
			RestoreBack callBack) throws Exception {

		Uri uri = Uri.parse("content://sms/");

		File file = new File(Environment.getExternalStorageDirectory(),
				"backup.xml");
		if (file.exists()) {
			if (flag) {
				context.getContentResolver().delete(uri, null, null);
			}
			FileInputStream iStream = new FileInputStream(file);
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(iStream, "utf-8");
			int max;
			String body = "", address = "", type = "", date = "";
			int etype = parser.getEventType();
			int process=0;
			while (etype != XmlPullParser.END_DOCUMENT) {
				String node = parser.getName();
				switch (etype) {
				case XmlPullParser.START_DOCUMENT://文档开始,并不是根节点
					
					break;

				case XmlPullParser.START_TAG:
					if (node.equals("smss")) {
						max = Integer.parseInt(parser
								.getAttributeValue(null, "max"));
						callBack.beforeRestore(max);
					}else
					if (node.equals("sms")) {
						callBack.onSmsRestore(process);
						process++;
					} else if (node.equals("body")) {
						body = parser.nextText();
					} else if (node.equals("address")) {
						address = parser.nextText();
					} else if (node.equals("type")) {
						type = parser.nextText();
					} else if (node.equals("date")) {
						date = parser.nextText();
					}

					break;

				case XmlPullParser.END_TAG:
					if (node.equals("sms")) {
						ContentValues contentValues = new ContentValues();
						contentValues.put("body", body);
						contentValues.put("address", address);
						contentValues.put("type", type);
						contentValues.put("date", date);
						context.getContentResolver().insert(uri, contentValues);
					}
					break;

				default:
					break;
				}
				etype = parser.next();
			}
			iStream.close();
		}

	}
}
