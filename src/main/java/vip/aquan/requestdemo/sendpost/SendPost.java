package vip.aquan.requestdemo.sendpost;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import vip.aquan.requestdemo.util.HttpHelper;
import vip.aquan.requestdemo.util.JsonMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * 向第三方发送post请求
 *
 * @author wcp
 * @date 2020/1/9
 */
public class SendPost {
    private static Logger logger = Logger.getLogger(SendPost.class);
    /**
     * 发送post请求
     * @param url
     * @param map
     * @return
     */
    public static JSONObject sendPost(String url, Map<String, String> map){
        try {
            //请求报文
            String query = JsonMapper.toJsonString(map);
            Map<String, String> param = new HashMap<String, String>();
            param.put("query",query);
            HttpHelper httpHelper = HttpHelper.getinstance();
            //返回数据
            String result = httpHelper.post(url, param);
            logger.info("result:"+result);
            JSONObject jsonObject = JSONObject.parseObject(result);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
