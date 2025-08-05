package project.flipnote.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import project.flipnote.user.entity.UserProfile;
import project.flipnote.user.entity.UserStatus;

public interface UserRepository extends JpaRepository<UserProfile, Long> {

	boolean existsByEmail(String email);

	boolean existsByPhone(String phone);

	Optional<UserProfile> findByIdAndStatus(Long id, UserStatus userStatus);

	Optional<UserProfile> findByEmailAndStatus(String email, UserStatus status);

	@Query("SELECT u.tokenVersion FROM UserProfile u WHERE u.id = :userId")
	Optional<Long> findTokenVersionById(@Param("userId") Long userId);

}
