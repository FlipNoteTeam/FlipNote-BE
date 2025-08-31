package project.flipnote.groupjoin.controller.docs;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.groupjoin.model.FindGroupJoinListMeResponse;
import project.flipnote.groupjoin.model.GroupJoinListResponse;
import project.flipnote.groupjoin.model.GroupJoinRequest;
import project.flipnote.groupjoin.model.GroupJoinRespondRequest;
import project.flipnote.groupjoin.model.GroupJoinRespondResponse;
import project.flipnote.groupjoin.model.GroupJoinResponse;

@Tag(name = "그룹 가입신청", description = "그룹 가입신청 관리 API")
@SecurityRequirement(name = "access-token")
public interface GroupJoinControllerDocs {
	@Operation(
		summary = "가입 신청 요청",
		description = "공개 그룹에 대해 가입 신청을 생성합니다. 그룹 정책에 따라 PENDING 또는 즉시 ACCEPT로 저장됩니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "가입 신청 생성 성공",
			content = @Content(schema = @Schema(implementation = GroupJoinResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 (검증 실패 등)"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "403", description = "비공개 그룹 → GROUP_JOIN_003",
			content = @Content(schema = @Schema(example = "{\"code\":\"GROUP_JOIN_003\",\"message\":\"그룹이 비공개입니다.\"}"))),
		@ApiResponse(responseCode = "404", description = "그룹 없음"),
		@ApiResponse(responseCode = "409", description = "이미 가입 신청 존재 → GROUP_JOIN_005 / 정원 초과 → GROUP_JOIN_006",
			content = @Content(schema = @Schema(example = "{\"code\":\"GROUP_JOIN_005\",\"message\":\"이미 신청한 그룹입니다.\"}")))
	})
	ResponseEntity<GroupJoinResponse> joinRequest(
		@Parameter(hidden = true) @AuthenticationPrincipal AuthPrinciple authPrinciple,
		@Parameter(description = "그룹 ID", example = "123") Long groupId,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "가입 신청 요청 데이터",
			required = true,
			content = @Content(schema = @Schema(implementation = GroupJoinRequest.class))
		)
		@Valid GroupJoinRequest req
	);

	@Operation(
		summary = "그룹 내 가입 신청 리스트 조회",
		description = "가입신청 관리 권한을 가진 멤버가 그룹 내 신청 내역을 조회합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = GroupJoinListResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "403", description = "권한 없음 → GROUP_JOIN_002",
			content = @Content(schema = @Schema(example = "{\"code\":\"GROUP_JOIN_002\",\"message\":\"그룹 내 권한이 없습니다.\"}"))),
		@ApiResponse(responseCode = "404", description = "그룹 없음")
	})
	ResponseEntity<GroupJoinListResponse> findGroupJoinList(
		@Parameter(hidden = true) @AuthenticationPrincipal AuthPrinciple authPrinciple,
		@Parameter(description = "그룹 ID", example = "123") Long groupId
	);

	@Operation(
		summary = "가입 신청 응답",
		description = "그룹 관리 권한자가 특정 가입 신청을 승인(ACCEPT) 또는 거절(DENY)합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "응답 처리 성공",
			content = @Content(schema = @Schema(implementation = GroupJoinRespondResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "403", description = "권한 없음 → GROUP_JOIN_002"),
		@ApiResponse(responseCode = "404", description = "가입신청 없음 → GROUP_JOIN_004"),
		@ApiResponse(responseCode = "409", description = "정원 초과 → GROUP_JOIN_006",
			content = @Content(schema = @Schema(example = "{\"code\":\"GROUP_JOIN_006\",\"message\":\"그룹 정원이 가득 찼습니다.\"}")))
	})
	ResponseEntity<GroupJoinRespondResponse> respondToJoinRequest(
		@Parameter(hidden = true) @AuthenticationPrincipal AuthPrinciple authPrinciple,
		@Parameter(description = "그룹 ID", example = "123") Long groupId,
		@Parameter(description = "가입신청 ID", example = "456") Long joinId,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "승인/거절 상태",
			required = true,
			content = @Content(schema = @Schema(implementation = GroupJoinRespondRequest.class))
		)
		@Valid GroupJoinRespondRequest req
	);

	//가입 신청 삭제
	@Operation(
		summary = "가입 신청 삭제(취소)",
		description = "신청자가 자신의 가입 신청을 취소합니다. 실제 삭제가 아닌 상태를 CANCEL로 변경합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "취소 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "403", description = "권한 없음(본인 아님/그룹 불일치 등)"),
		@ApiResponse(responseCode = "404", description = "가입신청 없음")
	})
	public ResponseEntity<Void> groupJoinDelete(
		@Parameter(hidden = true) @AuthenticationPrincipal AuthPrinciple authPrinciple,
		@Parameter(description = "그룹 ID", required = true, example = "123") Long groupId,
		@Parameter(description = "가입신청 ID", required = true, example = "456") Long joinId
	);

	//내가 신청한 가입신청 리스트 조회
	@Operation(
		summary = "내가 신청한 가입신청 리스트 조회",
		description = "현재 사용자 기준으로 본인이 신청한 가입신청 목록을 조회합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = FindGroupJoinListMeResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	public ResponseEntity<FindGroupJoinListMeResponse> findGroupJoinMe(
		@Parameter(hidden = true) @AuthenticationPrincipal AuthPrinciple authPrinciple
	);
}
