package project.flipnote.like.model;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import lombok.Getter;
import lombok.Setter;
import project.flipnote.common.model.request.PagingRequest;

@Getter
@Setter
public class LikeSearchRequest extends PagingRequest {

	@Override
	public PageRequest getPageRequest() {
		return PageRequest.of(getPage() - 1, getSize(), Sort.by(Sort.Direction.DESC, "id"));
	}
}
