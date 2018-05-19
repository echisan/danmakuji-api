package cc.dmji.api.service;

import cc.dmji.api.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by echisan on 2018/5/14
 */
public interface UserService {

    User insertUser(User user);

    void deleteUserById(String id);

    User deleteUserByNick(String nick);

    User deleteUserByEmail(String email);

    User updateUser(User user);

    User getUserById(String id);

    User getUserByNick(String nick);

    User getUserByEmail(String email);

    Page<User> listUser(Integer page, Integer size);

    List<User> listUser();

}
