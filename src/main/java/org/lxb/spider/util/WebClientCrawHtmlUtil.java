package org.lxb.spider.util;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class WebClientCrawHtmlUtil {

    private static WebClient webClient;

    public static WebClient getClient() {
        if(webClient == null) {
            synchronized (WebClientCrawHtmlUtil.class) {
                if(webClient == null) {
                    webClient = new WebClient(BrowserVersion.CHROME);
                    webClient.setAjaxController(new NicelyResynchronizingAjaxController());
                    //支持js
                    webClient.getOptions().setJavaScriptEnabled(true);
                    //忽略js错误
                    webClient.getOptions().setThrowExceptionOnScriptError(false);
                    //忽略css错误
                    webClient.setCssErrorHandler(new SilentCssErrorHandler());
                    //不执行CSS渲染
                    webClient.getOptions().setCssEnabled(false);
                    //超时时间
                    webClient.getOptions().setTimeout(30000);
                    //允许重定向
                    webClient.getOptions().setRedirectEnabled(true);
                    //允许cookie
                    webClient.getCookieManager().setCookiesEnabled(true);
                    webClient.setJavaScriptTimeout(30 * 1000);
                }
            }
        }
        return webClient;
    }

    public static void close() {
        if(webClient != null) {
            synchronized (WebClientCrawHtmlUtil.class) {
                try {
                    webClient.close();
                } finally {

                }
            }
        }
    }

}
