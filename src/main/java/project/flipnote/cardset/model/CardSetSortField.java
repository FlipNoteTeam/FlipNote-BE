package project.flipnote.cardset.model;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum CardSetSortField {
	ID, LIKE, BOOKMARK;

	public static Set<String> getFieldNames() {
		return Arrays.stream(values())
			.map(CardSetSortField::name)
			.collect(Collectors.toSet());
	}
}
