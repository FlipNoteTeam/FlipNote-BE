package project.flipnote.auth.entity;

import jakarta.persistence.Column;
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
import project.flipnote.common.entity.SoftDeletableEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_auth")
@Entity
public class UserAuth extends SoftDeletableEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String email;

	private String password;

	@Column(unique = true, nullable = false)
	private Long userId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AccountStatus status;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AccountRole role;

	@Column(nullable = false)
	private long tokenVersion;

	@Builder
	public UserAuth(
		String email,
		String password,
		Long userId
	) {
		this.email = email;
		this.password = password;
		this.userId = userId;
		this.status = AccountStatus.ACTIVE;
		this.role = AccountRole.USER;
		this.tokenVersion = 0L;
	}

	public void withdraw() {
		super.softDelete();

		this.status = AccountStatus.WITHDRAWN;

		increaseTokenVersion();
	}

	public void increaseTokenVersion() {
		this.tokenVersion++;
	}

	public void changePassword(String password) {
		this.password = password;
	}
}
