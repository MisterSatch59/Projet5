package com.dummy.myerp.testconsumer.consumer;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;

/**
 * Classe mère des classes de test d'intégration de la couche Business
 */
public abstract class ConsumerTestCase {

	static {
		SpringRegistry.init();
	}

	/** {@link DaoProxy} */
	private static final DaoProxy DAO_PROXY = (DaoProxy) SpringRegistry.getBean("DaoProxy");

	/** {@link ComptabiliteDaoImpl} */
	private static final ComptabiliteDao COMPTABILITE_DAO = (ComptabiliteDao) SpringRegistry.getBean("ComptabiliteDaoImpl");

	/** {@link ComptabiliteDaoImpl} */
	private static final DataSource DATA_SOURCE = (DataSource) SpringRegistry.getBean("dataSource");
	
	/** {@link ComptabiliteDaoImpl} */
	private static final DataSourceTransactionManager TX_MANAGER = (DataSourceTransactionManager) SpringRegistry.getBean("txManager");
	

	// ==================== Constructeurs ====================
	/**
	 * Constructeur.
	 */
	public ConsumerTestCase() {
	}

	// ==================== Getters/Setters ====================
	public static DaoProxy getDaoProxy() {
		return DAO_PROXY;
	}

	public static ComptabiliteDao getComptabiliteDao() {
		return COMPTABILITE_DAO;
	}

	public static DataSource getDataSource() {
		return DATA_SOURCE;
	}

	public static DataSourceTransactionManager getTxManager() {
		return TX_MANAGER;
	}

}
