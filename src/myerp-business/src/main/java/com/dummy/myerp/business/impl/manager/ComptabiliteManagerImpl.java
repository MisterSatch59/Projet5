package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.TransactionStatus;
import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;

/**
 * Comptabilite manager implementation.
 */
public class ComptabiliteManagerImpl extends AbstractBusinessManager implements ComptabiliteManager {

	// ==================== Attributs ====================

	// ==================== Constructeurs ====================

	// ==================== Méthodes GET ====================
	@Override
	public List<CompteComptable> getListCompteComptable() {
		return getDaoProxy().getComptabiliteDao().getListCompteComptable();
	}

	@Override
	public List<JournalComptable> getListJournalComptable() {
		return getDaoProxy().getComptabiliteDao().getListJournalComptable();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EcritureComptable> getListEcritureComptable() {
		return getDaoProxy().getComptabiliteDao().getListEcritureComptable();
	}

	// ==================== Méthodes "Métiers" ====================
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void addReference(EcritureComptable pEcritureComptable) {

		// 1. Remonter depuis la persitance la dernière valeur de la séquence du journal pour l'année de l'écriture
		Calendar vCalendar = Calendar.getInstance();
		vCalendar.setTime(pEcritureComptable.getDate());
		int vAnnee = vCalendar.get(Calendar.YEAR);

		SequenceEcritureComptable sequence = getDaoProxy().getComptabiliteDao().getDerniereSequenceEcritureComptable(pEcritureComptable.getJournal().getCode(), vAnnee);

		// 2. * S'il n'y a aucun enregistrement pour le journal pour l'année concernée
		SequenceEcritureComptable newSequence = new SequenceEcritureComptable();
		newSequence.setAnnee(vAnnee);

		if (sequence == null) {
			// Utiliser le numéro 1.
			newSequence.setDerniereValeur(1);
		} else {
			// Utiliser la dernière valeur + 1
			newSequence.setDerniereValeur(sequence.getDerniereValeur().intValue() + 1);
		}

		// 3. Mettre à jour la référence de l'écriture avec la référence calculée (RG_Compta_5)
		String numeroSequence = "";
		if (newSequence.getDerniereValeur() < 10000) {
			numeroSequence += "0";
		}
		if (newSequence.getDerniereValeur() < 1000) {
			numeroSequence += "0";
		}
		if (newSequence.getDerniereValeur() < 100) {
			numeroSequence += "0";
		}
		if (newSequence.getDerniereValeur() < 10) {
			numeroSequence += "0";
		}
		numeroSequence += newSequence.getDerniereValeur();
		pEcritureComptable.setReference(pEcritureComptable.getJournal().getCode() + "-" + newSequence.getAnnee() + "/" + numeroSequence);

		// 4. Enregistrer (insert/update) la valeur de la séquence en persitance
		TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
		if (newSequence.getDerniereValeur() == 1) {
			try {
				getDaoProxy().getComptabiliteDao().insertSequenceEcritureComptable(pEcritureComptable.getJournal().getCode(), newSequence);
				getTransactionManager().commitMyERP(vTS);
				vTS = null;
			} finally {
				getTransactionManager().rollbackMyERP(vTS);
			}

		} else {
			try {
				getDaoProxy().getComptabiliteDao().updateSequenceEcritureComptable(pEcritureComptable.getJournal().getCode(), newSequence);
				getTransactionManager().commitMyERP(vTS);
				vTS = null;
			} finally {
				getTransactionManager().rollbackMyERP(vTS);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void checkEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
		this.checkEcritureComptableUnit(pEcritureComptable);
		this.checkEcritureComptableContext(pEcritureComptable);
	}

	/**
	 * Vérifie que l'Ecriture comptable respecte les règles de gestion unitaires,
	 * c'est à dire indépendemment du contexte (unicité de la référence, exercie comptable non cloturé...)
	 *
	 * @param pEcritureComptable
	 *            -
	 * @throws FunctionalException
	 *             Si l'Ecriture comptable ne respecte pas les règles de gestion
	 */
	protected void checkEcritureComptableUnit(EcritureComptable pEcritureComptable) throws FunctionalException {
		// ===== Vérification des contraintes unitaires sur les attributs de l'écriture
		Set<ConstraintViolation<EcritureComptable>> vViolations = getConstraintValidator().validate(pEcritureComptable);
		if (!vViolations.isEmpty()) {
			throw new FunctionalException("L'écriture comptable ne respecte pas les règles de gestion.",
					new ConstraintViolationException(
							"L'écriture comptable ne respecte pas les contraintes de validation",
							vViolations));
		}

		// ===== RG_Compta_2 : Pour qu'une écriture comptable soit valide, elle doit être équilibrée
		if (!pEcritureComptable.isEquilibree()) {
			throw new FunctionalException("VIOLATION RG_COMPTA_2 : L'écriture comptable n'est pas équilibrée.");
		}

		// ===== RG_Compta_3 : une écriture comptable doit avoir au moins 2 lignes d'écriture (1 au débit, 1 au crédit)
		int vNbrCredit = 0;
		int vNbrDebit = 0;
		for (LigneEcritureComptable vLigneEcritureComptable : pEcritureComptable.getListLigneEcriture()) {
			if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getCredit(), BigDecimal.ZERO)) != 0) {
				vNbrCredit++;
			}
			if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getDebit(), BigDecimal.ZERO)) != 0) {
				vNbrDebit++;
			}
		}
		// On test le nombre de lignes car si l'écriture à une seule ligne
		// avec un montant au débit et un montant au crédit ce n'est pas valable
		if (pEcritureComptable.getListLigneEcriture().size() < 2
				|| vNbrCredit < 1
				|| vNbrDebit < 1) {
			throw new FunctionalException(
					"VIOLATION RG_COMPTA_3 : L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.");
		}

		// ===== RG_Compta_5 : Format et contenu de la référence
		String ref = pEcritureComptable.getReference();

		if (!ref.split("-")[0].equals(pEcritureComptable.getJournal().getCode())) {
			throw new FunctionalException(
					"VIOLATION RG_COMPTA_5 : La référence n'est pas conforme, le format XX-AAAA-XXXXX n'est pas respecté.");
		}

		Calendar vCalendar = Calendar.getInstance();
		vCalendar.setTime(pEcritureComptable.getDate());
		if (!ref.split("-")[1].split("/")[0].equals("" + vCalendar.get(Calendar.YEAR))) {
			throw new FunctionalException(
					"VIOLATION RG_COMPTA_5 : La référence n'est pas conforme, le format XX-AAAA-XXXXX n'est pas respecté.");
		}

		// vérifier que l'année dans la référence correspond bien à la date de l'écriture, idem pour le code journal...
	}

	/**
	 * Vérifie que l'Ecriture comptable respecte les règles de gestion liées au contexte
	 * (unicité de la référence, année comptable non cloturé...)
	 *
	 * @param pEcritureComptable
	 *            -
	 * @throws FunctionalException
	 *             Si l'Ecriture comptable ne respecte pas les règles de gestion
	 */
	protected void checkEcritureComptableContext(EcritureComptable pEcritureComptable) throws FunctionalException {
		// ===== RG_Compta_6 : La référence d'une écriture comptable doit être unique
		if (StringUtils.isNoneEmpty(pEcritureComptable.getReference())) {
			try {
				// Recherche d'une écriture ayant la même référence
				EcritureComptable vECRef = getDaoProxy().getComptabiliteDao().getEcritureComptableByRef(
						pEcritureComptable.getReference());

				// Si l'écriture à vérifier est une nouvelle écriture (id == null),
				// ou si elle ne correspond pas à l'écriture trouvée (id != idECRef),
				// c'est qu'il y a déjà une autre écriture avec la même référence
				if (pEcritureComptable.getId() == null
						|| !pEcritureComptable.getId().equals(vECRef.getId())) {
					throw new FunctionalException("VIOLATION RG_COMPTA_6 : Une autre écriture comptable existe déjà avec la même référence.");
				}
			} catch (NotFoundException vEx) {
				// Dans ce cas, c'est bon, ça veut dire qu'on n'a aucune autre écriture avec la même référence.
			}
		}
	}

	// ==================== Méthodes INSERT - UPDATE - DELETE ====================

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insertEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
		this.checkEcritureComptable(pEcritureComptable);
		TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
		try {
			getDaoProxy().getComptabiliteDao().insertEcritureComptable(pEcritureComptable);
			getTransactionManager().commitMyERP(vTS);
			vTS = null;
		} finally {
			getTransactionManager().rollbackMyERP(vTS);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
		this.checkEcritureComptable(pEcritureComptable); // ***** Oltenos : Ajout du check avant la modification en persistance de l'écriture comptable
		TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
		try {
			getDaoProxy().getComptabiliteDao().updateEcritureComptable(pEcritureComptable);
			getTransactionManager().commitMyERP(vTS);
			vTS = null;
		} finally {
			getTransactionManager().rollbackMyERP(vTS);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteEcritureComptable(Integer pId) {
		TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
		try {
			getDaoProxy().getComptabiliteDao().deleteEcritureComptable(pId);
			getTransactionManager().commitMyERP(vTS);
			vTS = null;
		} finally {
			getTransactionManager().rollbackMyERP(vTS);
		}
	}
}
