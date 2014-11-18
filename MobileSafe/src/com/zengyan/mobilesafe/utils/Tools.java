package com.zengyan.mobilesafe.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.R.string;

public class Tools {

	public static String getHttp(String url) throws Exception
	{

		//获取浏览器实例
		HttpClient client=new DefaultHttpClient();		
		//准备请求数据类型
		HttpGet httpget=new HttpGet(url);
		
		//System.out.println("准备连接网络");
		
		//发送数据给服务器
		HttpResponse response=client.execute(httpget);
		int code=response.getStatusLine().getStatusCode();
	
		if(code==200){
			System.out.println("连接成功");
			String respStr=EntityUtils.toString(response.getEntity(),"utf-8");
			System.out.println(respStr);
			return  respStr;
		
		}
		else
		{
			throw new IllegalStateException("NetworkException");
		}

	}
	public static String postUrl(String url,Map<String,String> rawParams) throws Exception
	{

		//获取浏览器实例
		HttpClient client=new DefaultHttpClient();		

		//准备请求数据类型
		HttpPost httppost=new HttpPost(url);
		
		//设置请求数据实体
	   List<NameValuePair> params=new ArrayList<NameValuePair>();
	
	   for(String key : rawParams.keySet()){
		   params.add(new BasicNameValuePair(key,rawParams.get(key)));
	   }
   
	   UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params,"utf-8");
	
		httppost.setEntity(entity);
		
		System.out.println("准备连接网络");
		
		//发送数据给服务器
		HttpResponse response=client.execute(httppost);
		int code=response.getStatusLine().getStatusCode();
	
		if(code==200){
			System.out.println("连接成功");
			String respStr=EntityUtils.toString(response.getEntity());
			System.out.println(respStr);
			return  respStr;
		
		}
		else
		{
			throw new IllegalStateException("NetworkException");
		}

	}
}
