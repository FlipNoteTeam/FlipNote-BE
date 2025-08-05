package project.flipnote.image.model;

import java.net.URL;

public record ImageUploadResponseDto(
	URL url
) {
	public static ImageUploadResponseDto from(URL url) {
		return new ImageUploadResponseDto(url);
	}
}
