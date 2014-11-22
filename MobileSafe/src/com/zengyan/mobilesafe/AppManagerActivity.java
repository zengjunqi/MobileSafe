package com.zengyan.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zengyan.mobilesafe.engine.AppInfoProvider;
import com.zengyan.mobilesafe.model.AppInfo;

public class AppManagerActivity extends Activity {

	private LinearLayout ll;
	private TextView tvme, tvsd, tvstatus;
	private ListView lvListView;
	private List<AppInfo> appinfos;
	private Adapter adapter;
	private List<AppInfo> userAppinfos;
	private List<AppInfo> systemAppinfos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_app_manager);

		ll = (LinearLayout) findViewById(R.id.ll_loadapp);
		tvme = (TextView) findViewById(R.id.tv_me_av);
		tvsd = (TextView) findViewById(R.id.tv_sd_av);
		tvstatus = (TextView) findViewById(R.id.tv_status);
		lvListView = (ListView) findViewById(R.id.lv_app_v);
		long romsize = getAvailSpace(Environment.getDataDirectory()
				.getAbsolutePath());
		long sdsize = getAvailSpace(Environment.getExternalStorageDirectory()
				.getAbsolutePath());
		tvme.setText("内存可用空间:" + Formatter.formatFileSize(this, romsize));
		tvsd.setText("SD可用空间:" + Formatter.formatFileSize(this, sdsize));

		ll.setVisibility(View.VISIBLE);

		new Thread() {
			public void run() {
				appinfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);

				userAppinfos = new ArrayList<AppInfo>();
				systemAppinfos = new ArrayList<AppInfo>();
				for (AppInfo info : appinfos) {
					if (info.isUserApp()) {
						userAppinfos.add(info);
					} else {
						systemAppinfos.add(info);
					}
				}

				runOnUiThread(new Runnable() {
					public void run() {

						lvListView.setAdapter(new AppManagerAdapter());
						ll.setVisibility(View.INVISIBLE);
					}
				});
			}
		}.start();

		lvListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			// 滚动时调用的方法
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				if (userAppinfos != null && systemAppinfos!=null) {

					if (firstVisibleItem > userAppinfos.size()) {
						tvstatus.setText("系统程序:"+systemAppinfos.size()+"个");
					}else {
						tvstatus.setText("用户程序:"+userAppinfos.size()+"个");
					}
				}

			}
		});

	}

	private class AppManagerAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return appinfos.size() + 2;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			AppInfo info;

			if (position == 0) {
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("用户程序:" + userAppinfos.size() + "个");
				return tv;
			} else if (position == userAppinfos.size() + 1) {
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("系统程序:" + systemAppinfos.size() + "个");
				return tv;
			} else if (position <= userAppinfos.size()) {
				int newposition = position - 1;
				info = userAppinfos.get(newposition);

			} else {
				int newposition = position - 1 - userAppinfos.size() - 1;
				info = systemAppinfos.get(newposition);
			}

			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {// 不仅检查是否为空,还要检查是否合适复用
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_managerapp, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) view.findViewById(R.id.iv_app_icon);
				holder.location = (TextView) view
						.findViewById(R.id.tv_app_location);
				holder.name = (TextView) view.findViewById(R.id.tv_app_name);
				view.setTag(holder);
			}
			// info = appinfos.get(position);
			holder.icon.setImageDrawable(info.getIcon());
			holder.name.setText(info.getName());
			// holder.location.setText(appinfos.get(position).g());
			if (info.isInRom()) {
				holder.location.setText("手机内存");
			} else {
				holder.location.setText("外部存储");
			}

			return view;
		}

	}

	static class ViewHolder {
		ImageView icon;
		TextView name, location;

	}

	private long getAvailSpace(String path) {

		StatFs statfs = new StatFs(path);
		long size = statfs.getBlockSize();
		long count = statfs.getAvailableBlocks();
		return size * count;

	}

}
