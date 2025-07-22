package project.flipnote.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import project.flipnote.user.entity.User;
import project.flipnote.user.entity.UserStatus;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(String email);

	boolean existsByPhone(String phone);

	Optional<User> findByIdAndStatus(Long id, UserStatus userStatus);

	Optional<User> findByEmailAndStatus(String email, UserStatus status);

	@Query("SELECT u.tokenVersion FROM User u WHERE u.id = :userId")
	Optional<Long> findTokenVersionById(@Param("userId") Long userId);

	@Modifying
	@Query("UPDATE User u SET u.tokenVersion = u.tokenVersion + 1 WHERE u.id = :id")
	void incrementTokenVersion(@Param("id") Long userId);
}
