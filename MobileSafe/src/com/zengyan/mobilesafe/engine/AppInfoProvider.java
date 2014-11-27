package com.zengyan.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.text.format.Formatter;

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
			int uid = packageInfo.applicationInfo.uid;
		long tx=TrafficStats.getUidTxBytes(uid);//发送上传的流量byte
		long rx=TrafficStats.getUidRxBytes(uid);//下载的流量byte
		if (tx==-1) {
			tx=0;
		}
		if (rx==-1) {
			rx=0;
		}
		appInfo.setTraffic("上传流量为:"+Formatter.formatFileSize(context,tx)+"Mb,下载流量为:"+Formatter.formatFileSize(context,rx)+"Mb");
		
//		TrafficStats.getMobileRxBytes();//获取手机3G/2G网络上传的总流量
//		TrafficStats.getMobileTxBytes();//获取手机3G/2G网络下载的总流量
//		TrafficStats.getTotalRxBytes();//获取手机全部网络接口,包括wifi,3G/2G网络上传的总流量
//		TrafficStats.getTotalTxBytes();////获取手机全部网络接口,包括wifi,3G/2G网络下载的总流量
		

			int flags = packageInfo.applicationInfo.flags;
			// flags一个值代表了很多状态
			if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				// 用户程序
				appInfo.setUserApp(true);
			} else {
				// 系统程序
				appInfo.setUserApp(false);
			}
			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
				// 手机内存
				appInfo.setInRom(true);
			} else {
				// 手机外存储
				appInfo.setInRom(false);
			}
			appInfos.add(appInfo);
		}

		return appInfos;
	}

}
