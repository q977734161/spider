package org.lxb.spider;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.lxb.spider.car.entity.CarConfigParams;
import org.lxb.spider.car.entity.CarInfo;
import org.lxb.spider.car.entity.CarTypeDetailInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CarTest {
    @Test
    public void test() throws IOException, SQLException, ClassNotFoundException {
        String path = "D:\\lixiaobao14\\Desktop\\test.html";
        List<String> allContentList = Files.readAllLines(Paths.get(path));
        CarInfo carInfo = new CarInfo();
        carInfo.setBrandName("奥迪");
        carInfo.setLetter("A");
        carInfo.setCarTypeDetailInfos(new ArrayList<>());
        StringBuffer sb = new StringBuffer();
        for(String content : allContentList) {
            sb.append(content);
        }
        Document document = Jsoup.parse(sb.toString());
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
            System.out.println(carName + ":" + carStyleInfo + ":" + carPrice);
            CarTypeDetailInfo carTypeDetailInfo = new CarTypeDetailInfo();
            carTypeDetailInfo.setIndex(index[0]);
            carTypeDetailInfo.setCarName(carName);
            carTypeDetailInfo.setCarPrice(carPrice);
            carTypeDetailInfo.setCarStyleInfo(carStyleInfo);
            carTypeDetailInfo.setCarConfigParams(new CarConfigParams());
            carInfo.getCarTypeDetailInfos().add(carTypeDetailInfo);
            index[0]++;
        });
        elements.forEach(element -> {
            String dataId = element.attr("data-id");
            String txt = element.text();
            Elements trElements = document.select("tr[id='"+dataId+"']");
            Element trElement = trElements.first();
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
                if(nextTrElement == null) {
                    System.out.println(nextTrElement);
                }
            }
        });

        //JdbcUtil.insert("bj",carInfo);
        System.out.println(JSONObject.toJSONString(carInfo));
    }

    private String getContent(StringBuffer stringBuffer) {
        if(stringBuffer.charAt(stringBuffer.length() -1) == '&') {
            return stringBuffer.substring(0,stringBuffer.length()-1);
        }
        return stringBuffer.toString();
    }
}
