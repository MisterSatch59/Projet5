package com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;

/**
 * Class de test de LigneEcritureComptableRM
 * 
 * @author Oltenos
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LigneEcritureComptableRMTest {

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
	 * test de LigneEcritureComptable mapRow(ResultSet pRS, int pRowNum) throws SQLException
	 * 
	 * @throws SQLException
	 */
	@Test
	public void mapRow() throws SQLException {
		// création d'une liste de CompteComptable
		List<CompteComptable> vList = new ArrayList<CompteComptable>();
		vList.add(new CompteComptable(1));
		CompteComptable vCompteComptable = new CompteComptable(16, "libelle 16");
		vList.add(vCompteComptable);
		CompteComptable vCompteComptable2 = new CompteComptable(19);
		vList.add(vCompteComptable2);
		vList.add(new CompteComptable(165));
		vList.add(new CompteComptable(5034));

		// définition de la list de CompteComptable retourné par le Mock de ComptabiliteDao
		Mockito.when(comptabiliteDao.getListCompteComptable()).thenReturn(vList);

		// Création du resultSet en paramètre
		Mockito.when(resultSet.getBigDecimal("credit")).thenReturn(new BigDecimal("12.54"));
		Mockito.when(resultSet.getBigDecimal("debit")).thenReturn(new BigDecimal("5463.27"));
		Mockito.when(resultSet.getString("libelle")).thenReturn("Libelle");
		Mockito.when(resultSet.getObject("compte_comptable_numero", Integer.class)).thenReturn(vCompteComptable.getNumero());

		// Test
		LigneEcritureComptableRM vLigneEcritureComptableRM = new LigneEcritureComptableRM();
		LigneEcritureComptable vResult = vLigneEcritureComptableRM.mapRow(resultSet, 0);

		// Vérifie vResult!=null
		Assert.assertTrue("Test LigneEcritureComptableRM.mapRow() : résultat null", vResult != null);

		// Vérifie les valeurs des attributs de vResult
		Assert.assertTrue("Test LigneEcritureComptableRM.mapRow() : credit faux", vResult.getCredit().compareTo(new BigDecimal("12.54")) == 0);
		Assert.assertTrue("Test LigneEcritureComptableRM.mapRow() : debit faux", vResult.getDebit().compareTo(new BigDecimal("5463.27")) == 0);
		Assert.assertTrue("Test LigneEcritureComptableRM.mapRow() : libelle faux", vResult.getLibelle().equals("Libelle"));
		Assert.assertTrue("Test LigneEcritureComptableRM.mapRow() : CompteComptable faux", vResult.getCompteComptable().equals(vCompteComptable));

	}
}
