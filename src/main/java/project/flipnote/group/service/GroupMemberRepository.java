package project.flipnote.group.service;

import org.springframework.data.jpa.repository.JpaRepository;

import project.flipnote.group.entity.GroupMember;

interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
}
