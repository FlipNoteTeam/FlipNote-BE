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

	private static final Set<String> ALLOWED_SORT_FIELDS = CardSetSortField.getFieldNames();

	private String keyword;
	private String category;

	@Override
	public PageRequest getPageRequest() {
		return PageRequest.of(getPage() - 1, getSize(), Sort.by(getOrder(), getSortBy()));
	}

	@Override
	public String getSortBy() {
		String sortBy = super.getSortBy();
		if (sortBy != null && ALLOWED_SORT_FIELDS.contains(sortBy)) {
			return sortBy;
		}

		return "ID";
	}
}
