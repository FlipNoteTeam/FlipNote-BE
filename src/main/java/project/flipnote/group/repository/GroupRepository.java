package project.flipnote.group.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import project.flipnote.group.entity.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

  Optional<Group> findByIdAndDeletedAtIsNull(Long groupId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select g from Group g where g.id = :id")
	Optional<Group> findByIdForUpdate(@Param("id") Long id);

}
