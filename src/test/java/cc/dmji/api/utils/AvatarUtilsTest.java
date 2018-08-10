package cc.dmji.api.utils;

import org.junit.Test;

import java.io.IOException;
import java.util.Base64;

import static org.junit.Assert.*;

public class AvatarUtilsTest {

    @Test
    public void create() throws IOException {
//        <img src="">
        String prefix = "<img src=\"";
        String end = "\">";
        for (int i = 1; i < 50; i++) {
            String base64Png = AvatarUtils.createBase64Png(i);
            System.out.println(prefix+base64Png+end);
        }


    }
}