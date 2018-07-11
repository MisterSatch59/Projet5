package com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.dummy.myerp.model.bean.comptabilite.CompteComptable;

/**
 * Class de test de CompteComptableRM
 * 
 * @author Oltenos
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CompteComptableRMTest {

	@Mock
	private ResultSet resultSet;

	/**
	 * test de CompteComptable mapRow(ResultSet pRS, int pRowNum) throws SQLException
	 * 
	 * @throws SQLException
	 */
	@Test
	public void mapRow() throws SQLException {
		// Création du resultSet en paramètre
		Mockito.when(resultSet.getInt("numero")).thenReturn(15);
		Mockito.when(resultSet.getString("libelle")).thenReturn("Libelle");

		// Test
		CompteComptableRM vCompteComptableRM = new CompteComptableRM();
		CompteComptable vResult = vCompteComptableRM.mapRow(resultSet, 0);

		// Vérifie vResult!=null
		Assert.assertTrue("Test CompteComptableRM.mapRow() : résultat null", vResult != null);

		// Vérifie les valeurs de vResult
		Assert.assertTrue("Test CompteComptableRM.mapRow() : numéro faux", vResult.getNumero().intValue() == 15);
		Assert.assertTrue("Test CompteComptableRM.mapRow() : libelle faux", vResult.getLibelle().equals("Libelle"));
	}
}
