package com.zengyan.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zengyan.mobilesafe.db.AppLockDao;
import com.zengyan.mobilesafe.engine.AppInfoProvider;
import com.zengyan.mobilesafe.model.AppInfo;
import com.zengyan.mobilesafe.utils.DensityUtil;

public class AppManagerActivity extends Activity implements OnClickListener {

	private LinearLayout ll;
	private TextView tvme, tvsd, tvstatus;
	private ListView lvListView;
	private List<AppInfo> appinfos;
	private AppManagerAdapter adapter;
	private List<AppInfo> userAppinfos;
	private List<AppInfo> systemAppinfos;
	PopupWindow popupWindow;
	private AppInfo appInfo;
	private LinearLayout ll_start;
	private LinearLayout ll_share;
	private LinearLayout ll_uninstall;
	private AppLockDao dbhelper;

	// private ImageView iv_status;

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
		dbhelper = new AppLockDao(this);
		ll.setVisibility(View.VISIBLE);

		fillData();

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
				dismissPopupWindow();
				if (userAppinfos != null && systemAppinfos != null) {

					if (firstVisibleItem > userAppinfos.size()) {
						tvstatus.setText("系统程序:" + systemAppinfos.size() + "个");
					} else {
						tvstatus.setText("用户程序:" + userAppinfos.size() + "个");
					}
				}

			}
		});

		lvListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (position == 0) {
					return;
				} else if (position == userAppinfos.size() + 1) {
					return;
				} else if (position <= userAppinfos.size()) {
					int newposition = position - 1;
					appInfo = userAppinfos.get(newposition);

				} else {
					int newposition = position - 1 - userAppinfos.size() - 1;
					appInfo = systemAppinfos.get(newposition);
				}
				dismissPopupWindow();
				/*
				 * TextView contentView = new TextView(getApplicationContext());
				 * contentView.setText(appInfo.getPackname());
				 * contentView.setTextColor(Color.BLACK);
				 */
				int dip = 60;
				int px = DensityUtil.dip2px(getApplicationContext(), dip);// 转换成px
				View contentView = View.inflate(getApplicationContext(),
						R.layout.popup_app_item, null);
				ll_start = (LinearLayout) contentView
						.findViewById(R.id.ll_start);
				ll_share = (LinearLayout) contentView
						.findViewById(R.id.ll_share);
				ll_uninstall = (LinearLayout) contentView
						.findViewById(R.id.ll_uninstall);

				ll_start.setOnClickListener(AppManagerActivity.this);
				ll_share.setOnClickListener(AppManagerActivity.this);
				ll_uninstall.setOnClickListener(AppManagerActivity.this);

				int[] location = new int[2];
				view.getLocationInWindow(location);
				// 创建悬浮窗体
				popupWindow = new PopupWindow(contentView, -2, -2);// -2就是wrap_content的值
				// 动画的播放必须要求窗体有背景颜色*****
				popupWindow.setBackgroundDrawable(new ColorDrawable(
						Color.TRANSPARENT));
				popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP,
						px, location[1]);

				ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 1.0f,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF);
				sa.setDuration(500);
				AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
				aa.setDuration(500);
				AnimationSet set = new AnimationSet(false);
				set.addAnimation(sa);
				set.addAnimation(aa);
				contentView.startAnimation(set);

			}

		});
		lvListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position == 0) {
					return true;// 返回true,代表这个事件到当前截止了,事件中止,不会在触发其他事件,如false则后面有事件也会继续触发
				} else if (position == userAppinfos.size() + 1) {
					return true;
				} else if (position <= userAppinfos.size()) {
					int newposition = position - 1;
					appInfo = userAppinfos.get(newposition);

				} else {
					int newposition = position - 1 - userAppinfos.size() - 1;
					appInfo = systemAppinfos.get(newposition);
				}
				ViewHolder holder=(ViewHolder) view.getTag();
				if (dbhelper.find(appInfo.getPackname())) {
					dbhelper.delete(appInfo.getPackname());
					holder.iv_status.setImageResource(R.drawable.unlock);
				} else {
					dbhelper.add(appInfo.getPackname());
					holder.iv_status.setImageResource(R.drawable.lock);
				}

				return true;
			}
		});

	}

	private void fillData() {
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

						if (adapter == null) { // 如果适配器为空 则创建适配器对象
												// 为listview设置adapter
							adapter = new AppManagerAdapter();
							lvListView.setAdapter(adapter);

						} else {//
							adapter.notifyDataSetChanged(); // 动态更新ListView
						}
						ll.setVisibility(View.INVISIBLE);
					}
				});
			}
		}.start();
	}

	@Override
	protected void onDestroy() {
		dismissPopupWindow();
		super.onDestroy();
	}

	private void dismissPopupWindow() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
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
				holder.iv_status = (ImageView) view
						.findViewById(R.id.iv_status);
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
			if (dbhelper.find(info.getPackname())) {
				holder.iv_status.setImageResource(R.drawable.lock);
			} else {
				holder.iv_status.setImageResource(R.drawable.unlock);
			}

			return view;
		}

	}

	static class ViewHolder {
		ImageView icon, iv_status;
		TextView name, location;

	}

	private long getAvailSpace(String path) {

		StatFs statfs = new StatFs(path);
		long size = statfs.getBlockSize();
		long count = statfs.getAvailableBlocks();
		return size * count;

	}

	@Override
	public void onClick(View v) {
		dismissPopupWindow();
		switch (v.getId()) {
		case R.id.ll_start:
			startApplication();
			break;
		case R.id.ll_share:
			shareApplication();
			break;
		case R.id.ll_uninstall:
			if (appInfo.isUserApp()) {
				uninstallApplication();
			} else {
				Toast.makeText(getApplicationContext(), "系统应用必须要有root权限才能卸载", 0)
						.show();
			}

			break;

		default:
			break;
		}
	}

	private void shareApplication() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT,
				"推荐您使用一款软件,名称为: " + appInfo.getName());
		startActivity(intent);
	}

	private void uninstallApplication() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.View");
		intent.setAction("android.intent.action.DELETE");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:" + appInfo.getPackname()));
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		fillData();
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void startApplication() {
		PackageManager pm = getPackageManager();
		/*
		 * Intent intent = new Intent();
		 * intent.setAction("android.intent.action.MAIN");
		 * intent.addCategory("android.intent.category.LAUNCHER");
		 * pm.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
		 */

		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackname());
		if (intent != null) {
			startActivity(intent);
		} else {
			Toast.makeText(getApplicationContext(), "没法启动程序.", 0).show();
		}

	}
}
