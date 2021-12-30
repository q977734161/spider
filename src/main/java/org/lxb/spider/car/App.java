package org.lxb.spider.car;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lxb.spider.car.entity.*;
import org.lxb.spider.car.util.HttpUtil;
import org.lxb.spider.car.util.JdbcUtil;
import org.lxb.spider.util.WebClientCrawHtmlUtil;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Hello world!
 * getcitylist : https://car.yiche.com/web_api/web_app/api/v1/city/get_area_list
 * https://car.yiche.com/web_api/car_model_api/api/v1/car/config_new_param?cid=508&param=%7B%22cityId%22%3A%223101%22%2C%22serialId%22%3A%221796%22%7D
 */
public class App 
{
    private static String[] CITY = new String[]{"bj"};
    public static final String BRAND_INFO = "brand_info";
    public static final String COMPLETE_INFO = "complete_info";
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
                File fileBak = new File(System.getProperty("user.dir") + File.separator + "car_cache_bak");
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
                    String itemLetter = regionInfo.getString("itemLetter");
                    String carName = regionInfo.getString("brandName");
                    String brandId = regionInfo.getString("brandId");
                    ListInfo listInfo = getListInfo(BASE_URL + carDetailHref);
                    if(listInfo.getItem() == null || listInfo.getItem().size() == 0) {
                        System.out.println(carName + ":" + carDetailHref);
                        continue;
                    }
                    do {
                        JSONObject completeInfo = cachedInfo.getJSONObject(COMPLETE_INFO);
                        if(completeInfo == null) {
                            completeInfo = new JSONObject();
                        }
                        for(ListInfo.Item item : listInfo.getItem()) {
                            String url = BASE_URL + item.getPeizhiUrl();
                            System.out.println(url);
                            if(completeInfo.containsKey(url)) {
                                continue;
                            }
                            WebClient webClient = WebClientCrawHtmlUtil.getClient();
                            HtmlPage page = webClient.getPage(url);
                            int executJobNums = webClient.waitForBackgroundJavaScript(100);
                            while (executJobNums > 1) {
                                executJobNums = webClient.waitForBackgroundJavaScript(1000);
                                System.err.println(executJobNums);
                            }
                            page.cleanUp();
                            getDetailInfoAndSave(page.asXml(),carName,itemLetter,brandId);
                            completeInfo.put(url,"1");
                            cachedInfo.put(COMPLETE_INFO, completeInfo);
                            File fileBak = new File(System.getProperty("user.dir") + File.separator + "car_cache_bak");
                            FileWriter fileWriter = new FileWriter(fileBak);
                            fileWriter.write(cachedInfo.toJSONString());
                            fileWriter.close();
                            file.delete();
                            fileBak.renameTo(file);

                            Thread.sleep(1000);
                        }
                        listInfo = getListInfo(BASE_URL + listInfo.getNextPageUrl());
                    } while (listInfo.hasNext() && listInfo.getItem() != null && listInfo.getItem().size() != 0);

                }
            }
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

    public static void getDetailInfoAndSave(String html,String brandName,String letter,String brandId) throws SQLException, ClassNotFoundException {
        CarInfo carInfo = new CarInfo();
        carInfo.setBrandName(brandName);
        carInfo.setLetter(letter);
        carInfo.setBrandId(brandId);
        carInfo.setCarTypeDetailInfos(new ArrayList<>());
        Document document = Jsoup.parse(html);
        Elements elements = document.select("ul.nav-box").select("li");
        Elements tdElements = document.select("div.main-table-box").select("table.main-param-table").select("tr.t-header").select("td");
        final int[] index = {0};
        tdElements.forEach(tdElem ->{
            String tdIndex = tdElem.attr("data-index");
            if(tdIndex.equals("0")) {
                return;
            }
            String carName = tdElem.select("em.car-name").text();
            String carStyleInfo = tdElem.select("span.car-style-info").text();
            String carPrice = tdElem.select("div.car-price").text();
            String dataCkid = tdElem.select("div.change-car-btn").attr("data-ckid");
            CarTypeDetailInfo carTypeDetailInfo = new CarTypeDetailInfo();
            if(StringUtils.isBlank(carName) && StringUtils.isBlank(carStyleInfo)) {
                return;
            }
            carTypeDetailInfo.setIndex(index[0]);
            carTypeDetailInfo.setCarName(carName);
            carTypeDetailInfo.setCarPrice(carPrice);
            carTypeDetailInfo.setCarStyleInfo(carStyleInfo);
            carTypeDetailInfo.setDataCkid(dataCkid);
            carTypeDetailInfo.setCarConfigParams(new CarConfigParams());
            carInfo.getCarTypeDetailInfos().add(carTypeDetailInfo);
            index[0]++;
        });
        elements.forEach(element -> {
            String dataId = element.attr("data-id");
            String txt = element.text();
            Elements trElements = document.select("tr[id='"+dataId+"']");
            Element trElement = trElements.first();
            if(trElement == null) {
                return;
            }
            Element nextTrElement = trElement.nextElementSibling();
            final AtomicReference<Boolean> hasNoCloneClass = new AtomicReference<>(false);
            final AtomicReference<String> noCloneClassTrContent = new AtomicReference<>("");
            AtomicReference<String> preSubDesc = new AtomicReference<>("");
            while (nextTrElement != null && (nextTrElement.hasClass("data-tr") || nextTrElement.hasClass("no-clone"))) {
                AtomicReference<String> subDesc = new AtomicReference<>("");
                index[0] = 0;
                boolean hasNoCloneClassBool = nextTrElement.hasClass("no-clone");
                hasNoCloneClass.set(hasNoCloneClassBool);

                nextTrElement.select("td").forEach(ele -> {
                    if(ele.hasAttr("rowspan")) {
                        subDesc.set(ele.text());
                    } else {
                        StringBuffer content = new StringBuffer();
                        if(subDesc.get().equals("外观颜色") || subDesc.get().equals("内饰颜色")) {

                            ele.select("div").select("span").forEach(spanEle -> {
                                content.append(spanEle.attr("title") + ":" + spanEle.attr("style").replace("background-color:","").replace("; height: 100%;",""))
                                        .append("&");
                            });
                        } else {
                            ele.select("div").select("span").forEach(spanElem -> {
                                String spanText = spanElem.text();
                                if(spanText.equals("○")) {
                                    content.append("(选配)");
                                    return;
                                } else if(spanText.equals("●")){
                                    content.append("(标配)");
                                    return;
                                }
                                if(spanElem.hasClass("optional-item-price")) {
                                    if(content.charAt(content.length()-1) == '&') {
                                        content.replace(content.length() - 1, content.length(), "").append(" ");
                                    }
                                }
                                content.append(spanText).append("&");
                            });
                        }
                        if(hasNoCloneClass.get()) {
                            noCloneClassTrContent.set(getContent(content));
                            return;
                        }
                        if(ele.select("div").size() == 0 || !ele.select("div").hasClass("div-in-td-content")) {
                            return;
                        }
                        switch (txt) {
                            case "基本信息":
                                carInfo.getCarTypeDetailInfos().get(index[0]).getCarConfigParams().getBaseInfo().put(subDesc.get(), getContent(content));
                                break;
                            case "车身":
                                carInfo.getCarTypeDetailInfos().get(index[0]).getCarConfigParams().getCarBodyInfo().put(subDesc.get(), getContent(content));
                                break;
                            case "发动机":
                                carInfo.getCarTypeDetailInfos().get(index[0]).getCarConfigParams().getEngineInfo().put(subDesc.get(), getContent(content));
                                break;
                            case "变速箱":
                                carInfo.getCarTypeDetailInfos().get(index[0]).getCarConfigParams().getGearboxInfo().put(subDesc.get(), getContent(content));
                                break;
                            case "底盘转向":
                                carInfo.getCarTypeDetailInfos().get(index[0]).getCarConfigParams().getChassisSteeringInfo().put(subDesc.get(), getContent(content));
                                break;
                            case "车轮制动":
                                carInfo.getCarTypeDetailInfos().get(index[0]).getCarConfigParams().getWheelBrakeInfo().put(subDesc.get(), getContent(content));
                                break;
                            case "主动安全":
                                carInfo.getCarTypeDetailInfos().get(index[0]).getCarConfigParams().getActiveSafetyInfo().put(subDesc.get(), getContent(content));
                                break;
                            case "被动安全":
                                carInfo.getCarTypeDetailInfos().get(index[0]).getCarConfigParams().getPassiveSafetyInfo().put(subDesc.get(), getContent(content));
                                break;
                            case "辅助/操控配置":
                                carInfo.getCarTypeDetailInfos().get(index[0]).getCarConfigParams().getAssistOperateInfo().put(subDesc.get(), getContent(content));
                                break;
                            case "辅助驾驶功能":
                                carInfo.getCarTypeDetailInfos().get(index[0]).getCarConfigParams().getAssistDriveInfo().put(subDesc.get(), getContent(content));
                                break;
                            case "辅助驾驶硬件":
                                carInfo.getCarTypeDetailInfos().get(index[0]).getCarConfigParams().getAssistDriveHardWareInfo().put(subDesc.get(), getContent(content));
                                break;
                            case "外部配置":
                                carInfo.getCarTypeDetailInfos().get(index[0]).getCarConfigParams().getOutConfigInfo().put(subDesc.get(), getContent(content));
                                break;
                            case "内部配置":
                                carInfo.getCarTypeDetailInfos().get(index[0]).getCarConfigParams().getInnerConfigInfo().put(subDesc.get(), getContent(content));
                                break;
                            case "座椅配置":
                                carInfo.getCarTypeDetailInfos().get(index[0]).getCarConfigParams().getSeatConfigInfo().put(subDesc.get(), getContent(content));
                                break;
                            case "车机/互联":
                                carInfo.getCarTypeDetailInfos().get(index[0]).getCarConfigParams().getInternetOfVehiclesInfo().put(subDesc.get(), getContent(content));
                                break;
                            case "影音娱乐":
                                carInfo.getCarTypeDetailInfos().get(index[0]).getCarConfigParams().getVideoEntertainmentInfo().put(subDesc.get(), getContent(content));
                                break;
                            case "灯光功能":
                                carInfo.getCarTypeDetailInfos().get(index[0]).getCarConfigParams().getLightFuncInfo().put(subDesc.get(), getContent(content));
                                break;
                            case "玻璃/后视镜":
                                carInfo.getCarTypeDetailInfos().get(index[0]).getCarConfigParams().getGrassRearviewMirrorInfo().put(subDesc.get(), getContent(content));
                                break;
                            case "空调/制冷":
                                carInfo.getCarTypeDetailInfos().get(index[0]).getCarConfigParams().getAirConditionInfo().put(subDesc.get(), getContent(content));
                                break;
                            case "选配包":
                                carInfo.getCarTypeDetailInfos().get(index[0]).getCarConfigParams().getOptionalInfo().put(subDesc.get(), getContent(content));
                                break;
                        }

                        index[0] ++;
                    }
                    preSubDesc.set(subDesc.get());
                });
                if(StringUtils.isNotBlank(preSubDesc.get()) && hasNoCloneClass.get()) {
                    for (CarTypeDetailInfo carTypeDetailInfo : carInfo.getCarTypeDetailInfos()) {
                        switch (txt) {
                            case "基本信息":
                                carTypeDetailInfo.getCarConfigParams().getBaseInfo().put(preSubDesc.get() + "_remark",noCloneClassTrContent.get());
                                break;
                            case "车身":
                                carTypeDetailInfo.getCarConfigParams().getCarBodyInfo().put(preSubDesc.get() + "_remark",noCloneClassTrContent.get());
                                break;
                            case "发动机":
                                carTypeDetailInfo.getCarConfigParams().getEngineInfo().put(preSubDesc.get() + "_remark",noCloneClassTrContent.get());
                                break;
                            case "变速箱":
                                carTypeDetailInfo.getCarConfigParams().getGearboxInfo().put(preSubDesc.get() + "_remark",noCloneClassTrContent.get());
                                break;
                            case "底盘转向":
                                carTypeDetailInfo.getCarConfigParams().getChassisSteeringInfo().put(preSubDesc.get() + "_remark",noCloneClassTrContent.get());
                                break;
                            case "车轮制动":
                                carTypeDetailInfo.getCarConfigParams().getWheelBrakeInfo().put(preSubDesc.get() + "_remark",noCloneClassTrContent.get());
                                break;
                            case "主动安全":
                                carTypeDetailInfo.getCarConfigParams().getActiveSafetyInfo().put(preSubDesc.get() + "_remark",noCloneClassTrContent.get());
                                break;
                            case "被动安全":
                                carTypeDetailInfo.getCarConfigParams().getPassiveSafetyInfo().put(preSubDesc.get() + "_remark",noCloneClassTrContent.get());
                                break;
                            case "辅助/操控配置":
                                carTypeDetailInfo.getCarConfigParams().getAssistOperateInfo().put(preSubDesc.get() + "_remark",noCloneClassTrContent.get());
                                break;
                            case "辅助驾驶功能":
                                carTypeDetailInfo.getCarConfigParams().getAssistDriveInfo().put(preSubDesc.get() + "_remark",noCloneClassTrContent.get());
                                break;
                            case "辅助驾驶硬件":
                                carTypeDetailInfo.getCarConfigParams().getAssistDriveHardWareInfo().put(preSubDesc.get() + "_remark",noCloneClassTrContent.get());
                                break;
                            case "外部配置":
                                carTypeDetailInfo.getCarConfigParams().getOutConfigInfo().put(preSubDesc.get() + "_remark",noCloneClassTrContent.get());
                                break;
                            case "内部配置":
                                carTypeDetailInfo.getCarConfigParams().getInnerConfigInfo().put(preSubDesc.get() + "_remark",noCloneClassTrContent.get());
                                break;
                            case "座椅配置":
                                carTypeDetailInfo.getCarConfigParams().getSeatConfigInfo().put(preSubDesc.get() + "_remark",noCloneClassTrContent.get());
                                break;
                            case "车机/互联":
                                carTypeDetailInfo.getCarConfigParams().getInternetOfVehiclesInfo().put(preSubDesc.get() + "_remark",noCloneClassTrContent.get());
                                break;
                            case "影音娱乐":
                                carTypeDetailInfo.getCarConfigParams().getVideoEntertainmentInfo().put(preSubDesc.get() + "_remark",noCloneClassTrContent.get());
                                break;
                            case "灯光功能":
                                carTypeDetailInfo.getCarConfigParams().getLightFuncInfo().put(preSubDesc.get() + "_remark",noCloneClassTrContent.get());
                                break;
                            case "玻璃/后视镜":
                                carTypeDetailInfo.getCarConfigParams().getGrassRearviewMirrorInfo().put(preSubDesc.get() + "_remark",noCloneClassTrContent.get());
                                break;
                            case "空调/制冷":
                                carTypeDetailInfo.getCarConfigParams().getAirConditionInfo().put(preSubDesc.get() + "_remark",noCloneClassTrContent.get());
                                break;
                            case "选配包":
                                carTypeDetailInfo.getCarConfigParams().getOptionalInfo().put(preSubDesc.get() + "_remark",noCloneClassTrContent.get());
                                break;
                        }
                    }
                    hasNoCloneClass.set(false);
                }
                nextTrElement = nextTrElement.nextElementSibling();
            }
        });

        JdbcUtil.insert("bj",carInfo);
        System.out.println(JSONObject.toJSONString(carInfo));
    }

    private static String getContent(StringBuffer stringBuffer) {
        if(stringBuffer.length() == 0) {
            return "";
        }
        if(stringBuffer.charAt(stringBuffer.length() -1) == '&') {
            return stringBuffer.substring(0,stringBuffer.length()-1);
        }
        return stringBuffer.toString();
    }

}
