/**
 *
 */
package com.alladeson.caurisit.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;

/**
 * @author TechDigita
 *
 */
@ConfigurationProperties(prefix = "api")
public class AppConfig {
	private String appName;
	private String appIfu;
	private String appRcm;
	private String appLogo;
	private String appCopyright;
	private String appCaurisitFrontend;
	private String baseUrl;
	private String uploadDir;
	private String emailNoReply;
	private String emailAdmin;
	private String mailSignupRequestTitle;
	private String mailSignupSuccessTitle;
	private String mailPwdResetRequestTitle;
	private String mailPwdResetTitle;
	private String mailDoublonTitle;
	private String mailNonVisaTitle;
	private String mailNonConformeTitle;
	private String mailDelivranceTitle;
	private String frontEndAppName;
	private String frontEndAppCopyright;
	private String frontEndBaseUrl;
	private String securityCorsOrigins;
	private String securityCorsMethods;
	private String subject;
	private String emailDest;
	private String mailContent;
	private String formatDemandeNo;
	private String formatJugementNo;
	private String qrCodeSecretKey;
	private String skChckBaseUri;
	private String saUsername;
	private String saPassword;
	private String skLoginUri;
	private String skChckUri;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	/**
	 * @return the appIfu
	 */
	public String getAppIfu() {
		return appIfu;
	}

	/**
	 * @param appIfu the appIfu to set
	 */
	public void setAppIfu(String appIfu) {
		this.appIfu = appIfu;
	}

	/**
	 * @return the appRcm
	 */
	public String getAppRcm() {
		return appRcm;
	}

	/**
	 * @param appRcm the appRcm to set
	 */
	public void setAppRcm(String appRcm) {
		this.appRcm = appRcm;
	}

	/**
	 * @return the appLogo
	 */
	public String getAppLogo() {
		return appLogo;
	}

	/**
	 * @param appLogo the appLogo to set
	 */
	public void setAppLogo(String appLogo) {
		this.appLogo = appLogo;
	}

	public String getAppCopyright() {
		return appCopyright;
	}

	public void setAppCopyright(String appCopyright) {
		this.appCopyright = appCopyright;
	}

	/**
	 * @return the appCaurisitFrontend
	 */
	public String getAppCaurisitFrontend() {
		return appCaurisitFrontend;
	}

	/**
	 * @param appCaurisitFrontend the appCaurisitFrontend to set
	 */
	public void setAppCaurisitFrontend(String appCaurisitFrontend) {
		this.appCaurisitFrontend = appCaurisitFrontend;
	}

	public String getMailSignupRequestTitle() {
		return mailSignupRequestTitle;
	}

	public void setMailSignupRequestTitle(String mailSignupRequestTitle) {
		this.mailSignupRequestTitle = mailSignupRequestTitle;
	}

	public String getMailSignupSuccessTitle() {
		return mailSignupSuccessTitle;
	}

	public void setMailSignupSuccessTitle(String mailSignupSuccessTitle) {
		this.mailSignupSuccessTitle = mailSignupSuccessTitle;
	}

	public String getMailPwdResetRequestTitle() {
		return mailPwdResetRequestTitle;
	}

	public void setMailPwdResetRequestTitle(String mailPwdResetRequestTitle) {
		this.mailPwdResetRequestTitle = mailPwdResetRequestTitle;
	}

	public String getMailPwdResetTitle() {
		return mailPwdResetTitle;
	}

	public void setMailPwdResetTitle(String mailPwdResetTitle) {
		this.mailPwdResetTitle = mailPwdResetTitle;
	}

	public String getFrontEndAppName() {
		return frontEndAppName;
	}

	public void setFrontEndAppName(String frontEndAppName) {
		this.frontEndAppName = frontEndAppName;
	}

	public String getFrontEndAppCopyright() {
		return frontEndAppCopyright;
	}

	public void setFrontEndAppCopyright(String frontEndAppCopyright) {
		this.frontEndAppCopyright = frontEndAppCopyright;
	}

	public String getFrontEndBaseUrl() {
		return frontEndBaseUrl;
	}

	public void setFrontEndBaseUrl(String frontEndBaseUrl) {
		this.frontEndBaseUrl = frontEndBaseUrl;
	}

