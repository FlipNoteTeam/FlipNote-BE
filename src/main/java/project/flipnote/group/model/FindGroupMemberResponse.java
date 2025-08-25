package project.flipnote.group.model;

import java.util.List;

public record FindGroupMemberResponse(
	List<GroupMemberInfo> groupMembers
) {
	public static FindGroupMemberResponse from(List<GroupMemberInfo> groupMembers) {
		return new FindGroupMemberResponse(groupMembers);
	}
}
