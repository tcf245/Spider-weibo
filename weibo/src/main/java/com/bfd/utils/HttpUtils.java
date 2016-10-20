package com.bfd.utils;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/********************************
 * ---------------------        *
 * < JavaHttpDemo.java >        *
 * ---------------------        *
 *         \   ^__^             *
 *          \  (**)\__$__$__    *
 *             (__)\       )\/\ *
 *              U  ||------|    *
 *                 ||     ||    *
 ********************************/
public class HttpUtils {
	private static final String ua = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36";
	public static void main(String[] args) throws Exception {
		// 链接
		String url = "https://rate.tmall.com/list_detail_rate.htm?itemId=530880692553&sellerId=1918545422&order=3&currentPage=1&callback=jsonp1207";
		
		// 请求头信息
		Map<String,String> headers = new HashMap<String, String>();
		headers.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		
		// post参数
//		Map<String,String> posts = new HashMap<String, String>();
		// posts.put("ids","430017");
		// get请求
		String content = httpGet(url,"", "utf-8");
		// post请求
//		String content = httpPost(url, "utf-8", headers, posts,null);
		
		System.out.println(content);
	}

	/**
	 * get 请求示例代码
	 * @param url
	 * @param charset
	 * @param headers
	 * @return
	 * @throws Exception
	 */
	public static String httpGet(String url, String charset,Map<String,String> headers,String ip) throws Exception {
		HttpClientBuilder httpBuilder = HttpClientBuilder.create();
		httpBuilder.setUserAgent(ua);
		
		HttpClient client = httpBuilder.build();
		HttpGet httpget = new HttpGet(url);
		
		if (null != ip && !"".equals(ip.trim())) {
			RequestConfig requestConfig = getRequestConfigByIp(ip);
			if (null != requestConfig) {
				httpget.setConfig(requestConfig);
			}
		}
		
		if (headers != null && headers.size() > 0) {
			for (String key : headers.keySet()) {
				httpget.setHeader(key, headers.get(key));
			}
		}
		HttpResponse response = client.execute(httpget);
		HttpEntity en = response.getEntity();
		String content = EntityUtils.toString(en, (charset == null|| "".equals(charset)) ? "utf8" : charset);
		return content;
	}


	/**
	 * get 请求示例代码
	 * @param url
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static String httpGet(String url,String cookie,String charset) throws Exception {
		HttpClientBuilder httpBuilder = HttpClientBuilder.create();
		httpBuilder.setUserAgent(ua);

		HttpClient client = httpBuilder.build();
		//解决重定向问题
		RequestConfig config = RequestConfig.custom().setCircularRedirectsAllowed(true).build();
		HttpGet httpget = new HttpGet(url);
		httpget.setConfig(config);

		Map<String,String> headers = new HashMap<String, String>();
		headers.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		headers.put("cookie",cookie);

		if (headers != null && headers.size() > 0) {
			for (String key : headers.keySet()) {
				httpget.setHeader(key, headers.get(key));
			}
		}

		HttpResponse response = client.execute(httpget);
		HttpEntity en = response.getEntity();
		String content = EntityUtils.toString(en, (charset == null || "".equals(charset)) ? "utf8" : charset);
		return content;
	}

	/**
	 *  post 请求示例代码
	 * @param url
	 * @param charset
	 * @param headers
	 * @param posts
	 * @param ip
	 * @return
	 * @throws Exception
	 */
	public static String httpPost(String url, String charset,Map<String,String> headers,Map<String,String> posts,String ip) throws Exception {
		HttpClientBuilder httpBuilder = HttpClientBuilder.create();
		httpBuilder.setUserAgent(ua);
		HttpClient client = httpBuilder.build();
		HttpPost httppost = new HttpPost(url);
		
		if (null != ip && !"".equals(ip)) {
			RequestConfig requestConfig = getRequestConfigByIp(ip);
			if (null != requestConfig) {
				httppost.setConfig(requestConfig);
			}
		}
		
		if (headers != null && headers.size() > 0) {
			for (String key : headers.keySet()) {
				httppost.setHeader(key, headers.get(key));
			}
		}
		
		List<NameValuePair> postList = new ArrayList<NameValuePair>();
		if (posts != null && posts.size() > 0) {
			for (String key : posts.keySet()) {
				postList.add(new BasicNameValuePair(key,posts.get(key)));
			}
			UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(postList);
			httppost.setEntity(postEntity);
		}
		
		HttpResponse response = client.execute(httppost);
		HttpEntity en = response.getEntity();
		String content = EntityUtils.toString(en, (charset == null|| "".equals(charset)) ? "utf8" : charset);
		return content;
	}
	
	
	/**
	 *  指定ip
	 * @param ip
	 * @return
	 */
	public static RequestConfig getRequestConfigByIp(String ip) {
		
		RequestConfig requestConfig = null;
		try {
			RequestConfig.Builder config_builder = RequestConfig.custom();
			InetAddress inetAddress = null;
			inetAddress = InetAddress.getByName(ip);
			config_builder.setLocalAddress(inetAddress);
			requestConfig = config_builder.build();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return requestConfig;
	}
}
