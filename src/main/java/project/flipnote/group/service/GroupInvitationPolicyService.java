package project.flipnote.group.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.BizException;
import project.flipnote.group.entity.GroupPermissionStatus;
import project.flipnote.group.exception.GroupInvitationErrorCode;

@RequiredArgsConstructor
@Service
public class GroupInvitationPolicyService {

	private final GroupService groupService;

	/**
	 * 그룹 초대 권한을 검증
	 *
	 * @param userId  권한을 검증할 회원 ID
	 * @param groupId 검증할 그룹 ID
	 * @author 윤정환
	 */
	public void validateGroupInvitePermission(Long userId, Long groupId) {
		if (!groupService.hasPermission(groupId, userId, GroupPermissionStatus.INVITE)) {
			throw new BizException(GroupInvitationErrorCode.NO_INVITATION_PERMISSION);
		}
	}

	/**
	 * 자기 자신을 초대했는지 검증
	 *
	 * @param inviterUserEmail 초대 보낸 회원 이메일
	 * @param inviteeEmail	   초대 받은 회원 이메일
	 * @author 윤정환
	 */
	public void validateSelfInvitation(String inviterUserEmail, String inviteeEmail) {
		if (Objects.equals(inviterUserEmail, inviteeEmail)) {
			throw new BizException(GroupInvitationErrorCode.CANNOT_INVITE_SELF);
		}
	}
}
