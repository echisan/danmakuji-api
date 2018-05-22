package cc.dmji.api.service.impl;

import cc.dmji.api.entity.User;
import cc.dmji.api.repository.UserRepository;
import cc.dmji.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by echisan on 2018/5/14
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User insertUser(User user) {
        // 加密密码存储
        String newPass = bCryptPasswordEncoder.encode(user.getPwd());
        user.setPwd(newPass);
        Timestamp ts = getTimestamp();
        user.setCreateTime(ts);
        user.setModifyTime(ts);
        return userRepository.save(user);
    }

    @Override
    public void deleteUserById(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public User deleteUserByNick(String nick) {
        return userRepository.deleteUserByNick(nick);
    }

    @Override
    public User deleteUserByEmail(String email) {
        return userRepository.deleteUserByEmail(email);
    }

    @Override
    public User updateUser(User user) {
        user.setModifyTime(new Timestamp(System.currentTimeMillis()));
        return userRepository.save(user);
    }

    @Override
    public User getUserById(String id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    @Override
    public User getUserByNick(String nick) {
        return userRepository.getUserByNick(nick);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    @Override
    public Page<User> listUser(Integer page, Integer size) {
        Sort sort = new Sort(Sort.Direction.ASC, "userId");
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findAll(pageable);
    }

    @Override
    public List<User> listUser() {
        return userRepository.findAll();
    }

    private Timestamp getTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
}
