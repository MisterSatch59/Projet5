package com.dummy.myerp.testbusiness.business;

import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.List;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.TechnicalException;

public class TestIntegrationCRUD extends BusinessTestCase {

	private static String[] listNomTable;

	/**
	 * Constructeur.
	 */
	public TestIntegrationCRUD() {
		super();

		listNomTable = new String[5];
		listNomTable[0] = "myerp.ligne_ecriture_comptable";
		listNomTable[1] = "myerp.ecriture_comptable";
		listNomTable[2] = "myerp.sequence_ecriture_comptable";
		listNomTable[3] = "myerp.journal_comptable";
		listNomTable[4] = "myerp.compte_comptable";
	}

	/**
	 * Réinitialisation de la base de données aprés chaque utilisation
	 * 
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		resetBD();
	}

	/**
	 * Permet la réinitialisation de la base de données après chaque test
	 * 
	 * @throws TechnicalException
	 */
	private static void resetBD() throws TechnicalException {
		DataSourceTransactionManager txManager = (DataSourceTransactionManager) SpringRegistry.getBean("txManagerMYERP");
		DataSource dataSource = (DataSource) SpringRegistry.getBean("dataSource");

		// vide les tables
		for (int i = 0; i < listNomTable.length; i++) {
			TransactionStatus vTransactionStatus = txManager.getTransaction(new DefaultTransactionDefinition());
			try {
				String vSQL = "DELETE FROM " + listNomTable[i];

				MapSqlParameterSource vParams = new MapSqlParameterSource();

				NamedParameterJdbcTemplate vJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

				vJdbcTemplate.update(vSQL, vParams);

				TransactionStatus vTScommit = vTransactionStatus;
				vTransactionStatus = null;
				txManager.commit(vTScommit);
			} finally {
				if (vTransactionStatus != null) {
					txManager.rollback(vTransactionStatus);
					throw new TechnicalException("Technical error with the database");
				}
			}

		}

		// Rempli les tables
		TransactionStatus vTransactionStatus = txManager.getTransaction(new DefaultTransactionDefinition());

		Resource resource = new ClassPathResource("remplirBD.sql");
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
		try {
			databasePopulator.execute(dataSource);

			TransactionStatus vTScommit = vTransactionStatus;
			vTransactionStatus = null;
			txManager.commit(vTScommit);
		} finally {
			if (vTransactionStatus != null) {
				txManager.rollback(vTransactionStatus);
				throw new TechnicalException("Technical error with the database");
			}
		}

	}

	/**
	 * Test de lecture dans la base de données
	 */
	@Test
	public void testGet() {
		List<CompteComptable> vListCompteComptable = getBusinessProxy().getComptabiliteManager().getListCompteComptable();
		List<EcritureComptable> vListEcritureComptable = getBusinessProxy().getComptabiliteManager().getListEcritureComptable();
		List<JournalComptable> vListJournalComptable = getBusinessProxy().getComptabiliteManager().getListJournalComptable();

		// Test
		assertNotNull(vListCompteComptable);
		assertNotNull(vListEcritureComptable);
		assertNotNull(vListJournalComptable);

		Assert.assertTrue("La liste de CompteComptable retourné ne correpond pas à l'attendu.", vListCompteComptable.size() == 7);
		Assert.assertTrue("La liste de EcritureComptable retourné ne correpond pas à l'attendu.", vListEcritureComptable.size() == 5);
		Assert.assertTrue("La liste de JournalComptable retourné ne correpond pas à l'attendu.", vListJournalComptable.size() == 4);

	}

	/**
	 * Test de suppression d'un élément dans la base de données
	 */
	@Test
	public void testDelete() {
		getBusinessProxy().getComptabiliteManager().deleteEcritureComptable(-1);
		List<EcritureComptable> vListEcritureComptable = getBusinessProxy().getComptabiliteManager().getListEcritureComptable();
		Assert.assertTrue("L'EcritureComptable n'a pas été supprimé.", vListEcritureComptable.size() == 4);
	}

	/**
	 * Test d'ajout d'un élément dans la base de données
	 * 
	 * @throws FunctionalException
	 */
	@Test
	public void testInsert() throws FunctionalException {
		List<EcritureComptable> vListEcritureComptable = getBusinessProxy().getComptabiliteManager().getListEcritureComptable();
		EcritureComptable vEcritureComptable = null;
		for (EcritureComptable ecritureComptable : vListEcritureComptable) {
			if (ecritureComptable.getId().intValue() == -1) {
				vEcritureComptable = ecritureComptable;
			}
		}

		vEcritureComptable.setJournal(new JournalComptable("BQ", "Banque"));

		Calendar vCalendar = Calendar.getInstance();
		vEcritureComptable.setDate(vCalendar.getTime());

		vEcritureComptable.setLibelle("Test");

		getBusinessProxy().getComptabiliteManager().addReference(vEcritureComptable);

		getBusinessProxy().getComptabiliteManager().insertEcritureComptable(vEcritureComptable);

		vListEcritureComptable = getBusinessProxy().getComptabiliteManager().getListEcritureComptable();
		Assert.assertTrue("L'EcritureComptable n'a pas été ajouté.", vListEcritureComptable.size() == 6);

		for (EcritureComptable ecritureComptable : vListEcritureComptable) {
			if (ecritureComptable.getId().intValue() == 1) {
				vEcritureComptable = ecritureComptable;
			}
		}
		Assert.assertTrue("Les LisgneEcritureComptable n'ont pas été ajoutées correctement.", vEcritureComptable.getListLigneEcriture().size() == 3);
	}

	/**
	 * Test de modification d'un élément dans la base de données
	 * 
	 * @throws FunctionalException
	 */
	@Test
	public void testUpdate() throws FunctionalException {
		//Récupération de l'EcritureComptable d'ID -1
		List<EcritureComptable> vListEcritureComptable = getBusinessProxy().getComptabiliteManager().getListEcritureComptable();
		EcritureComptable vEcritureComptableAttendu = null;
		for (EcritureComptable ecritureComptable : vListEcritureComptable) {
			if (ecritureComptable.getId().intValue() == -1) {
				vEcritureComptableAttendu = ecritureComptable;
			}
		}

		//Modification de l'EcritureComptable
		vEcritureComptableAttendu.setJournal(new JournalComptable("BQ", "Banque"));

		Calendar vCalendar = Calendar.getInstance();
		vEcritureComptableAttendu.setDate(vCalendar.getTime());

		vEcritureComptableAttendu.setLibelle("Test");

		getBusinessProxy().getComptabiliteManager().addReference(vEcritureComptableAttendu);

		//Mise à joiur dans la base de données
		getBusinessProxy().getComptabiliteManager().updateEcritureComptable(vEcritureComptableAttendu);

		//Récupération à nouveau de l'EcritureComptable d'ID -1
		vListEcritureComptable = getBusinessProxy().getComptabiliteManager().getListEcritureComptable();

		EcritureComptable vEcritureComptableModifie = null;
		for (EcritureComptable ecritureComptable : vListEcritureComptable) {
			if (ecritureComptable.getId().intValue() == -1) {
				vEcritureComptableModifie = ecritureComptable;
			}
		}

		// Test de la correpondance de l'EcritureComptable retourné avec l'EcritureComptable modifié
		Assert.assertTrue("L'objet retourné ne correpond pas à l'objet modifié.", ModelTestUtilities.isEqual(vEcritureComptableModifie, vEcritureComptableAttendu));
	}

}
