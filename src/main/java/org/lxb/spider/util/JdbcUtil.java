package org.lxb.spider.util;

import org.lxb.spider.entity.ListInfo;

import java.io.IOException;
import java.sql.*;
import java.util.List;

public class JdbcUtil {

    private static String URL = "jdbc:mysql://localhost:3306/house_info?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false&characterEncoding=utf-8";
    private static String USER_NAME = "root";
    private static String PASSWD = "511e1eb8939cde61";
    private static volatile Connection conn;
    private static String INSERT_SQL = "INSERT INTO `house_info`.`ershoufang_info`(`type`,`regionOrLine`,`areaOrSite`,`houseCode`,`title`,`address`,`houseInfo`,`followInfo`,`tag`,`totalPrice`,`unitPrice`,`detailInfo`,`updateTime`,`city`)\n" +
            "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
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

    public static void insert(String city,String type,String regionOrLine,String areaOrSite,List<ListInfo> listInfos) throws ClassNotFoundException, SQLException {

        PreparedStatement stmt = getConn().prepareStatement(INSERT_SQL);
        for (int i = 0; i < listInfos.size(); i++) {
            stmt.setString(1,type);
            stmt.setString(2,regionOrLine);
            stmt.setString(3,areaOrSite);
            stmt.setString(4,listInfos.get(i).getHouseId());
            stmt.setString(5,listInfos.get(i).getCommunity());
            stmt.setString(6,listInfos.get(i).getAddress());
            stmt.setString(7,listInfos.get(i).getHouseInfo());
            stmt.setString(8,listInfos.get(i).getFollowInfo());
            stmt.setString(9,listInfos.get(i).getTag());
            stmt.setString(10,listInfos.get(i).getTotalPrice());
            stmt.setString(11,listInfos.get(i).getUnitPrice());
            stmt.setString(12,listInfos.get(i).getDetailInfo());
            stmt.setLong(13,System.currentTimeMillis());
            stmt.setString(14,city);
            stmt.addBatch();
        };
        int[] rs = stmt.executeBatch();
        stmt.close();
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        PreparedStatement stmt = getConn().prepareStatement(INSERT_SQL);
        stmt.close();
    }

    public static void close() throws IOException, SQLException, ClassNotFoundException {
        if(getConn() != null) {
            getConn().close();
        }
    }

}
