package cc.dmji.api.service.impl;

import cc.dmji.api.entity.User;
import cc.dmji.api.repository.UserRepository;
import cc.dmji.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
    public List<User> listUser(Integer pn, Integer ps) {

        String sql = "select * from dm_user order by create_time desc limit ?,?";
        Integer limit = pn == 1 ? 0 : (pn - 1) * ps;
        return jdbcTemplate.query(sql, new UserMapper(), limit, ps);
    }

    @Override
    public List<User> listUser() {
        return userRepository.findAll();
    }

    @Override
    public Long countUsers() {
        return userRepository.count();
    }

    @Override
    public List<User> listUsersNickLike(String nick, Integer pn, Integer ps) {
        Integer limit = pn == 1 ? 0 : (pn -1) * ps;
        return userRepository.getUsersByNickLike("%" + nick + "%", limit, ps);
    }

    @Override
    public Long countUsersNickLike(String nick) {
        return userRepository.countByNickLike("%" + nick + "%");
    }

    @Override
    public Long countUsersByCreateTime(Date begin, Date end) {
        return userRepository.countByCreateTimeBetween(begin, end);
    }

    @Override
    public List<User> listUserByIdsIn(List<String> userId) {
        return userRepository.findByUserIdIn(userId);
    }

    private Timestamp getTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    class UserMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setPwd(rs.getString("pwd"));
            user.setFace(rs.getString("face"));
            user.setEmail(rs.getString("email"));
            user.setLockTime(rs.getInt("lock_time"));
            user.setSex(rs.getString("sex"));
            user.setPhone(rs.getString("phone"));
            user.setPhoneVerified(rs.getByte("phone_verified"));
            user.setCreateTime(rs.getTimestamp("create_time"));
            user.setModifyTime(rs.getTimestamp("modify_time"));
            user.setNick(rs.getString("nick"));
            user.setAge(rs.getInt("age"));
            user.setEmailVerified(rs.getByte("email_verified"));
            user.setRole(rs.getString("role"));
            user.setUserId(rs.getString("user_id"));
            user.setIsLock(rs.getByte("is_lock"));
            return user;
        }
    }

    @Override
    public List<User> listUserByNickIn(List<String> usernameList) {
        return userRepository.findByNickIn(usernameList);
    }
}
