package com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite;

import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;

/**
 * Class de test de SequenceEcritureComptableRM
 * 
 * @author Oltenos
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SequenceEcritureComptableRMTest {

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
	 * test de SequenceEcritureComptable mapRow(ResultSet pRS, int pRowNum) throws SQLException
	 * 
	 * @throws SQLException
	 */
	@Test
	public void mapRow() throws SQLException {
		// Création du resultSet en paramètre
		Mockito.when(resultSet.getInt("annee")).thenReturn(2018);
		Mockito.when(resultSet.getInt("derniere_valeur")).thenReturn(53);

		// Test
		SequenceEcritureComptableRM vSequenceEcritureComptableRM = new SequenceEcritureComptableRM();
		SequenceEcritureComptable vResult = vSequenceEcritureComptableRM.mapRow(resultSet, 0);

		// Vérifie vResult!=null
		Assert.assertTrue("Test SequenceEcritureComptableRM.mapRow() : résultat null", vResult != null);

		// Vérifie les valeurs de vResult
		Assert.assertTrue("Test SequenceEcritureComptableRM.mapRow() : numéro faux", vResult.getAnnee().intValue()==2018);
		Assert.assertTrue("Test SequenceEcritureComptableRM.mapRow() : libelle faux", vResult.getDerniereValeur().intValue()==53);

	}
}
