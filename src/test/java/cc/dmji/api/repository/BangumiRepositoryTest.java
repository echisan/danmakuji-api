package cc.dmji.api.repository;

import cc.dmji.api.entity.Bangumi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BangumiRepositoryTest {

    @Autowired
    BangumiRepository bangumiRepository;

    @Test
    public void findBangumisByBangumiNameLike() {
//        Page<Bangumi> pages = bangumiRepository.findBangumisByBangumiNameLike("%M%",PageRequest.of(2,20));
//        System.out.println("count:"+pages.getTotalElements());
//        System.out.println("total pages:"+pages.getTotalPages());
//        System.out.println("size:"+pages.getSize());
//        System.out.println("numberOfElements:"+pages.getNumberOfElements());
//        System.out.println("members:"+pages.getContent());

    }
}