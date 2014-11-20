package com.zengyan.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Setup3Activity extends BaseSetupActivity {
	private EditText st3_phoneno;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		st3_phoneno=(EditText) findViewById(R.id.st3_phoneno);
		String phone = sp.getString("phone", null);
		if (!TextUtils.isEmpty(phone)) {
			st3_phoneno.setText(phone);
		} 
	}
	

	@Override
	public void showNext() {
		String phone=st3_phoneno.getText().toString();
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(this, "安全号码还没有设置.", 0).show();
			return;
		}
		Editor editor=sp.edit();
		editor.putString("phone", phone);
		editor.commit();
		Intent intent = new Intent(this,Setup4Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
		
		
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(this,Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
		
	}
	
	/**
	 * 选择联系人的点击事件
	 * @param view
	 */
	public void selectContact(View view){
		Intent intent = new Intent(this,SelectContactActivity.class);
		startActivityForResult(intent, 0);
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(data == null)
			return;
		
		String phone = data.getStringExtra("phone").replace("-", "");
		st3_phoneno.setText(phone);
		
	}


}
