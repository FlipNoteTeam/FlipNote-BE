package project.flipnote.user.entity;

import org.springframework.beans.factory.annotation.Value;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.flipnote.auth.entity.AccountRole;
import project.flipnote.auth.entity.AccountStatus;
import project.flipnote.common.crypto.AesCryptoConverter;
import project.flipnote.common.entity.SoftDeletableEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_profiles")
@Entity
public class UserProfile extends SoftDeletableEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserStatus status;

	@Builder
	public UserProfile(
		String email,
		String name,
		String nickname,
		String profileImageUrl,
		String phone,
		boolean smsAgree
	) {
		this.email = email;
		this.name = name;
		this.nickname = nickname;
		this.profileImageUrl = profileImageUrl;
		this.phone = phone;
		this.smsAgree = smsAgree;
		this.status = UserStatus.ACTIVE;
	}

	public void update(String nickname, String phone, boolean smsAgree, String profileImageUrl) {
		this.nickname = nickname;
		this.phone = phone;
		this.smsAgree = smsAgree;
		this.profileImageUrl = profileImageUrl;
	}

	public void withdraw() {
		softDelete();

		this.status = UserStatus.WITHDRAWN;
	}
}
