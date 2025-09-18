package project.flipnote.image.entity;

public record ImageMeta(Long imageRefId, String url) {
	public static ImageMeta from(Long imageRefId, String url) {
		return new ImageMeta(imageRefId, url);
	}
}
