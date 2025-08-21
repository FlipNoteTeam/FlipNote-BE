package project.flipnote.common.model.response;

import java.util.List;
import java.util.Objects;

public record CursorPageResponse<T>(
	List<T> content,
	boolean hasNext,
	String nextCursor,
	int size
) {

	public static <T> CursorPageResponse<T> of(List<T> content, boolean hasNext, String nextCursor) {
		return new CursorPageResponse<>(content, hasNext, hasNext ? nextCursor : null, content.size());
	}

	public static <T> CursorPageResponse<T> of(List<T> content, boolean hasNext, Long nextCursorId) {
		String nextCursor = Objects.toString(nextCursorId, null);
		return of(content, hasNext, nextCursor);
	}
}
