package com.dummy.myerp.consumer.db;

import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import com.dummy.myerp.consumer.ConsumerHelper;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;

/**
 * Class de test de {@link AbstractDbConsumer} Regroupe les test à effectuer
 * avant configuration
 * 
 * @author Oltenos
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractDbConsumerTestSansConfig {

	@Mock
	private DaoProxy daoProxy;

	@Spy // Utilisation de Spy pour instancier une classe héritant de la classe abstraite et de tester les méthodes non static de AbstractDbConsumer
	private AbstractDbConsumer abstractDbConsumer;

	/**
	 * Test de static DaoProxy getDaoProxy()
	 */
	@Test
	public void getDaoProxy() {
		// Test getDaoProxy si ConsumerHelper non configuré, null attendu
		Assert.assertTrue("Test AbstractDbConsumer.getDaoProxy() avec ConsumerHelper non configuré", AbstractDbConsumer.getDaoProxy() == null);

		// Test getDaoProxy si ConsumerHelper configuré, daoProxy attendu
		ConsumerHelper.configure(daoProxy);
		Assert.assertTrue("Test AbstractDbConsumer.getDaoProxy() ConsumerHelper configuré", AbstractDbConsumer.getDaoProxy().equals(daoProxy));
	}

	/**
	 * Test de DataSource getDataSource(DataSourcesEnum pDataSourceId) sans config
	 * préalable : UnsatisfiedLinkError attendu
	 */
	@Test(expected = UnsatisfiedLinkError.class)
	public void getDataSourceNonConfig() {
		@SuppressWarnings("unused")
		DataSource vDataSource = abstractDbConsumer.getDataSource(DataSourcesEnum.MYERP);
	}

}
