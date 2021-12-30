package org.lxb.spider.car.util;

import com.alibaba.fastjson.JSONObject;
import org.lxb.spider.car.entity.CarInfo;
import org.lxb.spider.car.entity.CarTypeDetailInfo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class JdbcUtil {

    private static String URL = "jdbc:mysql://localhost:3306/house_info?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false";
    private static String USER_NAME = "root";
    private static String PASSWD = "511e1eb8939cde61";
    private static volatile Connection conn;
    private static String INSERT_SQL = "INSERT INTO `house_info`.`car_info`(`city`,`brandName`,`letter`,`carName`,`carStyleInfo`,`carPrice`," +
            "`baseInfo`,`carBodyInfo`,`engineInfo`,`gearboxInfo`,`chassisSteeringInfo`,`wheelBrakeInfo`,`activeSafetyInfo`,`passiveSafetyInfo`," +
            "`assistOperateInfo`,`assistDriveHardWareInfo`,`outConfigInfo`,`innerConfigInfo`,`siteConfigInfo`,`internetOfVehiclesInfo`, " +
            "`videoEntertainmentInfo`,`lightFuncInfo`,`grassRearviewMirrorInfo`,`airConditionInfo`,`optionalInfo`,`assistDriveInfo`, "+
            "`brandId`,`dataCkid`,`updateTime`)VALUES"+
            "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
    private static Connection getConn() throws ClassNotFoundException, SQLException {
        if(conn == null) {
            synchronized (JdbcUtil.class) {
                if(conn == null) {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    conn = DriverManager.getConnection(URL, USER_NAME, PASSWD);
                }
            }
        }
        return conn;
    }

    public static void insert(String city, CarInfo carInfo) throws ClassNotFoundException, SQLException {

        PreparedStatement stmt = getConn().prepareStatement(INSERT_SQL);
        for(CarTypeDetailInfo carTypeDetailInfo : carInfo.getCarTypeDetailInfos()) {
            stmt.setString(1,city);
            stmt.setString(2,carInfo.getBrandName());
            stmt.setString(3,carInfo.getLetter());
            stmt.setString(4,carTypeDetailInfo.getCarName());
            stmt.setString(5,carTypeDetailInfo.getCarStyleInfo());
            stmt.setString(6,carTypeDetailInfo.getCarPrice());
            stmt.setString(7,JSONObject.toJSONString(carTypeDetailInfo.getCarConfigParams().getBaseInfo()));
            stmt.setString(8,JSONObject.toJSONString(carTypeDetailInfo.getCarConfigParams().getCarBodyInfo()));
            stmt.setString(9,JSONObject.toJSONString(carTypeDetailInfo.getCarConfigParams().getEngineInfo()));
            stmt.setString(10,JSONObject.toJSONString(carTypeDetailInfo.getCarConfigParams().getGearboxInfo()));
            stmt.setString(11,JSONObject.toJSONString(carTypeDetailInfo.getCarConfigParams().getChassisSteeringInfo()));
            stmt.setString(12,JSONObject.toJSONString(carTypeDetailInfo.getCarConfigParams().getWheelBrakeInfo()));
            stmt.setString(13,JSONObject.toJSONString(carTypeDetailInfo.getCarConfigParams().getActiveSafetyInfo()));
            stmt.setString(14,JSONObject.toJSONString(carTypeDetailInfo.getCarConfigParams().getPassiveSafetyInfo()));
            stmt.setString(15,JSONObject.toJSONString(carTypeDetailInfo.getCarConfigParams().getAssistOperateInfo()));
            stmt.setString(16,JSONObject.toJSONString(carTypeDetailInfo.getCarConfigParams().getAssistDriveHardWareInfo()));
            stmt.setString(17,JSONObject.toJSONString(carTypeDetailInfo.getCarConfigParams().getOutConfigInfo()));
            stmt.setString(18,JSONObject.toJSONString(carTypeDetailInfo.getCarConfigParams().getInnerConfigInfo()));
            stmt.setString(19,JSONObject.toJSONString(carTypeDetailInfo.getCarConfigParams().getSeatConfigInfo()));
            stmt.setString(20,JSONObject.toJSONString(carTypeDetailInfo.getCarConfigParams().getInternetOfVehiclesInfo()));
            stmt.setString(21,JSONObject.toJSONString(carTypeDetailInfo.getCarConfigParams().getVideoEntertainmentInfo()));
            stmt.setString(22,JSONObject.toJSONString(carTypeDetailInfo.getCarConfigParams().getLightFuncInfo()));
            stmt.setString(23,JSONObject.toJSONString(carTypeDetailInfo.getCarConfigParams().getGrassRearviewMirrorInfo()));
            stmt.setString(24,JSONObject.toJSONString(carTypeDetailInfo.getCarConfigParams().getAirConditionInfo()));
            stmt.setString(25,JSONObject.toJSONString(carTypeDetailInfo.getCarConfigParams().getOptionalInfo()));
            stmt.setString(26,JSONObject.toJSONString(carTypeDetailInfo.getCarConfigParams().getAssistDriveInfo()));
            stmt.setString(27,carInfo.getBrandId());
            stmt.setString(28,carTypeDetailInfo.getDataCkid());
            stmt.setLong(29,System.currentTimeMillis());
            stmt.addBatch();
        }
        if(carInfo.getCarTypeDetailInfos().size() == 0 ) {
            stmt.setString(1,city);
            stmt.setString(2,carInfo.getBrandName());
            stmt.setString(3,carInfo.getLetter());
            stmt.setString(4,carInfo.getCarName());
            stmt.setString(5,"");
            stmt.setString(6,"");
            stmt.setString(7,JSONObject.toJSONString(new JSONObject()));
            stmt.setString(8,JSONObject.toJSONString(new JSONObject()));
            stmt.setString(9,JSONObject.toJSONString(new JSONObject()));
            stmt.setString(10,JSONObject.toJSONString(new JSONObject()));
            stmt.setString(11,JSONObject.toJSONString(new JSONObject()));
            stmt.setString(12,JSONObject.toJSONString(new JSONObject()));
            stmt.setString(13,JSONObject.toJSONString(new JSONObject()));
            stmt.setString(14,JSONObject.toJSONString(new JSONObject()));
            stmt.setString(15,JSONObject.toJSONString(new JSONObject()));
            stmt.setString(16,JSONObject.toJSONString(new JSONObject()));
            stmt.setString(17,JSONObject.toJSONString(new JSONObject()));
            stmt.setString(18,JSONObject.toJSONString(new JSONObject()));
            stmt.setString(19,JSONObject.toJSONString(new JSONObject()));
            stmt.setString(20,JSONObject.toJSONString(new JSONObject()));
            stmt.setString(21,JSONObject.toJSONString(new JSONObject()));
            stmt.setString(22,JSONObject.toJSONString(new JSONObject()));
            stmt.setString(23,JSONObject.toJSONString(new JSONObject()));
            stmt.setString(24,JSONObject.toJSONString(new JSONObject()));
            stmt.setString(25,JSONObject.toJSONString(new JSONObject()));
            stmt.setString(26,JSONObject.toJSONString(new JSONObject()));
            stmt.setString(27,carInfo.getBrandId());
            stmt.setString(28,"");
            stmt.setLong(29,System.currentTimeMillis());
            stmt.addBatch();
        }
        int[] rs = stmt.executeBatch();
        stmt.close();
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        PreparedStatement stmt = getConn().prepareStatement(INSERT_SQL);
        stmt.close();
    }

    public static void close() throws IOException, SQLException, ClassNotFoundException {
        try {
            if (getConn() != null) {
                getConn().close();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
