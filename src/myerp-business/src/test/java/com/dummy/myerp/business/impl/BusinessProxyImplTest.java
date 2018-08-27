package com.dummy.myerp.business.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.dummy.myerp.consumer.dao.contrat.DaoProxy;

/**
 * Class de test de {@link BusinessProxyImpl}
 * 
 * @author Oltenos
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class BusinessProxyImplTest {

	@Mock
	private DaoProxy daoProxy;

	@Mock
	private TransactionManager transactionManager;

	/**
	 * Test de static BusinessProxyImpl static DaoProxyImpl getInstance() sans configuration
	 * UnsatisfiedLinkError attendu
	 */
	@Test(expected = UnsatisfiedLinkError.class)
	public void getInstanceSansConfig() {
		BusinessProxyImpl.getInstance(null, transactionManager);
		BusinessProxyImpl.getInstance();
	}

	/**
	 * Test de static BusinessProxyImpl getInstance(DaoProxy pDaoProxy, TransactionManager pTransactionManager) et static DaoProxyImpl getInstance()
	 */
	@Test
	public void getInstance() {
		// Test que le résultat est non null
		BusinessProxyImpl vDaoProxyImpl = BusinessProxyImpl.getInstance(daoProxy, transactionManager);
		Assert.assertTrue("Test BusinessProxyImplTest.getInstance()", vDaoProxyImpl != null);

		// test que la méthode retourne le même objet (Pattern Singleton)
		BusinessProxyImpl vDaoProxyImpl2 = BusinessProxyImpl.getInstance();
		Assert.assertTrue("Test BusinessProxyImplTest.getInstance()", vDaoProxyImpl == vDaoProxyImpl2);
	}

}
