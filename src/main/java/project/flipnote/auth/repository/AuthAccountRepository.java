package project.flipnote.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import project.flipnote.auth.entity.AccountStatus;
import project.flipnote.auth.entity.AuthAccount;

public interface AuthAccountRepository extends JpaRepository<AuthAccount, Long> {

	boolean existsByEmail(String email);

	Optional<AuthAccount> findByEmailAndStatus(String email, AccountStatus status);

	boolean existsByEmailAndStatus(String email, AccountStatus status);

	@Modifying
	@Query("UPDATE AuthAccount aa SET aa.password = :password WHERE aa.email = :email")
	void updatePassword(@Param("email") String email, @Param("password") String password);

	Optional<AuthAccount> findByIdAndStatus(Long accountId, AccountStatus status);

	@Query("SELECT aa.tokenVersion FROM AuthAccount aa WHERE aa.id = :accountId")
	Optional<Long> findTokenVersionById(@Param("accountId") Long accountId);

	@Modifying
	@Query("UPDATE AuthAccount aa SET aa.tokenVersion = aa.tokenVersion + 1 WHERE aa.id = :userId")
	void incrementTokenVersion(@Param("userId") Long userId);
}
