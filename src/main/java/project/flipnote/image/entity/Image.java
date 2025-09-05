package project.flipnote.image.entity;

import org.checkerframework.checker.units.qual.C;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String url;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ImageStatus status = ImageStatus.PENDING;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ImageType type;

	@Column(nullable = false)
	private Long ownerId;

	@Version
	private Long version;

	@Builder
	private Image(String url, ImageStatus status, ImageType type, Long ownerId) {
		this.url = url;
		this.status = status;
		this.type = type;
		this.ownerId = ownerId;
	}

	public void changeStatus(ImageStatus status) {
		this.status = status;
	}
}
