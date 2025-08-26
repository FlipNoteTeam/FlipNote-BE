package project.flipnote.group.model;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import project.flipnote.common.model.request.PagingRequest;

public class GroupInvitationListRequest extends PagingRequest {

	@Override
	public PageRequest getPageRequest() {
		return PageRequest.of(getPage() - 1, getSize() + 1, Sort.by(Sort.Direction.DESC, "id"));
	}
}
