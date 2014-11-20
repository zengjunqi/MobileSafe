package com.zengyan.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LostFindActivity extends Activity {
	
	private SharedPreferences sp;
	private TextView lf_phone;
	private ImageView iv_protecting;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
	
		//判断一下，是否做过设置向导，如果没有做过，就跳转到设置向导页面去设置，否则就留着当前的页面
		boolean configed = sp.getBoolean("configed", false);
		if(configed){
			// 就在手机防盗页面
			setContentView(R.layout.activity_lost_find);
			lf_phone=(TextView) findViewById(R.id.lf_phone);
			iv_protecting = (ImageView) findViewById(R.id.iv_protecting);
			String phone = sp.getString("phone", null);
			if (!TextUtils.isEmpty(phone)) {
				lf_phone.setText(phone);
			} 

			//设置防盗保护的状态
			boolean protecting = sp.getBoolean("protecting", false);
			if(protecting){
				//已经开启防盗保护
				iv_protecting.setImageResource(R.drawable.lock);
			}else{
				//没有开启防盗保护
				iv_protecting.setImageResource(R.drawable.unlock);
			}
		}else{
			//还没有做过设置向导
			Intent intent = new Intent(this,Setup1Activity.class);
			startActivity(intent);
			//关闭当前页面
			finish();
		}
		
		
	}
	/**
	 * 重新进入手机防盗设置向导页面
	 * @param view
	 */
	public void reEnterSetup(View view){
		Intent intent = new Intent(this,Setup1Activity.class);
		startActivity(intent);
		//关闭当前页面
		finish();
	}

}
