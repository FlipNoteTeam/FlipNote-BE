package project.flipnote.user.controller.docs;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.user.model.MyInfoResponse;
import project.flipnote.user.model.UserInfoResponse;
import project.flipnote.user.model.UserUpdateRequest;
import project.flipnote.user.model.UserUpdateResponse;

@Tag(name = "User", description = "User API")
public interface UserControllerDocs {

	@Operation(summary = "회원 탈퇴", security = { @SecurityRequirement(name = "access-token") })
	ResponseEntity<Void> withdraw(AuthPrinciple userAuth);

	@Operation(summary = "회원 정보 수정", security = { @SecurityRequirement(name = "access-token") })
	ResponseEntity<UserUpdateResponse> update(AuthPrinciple userAuth, UserUpdateRequest req);

	@Operation(summary = "내 정보 조회", security = { @SecurityRequirement(name = "access-token") })
	ResponseEntity<MyInfoResponse> getMyInfo(AuthPrinciple userAuth);

	@Operation(summary = "회원 정보 조회", security = { @SecurityRequirement(name = "access-token") })
	ResponseEntity<UserInfoResponse> getUserInfo(Long userId);
}
