package project.flipnote.group.model;

import project.flipnote.group.entity.GroupMemberRole;

public record GroupMemberInfo(
	Long id,
	GroupMemberRole role,
	String name,
	String profile
) {
	public static GroupMemberInfo from(Long id, GroupMemberRole role, String name, String profile) {
		return new GroupMemberInfo(id, role, name, profile);
	}
}
