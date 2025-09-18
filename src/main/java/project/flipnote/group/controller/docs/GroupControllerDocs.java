package project.flipnote.group.controller.docs;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import project.flipnote.common.model.response.CursorPagingResponse;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.group.model.FindGroupMemberResponse;
import project.flipnote.group.model.GroupCreateRequest;
import project.flipnote.group.model.GroupCreateResponse;
import project.flipnote.group.model.GroupDetailResponse;
import project.flipnote.group.model.GroupInfo;
import project.flipnote.group.model.GroupListRequest;
import project.flipnote.group.model.GroupPutRequest;
import project.flipnote.group.model.GroupPutResponse;

@Tag(name = "그룹", description = "그룹 생성/수정/상세/삭제/멤버/목록 API")
@SecurityRequirement(name = "access-token")
public interface GroupControllerDocs {

	//그룹 생성
	@Operation(
		summary = "그룹 생성",
		description = "새 그룹을 생성하고 생성자를 OWNER로 등록합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "생성 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = GroupCreateResponse.class),
				examples = @ExampleObject(name = "성공", value = "{\"groupId\":123}")
			)
		),
		@ApiResponse(responseCode = "400", description = "잘못된 요청(최대 인원/카테고리 등)"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	ResponseEntity<GroupCreateResponse> create(
		@Parameter(hidden = true) @AuthenticationPrincipal AuthPrinciple authPrinciple,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "그룹 생성 요청 바디",
			required = true,
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = GroupCreateRequest.class),
				examples = @ExampleObject(name = "요청 예시", value = """
                {
                  "name": "백엔드 스터디",
                  "category": "IT",
                  "description": "스프링/인프라 중심의 백엔드 스터디 그룹입니다.",
                  "applicationRequired": true,
                  "publicVisible": true,
                  "maxMember": 20,
                  "imageRefId": 1
                }
                """)
			)
		)
		@Valid GroupCreateRequest req
	);

	//그룹 수정
	@Operation(
		summary = "그룹 수정",
		description = "기존 그룹 정보를 수정합니다. 오너만 가능합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "수정 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = GroupPutResponse.class),
				examples = @ExampleObject(name = "응답 예시", value = """
                {
                  "name": "백엔드 스터디(수정)",
                  "category": "IT",
                  "description": "소개 수정",
                  "applicationRequired": false,
                  "publicVisible": true,
                  "maxMember": 30,
                  "imageUrl": "https://cdn.example.com/group/cover_v2.png",
                  "createdAt": "2025-08-20T12:34:56",
                  "modifiedAt": "2025-08-31T16:10:00"
                }
                """)
			)
		),
		@ApiResponse(responseCode = "400", description = "잘못된 요청(최대 인원/카테고리 등)"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "403", description = "권한 없음(오너 아님)"),
		@ApiResponse(responseCode = "404", description = "그룹 없음")
	})
	ResponseEntity<GroupPutResponse> changeGroup(
		@Parameter(hidden = true) @AuthenticationPrincipal AuthPrinciple authPrinciple,
		@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "그룹 수정 요청 바디",
			required = true,
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = GroupPutRequest.class),
				examples = @ExampleObject(name = "요청 예시", value = """
                {
                  "name": "백엔드 스터디(수정)",
                  "category": "IT",
                  "description": "소개 수정",
                  "applicationRequired": false,
                  "publicVisible": true,
                  "maxMember": 30,
                  "imageRefId": 1
                }
                """)
			)
		)
		@Valid GroupPutRequest req,
		@Parameter(description = "그룹 ID", required = true, example = "1") Long groupId
	);

	@Operation(summary = "그룹 상세", description = "그룹 상세 정보를 조회합니다. 그룹 멤버만 접근 가능합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = GroupDetailResponse.class)
			)
		),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "404", description = "그룹 없음/그룹 내 유저 없음")
	})
	ResponseEntity<GroupDetailResponse> findGroupDetail(
		@Parameter(hidden = true) @AuthenticationPrincipal AuthPrinciple authPrinciple,
		@Parameter(description = "그룹 ID", required = true, example = "1") Long groupId
	);

	@Operation(summary = "그룹 삭제", description = "오너만 그룹을 삭제할 수 있습니다. 오너 외 멤버가 존재하면 삭제 불가입니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "삭제 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "403", description = "권한 없음(오너 아님)"),
		@ApiResponse(responseCode = "404", description = "그룹 없음"),
		@ApiResponse(responseCode = "409", description = "오너 외 멤버 존재")
	})
	ResponseEntity<Void> deleteGroup(
		@Parameter(hidden = true) @AuthenticationPrincipal AuthPrinciple authPrinciple,
		@Parameter(description = "그룹 ID", required = true, example = "123") Long groupId
	);

	@Operation(summary = "그룹내 멤버 조회", description = "그룹 멤버 목록을 조회합니다. 그룹 멤버만 접근 가능합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = FindGroupMemberResponse.class)
			)
		),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "404", description = "그룹 없음/그룹 내 유저 없음")
	})
	ResponseEntity<FindGroupMemberResponse> findGroupMembers(
		@Parameter(hidden = true) @AuthenticationPrincipal AuthPrinciple authPrinciple,
		@Parameter(description = "그룹 ID", required = true, example = "123") Long groupId
	);

	@Operation(summary = "그룹 전체 조회(커서 페이징)", description = "카테고리/커서/사이즈로 그룹 목록을 조회합니다.")
	@Parameters({
		@Parameter(
			name = "cursor",
			description = "커서 ID (이전 응답의 nextCursor). 기본값: null",
			example = "40",
			schema = @Schema(nullable = true)
		),
		@Parameter(
			name = "size",
			description = "페이지 크기. 기본값: 10",
			example = "10",
			schema = @Schema(defaultValue = "10", minimum = "1")
		),
		@Parameter(
			name = "category",
			description = "카테고리 필터 (예: IT). 기본값: null",
			example = "IT",
			schema = @Schema(nullable = true)
		),
		@Parameter(
			name = "sortBy",
			description = "(더미) 현재 미사용",
			example = "string",
			deprecated = true
		),
		@Parameter(
			name = "order",
			description = "(더미) 현재 미사용",
			example = "string",
			deprecated = true
		)
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = CursorPagingResponse.class)
			)
		),
		@ApiResponse(responseCode = "400", description = "잘못된 요청(카테고리 등)"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	ResponseEntity<CursorPagingResponse<GroupInfo>> findGroup(
		@Parameter(hidden = true) @AuthenticationPrincipal AuthPrinciple authPrinciple,
		@org.springdoc.core.annotations.ParameterObject
		@Valid GroupListRequest req
	);

	@Operation(summary = "내 그룹 전체 조회(커서 페이징)", description = "현재 사용자 기준으로 가입한 그룹 목록을 커서 페이징으로 조회합니다.")
	@Parameters({
		@Parameter(
			name = "cursor",
			description = "커서 ID (이전 응답의 nextCursor). 기본값: null",
			example = "40",
			schema = @Schema(nullable = true)
		),
		@Parameter(
			name = "size",
			description = "페이지 크기. 기본값: 10",
			example = "10",
			schema = @Schema(defaultValue = "10", minimum = "1")
		),
		@Parameter(
			name = "category",
			description = "카테고리 필터 (예: IT). 기본값: null",
			example = "IT",
			schema = @Schema(nullable = true)
		),
		@Parameter(
			name = "sortBy",
			description = "(더미) 현재 미사용",
			example = "string",
			deprecated = true
		),
		@Parameter(
			name = "order",
			description = "(더미) 현재 미사용",
			example = "string",
			deprecated = true
		)
	})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = CursorPagingResponse.class)
			)
		),
		@ApiResponse(responseCode = "400", description = "잘못된 요청(카테고리 등)"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	ResponseEntity<CursorPagingResponse<GroupInfo>> findMyGroup(
		@Parameter(hidden = true) @AuthenticationPrincipal AuthPrinciple authPrinciple,
		@org.springdoc.core.annotations.ParameterObject
		@Valid GroupListRequest req
	);
}
