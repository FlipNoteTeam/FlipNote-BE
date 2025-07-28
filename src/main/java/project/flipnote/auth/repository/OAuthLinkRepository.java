package project.flipnote.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import project.flipnote.auth.entity.OAuthLink;

public interface OAuthLinkRepository extends JpaRepository<OAuthLink, Long> {

	boolean existsByAccount_IdAndProviderId(Long accountId, String providerId);

	List<OAuthLink> findByAccountId(Long accountId);

	boolean existsByIdAndAccount_Id(Long id, Long accountId);

	@Query("""
		SELECT uol
		FROM OAuthLink uol
		JOIN FETCH uol.account
		WHERE uol.provider = :provider
		AND uol.providerId = :providerId
		""")
	Optional<OAuthLink> findByProviderAndProviderIdWithAccount(
		@Param("provider") String provider,
		@Param("providerId") String providerId
	);

}
