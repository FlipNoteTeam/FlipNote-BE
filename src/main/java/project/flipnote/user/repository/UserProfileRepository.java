package project.flipnote.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import project.flipnote.user.entity.UserProfile;
import project.flipnote.user.entity.UserStatus;
import project.flipnote.user.model.UserIdNickname;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

	boolean existsByEmail(String email);

	boolean existsByPhone(String phone);

	Optional<UserProfile> findByIdAndStatus(Long userId, UserStatus status);

	Optional<UserProfile> findByEmailAndStatus(String email, UserStatus status);

	List<UserIdNickname> findIdAndNicknameByIdIn(List<Long> ids);

	@Query("SELECT up.nickname FROM UserProfile up WHERE up.id = :userId")
	Optional<String> findNicknameById(@Param("userId") Long userId);

	boolean existsByIdAndStatus(Long userId, UserStatus userStatus);
}
