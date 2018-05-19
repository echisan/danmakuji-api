package cc.dmji.api;

import org.junit.Test;

import java.util.Date;

/**
 * Created by echisan on 2018/5/16
 */
public class DateTest {

    @Test
    public void dateTest(){
        long expire = 3600;
        long now = new Date().getTime();
        Date nowDate = new Date(now + expire * 1000);
        System.out.println(nowDate);
    }
}
