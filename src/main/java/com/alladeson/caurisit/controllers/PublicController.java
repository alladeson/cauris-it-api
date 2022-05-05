package com.alladeson.caurisit.controllers;

import com.alladeson.caurisit.services.FileService;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/public")
public class PublicController {
    
    @Autowired
    private FileService fileService;

	/**
	 * @param fileName
	 * @param request
	 * @return
	 * @see com.alladeson.caurisit.services.FileService#download(java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
    @GetMapping("/downloadFile/{fileName:.+}")
	public ResponseEntity<Resource> download(@PathVariable String fileName, HttpServletRequest request) {
		return fileService.download(fileName, request);
	}
    
    
}
