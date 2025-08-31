package project.flipnote.groupjoin.repository;

import java.util.List;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import project.flipnote.group.entity.QGroup;
import project.flipnote.group.model.GroupMemberInfo;
import project.flipnote.groupjoin.entity.QGroupJoin;
import project.flipnote.groupjoin.model.GroupJoinInfo;
import project.flipnote.groupjoin.model.MyGroupJoinInfo;
import project.flipnote.user.entity.QUserProfile;

@RequiredArgsConstructor
public class GroupJoinRepositoryImpl implements GroupJoinRepositoryCustom{

	private final JPAQueryFactory queryFactory;

	QGroup group = QGroup.group;
	QGroupJoin groupJoin = QGroupJoin.groupJoin;
	QUserProfile userProfile = QUserProfile.userProfile;

	@Override
	public List<GroupJoinInfo> findByGroup(Long groupId) {
		return queryFactory.select(Projections.constructor(
				GroupJoinInfo.class,
				groupJoin.id,
				groupJoin.user.id,
				groupJoin.user.nickname,
				groupJoin.joinIntro,
				groupJoin.status
			))
			.from(groupJoin)
			.where(groupJoin.group.id.eq(groupId))
			.fetch();
	}

	@Override
	public List<MyGroupJoinInfo> findByUser(Long userId) {
		return queryFactory.select(Projections.constructor(
				MyGroupJoinInfo.class,
				groupJoin.id,
				groupJoin.group.id,
				groupJoin.group.name,
				groupJoin.joinIntro,
				groupJoin.status
			))
			.from(groupJoin)
			.where(groupJoin.user.id.eq(userId))
			.fetch();
	}
}
