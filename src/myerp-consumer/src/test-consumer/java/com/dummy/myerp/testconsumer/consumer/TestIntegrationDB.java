package com.dummy.myerp.testconsumer.consumer;

import java.util.Calendar;

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

import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.technical.exception.NotFoundException;
import com.dummy.myerp.technical.exception.TechnicalException;

public class TestIntegrationDB extends ConsumerTestCase {

	private static String[] listNomTable;

	/**
	 * Constructeur.
	 */
	public TestIntegrationDB() {
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
	 * Permet la réinitialisation après chaque test
	 * 
	 * @throws TechnicalException
	 */
	private static void resetBD() throws TechnicalException {
		DataSourceTransactionManager txManager = getTxManager();

		// vide les tables
		for (int i = 0; i < listNomTable.length; i++) {
			TransactionStatus vTransactionStatus = txManager.getTransaction(new DefaultTransactionDefinition());
			try {
				String vSQL = "DELETE FROM " + listNomTable[i];

				MapSqlParameterSource vParams = new MapSqlParameterSource();

				NamedParameterJdbcTemplate vJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());

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
			databasePopulator.execute(getDataSource());

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
	 * 
	 * @throws NotFoundException
	 */
	@Test
	public void testGet() throws NotFoundException {
		// EcritureComptable attendu
		EcritureComptable vEcritureComptableAttendu = new EcritureComptable();
		vEcritureComptableAttendu.setId(-1);
		vEcritureComptableAttendu.setJournal(new JournalComptable("AC", "Achat"));
		vEcritureComptableAttendu.setReference("AC-2016/00001");

		Calendar vCalendar = Calendar.getInstance();
		vCalendar.set(2016, 12 - 1, 31);
		vEcritureComptableAttendu.setDate(vCalendar.getTime());

		vEcritureComptableAttendu.setLibelle("Cartouches d’imprimante");

		EcritureComptable vEcritureComptable = getComptabiliteDao().getEcritureComptable(-1);

		// Test
		boolean vTest = vEcritureComptableAttendu.getJournal().getCode().equals(vEcritureComptable.getJournal().getCode());
		vTest = vTest && vEcritureComptableAttendu.getJournal().getLibelle().equals(vEcritureComptable.getJournal().getLibelle());
		vTest = vTest && vEcritureComptableAttendu.getLibelle().equals(vEcritureComptable.getLibelle());
		vTest = vTest && vEcritureComptableAttendu.getReference().equals(vEcritureComptable.getReference());

		Calendar vTestCalendarAttendu = Calendar.getInstance();
		vTestCalendarAttendu.setTime(vEcritureComptableAttendu.getDate());
		Calendar vTestCalendar = Calendar.getInstance();
		vTestCalendar.setTime(vEcritureComptable.getDate());
		vTest = vTest && vTestCalendarAttendu.get(Calendar.YEAR) == vTestCalendar.get(Calendar.YEAR);
		vTest = vTest && vTestCalendarAttendu.get(Calendar.MONTH) == vTestCalendar.get(Calendar.MONTH);
		vTest = vTest && vTestCalendarAttendu.get(Calendar.DAY_OF_MONTH) == vTestCalendar.get(Calendar.DAY_OF_MONTH);

		Assert.assertTrue("L'objet retourné ne correpond pas à l'objet attendu.", vTest);
	}

	/**
	 * Test d'écriture dans la base de données
	 * 
	 * @throws NotFoundException
	 * @throws TechnicalException
	 */
	@Test
	public void testDelete() throws NotFoundException, TechnicalException {
		DataSourceTransactionManager txManager = getTxManager();

		TransactionStatus vTransactionStatus = txManager.getTransaction(new DefaultTransactionDefinition());
		try {
			getComptabiliteDao().deleteEcritureComptable(-1);

			TransactionStatus vTScommit = vTransactionStatus;
			vTransactionStatus = null;
			txManager.commit(vTScommit);
		} finally {
			if (vTransactionStatus != null) {
				txManager.rollback(vTransactionStatus);
				throw new TechnicalException("Technical error with the database");
			}
		}

		boolean notFound = false;
		try {
			getComptabiliteDao().getEcritureComptable(-1);
		} catch (NotFoundException e) {
			notFound = true;
		}

		if (!notFound) {
			Assert.fail("L'élément n'a pas été supprimé de la base de données");
		}

	}

}
