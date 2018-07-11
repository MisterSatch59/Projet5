package com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.dummy.myerp.consumer.ConsumerHelper;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;

/**
 * Class de test de LigneEcritureComptableRM
 * 
 * @author Oltenos
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class EcritureComptableRMTest {

	@Mock
	private static DaoProxy daoProxy;

	@Mock
	private static ComptabiliteDao comptabiliteDao;

	@Mock
	private ResultSet resultSet;

	/**
	 * Initialisation du context : config des Mock de daoProxy et comptabiliteDao et
	 * création de la liste de CompteComptable
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		// config des Mock de daoProxy et comptabiliteDao
		ConsumerHelper.configure(daoProxy);
		Mockito.when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
	}

	/**
	 * test de EcritureComptable mapRow(ResultSet pRS, int pRowNum) throws SQLException
	 * 
	 * @throws SQLException
	 */
	@Test
	public void mapRow() throws SQLException {
		// Création d'une liste de JournalComptable
		List<JournalComptable> vList = new ArrayList<JournalComptable>();

		JournalComptable journalComptable = new JournalComptable("coDe", "JournalComptable");
		vList.add(journalComptable);
		JournalComptable vJournalComptable1 = new JournalComptable("test", "JournalComptable 1");
		vList.add(vJournalComptable1);
		JournalComptable vJournalComptable2 = new JournalComptable("15326", "JournalComptable 2");
		vList.add(vJournalComptable2);
		JournalComptable vJournalComptable3 = new JournalComptable("est12", "JournalComptable 3");
		vList.add(vJournalComptable3);

		Mockito.when(comptabiliteDao.getListJournalComptable()).thenReturn(vList);

		// Création du resultSet en paramètre
		Mockito.when(resultSet.getInt("id")).thenReturn(12);
		Mockito.when(resultSet.getString("reference")).thenReturn("Reference");
		Date date = new Date(Calendar.getInstance().getTime().getTime());
		Mockito.when(resultSet.getDate("date")).thenReturn(date);
		Mockito.when(resultSet.getString("libelle")).thenReturn("Libelle");

		Mockito.when(resultSet.getString("journal_code")).thenReturn("coDe");

		// Test
		EcritureComptableRM vEcritureComptableRM = new EcritureComptableRM();
		EcritureComptable vResult = vEcritureComptableRM.mapRow(resultSet, 0);

		// Vérifie les valeurs de vResult :
		Assert.assertTrue("Test CompteComptableRM.mapRow() : id faux", vResult.getId().intValue() == 12);
		Assert.assertTrue("Test CompteComptableRM.mapRow() : reference faux", vResult.getReference().equals("Reference"));

		// Date
		Calendar resultCalendar = Calendar.getInstance();
		Calendar initCalendar = Calendar.getInstance();
		initCalendar.setTime(date);
		resultCalendar.setTime(vResult.getDate());
		boolean result = (resultCalendar.get(Calendar.YEAR) == initCalendar.get(Calendar.YEAR)
				&& resultCalendar.get(Calendar.MONTH) == initCalendar.get(Calendar.MONTH)
				&& resultCalendar.get(Calendar.DAY_OF_MONTH) == initCalendar.get(Calendar.DAY_OF_MONTH)
				&& resultCalendar.get(Calendar.HOUR_OF_DAY) == 0 && resultCalendar.get(Calendar.MINUTE) == 0
				&& resultCalendar.get(Calendar.SECOND) == 0 && resultCalendar.get(Calendar.MILLISECOND) == 0);
		Assert.assertTrue("Test CompteComptableRM.mapRow() : date fausse", result);

		Assert.assertTrue("Test CompteComptableRM.mapRow() : libelle faux", vResult.getLibelle().equals("Libelle"));
		Assert.assertTrue("Test CompteComptableRM.mapRow() : journal faux", vResult.getJournal().equals(journalComptable));
	}
}
