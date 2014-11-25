package com.zengyan.mobilesafe.db;

import java.util.ArrayList;
import java.util.List;

import com.zengyan.mobilesafe.model.BlackNumberInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * ���������ݿ����ɾ�Ĳ�ҵ����
 * @author Administrator
 *
 */
public class AppLockDao {
	private AppLockDBOpenHelper helper;
	/**
	 * ���췽��
	 * @param context ������
	 */
	public AppLockDao(Context context) {
		helper = new AppLockDBOpenHelper(context);
	}
	
	public void add(String packname){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packname", packname);
		db.insert("applock", null, values);
		db.close();
	}
	
	public void delete(String packname){
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("applock", "packname=?", new String[]{packname});
		db.close();
	}
	
	public boolean find(String packname){
		boolean result=false;
		SQLiteDatabase db=helper.getReadableDatabase();
		Cursor cursor=db.query("applock", null, "packname=?", new String[]{packname}, null, null,null);
		if (cursor.moveToNext()) {
			result=true;
		}
		cursor.close();
		db.close();
		return result;
		
	}
	
}