package project.flipnote.group.repository;

import java.util.List;

import project.flipnote.group.entity.Category;
import project.flipnote.group.model.GroupInfo;

public interface GroupRepositoryCustom {
	List<GroupInfo> findAllByCursor(Long lastId, Category category, int pageSize);
}
