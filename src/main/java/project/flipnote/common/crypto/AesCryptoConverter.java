package project.flipnote.common.crypto;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Converter
@Component
public class AesCryptoConverter implements AttributeConverter<String, String> {

	private final SecretKeySpec aesSecretKeySpec;

	private static final int IV_SIZE_BYTES = 16;
	private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

	@Override
	public String convertToDatabaseColumn(String attribute) {
		if (!StringUtils.hasText(attribute)) {
			return attribute;
		}

		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);

			byte[] iv = new byte[IV_SIZE_BYTES];
			new SecureRandom().nextBytes(iv);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

			cipher.init(Cipher.ENCRYPT_MODE, aesSecretKeySpec, ivParameterSpec);
			byte[] encrypted = cipher.doFinal(attribute.getBytes(StandardCharsets.UTF_8));

			byte[] combined = new byte[iv.length + encrypted.length];
			System.arraycopy(iv, 0, combined, 0, iv.length);
			System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

			return Base64.getEncoder().encodeToString(combined);
		} catch (Exception e) {
			throw new IllegalStateException("데이터 암호화에 실패했습니다.", e);
		}
	}

	@Override
	public String convertToEntityAttribute(String dbData) {
		if (!StringUtils.hasText(dbData)) {
			return dbData;
		}

		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);

			byte[] combined = Base64.getDecoder().decode(dbData);

			byte[] iv = new byte[IV_SIZE_BYTES];
			System.arraycopy(combined, 0, iv, 0, iv.length);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

			byte[] encrypted = new byte[combined.length - iv.length];
			System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);

			cipher.init(Cipher.DECRYPT_MODE, aesSecretKeySpec, ivParameterSpec);
			byte[] decrypted = cipher.doFinal(encrypted);

			return new String(decrypted, StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new IllegalStateException("데이터 복호화에 실패했습니다.", e);
		}
	}
}
