package project.flipnote.group.repository;

import java.util.List;

import project.flipnote.group.model.GroupMemberInfo;

public interface GroupMemberRepositoryCustom {
	List<GroupMemberInfo> findGroupMembers(Long groupId);
}
