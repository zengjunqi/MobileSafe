package com.zengyan.mobilesafe.unittest;

import com.zengyan.mobilesafe.db.BlackNumberDao;

import android.test.AndroidTestCase;

public class DBTest extends AndroidTestCase {

	public void add() {
		
		BlackNumberDao helper=new BlackNumberDao(getContext());
		String j="13500000";
		for (int i = 0; i <100; i++) {
			helper.add(j+""+i, "3");
		}
	
		
	}
}
