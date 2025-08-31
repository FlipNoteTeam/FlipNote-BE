package project.flipnote.groupjoin.repository;

import java.util.List;

import project.flipnote.groupjoin.model.GroupJoinInfo;
import project.flipnote.groupjoin.model.MyGroupJoinInfo;

public interface GroupJoinRepositoryCustom {
	List<GroupJoinInfo> findByGroup(Long groupId);

	List<MyGroupJoinInfo> findByUser(Long userId);
}
