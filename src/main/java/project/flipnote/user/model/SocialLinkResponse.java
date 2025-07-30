package project.flipnote.user.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import project.flipnote.auth.entity.OAuthLink;

public record SocialLinkResponse(

	Long socialLinkId,

	String provider,

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime linkedAt
) {

	public static SocialLinkResponse from(OAuthLink link) {
		return new SocialLinkResponse(
			link.getId(),
			link.getProvider(),
			link.getLinkedAt()
		);
	}
}
