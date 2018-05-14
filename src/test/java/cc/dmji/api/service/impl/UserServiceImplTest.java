package cc.dmji.api.service.impl;

import cc.dmji.api.entity.User;
import cc.dmji.api.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Rollback(value = true)
public class UserServiceImplTest {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImplTest.class);

    @Autowired
    private UserService userService;

    @Test
    public void insertUser() {
        User user = new User();
        user.setEmailVerified((byte) 0);
        user.setEmail("example1@email.com");
        user.setPwd("123345");
        user.setPhone("1123456576");
        user.setNick("dick");
        user.setAge(11);
        user.setFace("face/1/aa.jpg");
        user.setRole("USER");
        User resultUser = userService.insertUser(user);
        Assert.assertNotNull(resultUser);
    }

    @Test
    public void deleteUserById() {
        userService.deleteUserById("4028e381635e269c01635e26b1a80000");
    }

    @Test
    public void deleteUserByNick() {
        User resultUser = userService.deleteUserByNick("dick");
        User user = userService.getUserByNick("dick");
        assertNull(user);
    }

    @Test
    public void deleteUserByEmail() {
        User resultUser = userService.deleteUserByEmail("example1@email.com");
        User user = userService.getUserByEmail("example1@email.com");
        assertNull(user);
    }

    @Test
    public void updateUser() {
        User user = userService.getUserByNick("dick");
        user.setEmail("exampleLater@email.com");
        User user1 = userService.updateUser(user);
        assertEquals(user.getEmail(),user1.getEmail());
    }

    @Test
    public void getUserById() {
        User user = userService.getUserById("4028e381635e269c01635e26b1a80000");
        assertNotNull(user);
    }

    @Test
    public void getUserByNick() {
        User user = userService.getUserByNick("dick");
        assertNotNull(user);
    }

    @Test
    public void getUserByEmail() {
        User user = userService.getUserByEmail("example1@email.com");
        assertNotNull(user);
    }

    @Test
    public void listUser() {
        userService.listUser(0,10);
    }
}
