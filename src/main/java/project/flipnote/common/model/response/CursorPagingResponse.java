package project.flipnote.common.model.response;

import java.util.List;
import java.util.Objects;

public record CursorPagingResponse<T>(
	List<T> content,
	boolean hasNext,
	String nextCursor,
	int size
) {

	public static <T> CursorPagingResponse<T> of(List<T> content, boolean hasNext, String nextCursor) {
		return new CursorPagingResponse<>(content, hasNext, hasNext ? nextCursor : null, content.size());
	}

	public static <T> CursorPagingResponse<T> of(List<T> content, boolean hasNext, Long nextCursorId) {
		String nextCursor = Objects.toString(nextCursorId, null);
		return of(content, hasNext, nextCursor);
	}
}
