/**
 * 
 */
package com.alladeson.caurisit.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alladeson.caurisit.models.entities.Demande;
import com.alladeson.caurisit.models.entities.SerialKey;
import com.alladeson.caurisit.repositories.SerialKeyRepository.serialKeyValue;
import com.alladeson.caurisit.services.DemandeService;

/**
 * @author allad
 *
 */
@RestController
public class DemandeController {
	@Autowired
	private DemandeService demandeService;

	/**
	 * @param demande
	 * @return
	 * @see com.alladeson.caurisit.services.DemandeService#createDemande(com.alladeson.caurisit.models.entities.Demande)
	 */
	@PostMapping("demande")
	public Demande createDemande(Demande demande) {
		return demandeService.createDemande(demande);
	}

	/**
	 * @param demandeId
	 * @return
	 * @see com.alladeson.caurisit.services.DemandeService#getDemande(java.lang.Long)
	 */
	@GetMapping("demande/{id}")
	public Demande getDemande(@PathVariable(value = "id") Long demandeId) {
		return demandeService.getDemande(demandeId);
	}

	/**
	 * @return
	 * @see com.alladeson.caurisit.services.DemandeService#getAllDemande()
	 */
	@GetMapping("demande")
	public List<Demande> getAllDemande() {
		return demandeService.getAllDemande();
	}

	/**
	 * @param demandeId
	 * @param serialKey
	 * @return
	 * @see com.alladeson.caurisit.services.DemandeService#updateSerialKey(java.lang.Long,
	 *      java.lang.String)
	 */
	@PutMapping("demande/{id}/serialKey/{serialKeyId}")
	public Demande updateSerialKey(@PathVariable(value = "id") Long demandeId,
			@PathVariable(value = "serialKeyId") Long serialKeyId) {
		return demandeService.updateSerialKey(demandeId, serialKeyId);
	}

	/**
	 * @param demandeId
	 * @param fileName
	 * @param file
	 * @return
	 * @see com.alladeson.caurisit.services.DemandeService#setDemandeFiles(java.lang.Long,
	 *      java.lang.String, org.springframework.web.multipart.MultipartFile)
	 */
	@PutMapping("demande/{id}/fileName/{fileName}")
	public Demande setDemandeFiles(@PathVariable(value = "id") Long demandeId,
			@PathVariable(value = "fileName") String fileName, @RequestParam("file") MultipartFile file) {
		return demandeService.setDemandeFiles(demandeId, fileName, file);
	}

	/**
	 * @param demandeId
	 * @param statusName
	 * @return
	 * @see com.alladeson.caurisit.services.DemandeService#updateStatus(java.lang.Long,
	 *      java.lang.String)
	 */
	@PutMapping("demande/{id}/statusName/{statusName}")
	public Demande updateStatus(@PathVariable(value = "id") Long demandeId,
			@PathVariable(value = "statusName") String statusName) {
		return demandeService.updateStatus(demandeId, statusName);
	}

	/**
	 * @param demandeId
	 * @return
	 * @see com.alladeson.caurisit.services.DemandeService#deleteDemande(java.lang.Long)
	 */
	@DeleteMapping("demande/{id}")
	public boolean deleteDemande(@PathVariable(value = "id") Long demandeId) {
		return demandeService.deleteDemande(demandeId);
	}

	/**
	 * @param search
	 * @return
	 * @see com.alladeson.caurisit.services.DemandeService#getSerialKeyAutocomplete(java.lang.String)
	 */
	@GetMapping("demande/serial-key/autocomplete")
	public List<serialKeyValue> getSerialKeyAutocomplete(@RequestParam(name = "search", required = true) String search) {
		return demandeService.getSerialKeyAutocomplete(search);
	}

	/**
	 * @return
	 * @see com.alladeson.caurisit.services.DemandeService#getSerialKeysForDemande()
	 */
	@GetMapping("demande/serial-key/all")
	public List<SerialKey> getSerialKeysForDemande() {
		return demandeService.getSerialKeysForDemande();
	}	
}
