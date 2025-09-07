package project.flipnote.image.model;

import java.net.URL;

public record ImageUploadResponseDto(
	String url,
	Long imageRefId
) {
	public static ImageUploadResponseDto from(String url, Long imageRefId) {
		return new ImageUploadResponseDto(url, imageRefId);
	}
}
