package project.flipnote.group.repository;

import java.util.List;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import project.flipnote.group.entity.QGroupMember;
import project.flipnote.group.model.GroupMemberInfo;
import project.flipnote.user.entity.QUserProfile;

@RequiredArgsConstructor
public class GroupMemberRepositoryImpl implements GroupMemberRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	QUserProfile userProfile = QUserProfile.userProfile;
	QGroupMember groupMember = QGroupMember.groupMember;

	@Override
	public List<GroupMemberInfo> findGroupMembers(Long groupId) {
		return queryFactory.select(Projections.constructor(
				GroupMemberInfo.class,
				userProfile.id,
				groupMember.role,
				userProfile.name,
				userProfile.profileImageUrl
			))
			.from(groupMember)
			.join(groupMember.user, userProfile)
			.where(groupMember.group.id.eq(groupId))
			.fetch();
	}
}
