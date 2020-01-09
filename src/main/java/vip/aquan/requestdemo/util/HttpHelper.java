package vip.aquan.requestdemo.util;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.Map.Entry;

/**
 * HTTP请求客户端，提供GET和POST两种方式发送请求。
 * 
 * @author Administrator
 * 
 */
public class HttpHelper {

	private static final String APPLICATION_JSON = "application/json";

	private static final String CONTENT_TYPE_TEXT_JSON = "text/json";
	/**
	 * 日志对象。
	 */
	private static Logger logger = Logger.getLogger(HttpHelper.class);

	/**
	 * 默认HTTP请求客户端对象。
	 */
	private DefaultHttpClient _httpclient;

	/**
	 * 用户自定义消息头。
	 */
	private Map<String, String> _headers;

	/**
	 * 使用默认客户端对象。
	 */
	private HttpHelper() {
		// 1. 创建HttpClient对象。
		_httpclient = new DefaultHttpClient();
		logger.info("create _httpclient ...");
	}

	public static HttpHelper httpHelper;

	public static HttpHelper getinstance() {

		if (httpHelper == null) {
			httpHelper = new HttpHelper();
		}

		return httpHelper;
	}

	/**
	 * 调用者指定客户端对象。
	 * 
	 * @param
	 */
	public HttpHelper(Map<String, String> headers) {
		// 1. 创建HttpClient对象。
		_httpclient = new DefaultHttpClient();
		this._headers = headers;
		logger.info("create _httpclient ...");
	}

	/**
	 * HTTP POST请求。
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws InterruptedException
	 */
	public String post(String url, Map<String, String> params) {
		// 2. 创建请求方法的实例，并指定请求URL，添加请求参数。
		HttpPost post = postForm(url, params);
		logger.info("create httppost : " + url);

		return invoke(post);
	}

	/**
	 * HTTP GET请求。
	 * 
	 * @param url
	 * @return
	 */
	public String get(String url) {
		HttpGet get = new HttpGet(url);
		logger.info("create httpget : " + url);

		return invoke(get);
	}

	/**
	 * 发送请求，处理响应。
	 * 
	 * @param request
	 * @return
	 */
	private String invoke(HttpUriRequest request) {
		if (this._headers != null) {
			//
			addHeaders(request);
			logger.info("addHeaders to http ...");
		}

		HttpResponse response = null;
		String responseJson = "";
		try {
			// 3. 调用HttpClient对象的execute(HttpUriRequest
			// request)发送请求，返回一个HttpResponse。
			response = _httpclient.execute(request);
			responseJson = EntityUtils.toString(response.getEntity());
			logger.info("execute http success... ; body = " + responseJson);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("execute http exception...");
		} finally {
			// 4. 无论执行方法是否成功，都必须释放连接。
			request.abort();
			logger.info("release http ...");
		}

		return responseJson;
	}

	/**
	 * 获取post方法。
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	private HttpPost postForm(String url, Map<String, String> params) {
		HttpPost httpost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		// 组装参数。
		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}

		try {
			logger.info("set utf-8 form entity to httppost ...");
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return httpost;
	}

	/**
	 * 增加消息头。
	 * 
	 * @param httpost
	 */
	private void addHeaders(HttpUriRequest httpost) {
		Iterator<Entry<String, String>> it = this._headers.entrySet().iterator();
		Entry<String, String> entry = null;
		String name = null;
		String value = null;

		while (it.hasNext()) {
			entry = it.next();
			name = entry.getKey();
			value = entry.getValue();

			httpost.addHeader(name, value);
		}
	}

	/**
	 * 关闭HTTP客户端链接。
	 */
	public void shutdown() {
		_httpclient.getConnectionManager().shutdown();
		logger.info("shutdown _httpclient ...");
	}

	public  String httpPostWithJSON(String url, String json) throws Exception {
		// 将JSON进行UTF-8编码,以便传输中文
		String encoderJson = URLEncoder.encode(json, HTTP.UTF_8);

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);

		StringEntity se = new StringEntity(encoderJson);
		se.setContentType(CONTENT_TYPE_TEXT_JSON);
		se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
		httpPost.setEntity(se);
		
		
		HttpResponse httpResponse = httpClient.execute(httpPost);
		// 得到应答的字符串，这也是一个 JSON 格式保存的数据
		String retSrc = EntityUtils.toString(httpResponse.getEntity());

		logger.info(retSrc);

		
		return retSrc;
	}

	public static void main(String[] args) {
		HttpHelper test = HttpHelper.getinstance();
		Map<String, String> map = new HashMap<String, String>();

		/*
		 * //查询小区信息 map.put("app_key", "091ba1401bb04a03850d4add0605a979");
		 * map.put("agt_num", "80250"); map.put("departId", "80250000001");
		 * 
		 * test.post("http://121.40.204.191:8180/mdserver/service/getCommunity",
		 * map);
		 */
		/*
		 * //查询设备信息 map.put("app_key", "091ba1401bb04a03850d4add0605a979");
		 * map.put("agt_num", "80250"); map.put("pid", "BLZXC160600051");
		 * 
		 * test.post("http://121.40.204.191:8180/mdserver/service/queryDevice",
		 * map); <<<<<<< .mine
		 */
		/*
		 * //申请钥匙信息 map.put("app_key", "091ba1401bb04a03850d4add0605a979");
		 * map.put("agt_num", "80250"); map.put("pid", "BLZXC160600051");
		 * map.put("user_id", "13500008625"); map.put("validity", "20170724");
		 * 
		 * test.post("http://121.40.204.191:8180/mdserver/service/qryKeys",
		 * map);
		 */
		// 查询设备信息
		// Map<String, String> map = new HashMap<String, String>();
		// map.put("app_key", "091ba1401bb04a03850d4add0605a979");
		// map.put("agt_num", "80250");
		// map.put("pid", "BLZXC160600051");
		//
		// String json =
		// test.post("http://121.40.204.191:8180/mdserver/service/queryDevice",
		// map);
		//
		// System.err.println(json);
		// 查询钥匙列表
		map.put("app_key", "091ba1401bb04a03850d4add0605a979");
		map.put("agt_num", "80250");
		map.put("gid", "gh_04f098e1b9e3");
		map.put("openid", "oQVUuwGIUr5qVSlX6A18t5232323bw");
		map.put("mtype", "1");
		map.put("auid", "13500008624");

		test.post("http://wx.hzblzx.com/mdwxserver/pservice/qryAllKeysService.aspx", map);

		// MdHttpService.queryDevice("BLZXC160600051");

	}
}