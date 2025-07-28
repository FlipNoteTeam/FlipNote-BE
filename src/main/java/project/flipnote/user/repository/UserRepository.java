package project.flipnote.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import project.flipnote.user.entity.UserProfile;

public interface UserRepository extends JpaRepository<UserProfile, Long> {

	boolean existsByEmail(String email);

	boolean existsByPhone(String phone);
}
