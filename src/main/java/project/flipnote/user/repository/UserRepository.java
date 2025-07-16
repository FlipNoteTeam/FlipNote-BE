package project.flipnote.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import project.flipnote.user.entity.User;
import project.flipnote.user.entity.UserStatus;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(String email);

	boolean existsByPhone(String phone);

	Optional<User> findByEmailAndStatus(String email, UserStatus status);

	Optional<User> findByIdAndStatus(Long userId, UserStatus status);
}
