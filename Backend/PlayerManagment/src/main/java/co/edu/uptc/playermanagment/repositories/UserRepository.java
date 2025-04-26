package co.edu.uptc.playermanagment.repositories;

import co.edu.uptc.playermanagment.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);

    User findUserByEmail(String email);
}
