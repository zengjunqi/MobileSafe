package com.zengyan.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.zengyan.mobilesafe.model.AppInfo;

public class AppInfoProvider {

	/**
	 * 获取所有的安装的应用程序信息
	 * 
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context) {
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> pInfos = pm.getInstalledPackages(0);
		for (PackageInfo packageInfo : pInfos) {

			AppInfo appInfo = new AppInfo();
			appInfo.setPackname(packageInfo.packageName);
			appInfo.setIcon(packageInfo.applicationInfo.loadIcon(pm));
			appInfo.setName(packageInfo.applicationInfo.loadLabel(pm)
					.toString());
			
			int flags=packageInfo.applicationInfo.flags;
			//flags一个值代表了很多状态
			if ((flags & ApplicationInfo.FLAG_SYSTEM)==0) {	
				//用户程序
				appInfo.setUserApp(true);
			}else {
				//系统程序
				appInfo.setUserApp(false);
			}
			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE)==0) {
				//手机内存
				appInfo.setInRom(true);
			}else {
				//手机外存储
				appInfo.setInRom(false);
			}
			appInfos.add(appInfo);
		}

		return appInfos;
	}

}
