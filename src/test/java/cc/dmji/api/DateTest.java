package cc.dmji.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

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

    @Test
    public void timestampTest(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(timestamp);
    }

    @Test
    public void aVoid(){
        Long floor = 123L;
        Long curFloor = 23L;
        Long between = floor - curFloor;
        System.out.println((between+20)/20L);
        Integer page = Math.toIntExact((between + 20) / 20L);
        System.out.println(page);
    }

    @Test
    public void sql(){

        List<String> parentIds = new ArrayList<>();
        parentIds.add("4028ef816446fc6c01644701228e000f");
        parentIds.add("4028ef8164470a1f0164473a3cda0004");
        parentIds.add("4028ef816446fc6c01644701e3250022");

        StringBuilder sqlBuilder = new StringBuilder();

        String sql1 = "select * from dm_reply \n";
        String sql2 = "right join dm_user on dm_reply.user_id = dm_user.user_id \n";
        String sql3 = "where r_status='NORMAL'and parent_id in ( ";

        sqlBuilder.append(sql1).append(sql2).append(sql3);

        for (int i = 0; i < parentIds.size() - 1; i++) {
            String id = parentIds.get(i);
            String sep = ", ";
            sqlBuilder.append(id).append(sep);
        }
        String lastId = parentIds.get(parentIds.size() - 1);
        sqlBuilder.append(lastId).append(" ) ");
        String sql4 = "order by dm_reply.create_time";
        sqlBuilder.append(sql4);

        System.out.println(sqlBuilder.toString());



    }

    @Test
    public void enumTest(){
        Sex sex = Sex.valueOf("MAN");
        System.out.println(sex);
        Sex sex1 = Sex.valueOf("aa");
        System.out.println(sex1);
    }

    enum Sex{
        MAN,
        GRIL
    }

    @Test
    public void mapTest() throws JsonProcessingException {
        Map<String, Long> data = new HashMap<>();
        data.put("reply", 0L);
        data.put("system", 1L);
        data.put("at", 22L);
        data.put("like", 999L);
        data.put("total", 222L);
        String s = new ObjectMapper().writeValueAsString(data);
        System.out.println(s);

    }

    @Test
    public void jsonToMap() throws IOException {
        String json = "{\"total\":222,\"system\":1,\"at\":22,\"like\":999,\"reply\":0}";
       Map<String,Long> map = new ObjectMapper().readValue(json, new TypeReference<Map<String, Long>>() {});
        System.out.println(map);
    }

}
