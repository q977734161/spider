package org.lxb.spider;

import static org.junit.Assert.assertTrue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
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

    @Test
    public void testGetInfoFromList() throws IOException {
        App.getInfoFromList("https://bj.lianjia.com/ershoufang/anzhen1/pg14/");
    }
}
