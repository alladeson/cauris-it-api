/**
 * 
 */
package com.alladeson.caurisit.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alladeson.caurisit.models.entities.Audit;
import com.alladeson.caurisit.models.paylaods.AuditPayload;
import com.alladeson.caurisit.services.AuditService;

/**
 * @author allad
 *
 */
@RestController
public class AuditController {
	
	@Autowired
	private AuditService auditService;

	/**
	 * @param userId
	 * @param desc
	 * @param debut
	 * @param fin
	 * @return
	 * @see com.alladeson.caurisit.services.AuditService#getAll(java.lang.Long, java.lang.String, java.time.LocalDateTime, java.time.LocalDateTime)
	 */
	@GetMapping("/access/audit")
	public List<Audit> getAll(@RequestBody AuditPayload payload, @RequestParam(name = "search", required = false, defaultValue = "") String search) {
		return auditService.getAll(payload.getUserId(), payload.getDesc(), payload.getDebut(), payload.getFin(), search);
	}

	/**
	 * @param id
	 * @return
	 * @see com.alladeson.caurisit.services.AuditService#get(java.lang.Long)
	 */
	@GetMapping("/access/audit/{id}")
	public Audit get(@PathVariable(value = "id") Long id) {
		return auditService.get(id);
	}

	/**
	 * @return
	 * @see com.alladeson.caurisit.services.AuditService#getForConnectedUser()
	 */
	@GetMapping("/access/audit/connected-user")
	public List<Audit> getForConnectedUser() {
		return auditService.getForConnectedUser();
	}	
	
}
