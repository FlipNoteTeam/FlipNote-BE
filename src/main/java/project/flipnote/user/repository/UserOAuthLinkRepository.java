package project.flipnote.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import project.flipnote.user.entity.UserOAuthLink;

public interface UserOAuthLinkRepository extends JpaRepository<UserOAuthLink, Long> {

	boolean existsByUser_IdAndProviderId(Long userId, String providerId);

	List<UserOAuthLink> findByUser_Id(Long userId);

	boolean existsByIdAndUser_Id(Long id, Long userId);
}
