package project.flipnote.user.entity;

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
import project.flipnote.common.crypto.AesCryptoConverter;
import project.flipnote.common.entity.SoftDeletableEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
@Entity
public class User extends SoftDeletableEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String email;

	private String password;

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

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole role;

	@Builder
	public User(
		String email,
		String password,
		String name,
		String nickname,
		String profileImageUrl,
		String phone,
		boolean smsAgree
	) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.nickname = nickname;
		this.profileImageUrl = profileImageUrl;
		this.phone = phone;
		this.smsAgree = smsAgree;
		this.status = UserStatus.ACTIVE;
		this.role = UserRole.USER;
	}

	@Override
	public void softDelete() {
		super.softDelete();
		this.status = UserStatus.INACTIVE;
	}
}
