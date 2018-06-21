package cc.dmji.api.repository;

import cc.dmji.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {

    User getUserByNick(String nick);

    User getUserByEmail(String email);

    User deleteUserByNick(String nick);

    User deleteUserByEmail(String email);

    @Query(value = "select * from dm_user as u where u.nick like ?1 order by u.create_time limit ?2,?3", nativeQuery = true)
    List<User> getUsersByNickLike(String nick, Integer pn, Integer ps);

    Long countByNickLike(String nick);

    Long countByCreateTimeBetween(Date begin, Date end);

}
