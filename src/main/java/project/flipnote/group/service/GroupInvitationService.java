package project.flipnote.group.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.group.entity.GroupInvitation;
import project.flipnote.group.entity.GroupInvitationStatus;
import project.flipnote.group.entity.GroupMember;
import project.flipnote.group.entity.GroupMemberRole;
import project.flipnote.group.entity.GroupPermissionStatus;
import project.flipnote.group.exception.GroupInvitationErrorCode;
import project.flipnote.group.model.GroupInvitationCreateRequest;
import project.flipnote.group.model.GroupInvitationCreateResponse;
import project.flipnote.group.model.GroupInvitationRespondRequest;
import project.flipnote.group.repository.GroupInvitationRepository;
import project.flipnote.group.repository.GroupMemberRepository;
import project.flipnote.group.repository.GroupRepository;
import project.flipnote.user.entity.UserProfile;
import project.flipnote.user.service.UserService;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GroupInvitationService {

	private final UserService userService;
	private final GroupInvitationRepository groupInvitationRepository;
	private final GroupService groupService;
	private final GroupRepository groupRepository;
	private final GroupMemberRepository groupMemberRepository;
	private final EntityManager em;

	/**
	 * 그룹에 회원 혹은 비회원 초대
	 *
	 * @param authPrinciple 초대한 회원 인증 정보
	 * @param groupId       초대한 그룹 ID
	 * @param req           초대 대상 정보
	 * @author 윤정환
	 */
	@Transactional
	public GroupInvitationCreateResponse createGroupInvitation(AuthPrinciple authPrinciple, Long groupId, GroupInvitationCreateRequest req) {
		Long inviterUserId = authPrinciple.userId();
		validateGroupInvitePermission(inviterUserId, groupId);

		String inviterUserEmail = authPrinciple.email();
		String inviteeEmail = req.email();
		if (Objects.equals(inviterUserEmail, inviteeEmail)) {
			throw new BizException(GroupInvitationErrorCode.CANNOT_INVITE_SELF);
		}

		Long invitationId = userService.findActiveUserByEmail(inviteeEmail)
			.map(inviteeUser -> createUserInvitation(inviterUserId, groupId, inviteeUser))
			.orElseGet(() -> createGuestInvitation(inviterUserId, groupId, inviteeEmail));

		return new GroupInvitationCreateResponse(invitationId);
	}

	/**
	 * 그룹 초대를 취소
	 *
	 * @param userId       초대를 취소하는 회원 ID
	 * @param groupId      초대한 그룹 ID
	 * @param invitationId 취소할 초대의 ID
	 */
	@Transactional
	public void deleteGroupInvitation(Long userId, Long groupId, Long invitationId) {
		validateGroupInvitePermission(userId, groupId);

		GroupInvitation invitation = groupInvitationRepository
			.findByIdAndStatus(invitationId, GroupInvitationStatus.PENDING)
			.orElseThrow(() -> new BizException(GroupInvitationErrorCode.INVITATION_NOT_FOUND));

		groupInvitationRepository.delete(invitation);
	}

	/**
	 * 그룹 초대에 응답
	 *
	 * @param inviteeUserId 초대를 받은 회원 ID
	 * @param groupId       초대한 그룹 ID
	 * @param invitationId  응답할 초대의 ID
	 * @param req           초대에 응답할 정보
	 */
	@Transactional
	public void respondToGroupInvitation(
		Long inviteeUserId,
		Long groupId,
		Long invitationId,
		GroupInvitationRespondRequest req
	) {
		GroupInvitation invitation = groupInvitationRepository.findByIdAndGroupIdAndInviteeUserIdAndStatus(
				invitationId, groupId, inviteeUserId, GroupInvitationStatus.PENDING
			)
			.orElseThrow(() -> new BizException(GroupInvitationErrorCode.INVITATION_NOT_FOUND));

		invitation.respond(req.toEntityStatus());

		if (Objects.equals(invitation.getStatus(), GroupInvitationStatus.ACCEPTED)) {
			// TODO: GroupMember 에서 group과 user의 id만 가지고 있도록 수정
			GroupMember groupMember = GroupMember.builder()
				.group(groupRepository.getReferenceById(groupId))
				.user(em.getReference(UserProfile.class, inviteeUserId))
				.role(GroupMemberRole.MEMBER)
				.build();

			groupMemberRepository.save(groupMember);
		}
	}

	/**
	 * 그룹 초대 권한을 검증
	 *
	 * @param userId  권한을 검증할 회원 ID
	 * @param groupId 검증할 그룹 ID
	 * @author 윤정환
	 */
	private void validateGroupInvitePermission(Long userId, Long groupId) {
		if (!groupService.hasPermission(groupId, userId, GroupPermissionStatus.INVITE)) {
			throw new BizException(GroupInvitationErrorCode.NO_INVITATION_PERMISSION);
		}
	}

	/**
	 * 그룹에 회원 초대
	 *
	 * @param inviterUserId 초대한 회원 ID
	 * @param groupId       초대한 그룹 ID
	 * @param inviteeUser   초대 받는 user
	 * @author 윤정환
	 */
	private Long createUserInvitation(Long inviterUserId, Long groupId, UserProfile inviteeUser) {
		if (groupInvitationRepository.existsByGroupIdAndInviteeUserId(groupId, inviteeUser.getId())) {
			throw new BizException(GroupInvitationErrorCode.ALREADY_INVITED);
		}

		GroupInvitation invitation = GroupInvitation.builder()
			.groupId(groupId)
			.inviterUserId(inviterUserId)
			.inviteeUserId(inviteeUser.getId())
			.build();
		groupInvitationRepository.save(invitation);

		// TODO: 초대받은 회원한테 알림 전송

		return invitation.getId();
	}

	/**
	 * 그룹에 비회원 초대
	 *
	 * @param inviterUserId 초대한 회원 ID
	 * @param groupId       초대한 그룹 ID
	 * @param inviteeEmail  초대 받는 비회원 email
	 * @author 윤정환
	 */
	private Long createGuestInvitation(Long inviterUserId, Long groupId, String inviteeEmail) {
		if (groupInvitationRepository.existsByGroupIdAndInviteeEmail(groupId, inviteeEmail)) {
			throw new BizException(GroupInvitationErrorCode.ALREADY_INVITED);
		}

		GroupInvitation invitation = GroupInvitation.builder()
			.groupId(groupId)
			.inviterUserId(inviterUserId)
			.inviteeEmail(inviteeEmail)
			.build();
		groupInvitationRepository.save(invitation);

		// TODO: 초대받은 비회원한테 이메일 전송

		return invitation.getId();
	}
}
