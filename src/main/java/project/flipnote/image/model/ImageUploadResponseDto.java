package project.flipnote.image.model;

import java.net.URL;

public record ImageUploadResponseDto(
	URL url,
	Boolean isExist
) {
	public static ImageUploadResponseDto from(URL url, Boolean isExist) {
		return new ImageUploadResponseDto(url, isExist);
	}
}
