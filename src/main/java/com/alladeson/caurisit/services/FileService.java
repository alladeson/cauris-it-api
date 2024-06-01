/**
 *
 */
package com.alladeson.caurisit.services;

import com.alladeson.caurisit.config.AppConfig;
import com.alladeson.caurisit.models.paylaods.UploadFileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author William ALLADE
 *
 */

@Service
public class FileService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final Path fileStorageLocation;

	public FileService(AppConfig appConfig) {
		try {
			this.fileStorageLocation = Paths.get(appConfig.getUploadDir()).toAbsolutePath().normalize();
			logger.info("---> UploadDirLocation: {}", this.fileStorageLocation);
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
		}
	}

	public String store(MultipartFile file) {
		if (file == null || file.getOriginalFilename() == null)
			return null;
		var location = store(file, file.getOriginalFilename());
		return location.getFileName().toString();
//        // Normalize file name
//        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//        store(file, fileName);
//        return fileName;
	}

	public Path store(MultipartFile file, String filename) {
		try {

			return store(file, this.fileStorageLocation, filename);

		} catch (IOException ex) {
			throw new RuntimeException("Could not store file " + filename + ". Please try again!", ex);
		}
	}

	private Path store(MultipartFile file, Path location, String fileName) throws IOException {
		// Normalize file name
		var filename = StringUtils.cleanPath(fileName);
		var target = location.resolve(filename);

		// Copy file to the target location (Replacing existing file with the same name)
		Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

		System.out.println("lien du fichier : " + target);
		
		return target;
	}

	public Resource loadFileAsResource(String fileName) {
		try {
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new RuntimeException("File not found " + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new RuntimeException("File not found " + fileName, ex);
		}
	}

	public UploadFileResponse upload(MultipartFile file) {
		String fileName = this.store(file);

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
				.path(fileName).toUriString();

		return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
	}

	public List<UploadFileResponse> upload(MultipartFile[] files) {
		return Arrays.asList(files).stream().map(file -> upload(file)).collect(Collectors.toList());
	}

	public ResponseEntity<Resource> download(String fileName, HttpServletRequest request) {
		// Load file as Resource
		Resource resource = this.loadFileAsResource(fileName);

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Could not determine file type.");
		}

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

}
