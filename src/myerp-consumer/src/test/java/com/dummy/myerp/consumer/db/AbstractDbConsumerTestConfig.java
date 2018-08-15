package com.dummy.myerp.consumer.db;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import com.dummy.myerp.technical.exception.TechnicalException;

/**
 * Class de test de {@link AbstractDbConsumer} Regroupe les test à effectuer de
 * la configuration et après configuration
 * 
 * @author Oltenos
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractDbConsumerTestConfig {

	@Spy // Utilisation de Spy pour instancier une classe héritant de la classe abstraite et de tester les méthodes non static de AbstractDbConsumer
	private AbstractDbConsumer abstractDbConsumer;

	@Mock
	private DataSource dataSource;
	
	@Mock
	private JdbcTemplate vJdbcTemplate;

	/**
	 * Test de static void configure(Map<DataSourcesEnum, DataSource> pMapDataSource) avec le paramètre à null, TechnicalException attendu
	 * 
	 * @throws TechnicalException
	 */
	@Test(expected = TechnicalException.class)
	public void configureParamNull() throws TechnicalException {
		AbstractDbConsumer.configure(null);
	}

	/**
	 * Test de static void configure(Map<DataSourcesEnum, DataSource> pMapDataSource) avec le Map vide, TechnicalException attendu
	 * 
	 * @throws TechnicalException
	 */
	@Test(expected = TechnicalException.class)
	public void configureMapVide() throws TechnicalException {
		// Création Map vide
		Map<DataSourcesEnum, DataSource> vMapDataSource = new HashMap<DataSourcesEnum, DataSource>();
		// Test
		AbstractDbConsumer.configure(vMapDataSource);

	}

	/**
	 * Test de static void configure(Map<DataSourcesEnum, DataSource>
	 * pMapDataSource) avec les valeurs du Map null, TechnicalException attendu
	 * 
	 * @throws TechnicalException
	 */
	@Test(expected = TechnicalException.class)
	public void configureDataSourceNull() throws TechnicalException {
		// Création Map avec les clés de l'Enum mais Datasource null
		Map<DataSourcesEnum, DataSource> vMapDataSource = new HashMap<DataSourcesEnum, DataSource>();
		for (DataSourcesEnum dataSourcesEnum : DataSourcesEnum.values()) {
			vMapDataSource.put(dataSourcesEnum, null);
		}
		// Test
		AbstractDbConsumer.configure(vMapDataSource);
	}

	/**
	 * Test de static void configure(Map<DataSourcesEnum, DataSource>
	 * pMapDataSource) avec de bonnes valeurs
	 * 
	 * @throws TechnicalException
	 */
	@Test
	public void configure() throws TechnicalException {
		// Création Map avec les clés de l'Enum et dataSource
		Map<DataSourcesEnum, DataSource> vMapDataSource = new HashMap<DataSourcesEnum, DataSource>();
		for (DataSourcesEnum dataSourcesEnum : DataSourcesEnum.values()) {
			vMapDataSource.put(dataSourcesEnum, dataSource);
		}
		// Test
		AbstractDbConsumer.configure(vMapDataSource);
		for (DataSourcesEnum dataSourcesEnum : DataSourcesEnum.values()) {
			Assert.assertTrue("Test de AbstractDbConsumer.getDataSource()", abstractDbConsumer.getDataSource(dataSourcesEnum).equals(dataSource));
		}
	}

	/**
	 * Test de DataSource getDataSource(DataSourcesEnum pDataSourceId)
	 * 
	 * @throws TechnicalException
	 */
	@Test
	public void getDataSource() throws TechnicalException {
		// Création Map avec les clés de l'Enum et dataSource
		Map<DataSourcesEnum, DataSource> vMapDataSource = new HashMap<DataSourcesEnum, DataSource>();
		for (DataSourcesEnum dataSourcesEnum : DataSourcesEnum.values()) {
			vMapDataSource.put(dataSourcesEnum, dataSource);
		}
		// configuration de AbstractDbConsumer
		AbstractDbConsumer.configure(vMapDataSource);

		// Test
		Assert.assertTrue("Test DataSource getDataSource(DataSourcesEnum pDataSourceId) AbstractDbConsumer configuré", abstractDbConsumer.getDataSource(DataSourcesEnum.MYERP).equals(dataSource));
	}

}
