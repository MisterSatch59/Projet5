package com.dummy.myerp.testbusiness.business;

import java.util.Calendar;
import java.util.List;

import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;

public class ModelTestUtilities {

	/**
	 * Test l'égalité de tous les attribut (sauf l'Id et uniquement jour/mois/années uniquement pour la date)
	 * 
	 * @param pEcritureComptable1
	 * @param pEcritureComptable2
	 * @return
	 */
	public static boolean isEqual(EcritureComptable pEcritureComptable1, EcritureComptable pEcritureComptable2) {
		// Egalité du journal
		boolean vTest = pEcritureComptable1.getJournal().getCode().equals(pEcritureComptable2.getJournal().getCode());
		vTest = vTest && pEcritureComptable1.getJournal().getLibelle().equals(pEcritureComptable2.getJournal().getLibelle());

		// Egalité Libelle et Reference
		vTest = vTest && pEcritureComptable1.getLibelle().equals(pEcritureComptable2.getLibelle());
		vTest = vTest && pEcritureComptable1.getReference().equals(pEcritureComptable2.getReference());

		// Egalité jour/mois/années des dates
		Calendar vTestCalendar1 = Calendar.getInstance();
		vTestCalendar1.setTime(pEcritureComptable1.getDate());
		Calendar vTestCalendar2 = Calendar.getInstance();
		vTestCalendar2.setTime(pEcritureComptable2.getDate());
		vTest = vTest && vTestCalendar1.get(Calendar.YEAR) == vTestCalendar2.get(Calendar.YEAR);
		vTest = vTest && vTestCalendar1.get(Calendar.MONTH) == vTestCalendar2.get(Calendar.MONTH);
		vTest = vTest && vTestCalendar1.get(Calendar.DAY_OF_MONTH) == vTestCalendar2.get(Calendar.DAY_OF_MONTH);

		// Egalité des éléments de la list des LigneEcritureComptable
		List<LigneEcritureComptable> vListLigneEcriture1 = pEcritureComptable1.getListLigneEcriture();
		List<LigneEcritureComptable> vListLigneEcriture2 = pEcritureComptable2.getListLigneEcriture();

		if (vListLigneEcriture1.size() != vListLigneEcriture2.size()) {
			return false;
		} else {
			for (LigneEcritureComptable ligneEcritureComptable1 : vListLigneEcriture1) {
				boolean isIn2 = false;
				for (LigneEcritureComptable ligneEcritureComptable2 : vListLigneEcriture2) {
					if (isEqual(ligneEcritureComptable1, ligneEcritureComptable2))
						isIn2 = true;
				}
				vTest = vTest && isIn2;
			}
		}

		return vTest;
	}

	/**
	 * Test l'égalité de tous les attributs
	 * 
	 * @param pLigneEcritureComptable1
	 * @param pLigneEcritureComptable2
	 * @return
	 */
	public static boolean isEqual(LigneEcritureComptable pLigneEcritureComptable1, LigneEcritureComptable pLigneEcritureComptable2) {

		boolean vTest = true;

		// Egalité debit et crédit
		if (pLigneEcritureComptable1.getCredit() != null) {
			if (pLigneEcritureComptable2.getCredit() == null) {
				return false;
			} else {
				vTest = vTest && pLigneEcritureComptable1.getCredit().compareTo(pLigneEcritureComptable2.getCredit()) == 0;
			}
		} else {
			vTest = vTest && (pLigneEcritureComptable2.getCredit() == null);
		}
		if (pLigneEcritureComptable1.getDebit() != null) {
			if (pLigneEcritureComptable2.getDebit() == null) {
				return false;
			} else {
				vTest = vTest && pLigneEcritureComptable1.getDebit().compareTo(pLigneEcritureComptable2.getDebit()) == 0;
			}
		} else {
			vTest = vTest && (pLigneEcritureComptable2.getDebit() == null);
		}

		// Egalite des Compte Comptable
		vTest = vTest && isEqual(pLigneEcritureComptable1.getCompteComptable(), pLigneEcritureComptable2.getCompteComptable());

		// Egalité des Libelles
		vTest = vTest && pLigneEcritureComptable1.getLibelle().equals(pLigneEcritureComptable2.getLibelle());

		return vTest;
	}

	/**
	 * Test l'égalité de tous les attributs
	 * 
	 * @param pCompteComptable1
	 * @param pCompteComptable2
	 * @return
	 */
	public static boolean isEqual(CompteComptable pCompteComptable1, CompteComptable pCompteComptable2) {
		boolean vTest = pCompteComptable1.getNumero().intValue() == pCompteComptable2.getNumero().intValue();

		vTest = vTest && pCompteComptable1.getLibelle().equals(pCompteComptable2.getLibelle());

		return vTest;
	}

	/**
	 * Test l'égalité de tous les attributs
	 * 
	 * @param pJournalComptable1
	 * @param pJournalComptable2
	 * @return
	 */
	public static boolean isEqual(JournalComptable pJournalComptable1, JournalComptable pJournalComptable2) {
		return pJournalComptable1.getCode().equals(pJournalComptable2.getCode()) && pJournalComptable1.getLibelle().equals(pJournalComptable2.getLibelle());
	}

}
