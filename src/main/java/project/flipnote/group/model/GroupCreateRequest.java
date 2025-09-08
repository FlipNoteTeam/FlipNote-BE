package project.flipnote.group.model;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.*;
import project.flipnote.group.entity.Category;

public record GroupCreateRequest(
	@NotBlank(message = "그룹 이름을 입력해주세요.")
	@Size(max = 50, message = "그룹 이름은 최대 50자까지 입력할 수 있습니다.")
	String name,

	@NotNull(message = "그룹 카테고리를 선택해야 합니다.")
	Category category,

	@NotBlank(message = "그룹 설명을 입력해주세요.")
	@Size(max = 150, message = "그룹 설명은 최대 150자까지 입력할 수 있습니다.")
	String description,

	@NotNull(message = "가입 승인 필요 여부를 선택해주세요.")
	Boolean applicationRequired,

	@NotNull(message = "공개 여부를 선택해주세요.")
	Boolean publicVisible,

	@NotNull(message = "최대 인원 수를 입력해주세요.")
	@Min(value = 1, message = "최대 인원 수는 1명 이상이어야 합니다.")
	@Max(value = 100, message = "최대 인원 수는 100명을 초과할 수 없습니다.")
	Integer maxMember,

	@NotNull(message = "이미지 참조 id를 입력해주세요.")
	Long imageRefId
) {
}
