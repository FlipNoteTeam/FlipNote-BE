package project.flipnote.like.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.flipnote.like.entity.LikeTargetType;
import project.flipnote.like.repository.LikeRepository;

@RequiredArgsConstructor
@Service
public class LikeWriter {
	private final LikeRepository likeRepository;

	/**
	 * 좋아요를 삭제합니다.
	 *
	 * @param targetType  좋아요 삭제 대상의 타입
	 * @param targetId    좋아요 삭제 대상의 ID
	 * @author 윤정환
	 */
	public void delete(LikeTargetType targetType, Long targetId) {
		likeRepository.deleteByTargetTypeAndTargetId(targetType, targetId);
	}
}
