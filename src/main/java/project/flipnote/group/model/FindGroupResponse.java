package project.flipnote.group.model;

import java.util.List;

public record FindGroupResponse(
	List<GroupInfo> groups,
	Long next,
	Boolean hasNext
) {
	public static FindGroupResponse from(List<GroupInfo> groups, Long next, Boolean hasNext) {
		return new FindGroupResponse(groups, next, hasNext);
	}
}
