package com.dummy.myerp.consumer.dao.impl.cache;

import java.util.List;

import com.dummy.myerp.consumer.ConsumerHelper;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;

/**
 * Cache DAO de {@link JournalComptable}
 */
public class JournalComptableDaoCache {

	// ==================== Attributs ====================
	/** The List compte comptable. */
	private static List<JournalComptable> listJournalComptable; // ******** Oltenos : passage en static pour n'avoir qu'un seul cache...

	// ==================== Constructeurs ====================
	/**
	 * privé pour évité l'instantiation de cette classe utilisable uniquement de manière static
	 */
	private JournalComptableDaoCache() {
	}

	// ==================== Méthodes ====================
	/**
	 * Gets by code.
	 *
	 * @param pCode
	 *            le code du {@link JournalComptable}
	 * @return {@link JournalComptable} ou {@code null}
	 */
	public static JournalComptable getByCode(String pCode) {
		if (listJournalComptable == null) {
			listJournalComptable = ConsumerHelper.getDaoProxy().getComptabiliteDao().getListJournalComptable();
		}

		return JournalComptable.getByCode(listJournalComptable, pCode);
	}
}
