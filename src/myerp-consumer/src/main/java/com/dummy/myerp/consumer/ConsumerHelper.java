package com.dummy.myerp.consumer;

import com.dummy.myerp.consumer.dao.contrat.DaoProxy;

/**
 * Classe d'aide pour les classes du module consumer
 */
public class ConsumerHelper {

	/** Le DaoProxy à utiliser pour accéder aux autres classes de DAO */
	private static DaoProxy daoProxy;

	// ==================== Constructeurs ====================
	/**
	 * Constructeur privé pour évité l'instanciation de cette classe utilitaire utilisable uniquement de manière static
	 */
	private ConsumerHelper() {

	}

	/**
	 * Méthode de configuration de la classe
	 *
	 * @param pDaoProxy
	 *            -
	 */
	public static void configure(DaoProxy pDaoProxy) {
		daoProxy = pDaoProxy;
	}

	// ==================== Getters/Setters ====================
	public static DaoProxy getDaoProxy() {
		return daoProxy;
	}
}
