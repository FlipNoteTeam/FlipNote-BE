package project.flipnote.image.model;

import java.net.URL;

public record ImageUploadResponseDto(
	URL url,
	Long imageRefId
) {
	public static ImageUploadResponseDto from(URL url, Long imageRefId) {
		return new ImageUploadResponseDto(url, imageRefId);
	}
}