	public Logger getLogger() {
		return logger;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getUploadDir() {
		return uploadDir;
	}

	public void setUploadDir(String uploadDir) {
		this.uploadDir = uploadDir;
	}

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public String getEmailNoReply() {
		return emailNoReply;
	}

	public void setEmailNoReply(String emailNoReply) {
		this.emailNoReply = emailNoReply;
	}

	public String getEmailAdmin() {
		return emailAdmin;
	}

	public void setEmailAdmin(String emailAdmin) {
		this.emailAdmin = emailAdmin;
	}

	public String getSecurityCorsOrigins() {
		return securityCorsOrigins;
	}

	public void setSecurityCorsOrigins(String securityCorsOrigins) {
		this.securityCorsOrigins = securityCorsOrigins;
	}

	public String getSecurityCorsMethods() {
		return securityCorsMethods;
	}

	public void setSecurityCorsMethods(String securityCorsMethods) {
		this.securityCorsMethods = securityCorsMethods;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getEmailDest() {
		return emailDest;
	}

	public void setEmailDest(String emailDest) {
		this.emailDest = emailDest;
	}

	public String getMailContent() {
		return mailContent;
	}

	public void setMailContent(String mailContent) {
		this.mailContent = mailContent;
	}

	public String getMailDoublonTitle() {
		return mailDoublonTitle;
	}

	public void setMailDoublonTitle(String mailDoublonTitle) {
		this.mailDoublonTitle = mailDoublonTitle;
	}

	public String getMailNonVisaTitle() {
		return mailNonVisaTitle;
	}

	public void setMailNonVisaTitle(String mailNonVisaTitle) {
		this.mailNonVisaTitle = mailNonVisaTitle;
	}

	public String getMailDelivranceTitle() {
		return mailDelivranceTitle;
	}

	public void setMailDelivranceTitle(String mailDelivranceTitle) {
		this.mailDelivranceTitle = mailDelivranceTitle;
	}

	public String getMailNonConformeTitle() {
		return mailNonConformeTitle;
	}

	public void setMailNonConformeTitle(String mailNonConformeTitle) {
		this.mailNonConformeTitle = mailNonConformeTitle;
	}

	public String getFormatDemandeNo() {
		return formatDemandeNo;
	}

	public void setFormatDemandeNo(String formatDemandeNo) {
		this.formatDemandeNo = formatDemandeNo;
	}

	public String getFormatJugementNo() {
		return formatJugementNo;
	}

	public void setFormatJugementNo(String formatJugementNo) {
		this.formatJugementNo = formatJugementNo;
	}

	public String getQrCodeSecretKey() {
		return qrCodeSecretKey;
	}

	public void setQrCodeSecretKey(String qrCodeSecretKey) {
		this.qrCodeSecretKey = qrCodeSecretKey;
	}

	/**
	 * @return the skChckBaseUri
	 */
	public String getSkChckBaseUri() {
		return skChckBaseUri;
	}

	/**
	 * @param skChckBaseUri the skChckBaseUri to set
	 */
	public void setSkChckBaseUri(String skChckBaseUri) {
		this.skChckBaseUri = skChckBaseUri;
	}

	/**
	 * @return the saUsername
	 */
	public String getSaUsername() {
		return saUsername;
	}

	/**
	 * @param saUsername the saUsername to set
	 */
	public void setSaUsername(String saUsername) {
		this.saUsername = saUsername;
	}

	/**
	 * @return the saPassword
	 */
	public String getSaPassword() {
		return saPassword;
	}

	/**
	 * @param saPassword the saPassword to set
	 */
	public void setSaPassword(String saPassword) {
		this.saPassword = saPassword;
	}

	/**
	 * @return the skLoginUri
	 */
	public String getSkLoginUri() {
		return skLoginUri;
	}

	/**
	 * @param skLoginUri the skLoginUri to set
	 */
	public void setSkLoginUri(String skLoginUri) {
		this.skLoginUri = skLoginUri;
	}

	/**
	 * @return the skChckUri
	 */
	public String getSkChckUri() {
		return skChckUri;
	}

	/**
	 * @param skChckUri the skChckUri to set
	 */
	public void setSkChckUri(String skChckUri) {
		this.skChckUri = skChckUri;
	}

	@PostConstruct
	public void init() throws IllegalAccessException {

		logger.info(">> API CUSTOM PROPERTIES");
		// logger.info("---> API.BASE-URL: {}", this.getBaseUrl());

		Field[] fields = this.getClass().getDeclaredFields();
		for (Field f : fields) {
			if (f.getType().equals(Logger.class))
				continue;

			logger.info("---> api.{}={}", camelToHyphen(f.getName()), f.get(this));
		}
	}

	private String camelToHyphen(String camel) {
		String regex = "([a-z])([A-Z])";
		String replacement = "$1-$2";
		String hyphen = camel.replaceAll(regex, replacement).toLowerCase();
		return hyphen;
	}

}
