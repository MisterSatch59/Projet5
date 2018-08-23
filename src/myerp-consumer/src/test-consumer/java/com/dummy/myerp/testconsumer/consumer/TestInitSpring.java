package com.dummy.myerp.testconsumer.consumer;

import org.junit.Test;
import com.dummy.myerp.technical.exception.TechnicalException;

import static org.junit.Assert.assertNotNull;

/**
 * Classe de test de l'initialisation du contexte Spring
 */
public class TestInitSpring extends ConsumerTestCase {

	/**
	 * Constructeur.
	 */
	public TestInitSpring() {
		super();
	}

	/**
	 * Teste l'initialisation du contexte Spring
	 * 
	 * @throws TechnicalException
	 */
	@Test
	public void testInit() throws TechnicalException {
		assertNotNull(getDataSource());

		assertNotNull(getDaoProxy());
		assertNotNull(getDaoProxy().getComptabiliteDao());
		assertNotNull(getDaoProxy().getComptabiliteDao().getListCompteComptable());
		assertNotNull(getComptabiliteDao());

	}
}
