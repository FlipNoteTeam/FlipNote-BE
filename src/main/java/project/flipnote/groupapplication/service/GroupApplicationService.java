package project.flipnote.groupapplication.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.security.dto.UserAuth;
import project.flipnote.group.entity.Group;
import project.flipnote.group.exception.GroupErrorCode;
import project.flipnote.group.repository.GroupRepository;
import project.flipnote.groupapplication.entity.GroupApplication;
import project.flipnote.groupapplication.entity.GroupApplicationStatus;
import project.flipnote.groupapplication.model.GroupApplicationJoinRequest;
import project.flipnote.groupapplication.model.GroupApplicationJoinResponse;
import project.flipnote.groupapplication.repository.GroupApplicationRepository;
import project.flipnote.user.entity.User;
import project.flipnote.user.entity.UserStatus;
import project.flipnote.user.exception.UserErrorCode;
import project.flipnote.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupApplicationService {
	private final GroupRepository groupRepository;
	private final UserRepository userRepository;
	private final GroupApplicationRepository groupApplicationRepository;

	//유저 정보 조회
	public User findUser(UserAuth userAuth) {
		return userRepository.findByIdAndStatus(userAuth.userId(), UserStatus.ACTIVE).orElseThrow(
				() -> new BizException(UserErrorCode.USER_NOT_FOUND)
		);
	}

	public Group findGroup(Long groupId) {
		return groupRepository.findById(groupId).orElseThrow(
				() -> new BizException(GroupErrorCode.GROUP_NOT_FOUND)
		);
	}
	//가입 신청 요청
	public GroupApplicationJoinResponse joinRequest(UserAuth userAuth, Long groupId, GroupApplicationJoinRequest req) {
		//유저 조회
		User user = findUser(userAuth);
		//그룹 조회
		Group group = findGroup(groupId);

		GroupApplication groupApplication = GroupApplication.builder()
				.group(group)
				.user(user)
				.joinIntro(req.joinIntro())
				.status(GroupApplicationStatus.PENDING)
				.build();

		GroupApplication saveGroupApplication = groupApplicationRepository.save(groupApplication);

		return GroupApplicationJoinResponse.from(saveGroupApplication.getId());
	}
}
