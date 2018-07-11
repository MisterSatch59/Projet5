package com.dummy.myerp.consumer.dao.impl;

import org.junit.Assert;
import org.junit.Test;

/**
 * Class de test de {@link DaoProxyimpl}
 * 
 * @author Oltenos
 *
 */
public class DaoProxyImplTest {

	/**
	 * Test de static DaoProxyImpl getInstance()
	 */
	@Test
	public void getInstance() {
		// Test que le résultat est non null
		DaoProxyImpl vDaoProxyImpl = DaoProxyImpl.getInstance();
		Assert.assertTrue("Test DaoProxyImpl.getInstance()", vDaoProxyImpl != null);

		// test que la méthode retourne le même objet (Pattern Singleton)
		DaoProxyImpl vDaoProxyImpl2 = DaoProxyImpl.getInstance();
		Assert.assertTrue("Test DaoProxyImpl.getInstance()", vDaoProxyImpl == vDaoProxyImpl2);
	}

}
