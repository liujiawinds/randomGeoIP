package org.liujia.randomGeoIP;

import com.maxmind.geoip2.exception.GeoIp2Exception;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by liujia on 2018/4/18.
 */
public class BootTest {

    public static void main(String[] args) throws IOException, URISyntaxException, GeoIp2Exception {
        Boot.init();
        Boot boot = new Boot();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            String randomChineseIP = boot.getRandomChineseIP();
            System.out.println(randomChineseIP);
            String geoForIP = boot.getGeoForIP(randomChineseIP);
            System.out.println(geoForIP);
        }
        System.out.println(String.format("cost: %sms", System.currentTimeMillis() - start));
    }

}