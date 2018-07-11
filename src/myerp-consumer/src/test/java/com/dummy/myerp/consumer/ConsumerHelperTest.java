package com.dummy.myerp.consumer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.dummy.myerp.consumer.dao.contrat.DaoProxy;

/**
 * Class de test de {@link ConsumerHelper}
 * @author Oltenos
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ConsumerHelperTest {
	
	@Mock
	private DaoProxy daoProxy;

	/**
	 * Test de static void configure(DaoProxy pDaoProxy)
	 */
	@Test
	public void configure() {
		ConsumerHelper.configure(daoProxy);
		//Vérifie que le paramètre daoProxy à bien été configuré
		Assert.assertTrue("Test ConsumerHelper.configure()", ConsumerHelper.getDaoProxy().equals(daoProxy));
	}

}
