package com.alladeson.caurisit.utils;

import com.alladeson.caurisit.config.AppConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.expression.ThymeleafEvaluationContext;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class Tool {
	public static final String COMMA_DELIMITER = ";";

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private JavaMailSender emailSender;
	@Autowired
	private SpringTemplateEngine templateEngine;
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private AppConfig appConfig;

	private ObjectMapper jsonSerializer = null;

	// @Bean
	private ObjectMapper getJsonSerializer() {
		if (jsonSerializer == null) {
			jsonSerializer = new ObjectMapper();
			jsonSerializer.setDateFormat(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"));
			jsonSerializer.findAndRegisterModules();
		}
		return jsonSerializer;
	}

	// @Bean
	public String toJson(Object obj) /* throws JsonProcessingException */ {
		try {
			return getJsonSerializer().writeValueAsString(obj);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		return null;
	}

	public static String formatDate(Date date, String format) {
		if (date == null)
			return null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return simpleDateFormat.format(date);
	}
	
	/**
	 * @param dateString
	 * @param formatter
	 * @return
	 * @throws ParseException
	 */
	public Date stringToDate(String dateString, String formatter) throws ParseException {
		SimpleDateFormat SDFormat = new SimpleDateFormat(formatter);
//						SDFormat.setTimeZone(TimeZone.getTimeZone("UTC+1"));
		Calendar cal = Calendar.getInstance();
		cal.setTime(SDFormat.parse(dateString));
		Date date = cal.getTime();
		return date;
	}

	public static String formatDate(LocalDate date, String format) {
		if (date == null)
			return null;
		return date.format(DateTimeFormatter.ofPattern(format));
	}

	public static String formatDate(LocalDateTime date, String format) {
		if (date == null)
			return null;
		return date.format(DateTimeFormatter.ofPattern(format));
	}

	public boolean sendMail(String fromEmail, String fromName, String[] toEmails, String subject, String text) {

		return this.sendMail(fromEmail, fromName, toEmails, subject, text, null, null, null);
	}

	public boolean sendMail(String fromEmail, String fromName, String[] toEmails, String subject, String text,
			File[] attachments) {

		return this.sendMail(fromEmail, fromName, toEmails, subject, text, null, null, attachments);
	}

	public boolean sendMail(String fromEmail, String fromName, String[] toEmails, String subject, String template,
			Map<String, Object> variables) {

		return this.sendMail(fromEmail, fromName, toEmails, subject, null, template, variables, null);
	}

	public boolean sendMail(String fromEmail, String fromName, String[] toEmails, String subject, String template,
			Map<String, Object> variables, File[] attachments) {

		return this.sendMail(fromEmail, fromName, toEmails, subject, null, template, variables, attachments);
	}

	private boolean sendMail(String fromEmail, String fromName, String[] toEmails, String subject, String text,
			String template, Map<String, Object> variables, File[] attachments) {
		try {
			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());

			helper.setFrom(fromEmail, fromName);
			helper.setTo(toEmails);
			helper.setSubject(subject);

			if (StringUtils.hasText(text)) {

				helper.setText(text);

			} else if (StringUtils.hasText(template)) {

				if (variables == null)
					variables = new HashMap<>();
				Context context = new Context();
				context.setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME,
						new ThymeleafEvaluationContext(applicationContext, null));
				context.setVariables(variables);
				String html = templateEngine.process(template, context);

				helper.setText(html, true);
			}

			if (attachments != null) {
				for (File file : attachments) {
					helper.addAttachment(file.getName(), file);
				}
			}

			emailSender.send(message);

			return true;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return false;
		}
	}

	/**
	 * Read the resource file.
	 *
	 * @param resourcePath
	 * @return
	 * @throws IOException
	 */
	public String getResourceAsString(String resourcePath) throws IOException {
		// get the resource
		InputStream resource = getResourceAsStream(resourcePath);
		// read the content
		return new String(resource.readAllBytes(), StandardCharsets.UTF_8);
//        // read the content
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource))) {
//            return reader.lines().collect(Collectors.joining("\n"));
//        }

//        // get the resource
//        File file = ResourceUtils.getFile("classpath:"+resourcePath); // NOK: Don't work online (tested on hereku)
//        // read the content
//        return new String(Files.readAllBytes(file.toPath()));
	}

	public InputStream getResourceAsStream(String resourcePath) throws IOException {
		var resource = new ClassPathResource(resourcePath).getInputStream();
		return resource;
	}

	public ResponseEntity<byte[]> generateReport(Collection<?> collection, String reportTemplate, String reportFileName)
			throws JRException, IOException {
		JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(collection);
		JasperReport compileReport = JasperCompileManager.compileReport(this.getResourceAsStream(reportTemplate));
		// JasperReport compileReport = JasperCompileManager.compileReport(new
		// FileInputStream(reportTemplate));
		HashMap<String, Object> map = new HashMap<>();
		JasperPrint report = JasperFillManager.fillReport(compileReport, map, beanCollectionDataSource);

		byte[] reportPdf = JasperExportManager.exportReportToPdf(report);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + reportFileName);
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(reportPdf);
	}

	public String generateInvoiceAndStoreIt(Collection<?> collection, HashMap<String, Object> params,
			String invoiceTemplate, String invoiceFileName) throws JRException, IOException {
		JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(collection);
		JasperReport compileReport = JasperCompileManager.compileReport(this.getResourceAsStream(invoiceTemplate));
		JasperPrint report = JasperFillManager.fillReport(compileReport, params, beanCollectionDataSource);
		JasperExportManager.exportReportToPdfFile(report, appConfig.getUploadDir() + "/" +invoiceFileName);
		return invoiceFileName;	
	}
	
	public ResponseEntity<byte[]> generateInvoice(Collection<?> collection, HashMap<String, Object> params,
			String invoiceTemplate, String invoiceFileName) throws JRException, IOException {
		JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(collection);
		JasperReport compileReport = JasperCompileManager.compileReport(this.getResourceAsStream(invoiceTemplate));
		JasperPrint report = JasperFillManager.fillReport(compileReport, params, beanCollectionDataSource);
//		JasperExportManager.exportReportToPdfFile(report, appConfig.getUploadDir() + "/" +invoiceFileName);
		byte[] reportPdf = JasperExportManager.exportReportToPdf(report);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + invoiceFileName);
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(reportPdf);
	}
	
	public ResponseEntity<byte[]> generateConfigReport(Collection<?> collection, HashMap<String, Object> params,
			String reportTemplate, String reportFileName) throws JRException, IOException {
		JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(collection);
		JasperReport compileReport = JasperCompileManager.compileReport(this.getResourceAsStream(reportTemplate));
		JasperPrint report = JasperFillManager.fillReport(compileReport, params, beanCollectionDataSource);
		JasperExportManager.exportReportToPdfFile(report, appConfig.getUploadDir() + "/" + reportFileName);
		byte[] reportPdf = JasperExportManager.exportReportToPdf(report);
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + reportFileName);
		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(reportPdf);
	}

	/* Conversion des nombres en lettres françaises */

	/**
	 * Liste des dizaines
	 */
	private static final String[] dizaineNames = { "", //
			"", //
			"vingt", //
			"trente", //
			"quarante", //
			"cinquante", //
			"soixante", //
			"soixante", //
			"quatre-vingt", //
			"quatre-vingt" //
	};// from w w w. j a va 2 s . c o m

	/**
	 * Liste de 1 à 20
	 */
	private static final String[] uniteNames1 = { "", //
			"un", //
			"deux", //
			"trois", //
			"quatre", //
			"cinq", //
			"six", //
			"sept", //
			"huit", //
			"neuf", //
			"dix", //
			"onze", //
			"douze", //
			"treize", //
			"quatorze", //
			"quinze", //
			"seize", //
			"dix-sept", //
			"dix-huit", //
			"dix-neuf" //
	};
	/**
	 * List de 2 à 10
	 */
	private static final String[] uniteNames2 = { "", //
			"", //
			"deux", //
			"trois", //
			"quatre", //
			"cinq", //
			"six", //
			"sept", //
			"huit", //
			"neuf", //
			"dix" //
	};

	/**
	 * Conversion des nombres de 0 à 100
	 * 
	 * @param number
	 * @return
	 */
	private static String convertZeroToHundred(int number) {

		int laDizaine = number / 10;
		int lUnite = number % 10;
		String resultat = "";

		switch (laDizaine) {
		case 1:
		case 7:
		case 9:
			lUnite = lUnite + 10;
			break;
		default:
		}

		String laLiaison = "";
		if (laDizaine > 1) {
			laLiaison = "-";
		}
		switch (lUnite) {
		case 0:
			laLiaison = "";
			break;
		case 1:
			if (laDizaine == 8) {
				laLiaison = "-";
			} else {
				laLiaison = " et ";
			}
			break;
		case 11:
			if (laDizaine == 7) {
				laLiaison = " et ";
			}
			break;
		default:
		}

		// dizaines en lettres
		switch (laDizaine) {
		case 0:
			resultat = uniteNames1[lUnite];
			break;
		case 8:
			if (lUnite == 0) {
				resultat = dizaineNames[laDizaine];
			} else {
				resultat = dizaineNames[laDizaine] + laLiaison + uniteNames1[lUnite];
			}
			break;
		default:
			resultat = dizaineNames[laDizaine] + laLiaison + uniteNames1[lUnite];
		}
		return resultat;
	}

	/**
	 * Conversion des nombre moins de 1000
	 * 
	 * @param number
	 * @return
	 */
	private static String convertLessThanOneThousand(int number) {

		int lesCentaines = number / 100;
		int leReste = number % 100;
		String sReste = convertZeroToHundred(leReste);

		String resultat;
		switch (lesCentaines) {
		case 0:
			resultat = sReste;
			break;
		case 1:
			if (leReste > 0) {
				resultat = "cent " + sReste;
			} else {
				resultat = "cent";
			}
			break;
		default:
			if (leReste > 0) {
				resultat = uniteNames2[lesCentaines] + " cent " + sReste;
			} else {
				resultat = uniteNames2[lesCentaines] + " cents";
			}
		}
		return resultat;
	}

	/**
	 * La fonction de conversion des nombres
	 * @param number
	 * @return
	 */
	public static String convert(long number) {
		if (number == 0) {
			return "zero";
		}

		String snumber = Long.toString(number);
		String mask = "000000000000";
		DecimalFormat df = new DecimalFormat(mask);
		snumber = df.format(number);

		int lesMilliards = Integer.parseInt(snumber.substring(0, 3));
		int lesMillions = Integer.parseInt(snumber.substring(3, 6));
		int lesCentMille = Integer.parseInt(snumber.substring(6, 9));
		int lesMille = Integer.parseInt(snumber.substring(9, 12));

		String tradMilliards;
		switch (lesMilliards) {
		case 0:
			tradMilliards = "";
			break;
		case 1:
			tradMilliards = convertLessThanOneThousand(lesMilliards) + " milliard ";
			break;
		default:
			tradMilliards = convertLessThanOneThousand(lesMilliards) + " milliards ";
		}
		String resultat = tradMilliards;

		String tradMillions;
		switch (lesMillions) {
		case 0:
			tradMillions = "";
			break;
		case 1:
			tradMillions = convertLessThanOneThousand(lesMillions) + " million ";
			break;
		default:
			tradMillions = convertLessThanOneThousand(lesMillions) + " millions ";
		}
		resultat = resultat + tradMillions;

		String tradCentMille;
		switch (lesCentMille) {
		case 0:
			tradCentMille = "";
			break;
		case 1:
			tradCentMille = "mille ";
			break;
		default:
			tradCentMille = convertLessThanOneThousand(lesCentMille) + " mille ";
		}
		resultat = resultat + tradCentMille;

		String tradMille;
		tradMille = convertLessThanOneThousand(lesMille);
		resultat = resultat + tradMille;

		return resultat;
	}
	
	/* Fin conversion des nombres */

}
