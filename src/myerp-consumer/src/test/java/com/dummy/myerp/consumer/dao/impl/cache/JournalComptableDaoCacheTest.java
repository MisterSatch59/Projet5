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
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;

/**
 * Class de test de {@link JournalComptableDaoCache}
 * 
 * @author Oltenos
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class JournalComptableDaoCacheTest {

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
	 * Test de JournalComptable getByCode(String pCode)
	 */
	@Test
	public void getByCode() {
		// création d'une liste de JournalComptable
		List<JournalComptable> vList = new ArrayList<JournalComptable>();

		JournalComptable vJournalComptable1 = new JournalComptable("test", "JournalComptable 1");
		vList.add(vJournalComptable1);
		JournalComptable vJournalComptable2 = new JournalComptable("coDe", "JournalComptable 2");
		vList.add(vJournalComptable2);
		JournalComptable vJournalComptable3 = new JournalComptable("15326", "JournalComptable 3");
		vList.add(vJournalComptable3);
		JournalComptable vJournalComptable4 = new JournalComptable("est12", "JournalComptable 4");
		vList.add(vJournalComptable4);

		// définition de la list de JournalComptable retourné par le Mock de ComptabiliteDao
		Mockito.when(comptabiliteDao.getListJournalComptable()).thenReturn(vList);

		// Test de trois journaux de la liste avec des code uniquement numérique/uniquement caractère et mélangé
		Assert.assertTrue(vList.toString(), JournalComptableDaoCache.getByCode("coDe").equals(vJournalComptable2));
		Assert.assertTrue(vList.toString(), JournalComptableDaoCache.getByCode("15326").equals(vJournalComptable3));
		Assert.assertTrue(vList.toString(), JournalComptableDaoCache.getByCode("est12").equals(vJournalComptable4));

		// Test de deux journaux qui ne sont pas dans la liste
		Assert.assertTrue(vList.toString(), JournalComptableDaoCache.getByCode("blabl") == null);
		Assert.assertTrue(vList.toString(), JournalComptableDaoCache.getByCode("Argh") == null);

	}

}
