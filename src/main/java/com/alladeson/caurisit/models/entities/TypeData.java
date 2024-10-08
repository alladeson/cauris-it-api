/**
 * 
 */
package com.alladeson.caurisit.models.entities;

/**
 * @author William ALLADE
 *
 */
public enum TypeData {
	/**
	 * U pour l'unité d'article
	 * IMPOT pour les types de taxe impôt
	 * AIB pour les types de taxe aib
	 * FV pour les types de facture de vente
	 * FA pour les types de facture d'avoir
	 * A4 pour le format A4 lors de l'impression des factures
	 * A8 pour le format A8 (petit ticket) lors de l'impression des factures
	 * ENTRE pour le mouvement d'entrée des articles (approvisionnement)
	 * SORTIE pour le mouvement de sortie des articles (vente)
	 * */
	U, IMPOT, AIB, FV, FA, A4, A8, ENTREE, SORTIE, RESTAURATION, KG
}
