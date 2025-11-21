package project.flipnote.group.repository;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import project.flipnote.group.entity.Category;
import project.flipnote.group.model.GroupInfo;

public interface GroupRepositoryCustom {
	List<GroupInfo> findAllByCursor(Long lastId, Category category, int pageSize);

	List<GroupInfo> findAllByCursorAndUserId(Long lastId, Category category, int pageSize, Long userId);

	List<GroupInfo> findAllByCursorAndCreatedUserId(Long cursorId, Category category, int size, Long id);
}
