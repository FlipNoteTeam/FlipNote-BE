package project.flipnote.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import project.flipnote.auth.entity.AccountStatus;
import project.flipnote.auth.entity.UserAuth;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {

	boolean existsByEmail(String email);

	Optional<UserAuth> findByEmailAndStatus(String email, AccountStatus status);

	boolean existsByEmailAndStatus(String email, AccountStatus status);

	@Modifying
	@Query("UPDATE UserAuth aa SET aa.password = :password WHERE aa.email = :email")
	void updatePassword(@Param("email") String email, @Param("password") String password);

	Optional<UserAuth> findByIdAndStatus(Long authId, AccountStatus status);

	@Query("SELECT aa.tokenVersion FROM UserAuth aa WHERE aa.userId = :userId")
	Optional<Long> findTokenVersionById(@Param("userId") Long userId);

	@Modifying
	@Query("UPDATE UserAuth aa SET aa.tokenVersion = aa.tokenVersion + 1 WHERE aa.userId = :userId")
	void incrementTokenVersion(@Param("userId") Long userId);
}
