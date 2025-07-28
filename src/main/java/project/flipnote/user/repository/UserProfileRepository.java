package project.flipnote.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import project.flipnote.user.entity.UserProfile;
import project.flipnote.user.entity.UserStatus;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

	boolean existsByEmail(String email);

	boolean existsByPhone(String phone);

	Optional<UserProfile> findByIdAndStatus(Long userId, UserStatus status);
}
