package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import com.dummy.myerp.business.contrat.BusinessProxy;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.business.impl.TransactionManager;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class ComptabiliteManagerImplTest {

	private ComptabiliteManagerImpl manager = new ComptabiliteManagerImpl();

	@Mock
	private DaoProxy daoProxy;
	@Mock
	private TransactionManager transactionManager;
	@Mock
	private BusinessProxy businessProxy;
	@Mock
	private ComptabiliteDao comptabiliteDao;

	/**
	 * Initialisation du context : config du Mock de daoProxy
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		// config du Mock de daoProxy
		AbstractBusinessManager.configure(businessProxy, daoProxy, transactionManager);

		Mockito.when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
	}

	/**
	 * Test de void addReference(EcritureComptable pEcritureComptable) avec une première référence dans l'année
	 */
	@Test
	public void addReferenceNouvelleAnnee() {
		EcritureComptable ecritureComptable = new EcritureComptable();
		ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
		ecritureComptable.setDate(new Date());
		ecritureComptable.setLibelle("Libelle");
		ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "test"),
				null, new BigDecimal(123),
				null));
		ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2, "test"),
				null, null,
				new BigDecimal(123)));

		String referenceAttendu = "AC-2018/00001";

		Mockito.when(comptabiliteDao.getDerniereSequenceEcritureComptable("AC", 2018)).thenReturn(null);

		// Appel de la méthode à tester
		manager.addReference(ecritureComptable);

		// Vérifie que la référence est correcte
		Assert.assertTrue("Test ComptabiliteManagerImpl.addReference(EcritureComptable pEcritureComptable) : La référence généré n'est pas correcte",
				ecritureComptable.getReference().equals(referenceAttendu));

		// Vérife l'enregistrement en persistance
		Mockito.verify(comptabiliteDao).insertSequenceEcritureComptable(Matchers.eq("AC"), Matchers.any());
	}

	/**
	 * Test de void addReference(EcritureComptable pEcritureComptable)
	 */
	@Test
	public void addReference() {
		EcritureComptable ecritureComptable2 = new EcritureComptable();
		ecritureComptable2.setJournal(new JournalComptable("AC", "Achat"));
		ecritureComptable2.setDate(new Date());
		ecritureComptable2.setLibelle("Libelle");
		ecritureComptable2.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "test"),
				null, new BigDecimal(1234),
				null));
		ecritureComptable2.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2, "test"),
				null, null,
				new BigDecimal(1234)));

		String referenceAttendu2 = "AC-2018/00010";

		Mockito.when(comptabiliteDao.getDerniereSequenceEcritureComptable("AC", 2018)).thenReturn(new SequenceEcritureComptable(2018, 9));

		// Appel de la méthode à tester
		manager.addReference(ecritureComptable2);

		// Vérifie que la référence est correcte
		Assert.assertTrue("Test ComptabiliteManagerImpl.addReference(EcritureComptable pEcritureComptable) : La référence généré n'est pas correcte",
				ecritureComptable2.getReference().equals(referenceAttendu2));

		// Vérife l'enregistrement en persistance
		Mockito.verify(comptabiliteDao).updateSequenceEcritureComptable(Matchers.eq("AC"), Matchers.any());
	}

	/**
	 * Test valide de void checkEcritureComptable(EcritureComptable pEcritureComptable)
	 * 
	 * @throws Exception
	 */
	@Test
	public void checkEcritureComptableUnit() throws Exception {
		EcritureComptable ecritureComptable = new EcritureComptable();
		ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
		ecritureComptable.setDate(new Date());
		ecritureComptable.setLibelle("Libelle");
		ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "test"),
				null, new BigDecimal(123),
				null));
		ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2, "test"),
				null, null,
				new BigDecimal(123)));
		ecritureComptable.setReference("AC-2018/00001");

		manager.checkEcritureComptableUnit(ecritureComptable);
	}

	/**
	 * Test de void checkEcritureComptable(EcritureComptable pEcritureComptable) avec violation de contraites d'attribut
	 * 
	 * @throws Exception
	 */
	@Test(expected = FunctionalException.class)
	public void checkEcritureComptableUnitViolation() throws Exception {
		EcritureComptable vEcritureComptable;
		vEcritureComptable = new EcritureComptable();
		manager.checkEcritureComptableUnit(vEcritureComptable);
	}

	/**
	 * Test de void checkEcritureComptable(EcritureComptable pEcritureComptable) avec vialation de la RG2
	 * 
	 * @throws Exception
	 */
	@Test(expected = FunctionalException.class)
	public void checkEcritureComptableUnitRG2() throws Exception {
		EcritureComptable vEcritureComptable;
		vEcritureComptable = new EcritureComptable();
		vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
		vEcritureComptable.setDate(new Date());
		vEcritureComptable.setLibelle("Libelle");
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "test"),
				null, new BigDecimal(123),
				null));
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2, "test"),
				null, null,
				new BigDecimal(1234)));
		manager.checkEcritureComptableUnit(vEcritureComptable);
	}

	/**
	 * Test de void checkEcritureComptable(EcritureComptable pEcritureComptable) avec vialation de la RG3
	 * 
	 * @throws Exception
	 */
	@Test(expected = FunctionalException.class)
	public void checkEcritureComptableUnitRG3() throws Exception {
		EcritureComptable vEcritureComptable;
		vEcritureComptable = new EcritureComptable();
		vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
		vEcritureComptable.setDate(new Date());
		vEcritureComptable.setLibelle("Libelle");
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "test"),
				null, new BigDecimal(123),
				null));
		vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "test"),
				null, new BigDecimal(123),
				null));
		manager.checkEcritureComptableUnit(vEcritureComptable);
	}

	/**
	 * Test de void checkEcritureComptable(EcritureComptable pEcritureComptable) avec vialation de la RG5
	 * 
	 * @throws Exception
	 */
	@Test(expected = FunctionalException.class)
	public void checkEcritureComptableUnitRG5() throws Exception {
		EcritureComptable ecritureComptable = new EcritureComptable();
		ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
		ecritureComptable.setDate(new Date());
		ecritureComptable.setLibelle("Libelle");
		ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "test"),
				null, new BigDecimal(123),
				null));
		ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2, "test"),
				null, null,
				new BigDecimal(123)));
		ecritureComptable.setReference("AC-2017/00001");

		manager.checkEcritureComptableUnit(ecritureComptable);
	}

	/**
	 * Test de void checkEcritureComptable(EcritureComptable pEcritureComptable) avec vialation de la RG7
	 * 
	 * @throws Exception
	 */
	@Test(expected = FunctionalException.class)
	public void checkEcritureComptableUnitRG7() throws Exception {
		EcritureComptable ecritureComptable = new EcritureComptable();
		ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
		ecritureComptable.setDate(new Date());
		ecritureComptable.setLibelle("Libelle");
		ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "test"),
				null, new BigDecimal(123.456),
				null));
		ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2, "test"),
				null, null,
				new BigDecimal(123.456)));

		manager.checkEcritureComptableUnit(ecritureComptable);
	}

	/**
	 * Test valide de void checkEcritureComptableContext(EcritureComptable pEcritureComptable)
	 * 
	 * @throws FunctionalException
	 */
	@Test
	public void checkEcritureComptableContext() throws FunctionalException {
		EcritureComptable ecritureComptable = new EcritureComptable();
		ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
		ecritureComptable.setDate(new Date());
		ecritureComptable.setLibelle("Libelle");
		ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "test"),
				null, new BigDecimal(123),
				null));
		ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2, "test"),
				null, null,
				new BigDecimal(123)));

		manager.checkEcritureComptableContext(ecritureComptable);
	}

	/**
	 * Test de void checkEcritureComptableContext(EcritureComptable pEcritureComptable) avec vialation de la RG6
	 * 
	 * @throws NotFoundException
	 * @throws Exception
	 */
	@Test(expected = FunctionalException.class)
	public void checkEcritureComptableContextRG6() throws FunctionalException, NotFoundException {
		EcritureComptable ecritureComptable1 = new EcritureComptable();
		ecritureComptable1.setJournal(new JournalComptable("AC", "Achat"));
		ecritureComptable1.setDate(new Date());
		ecritureComptable1.setLibelle("Libelle");
		ecritureComptable1.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "test"),
				null, new BigDecimal(123),
				null));
		ecritureComptable1.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2, "test"),
				null, null,
				new BigDecimal(123)));
		ecritureComptable1.setReference("AC-2018/00001");

		EcritureComptable ecritureComptable2 = new EcritureComptable();
		ecritureComptable2.setJournal(new JournalComptable("VE", "Vente"));
		ecritureComptable2.setDate(new Date());
		ecritureComptable2.setLibelle("Libelle numero 2");
		ecritureComptable2.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1, "test"),
				null, new BigDecimal(1234),
				null));
		ecritureComptable2.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2, "test"),
				null, null,
				new BigDecimal(1234)));
		ecritureComptable2.setReference("AC-2018/00001");

		Mockito.when(comptabiliteDao.getEcritureComptableByRef("AC-2018/00001")).thenReturn(ecritureComptable1);

		manager.checkEcritureComptableContext(ecritureComptable2);

	}

}
