package com.ctc.order.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@SpringBootTest
public class OrderUtilTest {

	private static final Logger log = LoggerFactory.getLogger(OrderUtilTest.class);
	
	@Value("${api.url}")
	private String apiUrl;

	@Test
	public void testGetApiKey() {
		
		String apiKey = OrderUtil.getApiKey();
		assertEquals("Q8P5vdot8RRonx9Wka", apiKey);
	}
	
	@Test
	public void testGetSecret() {		
		String secret = OrderUtil.getSecret();
		assertEquals("VyG6wQk2AYZqxdhFkPWmBcZsT5nyajxCy2sh", secret);
	}
	
	@Test
	public void testReadFile() {
		log.info(FileUtil.readFile(".api").toString());
	}
	
	@Test
	public void testGenQueryString() throws UnknownHostException {
		
		TreeMap<String,String> map = OrderUtil.getTreeMap();
		map.put("symbol", "XRPSD");
        map.put("order_type", "Market");
        map.put("qty", "1");
        map.put("side", "Sell");
        map.put("time_in_force", "GoodTillCancel");
        map.put("timestamp", ZonedDateTime.now().toInstant().toEpochMilli()+"");
        map.put("api_key", OrderUtil.getApiKey());
		
		String queryStr = OrderUtil.genQueryString(map, OrderUtil.getSecret());
		log.info(queryStr);
		log.info(apiUrl);
		
	}
	
	@Test
	public void testOrderCreate() {
		TreeMap<String,String> map = OrderUtil.getTreeMap();
		map.put("symbol", "XRPUSD");
        map.put("order_type", "Market");
        map.put("qty", "1");
        map.put("side", "Sell");
        map.put("time_in_force", "GoodTillCancel");
        map.put("timestamp", ZonedDateTime.now().toInstant().toEpochMilli()+"");
        map.put("api_key", OrderUtil.getApiKey());
		
		String queryStr = OrderUtil.genQueryString(map, OrderUtil.getSecret());
		
		OkHttpClient client = new OkHttpClient();
        RequestBody body=new FormBody.Builder().build();
        
        Request request = new Request.Builder()
                .post(body)
                .url(apiUrl+"/v2/private/order/create?"+queryStr)
                .build();
        Call call = client.newCall(request);
        
        ObjectMapper mapper = new ObjectMapper();
        try {
            Response response = call.execute();            
            Map<String, String> resMap = mapper.readValue(response.body().string(),Map.class);
        }catch (IOException e){
            e.printStackTrace();
        }
	}

}
