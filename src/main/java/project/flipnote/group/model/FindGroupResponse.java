package project.flipnote.group.model;

import java.util.List;

public record FindGroupResponse(
	List<GroupInfo> groups,
	Long next
) {
	public static FindGroupResponse from(List<GroupInfo> groups, Long next) {
		return new FindGroupResponse(groups, next);
	}
}
