package project.flipnote.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.flipnote.common.crypto.AesCryptoConverter;
import project.flipnote.common.entity.SoftDeletableEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "user_profiles")
@Entity
public class UserProfile extends SoftDeletableEntity {

	@Id
	private Long id;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String nickname;

	private String profileImageUrl;

	@Convert(converter = AesCryptoConverter.class)
	@Column(unique = true, length = 1024)
	private String phone;

	private boolean smsAgree;

	public void update(String nickname, String phone, boolean smsAgree, String profileImageUrl) {
		this.nickname = nickname;
		this.phone = phone;
		this.smsAgree = smsAgree;
		this.profileImageUrl = profileImageUrl;
	}
}
