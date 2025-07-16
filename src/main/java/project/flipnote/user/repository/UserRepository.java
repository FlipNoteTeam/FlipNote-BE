package project.flipnote.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.lettuce.core.dynamic.annotation.Param;
import project.flipnote.user.entity.User;
import project.flipnote.user.entity.UserStatus;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(String email);

	boolean existsByPhone(String phone);

	Optional<User> findByEmailAndStatus(String email, UserStatus status);

	Optional<User> findByIdAndStatus(Long userId, UserStatus status);

	@Query("SELECT u.tokenVersion FROM User u WHERE u.id = :userId")
	Optional<Long> findTokenVersionById(@Param("userId") Long userId);
}
