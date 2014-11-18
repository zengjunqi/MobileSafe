package com.zengyan.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zengyan.mobilesafe.R;

public class SettingItem extends RelativeLayout {

	private CheckBox cb_status;
	private TextView tv_desc;
	private TextView tv_title;
	
	private  String desc_on;
	private String desc_off;
	
	/**
	 * 初始化布局文件
	 * @param context
	 */
	private void iniView(Context context) {
		
		//把一个布局文件---》View 并且加载在SettingItemView
		View.inflate(context, R.layout.setting_item_view, this);
		cb_status = (CheckBox) this.findViewById(R.id.cb_status);
		tv_desc = (TextView) this.findViewById(R.id.tv_desc);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
		
	}
	public SettingItem(Context context) {
		super(context);
		iniView(context);
		// TODO Auto-generated constructor stub
	}

	public SettingItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		iniView(context);
		
	}

	public SettingItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		iniView(context);
		String title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.zengyan.mobilesafe", "titletext");
		desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.zengyan.mobilesafe", "desc_on");
		desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.zengyan.mobilesafe", "desc_off");
		tv_title.setText(title);
		setDesc(desc_off);
	}

	/**
	 * 校验组合控件是否选中
	 */
	
	public boolean isChecked(){
		return cb_status.isChecked();
	}
	
	/**
	 * 设置组合控件的状态
	 */
	
	public void setChecked(boolean checked){
		if(checked){
			setDesc(desc_on);
		}else{
			setDesc(desc_off);
		}
		cb_status.setChecked(checked);
	}
	
	/**
	 * 设置 组合控件的描述信息
	 */
	
	public void setDesc(String text){
		tv_desc.setText(text);
	}
	
	
}
