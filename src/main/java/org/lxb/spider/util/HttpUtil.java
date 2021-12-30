package org.lxb.spider.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.internal.StringUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

public class HttpUtil {

    private static volatile CloseableHttpClient client;

    private HttpUtil(){
    }

    private static CloseableHttpClient getClient() {
        if (client == null) {
            synchronized (HttpUtil.class) {
                if (client == null) {
                    RequestConfig defaultRequestConfig = RequestConfig.custom()
                            .setSocketTimeout(30000)
                            .setConnectTimeout(30000)
                            .setConnectionRequestTimeout(30000)
                            .setStaleConnectionCheckEnabled(true)
                            .build();

                    client = HttpClients.custom()
                            .setDefaultRequestConfig(defaultRequestConfig)
                            .build();
                }
            }
        }
        return client;
    }

    public static void close() throws IOException {
        if(client != null) {
            client.close();
        }
    }

    public static String get(String reqUrl, Map<String,String> headers) {
        CloseableHttpResponse response = null;
        HttpGet httpGet = new HttpGet(reqUrl);
        headers.forEach((k,v) -> {
            httpGet.setHeader(k,v);
        });

        try {
            response = getClient().execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(response.getEntity());
                return result;
            } else {
                System.err.println("executing result---服务器连接异常");
            }
        } catch (Exception e) {
            System.err.println("Exception================" + e.toString());
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    public static void main(String[] args) {
        get("https://bj.lianjia.com/ershoufang/housestat?hid=101113868475&rid=1120057040904717",new HashMap<>());
    }
}
