package com.zengyan.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class LostFindActivity extends Activity {
	
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		//�ж�һ�£��Ƿ����������򵼣����û������������ת��������ҳ��ȥ���ã���������ŵ�ǰ��ҳ��
		boolean configed = sp.getBoolean("configed", false);
		if(configed){
			// �����ֻ�����ҳ��
			setContentView(R.layout.activity_lost_find);
		}else{
			//��û������������
			Intent intent = new Intent(this,Setup1Activity.class);
			startActivity(intent);
			//�رյ�ǰҳ��
			finish();
		}
		
		
	}
	/**
	 * ���½����ֻ�����������ҳ��
	 * @param view
	 */
	public void reEnterSetup(View view){
		Intent intent = new Intent(this,Setup1Activity.class);
		startActivity(intent);
		//�رյ�ǰҳ��
		finish();
	}

}