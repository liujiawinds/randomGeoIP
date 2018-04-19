package org.liujia.randomGeoIP;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Location;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by liujia on 2018/4/18.
 * Generate Chinese random ip and geo.
 * ref: https://github.com/taliu/chinese-random-ip
 * https://www.maxmind.com/zh/geoip-demo
 */
public class GeoIPGenerator {

    private Random random = new Random();
    private static DatabaseReader geoDB;
    private static JSONObject ipDB;

    public static void init() throws IOException, URISyntaxException {
        initIPDB();
        initGeoDB();
    }

    private static void initIPDB() throws URISyntaxException, IOException {
        StringBuilder sb = new StringBuilder();
        List<String> lines;
        lines = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("ChineseIP.json").toURI()));
        lines.stream().forEachOrdered(sb::append);
        ipDB = JSON.parseObject(sb.toString());
    }

    private static void initGeoDB() throws IOException, URISyntaxException {
        File database = new File(String.valueOf(Paths.get(ClassLoader.getSystemResource("GeoLite2-City.mmdb").toURI())));
        geoDB = new DatabaseReader.Builder(database).build();
    }


    public String getRandomChineseIP() throws IOException {
        List<String> provinces = new ArrayList<>(ipDB.keySet());
        int i = random.nextInt(provinces.size());
        String province = provinces.get(i);

        JSONArray ranges = ipDB.getJSONArray(province);
        int i1 = random.nextInt(ranges.size());
        JSONObject jsonObject = ranges.getJSONObject(i1);
        Long min = ipToLong(jsonObject.getString("min"));
        Long max = ipToLong(jsonObject.getString("max"));

        // data accuracy loss, maybe a bug
        long l = random.nextInt((int) (max - min + 1)) + min;
        return longToIp(l);
    }

    /**
     * @return returns geo for ip, null if not exists in db.
     */
    public String getGeoForIP(String ip) throws IOException {
        InetAddress ipAddress = InetAddress.getByName(ip);
        CityResponse response = null;
        try {
            response = geoDB.city(ipAddress);
        } catch (GeoIp2Exception e) {
            System.out.println("not found ip address in db.");
        }
        if (response == null) {
            return null;
        }
        Location location = response.getLocation();
        return location.getLatitude() + "," + location.getLongitude() +"," + response.getCity().getNames().get("zh-CN");
    }

    private static String longToIp(long ip) {
        long ip4 = ip & 0xFF;
        long ip3 = (ip >> 8) & 0xFF;
        long ip2 = (ip >> 16) & 0xFF;
        long ip1 = (ip >> 24) & 0xFF;
        return String.format("%d.%d.%d.%d", ip1, ip2, ip3, ip4);
    }

    private static Long ipToLong(String ip) {
        if (ip == null || ip.equals("")) {
            return -1L;
        }
        Long ips = 0L;
        String[] numbers = ip.split("\\.");

        for (int i = 0; i < 4; ++i) {
            ips = ips << 8 | Integer.parseInt(numbers[i]);
        }
        return ips;
    }

    public String[] getRandomGeoIP() throws IOException {
        String randomChineseIP = getRandomChineseIP();
        String geoForIP = getGeoForIP(randomChineseIP);

        String[] ret = new String[]{randomChineseIP, geoForIP};
        while (ret[1] == null) {
            ret = getRandomGeoIP();
        }
        return ret;
    }
}
