/**
 * 
 */
package com.alladeson.caurisit.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.alladeson.caurisit.config.AppConfig;
import com.google.zxing.WriterException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author TechDigita
 *
 */
@Service
public class AESCrypto {

	/* Debut Préparation du cryptage */

	/* Crypate basic */
	private static SecretKeySpec secretKey;
	private static byte[] key;

	// La route pour enregistrer l'image du code QR générer
	private static final String QR_CODE_IMAGE_PATH = "./src/main/resources/static/img/QRCode.png";
	// Le code secret par défaut pour le cryptage
	private static final String QR_CODE_SECRET_KEY = "ehotel";

	@Autowired
	private AppConfig config;

	public static void setKey(String myKey) {
		MessageDigest sha = null;
		try {
			key = myKey.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static String basicEncrypt(String strToEncrypt, String secret) {
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
		}
		return null;
	}

	public static String basicDecrypt(String strToDecrypt, String secret) {
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception e) {
			System.out.println("Error while decrypting: " + e.toString());
		}
		return null;
	}
	/* Fin Crypate basic */

	/* Cryptage avancé basé sur le IV et l'algorithme de cryptage */
	public static String encrypt(String algorithm, String input, SecretKey key, IvParameterSpec iv)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
			InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		byte[] cipherText = cipher.doFinal(input.getBytes());
		return Base64.getEncoder().encodeToString(cipherText);
	}

	public static String decrypt(String algorithm, String cipherText, SecretKey key, IvParameterSpec iv)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
			InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.DECRYPT_MODE, key, iv);
		byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
		return new String(plainText);
	}

	public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(n);
		SecretKey key = keyGenerator.generateKey();
		return key;
	}

	public static IvParameterSpec generateIv() {
		byte[] iv = new byte[16];
		new SecureRandom().nextBytes(iv);
		return new IvParameterSpec(iv);
	}
	/* Fin Cryptage avancé basé sur le IV et l'algorithme de cryptage */

	/* Cryptage basé sur le mot de passe */
	public static String encryptPasswordBased(String plainText, SecretKey key, IvParameterSpec iv)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
			InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));
	}

	public static String decryptPasswordBased(String cipherText, SecretKey key, IvParameterSpec iv)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
			InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		cipher.init(Cipher.DECRYPT_MODE, key, iv);
		return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)));
	}
	/* Fin Cryptage basé sur le mot de passe */

	/* Fin Préparation du cryptage */

	/* Les méthodes exposées */
	/**
	 * Cryptage basic basé sur une clé en chaîne de caractère
	 * 
	 * @param originalString Le texte à crypter
	 * @param secretKey      Le clé en chaîne de caractère
	 * @return {@link String} La chaîne de caractère cryptée
	 */
	public String executeBasicEncrypt(String originalString, String secretKey) {

		return basicEncrypt(originalString, secretKey != null ? secretKey : QR_CODE_SECRET_KEY);
	}

	/**
	 * Décryptage basic basé sur une clé en chaîne de caractère
	 * 
	 * @param encryptedString La chaîne de caractère cryptée
	 * @param secretKey       Le clé en chaîne de caractère ayant servir pour le
	 *                        cryptage
	 * @return {@link String} La chaîne de caractère originale
	 */
	public String executeBasicDecrypt(String encryptedString, String secretKey) {
		return basicDecrypt(encryptedString, config.getQrCodeSecretKey());
		//return basicDecrypt(encryptedString, secretKey != null ? secretKey : QR_CODE_SECRET_KEY);
	}

	
    public String getQRCode(String originalString, String secretKey){        

		String encryptedString = basicEncrypt(originalString, secretKey != null ? secretKey : QR_CODE_SECRET_KEY);

        try {
            // Generate and Save Qr Code Image in static/image folder
            QRCodeGenerator.generateQRCodeImage(encryptedString,250,250,QR_CODE_IMAGE_PATH);

        } catch (WriterException | IOException e) {
            e.printStackTrace();

			return "false";
        }

        return "true";
    }
}
