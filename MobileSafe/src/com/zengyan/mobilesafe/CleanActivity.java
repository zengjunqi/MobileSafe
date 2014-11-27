package com.zengyan.mobilesafe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.TextureView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CleanActivity extends Activity {

	private ProgressBar pbBar;
	private TextView tvStatus;
	private PackageManager pmManager;
	private LinearLayout llContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_clean_cache);
		tvStatus = (TextView) findViewById(R.id.tv_clean_status);
		pbBar = (ProgressBar) findViewById(R.id.pb_clean);
		llContainer = (LinearLayout) findViewById(R.id.ll_diplay_clean);
		scanCache();
	}

	private void scanCache() {
		// TODO Auto-generated method stub
		pmManager = getPackageManager();
		new Thread() {
			@Override
			public void run() {
				Method method = null;
				List<PackageInfo> infos = pmManager.getInstalledPackages(0);

				Method[] ms = PackageManager.class.getMethods();
				for (Method mo : ms) {
					if ("getPackageSizeInfo".equals(mo.getName())) {
						method = mo;
						break;
					}
				}
				pbBar.setMax(infos.size());
				System.out.println("======+===" + infos.size());
				int process = 0;
				for (PackageInfo info : infos) {
					try {
						method.invoke(pmManager, info.packageName,
								new MyDataObserver());
						Thread.sleep(500);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					process++;
					pbBar.setProgress(process);
					System.out.println("process======+===" + process);
				}

				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						tvStatus.setText("扫描完毕......");
					}
				});

			};

		}.start();
	}

	public void cleanAll(View view) {
		// /freeStorageAndNotify
		Method[] methods = PackageManager.class.getMethods();
		for (Method method : methods) {
			if ("freeStorageAndNotify".equals(method.getName())) {
				try {
					method.invoke(pmManager, Integer.MAX_VALUE,
							new IPackageDataObserver.Stub() {
								@Override
								public void onRemoveCompleted(
										String packageName, boolean succeeded)
										throws RemoteException {
									if (succeeded) {

										Toast.makeText(getApplicationContext(),
												"success", 0).show();
									} else {
										Toast.makeText(getApplicationContext(),
												"failed", 0).show();
									}
								}
							});
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}
		}
	}

	private class MyDataObserver extends IPackageStatsObserver.Stub {

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			final long cache = pStats.cacheSize;
			final String packname = pStats.packageName;
			final ApplicationInfo appInfo;
			try {
				appInfo = pmManager.getApplicationInfo(packname, 0);

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						tvStatus.setText("正在扫描:" + packname);
						if (cache > 0) {
							TextView tView = new TextView(
									getApplicationContext());
							tView.setText(packname// appInfo.loadLabel(pmManager)
									+ "-缓存大小:"
									+ Formatter.formatFileSize(
											getApplicationContext(), cache));

							llContainer.addView(tView);

						}
					}
				});

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
}
