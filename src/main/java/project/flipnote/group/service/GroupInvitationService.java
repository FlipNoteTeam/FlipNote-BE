package project.flipnote.group.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.model.event.GroupInvitationCreatedEvent;
import project.flipnote.common.model.response.PagingResponse;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.group.entity.GroupInvitation;
import project.flipnote.group.entity.GroupInvitationStatus;
import project.flipnote.group.entity.GroupPermissionStatus;
import project.flipnote.group.exception.GroupErrorCode;
import project.flipnote.group.exception.GroupInvitationErrorCode;
import project.flipnote.group.model.GroupInvitationCreateRequest;
import project.flipnote.group.model.GroupInvitationCreateResponse;
import project.flipnote.group.model.GroupInvitationListRequest;
import project.flipnote.group.model.GroupInvitationRespondRequest;
import project.flipnote.group.model.IncomingGroupInvitationResponse;
import project.flipnote.group.model.OutgoingGroupInvitationResponse;
import project.flipnote.group.model.event.GuestGroupInvitationCreateEvent;
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
	private final GroupMemberPolicyService groupMemberPolicyService;
	private final ApplicationEventPublisher eventPublisher;
	private final GroupInvitationPolicyService groupInvitationPolicyService;

	/**
	 * 그룹에 회원 혹은 비회원 초대
	 *
	 * @param authPrinciple 초대한 회원 인증 정보
	 * @param groupId       초대한 그룹 ID
	 * @param req           초대 대상 정보
	 * @author 윤정환
	 */
	@Transactional
	public GroupInvitationCreateResponse createGroupInvitation(
		AuthPrinciple authPrinciple,
		Long groupId,
		GroupInvitationCreateRequest req
	) {
		Long inviterUserId = authPrinciple.userId();
		groupInvitationPolicyService.validateGroupInvitePermission(inviterUserId, groupId);

		String inviterUserEmail = authPrinciple.email();
		String inviteeEmail = req.email();
		groupInvitationPolicyService.validateSelfInvitation(inviterUserEmail, inviteeEmail);

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
	 * @author 윤정환
	 */
	@Transactional
	public void deleteGroupInvitation(Long userId, Long groupId, Long invitationId) {
		groupInvitationPolicyService.validateGroupInvitePermission(userId, groupId);

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
	 * @author 윤정환
	 */
	@Transactional
	public void respondToGroupInvitation(
		Long inviteeUserId,
		Long groupId,
		Long invitationId,
		GroupInvitationRespondRequest req
	) {
		GroupInvitation invitation = groupInvitationRepository.findByIdAndGroup_IdAndInviteeUserIdAndStatus(
				invitationId, groupId, inviteeUserId, GroupInvitationStatus.PENDING
			)
			.orElseThrow(() -> new BizException(GroupInvitationErrorCode.INVITATION_NOT_FOUND));

		invitation.validateNotExpired();

		invitation.respond(req.toEntityStatus());

		if (invitation.getStatus() == GroupInvitationStatus.ACCEPTED) {
			try {
				groupMemberPolicyService.addGroupMember(inviteeUserId, groupId);
			} catch (BizException ex) {
				// 이미 그룹 멤버인 경우 롤백되지 않게 하기 위함
				if (ex.getErrorCode() != GroupErrorCode.ALREADY_GROUP_MEMBER) {
					throw ex;
				}
			}
		}
	}

	/**
	 * 그룹 초대 보낸 목록을 페이징하여 조회
	 *
	 * @param userId  초대 보낸 목록을 조회하는 회원 ID
	 * @param groupId 초대한 그룹 ID
	 * @param req    페이징을 위한 정보
	 * @return 페이징된 그룹 초대 보낸 목록 응답
	 * @author 윤정환
	 */
	public PagingResponse<OutgoingGroupInvitationResponse> getOutgoingInvitations(
		Long userId,
		Long groupId,
		GroupInvitationListRequest req
	) {
		if (!groupService.hasPermission(groupId, userId, GroupPermissionStatus.INVITE)) {
			throw new BizException(GroupInvitationErrorCode.NO_INVITATION_PERMISSION);
		}

		// TODO: Projection 및 카운트 쿼리 튜닝 필요
		Page<GroupInvitation> invitationPage
			= groupInvitationRepository.findAllByGroup_Id(groupId, req.getPageRequest());

		List<Long> inviteeUserIds = invitationPage.getContent()
			.stream()
			.map(GroupInvitation::getInviteeUserId)
			.filter(Objects::nonNull)
			.toList();
		Map<Long, String> idAndNicknames = userService.getIdAndNicknames(inviteeUserIds);

		Page<OutgoingGroupInvitationResponse> res = invitationPage.map(
			(invitation) -> OutgoingGroupInvitationResponse.from(
				invitation, idAndNicknames.getOrDefault(invitation.getInviteeUserId(), "")
			)
		);

		return PagingResponse.from(res);
	}

	/**
	 * 그룹 초대 받은 목록을 페이징하여 조회
	 *
	 * @param userId 초대 받은 목록을 조회하는 회원 ID
	 * @param req    페이징을 위한 정보
	 * @return 페이징된 그룹 초대 받은 목록 응답
	 * @author 윤정환
	 */
	public PagingResponse<IncomingGroupInvitationResponse> getIncomingInvitations(
		Long userId,
		GroupInvitationListRequest req
	) {
		// TODO: Projection 및 카운트 쿼리 튜닝 필요
		Page<GroupInvitation> invitationPage
			= groupInvitationRepository.findAllByInviteeUserId(userId, req.getPageRequest());

		Page<IncomingGroupInvitationResponse> res = invitationPage.map(IncomingGroupInvitationResponse::from);
		return PagingResponse.from(res);
	}

	/**
	 * 회원가입시 비회원 그룹 초대를 수락
	 *
	 * @param inviteeEmail 초대 받은 회원 email
	 * @author 윤정환
	 */
	@Transactional
	public void acceptPendingInvitationsOnRegister(String inviteeEmail) {
		List<GroupInvitation> invitations = groupInvitationRepository
			.findAllByInviteeEmailAndStatus(inviteeEmail, GroupInvitationStatus.PENDING);

		for (GroupInvitation invitation : invitations) {
			if (invitation.isExpired()) {
				continue;
			}

			try {
				groupMemberPolicyService.addGroupMember(invitation.getInviteeUserId(), invitation.getGroup().getId());
				invitation.respond(GroupInvitationStatus.ACCEPTED);
			} catch (BizException ex) {
				if (ex.getErrorCode() == GroupErrorCode.ALREADY_GROUP_MEMBER) {
					invitation.respond(GroupInvitationStatus.ACCEPTED);
				}
			} catch (Exception ignored) {
			}
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
		if (groupMemberRepository.existsByGroup_idAndUser_id(groupId, inviteeUser.getId())) {
			throw new BizException(GroupErrorCode.ALREADY_GROUP_MEMBER);
		}

		if (groupInvitationRepository
			.existsByGroup_IdAndInviteeUserIdAndStatus(groupId, inviteeUser.getId(), GroupInvitationStatus.PENDING)
		) {
			throw new BizException(GroupInvitationErrorCode.ALREADY_INVITED);
		}

		GroupInvitation invitation = GroupInvitation.builder()
			.group(groupRepository.getReferenceById(groupId))
			.inviterUserId(inviterUserId)
			.inviteeUserId(inviteeUser.getId())
			.inviteeEmail(inviteeUser.getEmail())
			.build();
		groupInvitationRepository.save(invitation);

		eventPublisher.publishEvent(new GroupInvitationCreatedEvent(groupId, inviteeUser.getId()));

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
		if (groupInvitationRepository
			.existsByGroup_IdAndInviteeEmailAndStatus(groupId, inviteeEmail, GroupInvitationStatus.PENDING)
		) {
			throw new BizException(GroupInvitationErrorCode.ALREADY_INVITED);
		}

		GroupInvitation invitation = GroupInvitation.builder()
			.group(groupRepository.getReferenceById(groupId))
			.inviterUserId(inviterUserId)
			.inviteeEmail(inviteeEmail)
			.build();
		groupInvitationRepository.save(invitation);

		String groupName = groupRepository.findGroupNameById(groupId)
			.orElseThrow(() -> new BizException(GroupErrorCode.GROUP_NOT_FOUND));
		eventPublisher.publishEvent(new GuestGroupInvitationCreateEvent(inviteeEmail, groupName));

		return invitation.getId();
	}
}
