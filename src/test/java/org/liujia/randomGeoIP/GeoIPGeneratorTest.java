package org.liujia.randomGeoIP;

import com.maxmind.geoip2.exception.GeoIp2Exception;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by liujia on 2018/4/18.
 */
public class GeoIPGeneratorTest {

    public static void main(String[] args) throws IOException, URISyntaxException, GeoIp2Exception {
        GeoIPGenerator.init();
        GeoIPGenerator generator = new GeoIPGenerator();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            String[] randomGeoIP = generator.getRandomGeoIP();
            System.out.println(randomGeoIP[0] + "   " + randomGeoIP[1]);
        }
        System.out.println(String.format("cost: %sms", System.currentTimeMillis() - start));
    }

}