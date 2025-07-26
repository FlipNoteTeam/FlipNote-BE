package project.flipnote.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import project.flipnote.user.entity.UserOAuthLink;

public interface UserOAuthLinkRepository extends JpaRepository<UserOAuthLink, Long> {

	boolean existsByUser_IdAndProviderId(Long userId, String providerId);
}
