package org.lxb.spider;

import static org.junit.Assert.assertTrue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.Test;
import org.lxb.spider.util.WebClientCrawHtmlUtil;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    //@Test
    public void shouldAnswerWithTrue() throws IOException {

        File file = new File(System.getProperty("user.dir") + File.separator + "cache");
        System.out.println("cache file : " + file.getAbsolutePath());
        if(!file.exists()) {
            file.createNewFile();
        }
        FileReader fileReader = new FileReader(file);
        char[] buffer = new char[1024];
        StringBuffer allContent = new StringBuffer();
        int len = fileReader.read(buffer);
        while (len != -1) {
            allContent.append(buffer,0,len);
            len = fileReader.read(buffer);
        }
        fileReader.close();
        JSONObject jsonObject;
        if(allContent.length() != 0) {
            jsonObject = JSON.parseObject(allContent.toString());
        } else {
            jsonObject = new JSONObject();
        }
        System.out.println("完成数据插入数据库");
        File fileBak = new File(System.getProperty("user.dir") + File.separator + "cache_bak");
        FileWriter fileWriter = new FileWriter(fileBak);
        jsonObject.put("index",System.currentTimeMillis());
        fileWriter.write(jsonObject.toJSONString());
        fileWriter.close();
        file.delete();
        fileBak.renameTo(file);
        assertTrue( true );
    }

    //@Test
    public void testGetInfoFromList() throws IOException, InterruptedException {
        App.getTotalPageInfo("https://sh.lianjia.com/ershoufang/yangpu/");
    }

    //@Test
    public void testA8() throws IOException, SQLException, ClassNotFoundException {
        org.lxb.spider.car.App app = new org.lxb.spider.car.App();
        String url = "https://car.yiche.com/aodiq4-5786/peizhi/";
        System.out.println(url);
        WebClient webClient = WebClientCrawHtmlUtil.getClient();
        HtmlPage page = webClient.getPage(url);
        int executJobNums = webClient.waitForBackgroundJavaScript(100);
        while (executJobNums > 1) {
            executJobNums = webClient.waitForBackgroundJavaScript(1000);
            System.err.println(executJobNums);
        }
        page.cleanUp();
        app.getDetailInfoAndSave(page.asXml(),"carName","","itemLetter","brandId");

    }
}
