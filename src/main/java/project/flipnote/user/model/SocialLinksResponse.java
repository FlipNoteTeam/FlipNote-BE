package project.flipnote.user.model;

import java.util.List;

import project.flipnote.user.entity.UserOAuthLink;

public record SocialLinksResponse(
	List<SocialLinkResponse> socialLinks
) {

	public static SocialLinksResponse from(List<UserOAuthLink> links) {
		List<SocialLinkResponse> socialLinks = links.stream()
			.map(SocialLinkResponse::from)
			.toList();

		return new SocialLinksResponse(socialLinks);
	}
}
