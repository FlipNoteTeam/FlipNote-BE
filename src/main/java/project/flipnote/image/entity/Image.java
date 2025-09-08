package project.flipnote.image.entity;

import org.checkerframework.checker.units.qual.C;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

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
import project.flipnote.common.entity.BaseEntity;
import project.flipnote.common.entity.SoftDeletableEntity;

@Getter
@Entity
@Table(name = "images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE images SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Image extends SoftDeletableEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	//md5 키값
	@Column(nullable = false, unique = true, length = 32)
	private String hash;

	@Column(nullable = false)
	private String s3Key;

	private String mimeType;

	private Long sizeBytes;

	@Version
	private Long version;

	@Builder
	private Image(String hash, String s3Key, String mimeType, Long sizeBytes) {
		this.hash = hash;
		this.s3Key = s3Key;
		this.mimeType = mimeType;
		this.sizeBytes = sizeBytes;
	}

	public void updateMetadata(String mimeType, Long sizeBytes) {
		this.mimeType = mimeType;
		this.sizeBytes = sizeBytes;
	}
}
