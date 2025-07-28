package project.flipnote.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import project.flipnote.user.entity.UserOAuthLink;

public interface UserOAuthLinkRepository extends JpaRepository<UserOAuthLink, Long> {

	boolean existsByUser_IdAndProviderId(Long userId, String providerId);

	List<UserOAuthLink> findByUser_Id(Long userId);

	boolean existsByIdAndUser_Id(Long id, Long userId);

	@Query("""
		SELECT uol
		FROM UserOAuthLink uol
		JOIN FETCH uol.user
		WHERE uol.provider = :provider
		  AND uol.providerId = :providerId
		""")
	Optional<UserOAuthLink> findByProviderAndProviderIdWithUser(
		@Param("provider") String provider,
		@Param("providerId") String providerId
	);

}
