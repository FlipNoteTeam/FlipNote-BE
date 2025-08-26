package project.flipnote.cardset.model;

import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import lombok.Getter;
import lombok.Setter;
import project.flipnote.common.model.request.PagingRequest;

@Getter
@Setter
public class CardSetSearchRequest extends PagingRequest {

	private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id");

	private String keyword;
	private String category;

	@Override
	public PageRequest getPageRequest() {
		String sortBy = this.getSortBy();
		String effectiveSortBy = (sortBy != null && ALLOWED_SORT_FIELDS.contains(sortBy)) ? sortBy : "id";

		Sort.Direction direction;
		try {
			direction = Sort.Direction.fromString(this.getOrder());
		} catch (IllegalArgumentException e) {
			direction = Sort.Direction.DESC;
		}

		return PageRequest.of(getPage() - 1, getSize(), Sort.by(direction, effectiveSortBy));
	}
}
