package project.flipnote.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import project.flipnote.auth.entity.OAuthLink;

public interface OAuthLinkRepository extends JpaRepository<OAuthLink, Long> {

	boolean existsByUserAuth_IdAndProviderAndProviderId(Long authId, String provider, String providerId);

	List<OAuthLink> findByUserAuth_Id(Long authId);

	boolean existsByIdAndUserAuth_Id(Long id, Long authId);

	@Query("""
		SELECT uol
		FROM OAuthLink uol
		JOIN FETCH uol.userAuth
		WHERE uol.provider = :provider
		AND uol.providerId = :providerId
		""")
	Optional<OAuthLink> findByProviderAndProviderIdWithUserAuth(
		@Param("provider") String provider,
		@Param("providerId") String providerId
	);

}
