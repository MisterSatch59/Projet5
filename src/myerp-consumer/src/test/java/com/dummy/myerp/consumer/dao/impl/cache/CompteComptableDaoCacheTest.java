package com.dummy.myerp.consumer.dao.impl.cache;

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

/**
 * Class de test de {@link CompteComptableDaoCache}
 * 
 * @author Oltenos
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CompteComptableDaoCacheTest {

	@Mock
	private DaoProxy daoProxy;

	@Mock
	private ComptabiliteDao comptabiliteDao;

	/**
	 * Initialisation du context : config des Mock de daoProxy et comptabiliteDao
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
	 * Test de CompteComptable getByNumero(Integer pNumero)
	 */
	@Test
	public void getByNumero() {
		// création d'une liste de CompteComptable
		List<CompteComptable> vList = new ArrayList<CompteComptable>();
		vList.add(new CompteComptable(1));
		CompteComptable vCompteComptable = new CompteComptable(16,"libelle");
		vList.add(vCompteComptable);
		CompteComptable vCompteComptable2 = new CompteComptable(19);
		vList.add(vCompteComptable2);
		vList.add(new CompteComptable(165));
		vList.add(new CompteComptable(5034));
		
		// définition de la list de CompteComptable retourné par le Mock de ComptabiliteDao
		Mockito.when(comptabiliteDao.getListCompteComptable()).thenReturn(vList);

		// Test de deux comptes de la liste
		Assert.assertTrue(vList.toString(), CompteComptableDaoCache.getByNumero(16).equals(vCompteComptable));
		Assert.assertTrue(vList.toString(), CompteComptableDaoCache.getByNumero(19).equals(vCompteComptable2));

		// Test de deux comptes qui ne sont pas dans la liste avec un numéro au milieu et un autre plus grand que le plus grand numéro de la liste
		Assert.assertTrue(vList.toString(), CompteComptableDaoCache.getByNumero(17) == null);
		Assert.assertTrue(vList.toString(), CompteComptableDaoCache.getByNumero(7598) == null);

	}

}
