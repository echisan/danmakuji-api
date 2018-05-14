package cc.dmji.api.repository;

import cc.dmji.api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    User getUserByNick(String nick);

    User getUserByEmail(String email);

    User deleteUserByNick(String nick);

    User deleteUserByEmail(String email);
}
