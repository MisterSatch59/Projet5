package com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.dummy.myerp.model.bean.comptabilite.JournalComptable;

/**
 * Class de test de JournalComptableRM
 * 
 * @author Oltenos
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class JournalComptableRMTest {

	@Mock
	private ResultSet resultSet;

	/**
	 * test de JournalComptable mapRow(ResultSet pRS, int pRowNum) throws SQLException
	 * 
	 * @throws SQLException
	 */
	@Test
	public void mapRow() throws SQLException {
		// Création du resultSet en paramètre
		Mockito.when(resultSet.getString("code")).thenReturn("coDe");
		Mockito.when(resultSet.getString("libelle")).thenReturn("Libelle");

		// Test
		JournalComptableRM vJournalComptableRM = new JournalComptableRM();
		JournalComptable vResult = vJournalComptableRM.mapRow(resultSet, 0);

		// Vérifie vResult!=null
		Assert.assertTrue("Test JournalComptableRM.mapRow() : résultat null", vResult != null);

		// Vérifie les valeurs de vResult
		Assert.assertTrue("Test JournalComptableRM.mapRow() : numéro faux", vResult.getCode().equals("coDe"));
		Assert.assertTrue("Test JournalComptableRM.mapRow() : libelle faux", vResult.getLibelle().equals("Libelle"));

	}
}
