package project.flipnote.group.model;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import lombok.Getter;
import lombok.Setter;
import project.flipnote.common.model.request.CursorPagingRequest;

@Setter
@Getter
public class GroupListRequest extends CursorPagingRequest {

	private String category;

	@Override
	public PageRequest getPageRequest() {
		return PageRequest.of(0, getSize(), Sort.by(Sort.Direction.DESC, "id"));
	}
}
