package org.lxb.spider.car;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.lxb.spider.car.entity.*;
import org.lxb.spider.car.util.HttpUtil;
import org.lxb.spider.car.util.JdbcUtil;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class App 
{
    private static String[] CITY = new String[]{"bj"};
    public static final String BRAND_INFO = "brand_info";
    public static final String COMPLETE_REGION = "complete_region_info";
    public static final String COMPLETE_SUBWAY = "complete_subway_info";
    public static final String SUBWAY_INFO = "subway_info";
    private static String BASE_URL = "https://car.yiche.com";
    private static String XUANCHEGONGJU_URL = BASE_URL + "/xuanchegongju";
    private static String ERSHOUFANG_URL = "https://${city}.lianjia.com/ershoufang/";
    private static String STAT_BASE_URL = "https://${city}.lianjia.com/ershoufang/housestat";
    public static void main(String[] args ) throws IOException, SQLException, ClassNotFoundException, InterruptedException {
        for (int index = 0; index < CITY.length; index++) {
            File file = new File(System.getProperty("user.dir") + File.separator + "car_cache");
            System.out.println("cache file : " + file.getAbsolutePath());
            if (!file.exists()) {
                file.createNewFile();
            }
            FileReader fileReader = new FileReader(file);
            char[] buffer = new char[1024];
            StringBuffer allContent = new StringBuffer();
            int len = fileReader.read(buffer);
            while (len != -1) {
                allContent.append(buffer, 0, len);
                len = fileReader.read(buffer);
            }
            fileReader.close();
            JSONObject cachedInfo;
            if (allContent.length() != 0) {
                cachedInfo = JSON.parseObject(allContent.toString());
            } else {
                cachedInfo = new JSONObject();
            }
            if (!cachedInfo.containsKey(BRAND_INFO) || cachedInfo.getJSONArray(BRAND_INFO).size() == 0) {
                List<BrandInfo> regionInfos = getBrandInfo(XUANCHEGONGJU_URL);
                cachedInfo.put(BRAND_INFO, JSONArray.parseArray(JSONObject.toJSONString(regionInfos)));
                File fileBak = new File(System.getProperty("user.dir") + File.separator + "cache_cache_bak");
                FileWriter fileWriter = new FileWriter(fileBak);
                fileWriter.write(cachedInfo.toJSONString());
                fileWriter.close();
                file.delete();
                fileBak.renameTo(file);
            }
            JSONArray brandInfos = cachedInfo.getJSONArray(BRAND_INFO);
            if(brandInfos.size() == 0) {
                System.err.println("brand info is null");
            } else {
                for (int brandIndex = 0; brandIndex < brandInfos.size(); brandIndex++) {
                    JSONObject regionInfo = (JSONObject) brandInfos.get(brandIndex);
                    String carDetailHref = regionInfo.getString("href");
                    String carName = regionInfo.getString("brandName");
                    ListInfo listInfo = getListInfo(BASE_URL + carDetailHref);
                    if(listInfo.getItem() == null || listInfo.getItem().size() == 0) {
                        System.out.println(carName + ":" + carDetailHref);
                        continue;
                    }
                    do {
                        System.out.println(listInfo);
                        listInfo = getListInfo(BASE_URL + listInfo.getNextPageUrl());
                    } while (listInfo.hasNext() && listInfo.getItem() != null && listInfo.getItem().size() != 0);

                }
            }
            /*

            // List<ListInfo> listInfos = getInfoFromList("https://bj.lianjia.com/ershoufang/");
            JSONArray brandInfos = cachedInfo.getJSONArray(BRAND_INFO);
            if(brandInfos.size() == 0) {
                System.err.println("brand info is null");
            } else {
                for (int regionIndex = 0; regionIndex < brandInfos.size(); regionIndex++) {
                    JSONObject regionInfo = (JSONObject) brandInfos.get(regionIndex);
                    String regionName = regionInfo.getString("regionName");
                    JSONArray areaInfos = regionInfo.getJSONArray("areaInfoList");
                    for (int areaIndenx = 0; areaIndenx < areaInfos.size();areaIndenx ++) {
                        String href = ((JSONObject)areaInfos.get(areaIndenx)).getString("href");
                        int totalPage = getTotalPageInfo(BASE_URL.replace("${city}", CITY[index]) + href);//"https://bj.lianjia.com/ershoufang/");
                        String areaName =  ((JSONObject)areaInfos.get(areaIndenx)).getString("areaName");
                        JSONObject completeRegion = cachedInfo.getJSONObject(COMPLETE_REGION);
                        int currentPage = completeRegion != null && completeRegion.containsKey(href)?(completeRegion.getInteger(href) + 1):1;
                        for (int i = currentPage; i <= totalPage; i++) {
                            Thread.sleep(5000);
                            String url = BASE_URL.replace("${city}", CITY[index]) + href + "pg" + i;
                            System.out.println("获取分页数据：" + url);
                            List<ListInfo> listInfos = getInfoFromList(url);
                            int finalIndex = index;
                            listInfos.forEach(listInfo -> {
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                String detailUrl = listInfo.getDetailUrl();
                                System.out.println("开始获取详细数据 ：" + detailUrl);
                                try {
                                    DetailInfo detailInfo = getDetailInfo(detailUrl, finalIndex);
                                    listInfo.setDetailInfo(JSONObject.toJSONString(detailInfo));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                System.out.println("获取详细数据结束 ：" + detailUrl);
                            });
                            System.out.println("开始将数据插入数据库");
                            JdbcUtil.insert("1",regionName,areaName,listInfos);
                            System.out.println("完成数据插入数据库");
                            completeRegion.put(href,i);
                            cachedInfo.put(COMPLETE_REGION,completeRegion);
                            File fileBak = new File(System.getProperty("user.dir") + File.separator + "cache_bak");
                            FileWriter fileWriter = new FileWriter(fileBak);
                            fileWriter.write(cachedInfo.toJSONString());
                            fileWriter.close();
                            file.delete();
                            fileBak.renameTo(file);
                        }
                    }
                }
            }*/
        }
        HttpUtil.close();
        JdbcUtil.close();
    }

    private static ListInfo getListInfo(String carDetailHref) throws IOException {
        Document document = Jsoup.connect(carDetailHref)
                .userAgent("Mozilla")
                .timeout(30000)
                .get();
        Elements regionInfo = document.select("div.search-result-list").select("div.search-result-list-item");
        boolean hasNext = document.select("div[id='pagination-list']").select("a[data-current='next']").hasClass("disabled");
        String nextUrl = document.select("div[id='pagination-list']").select("a[data-current='next']").attr("href");
        ListInfo listInfo = new ListInfo(nextUrl,!hasNext);
        listInfo.setItem(new ArrayList<>());
        regionInfo.forEach(element -> {
            String dataId = element.attr("data-id");
            String name = element.select("a[target='_blank']").select("p.cx-name").text();
            String price = element.select("a[target='_blank']").select("p.cx-price").text();
            String peizhiDetailHref = element.select("p.cx-params").select("a.cx-params-font").attr("href");
            ListInfo.Item item = new ListInfo.Item(dataId,name,price,peizhiDetailHref);
            listInfo.addItem(item);
        });
        return listInfo;
    }

    private static  List<RegionInfo>  getRegionInfo(String url) throws IOException {
        Document document = Jsoup.connect(url)
                .userAgent("Mozilla")
                .timeout(30000)
                .get();
        Elements regionInfo = document.select("div.position").select("div[data-role='ershoufang'] > div");
        List<RegionInfo> list = new ArrayList<>();
        regionInfo.get(0).select("a").forEach(element -> {
            String href = element.attr("href");
            String region = element.text();
            RegionInfo regionInfo1 = new RegionInfo(href,region);
            list.add(regionInfo1);
        });
        return list;
    }

    private static  List<BrandInfo> getBrandInfo(String url) throws IOException {
        Document document = Jsoup.connect(url)
                .userAgent("Mozilla")
                .timeout(30000)
                .get();
        Elements brandInfo = document.select("div.brand-list > div");
        List<BrandInfo> list = new ArrayList<>();
        if(brandInfo.size() > 1) {
            brandInfo.forEach(element -> {
                String itemLetter = element.attr("data-index");
                element.select("div.item-brand").forEach(childElement -> {
                    String brandId = childElement.attr("data-id");
                    String brandName = childElement.attr("data-name");
                    String href = childElement.select("a").attr("href");
                    BrandInfo brandInfoEntity = new BrandInfo(itemLetter,brandId,brandName,href);
                    list.add(brandInfoEntity);
                });
            });
        }
        return list;
    }

    private static  List<SubWayInfo>  getSubWayInfo(String url) throws IOException {
        Document document = Jsoup.connect(url)
                .userAgent("Mozilla")
                .timeout(30000)
                .get();
        Elements regionInfo = document.select("div.position").select("div[data-role='ditiefang'] > div");
        List<SubWayInfo> list = new ArrayList<>();
        regionInfo.get(0).select("a").forEach(element -> {
            String href = element.attr("href");
            String region = element.text();
            SubWayInfo regionInfo1 = new SubWayInfo(href,region);
            list.add(regionInfo1);
        });
        return list;
    }

    private static  List<SiteInfo>  getSiteInfo(String url) throws IOException {
        Document document = Jsoup.connect(url)
                .userAgent("Mozilla")
                .timeout(30000)
                .get();
        Elements regionInfo = document.select("div.position").select("div[data-role='ditiefang'] > div");
        List<SiteInfo> list = new ArrayList<>();
        if(regionInfo.size() > 1) {
            regionInfo.get(1).select("a").forEach(element -> {
                String href = element.attr("href");
                String areName = element.text();
                SiteInfo areaInfo = new SiteInfo(href, areName);
                list.add(areaInfo);
            });
        }
        return list;
    }


    private static int getTotalPageInfo(String url) throws IOException {
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla")
                    .timeout(30000)
                    .get();
        String pageInfo = document.select("div.contentBottom").select("div.page-box").attr("page-data");
        try {
            JSONObject object = JSONObject.parseObject(pageInfo);
            return object.getInteger("totalPage");
        }catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }


    private static DetailInfo getDetailInfo(String url, int cityIndex) throws IOException {
        Document document = Jsoup.connect(url)
                .userAgent("Mozilla")
                .timeout(30000)
                .get();

        Elements baseInfoElements = document.select("div.baseinform").select("div.introContent").select("div.base").select("div.content").select("li");

        DetailInfo detailInfo = new DetailInfo();
        JSONObject jsonObject = new JSONObject();
        baseInfoElements.forEach(element -> {
            try {
                String propertyKey = ((Element) element.childNodes().get(0)).html();
                String propertyValue = ((TextNode) element.childNodes().get(1)).text();
                jsonObject.put(propertyKey, propertyValue);
            }catch (Exception e) {
                System.out.println(element);
            }
        });
        detailInfo.setBaseInfo(jsonObject);

        Elements transActionInfoElements = document.select("div.baseinform").select("div.introContent").select("div.transaction").select("div.content").select("li");
        JSONObject transactionInfo = new JSONObject();
        transActionInfoElements.forEach(element -> {
            try {
                String propertyKey = ((Element) element.childNodes().get(1)).html();
                String propertyValue = ((Element) element.childNodes().get(3)).text();
                transactionInfo.put(propertyKey, propertyValue);
            }catch (Exception e) {
                System.out.println(element);
            }
        });
        detailInfo.setTransactionInfo(transactionInfo);

        Elements specialInfoElements = document.select("div.baseinform").select("div.showbasemore").select("div.clear");
        JSONObject specialInfo = new JSONObject();
        specialInfoElements.forEach(element -> {
            try {
                if(element.attr("class").contains("tags")) {
                    String propertyKey = ((Element) element.childNodes().get(1)).html();
                    Elements aElements = ((Element) element.childNodes().get(3)).select("a");
                    JSONArray array = new JSONArray();
                    aElements.forEach(element1 -> {
                        array.add(element1.html());
                    });
                    specialInfo.put(propertyKey, array);
                } else {
                    String propertyKey = ((Element) element.childNodes().get(1)).html();
                    String propertyValue = ((Element) element.childNodes().get(3)).text();
                    specialInfo.put(propertyKey, propertyValue);
                }
            }catch (Exception e) {
                System.out.println(element);
            }
        });
        detailInfo.setSpecialInfo(specialInfo);

        Elements recommendElements = document.select("div.shuofang").select("div.bd").select("div.txt > div");
        JSONObject recommendInfo = new JSONObject();
        recommendElements.forEach(element -> {
            try {
                String propertyKey = ((Element) element.childNodes().get(1)).html();
                String propertyValue = ((Element) element.childNodes().get(3)).text();
                recommendInfo.put(propertyKey, propertyValue);
            } catch (Exception e) {
                System.out.println(element);
            }
        });
        detailInfo.setRecommandInfo(recommendInfo);

        String rid = document.select("div.sellDetailHeader").select("div.title-wrapper").select("div.btnContainer").attr("data-lj_action_housedel_id");
        String hid = document.select("div.sellDetailHeader").select("div.title-wrapper").select("div.btnContainer").attr("data-lj_action_resblock_id");
        String header =
                "Connection: keep-alive\n" +
                "sec-ch-ua: \" Not A;Brand\";v=\"99\", \"Chromium\";v=\"96\", \"Google Chrome\";v=\"96\"\n" +
                "Accept: application/json, text/javascript, */*; q=0.01\n" +
                "X-Requested-With: XMLHttpRequest\n" +
                "sec-ch-ua-mobile: ?0\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36\n" +
                "sec-ch-ua-platform: \"Windows\"\n" +
                "Sec-Fetch-Site: same-origin\n" +
                "Sec-Fetch-Mode: cors\n" +
                "Sec-Fetch-Dest: empty\n" +
                "Referer: https://bj.lianjia.com/ershoufang/101113209839.html\n" +
                "Accept-Encoding: gzip, deflate, br\n" +
                "Accept-Language: zh-CN,zh;q=0.9\n" +
                "Cookie: SECKEY_ABVK=jGWBWeLkbji92fKuYl3SXJbSW210CsaQKLELsK/q6fQ%3D; BMAP_SECKEY=e7ccd76a71cca7384bc9d56993ddbed2e19bbff4744b85e39bb3d65be30e7613e76ae0b8689ae7f5bb14207898aef6950e69432a9314fa542a239fa64bfb5b45be849e38e97fa4477b331369f476b4a939ed5fa6b44deedc9b204633c1ebe15d8360d7266162d78703ebb381263ea08005dc57c460355a9d65b2575a4a119f1e55256c703ee51bb66bcd035b92a02c46e8e54db0521bef45bd23ce179d54bc138f5df4cafe140bc1546090da7fd147adb333965a60db0ba115d0915a3c24c847174787c72e3804625b7c97a34c96d9ae62465ba8a577cc1d373083c0c06d5264907174331374567aafbf3e51615642ec; select_city=110000; lianjia_uuid=bd630f5e-c522-4c1d-9213-9e19d7fd2d85; _smt_uid=61c28c27.42678e3d; _jzqc=1; _jzqx=1.1640139815.1640139815.1.jzqsr=google%2Ecom%2Ehk|jzqct=/.-; _jzqckmp=1; _qzjc=1; UM_distinctid=17ddff37cf4aae-05d92a7cd44589-4303066-144000-17ddff37cf5fc3; sajssdk_2015_cross_new_user=1; Hm_lvt_9152f8221cb6243a53c83b956842be8a=1640139817; _ga=GA1.2.1529588944.1640139818; _gid=GA1.2.1143408819.1640139818; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%2217ddff37dffb43-02a075688193c6-4303066-1327104-17ddff37e00b1c%22%2C%22%24device_id%22%3A%2217ddff37dffb43-02a075688193c6-4303066-1327104-17ddff37e00b1c%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E7%9B%B4%E6%8E%A5%E6%B5%81%E9%87%8F%22%2C%22%24latest_referrer%22%3A%22%22%2C%22%24latest_referrer_host%22%3A%22%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC_%E7%9B%B4%E6%8E%A5%E6%89%93%E5%BC%80%22%7D%7D; lianjia_ssid=14fc42df-18ee-0564-4bc6-903fa7495069; _jzqa=1.1738435168839904500.1640139815.1640139815.1640156648.2; CNZZDATA1254525948=1709056517-1640138499-https%253A%252F%252Fwww.google.com.hk%252F%7C1640160099; CNZZDATA1255633284=970310390-1640138900-https%253A%252F%252Fwww.google.com.hk%252F%7C1640155294; srcid=eyJ0Ijoie1wiZGF0YVwiOlwiOWY2MTkzYTQyMTJlYzkxYTQyY2E0YWE4MjYwM2YyZWFmOGUwZjIyMDM3NTQwNTc4MDYxZjIwNzY5MWRmZmNlYzdjOGFkMzNlYjgwMzgyZjk5ZjM5YjhmMGE4NmEwNGMyODE1NzQ4MDE4ODc2ODkyZTRlZjAyNGFjMWI0NGM5OTZmODQ2MmEyNDRlM2NjYzRhNzlhYjNiZmRkNTE2MTVjNjFhNTRjOTA2Nzg5ODFmNjIyOGU3Y2RjNGIxNDIzYzhjNDRmNThlYzcwYTNlZjYyNTBlNGZmOTNlMWRiM2JlYjhhNDRmN2ZjYzQ0NGM3ZjhjZmQ1MGQ0M2VjZTI5OGM3NjAxNDJkMjRmYmYzOTc1NTcxODBiMDM3Zjg5ZDNiNzAxNWQyYTcwMTdkNzU2ZGM0NjdjNWNhMjY5MDVmNDIwNGQ2ZWQ1Njk5ZTUyMGQ0OTRkYTk4MGNjNmE0ODk1ZGRiMlwiLFwia2V5X2lkXCI6XCIxXCIsXCJzaWduXCI6XCIxNmRmMmEyYlwifSIsInIiOiJodHRwczovL2JqLmxpYW5qaWEuY29tL2Vyc2hvdWZhbmcvMTAxMTEzODY4NDc1Lmh0bWwiLCJvcyI6IndlYiIsInYiOiIwLjEifQ==; Hm_lpvt_9152f8221cb6243a53c83b956842be8a=1640162010; CNZZDATA1253477573=18736937-1640139188-https%253A%252F%252Fwww.google.com.hk%252F%7C1640160788; CNZZDATA1255604082=736077965-1640138639-https%253A%252F%252Fwww.google.com.hk%252F%7C1640161089; _qzja=1.1721480162.1640139815577.1640139815577.1640156648167.1640160572964.1640162011756.0.0.0.15.2; _qzjb=1.1640156648167.9.0.0.0; _qzjto=15.2.0; _jzqb=1.9.10.1640156648.1";

        String[] headers = header.split("\\n");
        Map<String,String> headerMap = new HashMap<>();
        for(String singleInfo : headers) {
            String[] splitInfos = singleInfo.split(":");
            headerMap.put(splitInfos[0],splitInfos[1]);
        }

        JSONObject communityInfo = new JSONObject();
        String result = HttpUtil.get(STAT_BASE_URL.replace("${city}", CITY[cityIndex]) + "?hid="+hid+"&rid=" + rid,headerMap);
        if(StringUtils.isNotBlank(result)) {
            JSONObject resultObj = JSONObject.parseObject(result);
            int resultCode = resultObj.getInteger("code");
            if (resultCode == 1) {
                JSONObject data = resultObj.getJSONObject("data");
                try {

                JSONObject resblockCard = data.getJSONObject("resblockCard");
                communityInfo.put("小区均价", resblockCard.getString("unitPrice") + "元/㎡");
                communityInfo.put("建筑年代", resblockCard.getString("buildYear"));
                communityInfo.put("建筑类型", resblockCard.getString("buildType"));
                communityInfo.put("楼栋总数", resblockCard.getString("buildNum"));
                communityInfo.put("户型总数", resblockCard.getString("frameNum") + "个");
                communityInfo.put("挂牌房源", resblockCard.getString("sellNum") + "套在售二手房   " +
                        resblockCard.getString("rentNum") + "套出租房源");
                } catch (Exception e) {
                    System.err.println("get resblockCard from data error : data => " + data.toJSONString());
                }
                communityInfo.put("经纬度", data.getString("resblockPosition"));
            }
            detailInfo.setCommunityInfo(communityInfo);
        }
        return detailInfo;
    }
}
