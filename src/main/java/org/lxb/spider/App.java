package org.lxb.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.lxb.spider.entity.*;
import org.lxb.spider.util.HttpUtil;
import org.lxb.spider.util.JdbcUtil;

import java.io.*;
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
    private static String[] CITY = new String[]{};
    public static final String REGION_INFO = "region_info";
    public static final String COMPLETE_REGION = "complete_region_info";
    public static final String COMPLETE_SUBWAY = "complete_subway_info";
    public static final String SUBWAY_INFO = "subway_info";
    private static String BASE_URL = "https://${city}.lianjia.com";
    private static String ERSHOUFANG_URL = "https://${city}.lianjia.com/ershoufang/";
    private static String STAT_BASE_URL = "https://${city}.lianjia.com/ershoufang/housestat";
    public static void main(String[] args ) throws IOException, SQLException, ClassNotFoundException, InterruptedException {
        if(args.length > 0) {
            String[] newCity = new String[CITY.length + args.length];
            int index = 0;
            for (String name: CITY) {
                newCity[index] = name;
                index ++;
            }

            for (String name: args) {
                newCity[index] = name;
                index ++;
            }
            CITY = newCity;
        }
        for (int index = 0; index < CITY.length; index++) {
            File file = new File(System.getProperty("user.dir") + File.separator + CITY[index] +"_cache");
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
            if (!cachedInfo.containsKey(REGION_INFO) || cachedInfo.getJSONArray(REGION_INFO).size() == 0) {
                List<RegionInfo> regionInfos = getRegionInfo(ERSHOUFANG_URL.replace("${city}", CITY[index]));
                for (int i = 0; i < regionInfos.size(); i++) {
                    List<AreaInfo> areaInfoList = getAreaInfo(BASE_URL.replace("${city}", CITY[index]) + regionInfos.get(i).getHref());
                    regionInfos.get(i).setAreaInfoList(areaInfoList);
                }
                cachedInfo.put(REGION_INFO, JSONArray.parseArray(JSONObject.toJSONString(regionInfos)));
                File fileBak = new File(System.getProperty("user.dir") + File.separator + "cache_bak");
                FileWriter fileWriter = new FileWriter(fileBak);
                fileWriter.write(cachedInfo.toJSONString());
                fileWriter.close();
                file.delete();
                fileBak.renameTo(file);
            }

            if (!cachedInfo.containsKey(SUBWAY_INFO) || cachedInfo.getJSONArray(SUBWAY_INFO).size() == 0) {
                List<SubWayInfo> regionInfos = getSubWayInfo(ERSHOUFANG_URL.replace("${city}", CITY[index]));
                for (int i = 0; i < regionInfos.size(); i++) {
                    List<SiteInfo> areaInfoList = getSiteInfo(BASE_URL.replace("${city}", CITY[index]) + regionInfos.get(i).getHref());
                    regionInfos.get(i).setSiteInfoList(areaInfoList);
                }
                cachedInfo.put(SUBWAY_INFO, JSONArray.parseArray(JSONObject.toJSONString(regionInfos)));
                File fileBak = new File(System.getProperty("user.dir") + File.separator + "cache_bak");
                FileWriter fileWriter = new FileWriter(fileBak);
                fileWriter.write(cachedInfo.toJSONString());
                fileWriter.close();
                file.delete();
                fileBak.renameTo(file);
            }

            // List<ListInfo> listInfos = getInfoFromList("https://bj.lianjia.com/ershoufang/");
            JSONArray regionInfos = cachedInfo.getJSONArray(REGION_INFO);
            if(regionInfos.size() == 0) {
                System.err.println("region info is null");
            } else {
                for (int regionIndex = 0; regionIndex < regionInfos.size(); regionIndex++) {
                    JSONObject regionInfo = (JSONObject) regionInfos.get(regionIndex);
                    String regionName = regionInfo.getString("regionName");
                    JSONArray areaInfos = regionInfo.getJSONArray("areaInfoList");
                    for (int areaIndenx = 0; areaIndenx < areaInfos.size();areaIndenx ++) {
                        String href = ((JSONObject)areaInfos.get(areaIndenx)).getString("href");
                        int totalPage = getTotalPageInfo(BASE_URL.replace("${city}", CITY[index]) + href);//"https://bj.lianjia.com/ershoufang/");
                        String areaName =  ((JSONObject)areaInfos.get(areaIndenx)).getString("areaName");
                        JSONObject completeRegion = cachedInfo.getJSONObject(COMPLETE_REGION);
                        int currentPage = completeRegion != null && completeRegion.containsKey(href)?(completeRegion.getInteger(href) + 1):1;
                        if(completeRegion == null) {
                            completeRegion = new JSONObject();
                        }
                        Thread.sleep(1000);
                        System.out.println(href + ": currentPage=" + currentPage + ",totalPage:" + totalPage);
                        for (int i = currentPage; i <= totalPage; i++) {
                            Thread.sleep(2000);
                            String url = BASE_URL.replace("${city}", CITY[index]) + href + "pg" + i;
                            System.out.println("获取分页数据：" + url);
                            List<ListInfo> listInfos = getInfoFromList(url);
                            int finalIndex = index;
                            listInfos.forEach(listInfo -> {
                                try {
                                    Thread.sleep(1000);
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
                            JdbcUtil.insert(CITY[index],"1",regionName,areaName,listInfos);
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
            }
            JSONArray subwayInfos = cachedInfo.getJSONArray(SUBWAY_INFO);
            if(subwayInfos.size() == 0) {
                System.err.println("subway info is null");
            } else {
                for (int subwayIndex = 0; subwayIndex < subwayInfos.size(); subwayIndex++) {
                    JSONObject subwayInfo = (JSONObject) subwayInfos.get(subwayIndex);
                    String lineName = subwayInfo.getString("lineName");
                    JSONArray siteInfoList = subwayInfo.getJSONArray("siteInfoList");
                    for (int areaIndenx = 0; areaIndenx < siteInfoList.size();areaIndenx ++) {
                        String href = ((JSONObject)siteInfoList.get(areaIndenx)).getString("href");
                        int totalPage = getTotalPageInfo(BASE_URL.replace("${city}", CITY[index]) + href);//"https://bj.lianjia.com/ershoufang/");
                        String siteName =  ((JSONObject)siteInfoList.get(areaIndenx)).getString("siteName");
                        JSONObject completeRegion = cachedInfo.getJSONObject(COMPLETE_SUBWAY);
                        int currentPage = completeRegion != null && completeRegion.containsKey(href)?(completeRegion.getInteger(href)+1):1;
                        if(completeRegion == null) {
                            completeRegion = new JSONObject();
                        }
                        for (int i = currentPage; i <= totalPage; i++) {
                            Thread.sleep(5000);
                            String url = BASE_URL.replace("${city}", CITY[index]) + href + "pg" + i;
                            System.out.println("获取分页数据：" + url);
                            List<ListInfo> listInfos = getInfoFromList(url);
                            int finalIndex1 = index;
                            listInfos.forEach(listInfo -> {
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                String detailUrl = listInfo.getDetailUrl();
                                System.out.println("开始获取详细数据 ：" + detailUrl);
                                try {
                                    DetailInfo detailInfo = getDetailInfo(detailUrl, finalIndex1);
                                    listInfo.setDetailInfo(JSONObject.toJSONString(detailInfo));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                System.out.println("获取详细数据结束 ：" + detailUrl);
                            });
                            System.out.println("开始将数据插入数据库");
                            JdbcUtil.insert(CITY[index],"2",lineName,siteName,listInfos);
                            System.out.println("完成数据插入数据库");
                            completeRegion.put(href,i);
                            cachedInfo.put(COMPLETE_SUBWAY,completeRegion);
                            File fileBak = new File(System.getProperty("user.dir") + File.separator + "cache_bak");
                            FileWriter fileWriter = new FileWriter(fileBak);
                            fileWriter.write(cachedInfo.toJSONString());
                            fileWriter.close();
                            file.delete();
                            fileBak.renameTo(file);
                        }
                    }
                }
            }

        }
        HttpUtil.close();
        JdbcUtil.close();
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

    private static  List<AreaInfo>  getAreaInfo(String url) throws IOException {
        Document document = Jsoup.connect(url)
                .userAgent("Mozilla")
                .timeout(30000)
                .get();
        Elements regionInfo = document.select("div.position").select("div[data-role='ershoufang'] > div");
        List<AreaInfo> list = new ArrayList<>();
        if(regionInfo.size() > 1) {
            regionInfo.get(1).select("a").forEach(element -> {
                String href = element.attr("href");
                String areName = element.text();
                AreaInfo areaInfo = new AreaInfo(href, areName);
                list.add(areaInfo);
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

    private static int retryTime = 0;

    public static int getTotalPageInfo(String url) throws IOException, InterruptedException {
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla")
                    .timeout(30000)
                    .get();
        String pageInfo = document.select("div.contentBottom").select("div.page-box").attr("page-data");
        try {
            JSONObject object = JSONObject.parseObject(pageInfo);
            int num = object.getInteger("totalPage");
            retryTime = 0;
            return num;
        }catch (Exception e) {
            e.printStackTrace();
            System.err.println(document.toString());
            System.err.println(pageInfo);
            try {
                String value = document.select("div.leftContent").select("h2.total").select("span").text();
                if (value.equals("0")) {
                    return 0;
                }
            } finally {

            }
            Thread.sleep(10000);
            retryTime ++;
            if(retryTime < 10) {
                getTotalPageInfo(url);
            }
            return 0;
        }

    }

    public static List<ListInfo> getInfoFromList(String url) throws IOException {
        Document document = Jsoup.connect(url)
                .userAgent("Mozilla")
                .timeout(3000)
                .get();

        Elements elements = document.select("div.info");

        List<ListInfo> listInfos = new ArrayList<>();
        elements.forEach(element -> {
            String community = element.select("div.title").select("a").html();
            String detailUrl = element.select("div.title").select("a[href]").attr("href");
            String houseCode = element.select("div.title").select("a[href]").attr("data-housecode");
            Elements elementAddress = element.select("div.flood").select("div.positionInfo").select("a");
            StringBuffer addressBuffer = new StringBuffer();
            elementAddress.forEach(element1 -> addressBuffer.append(element1.html()).append("-"));
            String address = addressBuffer.substring(0,addressBuffer.length()-1);
            String houseInfo = element.select("div.address").select("div.houseInfo").text();
            String followInfo = element.select("div.followInfo").text();
            Elements elementTag = element.select("div.tag").select("span");
            StringBuffer tagBuffer = new StringBuffer();
            elementTag.forEach(element12 -> tagBuffer.append(element12.html()).append("-"));
            String tag = "";
            if(tagBuffer.length() > 1) {
                tag = tagBuffer.substring(0, tagBuffer.length() - 1);
            } else {
                tag = "-";
            }
            String totalPrice = element.select("div.priceInfo").select("div.totalPrice").select("span").html();
            String unitPrice = element.select("div.priceInfo").select("div.unitPrice").select("span").html();

            ListInfo listInfo = new ListInfo(houseCode,community,detailUrl,
                    address,houseInfo,followInfo,tag,totalPrice,unitPrice);
            listInfos.add(listInfo);
        });
        return listInfos;
    }

    private static DetailInfo getDetailInfo(String url,int cityIndex) throws IOException {
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
                "Accept-Language: zh-CN,zh;q=0.9\n";

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
