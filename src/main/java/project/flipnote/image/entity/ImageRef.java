package project.flipnote.image.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.flipnote.common.entity.SoftDeletableEntity;

@Getter
@Entity
@Table(name = "image_references")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageRef extends SoftDeletableEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private ReferenceType  referenceType;

	@Column
	private Long referenceId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "image_id", nullable = false)
	private Image image;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ImageStatus status = ImageStatus.PENDING;

	@Builder
	private ImageRef(Image image) {
		this.image = image;
	}

	public void activateFor(ReferenceType referenceType, Long referenceId) {
		this.referenceType = referenceType;
		this.referenceId = referenceId;
		this.status = ImageStatus.USING;
	}

	public void updateStatus(ImageStatus status) {
		this.status = status;
	}
}
