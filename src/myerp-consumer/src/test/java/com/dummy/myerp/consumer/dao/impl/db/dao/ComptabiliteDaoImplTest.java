package com.dummy.myerp.consumer.dao.impl.db.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.dummy.myerp.consumer.ConsumerHelper;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.consumer.db.AbstractDbConsumer;
import com.dummy.myerp.consumer.db.DataSourcesEnum;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.technical.exception.NotFoundException;
import com.dummy.myerp.technical.exception.TechnicalException;

/**
 * Class de test de ComptabiliteDaoImpl
 * 
 * @author Oltenos
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ComptabiliteDaoImplTest {

	private static DataSource dataSource;

	private static PlatformTransactionManager platformTransactionManager;

	private static String[] listNomTable;

	@Mock
	private static DaoProxy daoProxy;

	private static ComptabiliteDao comptabiliteDao;

	private List<CompteComptable> listCompteComptableAttendu;
	private List<JournalComptable> listJournalComptableAttendu;
	private List<EcritureComptable> listEcritureComptableAttendu; // Les list des ligne d'écriture comptable sont vide.
	private List<LigneEcritureComptable> listLigneEcritureAttendu;

	/**
	 * Initialisation du context : config de la dataSource de le BD de test
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Récupération de la dataSource de le BD de test
		dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
		platformTransactionManager = new DataSourceTransactionManager(dataSource);

		// Création Map avec les clés de l'Enum et dataSource
		Map<DataSourcesEnum, DataSource> vMapDataSource = new HashMap<DataSourcesEnum, DataSource>();
		for (DataSourcesEnum dataSourcesEnum : DataSourcesEnum.values()) {
			vMapDataSource.put(dataSourcesEnum, dataSource);
		}
		// configuration de AbstractDbConsumer
		AbstractDbConsumer.configure(vMapDataSource);

		listNomTable = new String[5];
		listNomTable[0] = "myerp.ligne_ecriture_comptable";
		listNomTable[1] = "myerp.ecriture_comptable";
		listNomTable[2] = "myerp.sequence_ecriture_comptable";
		listNomTable[3] = "myerp.journal_comptable";
		listNomTable[4] = "myerp.compte_comptable";

		// Initialisation de la base de données H2
		initBD();
		remplirBD();

		// Création de l'instance de ComptabiliteDao avec les requetes SQL issue du fichier xml pour les tests
		@SuppressWarnings("resource")
		ApplicationContext vApplicationContext = new ClassPathXmlApplicationContext("classpath:com/dummy/myerp/consumer/sqlContext.xml");

		comptabiliteDao = vApplicationContext.getBean("ComptabiliteDaoImpl", ComptabiliteDao.class);
	}

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
	 * Réinitialisation de la base de données aprés chaque utilisation
	 * 
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		resetBD();
	}

	/**
	 * Permet la réinitialisation des données entre chaque test
	 * 
	 * @throws TechnicalException
	 */
	private static void resetBD() throws TechnicalException {
		// vide les tables
		for (int i = 0; i < listNomTable.length; i++) {
			TransactionStatus vTransactionStatus = platformTransactionManager.getTransaction(new DefaultTransactionDefinition());

			String vSQL = "DELETE FROM " + listNomTable[i];

			MapSqlParameterSource vParams = new MapSqlParameterSource();

			NamedParameterJdbcTemplate vJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

			vJdbcTemplate.update(vSQL, vParams);

			TransactionStatus vTScommit = vTransactionStatus;
			vTransactionStatus = null;
			platformTransactionManager.commit(vTScommit);

		}

		remplirBD();

	}

	/**
	 * Permet la création de la base de données
	 * 
	 * @throws TechnicalException
	 */
	private static void initBD() throws TechnicalException {

		// Création des données
		TransactionStatus vTransactionStatus = platformTransactionManager.getTransaction(new DefaultTransactionDefinition());

		Resource resource = new ClassPathResource("creerBD.sql");
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
		databasePopulator.execute(dataSource);

		TransactionStatus vTScommit = vTransactionStatus;
		vTransactionStatus = null;
		platformTransactionManager.commit(vTScommit);

	}

	/**
	 * Permet la création des données
	 * 
	 * @throws TechnicalException
	 */
	private static void remplirBD() throws TechnicalException {

		// Création des données
		TransactionStatus vTransactionStatus = platformTransactionManager.getTransaction(new DefaultTransactionDefinition());

		Resource resource = new ClassPathResource("remplirBD.sql");
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
		databasePopulator.execute(dataSource);

		TransactionStatus vTScommit = vTransactionStatus;
		vTransactionStatus = null;
		platformTransactionManager.commit(vTScommit);

	}

	/**
	 * Création de la list des CompteComptable attendu (même vealeurs que la base de données)
	 */
	private void creerListCompteComptableAttendu() {
		listCompteComptableAttendu = new ArrayList<CompteComptable>();
		listCompteComptableAttendu.add(new CompteComptable(401, "Fournisseurs"));
		listCompteComptableAttendu.add(new CompteComptable(411, "Clients"));
		listCompteComptableAttendu.add(new CompteComptable(4456, "Taxes sur le chiffre d'affaires déductibles"));
		listCompteComptableAttendu.add(new CompteComptable(4457, "Taxes sur le chiffre d'affaires collectées par l'entreprise"));
		listCompteComptableAttendu.add(new CompteComptable(512, "Banque"));
		listCompteComptableAttendu.add(new CompteComptable(606, "Achats non stockés de matières et fournitures"));
		listCompteComptableAttendu.add(new CompteComptable(706, "Prestations de services"));
	}

	/**
	 * Création de la list des JournalComptable attendu (même vealeurs que la base de données)
	 */
	private void creerListJournalComptableAttendu() {

		listJournalComptableAttendu = new ArrayList<JournalComptable>();
		listJournalComptableAttendu.add(new JournalComptable("AC", "Achat"));
		listJournalComptableAttendu.add(new JournalComptable("VE", "Vente"));
		listJournalComptableAttendu.add(new JournalComptable("BQ", "Banque"));
		listJournalComptableAttendu.add(new JournalComptable("OD", "Opérations Diverses"));

	}

	/**
	 * Création de la list des EcritureComptable attendu (même vealeurs que la base de données)
	 */
	private void creerListEcritureComptableAttendu() {
		ComptabiliteDao vComptabiliteDaoImpl = ComptabiliteDaoImpl.getInstance();
		List<JournalComptable> vListJounalComptable = vComptabiliteDaoImpl.getListJournalComptable();

		listEcritureComptableAttendu = new ArrayList<EcritureComptable>();

		// Id = -1
		EcritureComptable vEcritureComptable = new EcritureComptable();
		vEcritureComptable.setId(-1);
		for (JournalComptable journalComptable : vListJounalComptable) {
			if (journalComptable.getCode().equals("AC"))
				vEcritureComptable.setJournal(journalComptable);
		}
		vEcritureComptable.setReference("AC-2016/00001");

		Calendar vCalendar = Calendar.getInstance();
		vCalendar.set(2016, 12 - 1, 31);
		vEcritureComptable.setDate(vCalendar.getTime());

		vEcritureComptable.setLibelle("Cartouches d’imprimante");

		listEcritureComptableAttendu.add(vEcritureComptable);

		// Id = -2
		EcritureComptable vEcritureComptable2 = new EcritureComptable();
		vEcritureComptable2.setId(-2);
		for (JournalComptable journalComptable : vListJounalComptable) {
			if (journalComptable.getCode().equals("VE"))
				vEcritureComptable2.setJournal(journalComptable);
		}
		vEcritureComptable2.setReference("VE-2016/00002");

		Calendar vCalendar2 = Calendar.getInstance();
		vCalendar2.set(2016, 12 - 1, 30);
		vEcritureComptable2.setDate(vCalendar2.getTime());

		vEcritureComptable2.setLibelle("TMA Appli Xxx");

		listEcritureComptableAttendu.add(vEcritureComptable2);

		// Id = -3
		EcritureComptable vEcritureComptable3 = new EcritureComptable();
		vEcritureComptable3.setId(-3);
		for (JournalComptable journalComptable : vListJounalComptable) {
			if (journalComptable.getCode().equals("BQ"))
				vEcritureComptable3.setJournal(journalComptable);
		}
		vEcritureComptable3.setReference("BQ-2016/00003");

		Calendar vCalendar3 = Calendar.getInstance();
		vCalendar3.set(2016, 12 - 1, 29);
		vEcritureComptable3.setDate(vCalendar3.getTime());

		vEcritureComptable3.setLibelle("Paiement Facture F110001");

		listEcritureComptableAttendu.add(vEcritureComptable3);

		// Id = -4
		EcritureComptable vEcritureComptable4 = new EcritureComptable();
		vEcritureComptable4.setId(-4);
		for (JournalComptable journalComptable : vListJounalComptable) {
			if (journalComptable.getCode().equals("VE"))
				vEcritureComptable4.setJournal(journalComptable);
		}
		vEcritureComptable4.setReference("VE-2016/00004");

		Calendar vCalendar4 = Calendar.getInstance();
		vCalendar4.set(2016, 12 - 1, 28);
		vEcritureComptable4.setDate(vCalendar4.getTime());

		vEcritureComptable4.setLibelle("TMA Appli Yyy");

		listEcritureComptableAttendu.add(vEcritureComptable4);

		// Id = -5
		EcritureComptable vEcritureComptable5 = new EcritureComptable();
		vEcritureComptable5.setId(-5);
		for (JournalComptable journalComptable : vListJounalComptable) {
			if (journalComptable.getCode().equals("BQ"))
				vEcritureComptable5.setJournal(journalComptable);
		}
		vEcritureComptable5.setReference("BQ-2016/00005");

		Calendar vCalendar5 = Calendar.getInstance();
		vCalendar5.set(2016, 12 - 1, 27);
		vEcritureComptable5.setDate(vCalendar5.getTime());

		vEcritureComptable5.setLibelle("Paiement Facture C110002");

		listEcritureComptableAttendu.add(vEcritureComptable5);

	}

	/**
	 * Création de la list des LigneEcriture attendu (même vealeurs que la base de données) UNIQUEMENT POUR L'EcritureComptable d'id -1!!
	 */
	private void creerListLigneEcritureAttendu() {
		ComptabiliteDao vComptabiliteDaoImpl = ComptabiliteDaoImpl.getInstance();

		List<CompteComptable> vListCompteComptable = vComptabiliteDaoImpl.getListCompteComptable();

		// Création des LigneEcritureComptable attendues
		LigneEcritureComptable ligne1 = new LigneEcritureComptable();
		for (CompteComptable compteComptable : vListCompteComptable) {
			if (compteComptable.getNumero() == 606)
				ligne1.setCompteComptable(compteComptable);
		}
		ligne1.setLibelle("Cartouches d’imprimante");
		ligne1.setDebit(new BigDecimal("43.95"));
		ligne1.setCredit(null);

		LigneEcritureComptable ligne2 = new LigneEcritureComptable();
		for (CompteComptable compteComptable : vListCompteComptable) {
			if (compteComptable.getNumero() == 4456)
				ligne2.setCompteComptable(compteComptable);
		}
		ligne2.setLibelle("TVA 20%");
		ligne2.setDebit(new BigDecimal("8.79"));
		ligne2.setCredit(null);

		LigneEcritureComptable ligne3 = new LigneEcritureComptable();
		for (CompteComptable compteComptable : vListCompteComptable) {
			if (compteComptable.getNumero() == 401)
				ligne3.setCompteComptable(compteComptable);
		}
		ligne3.setLibelle("Facture F110001");
		ligne3.setDebit(null);
		ligne3.setCredit(new BigDecimal("52.74"));

		listLigneEcritureAttendu = new ArrayList<LigneEcritureComptable>();
		listLigneEcritureAttendu.add(ligne1);
		listLigneEcritureAttendu.add(ligne2);
		listLigneEcritureAttendu.add(ligne3);
	}

	/**
	 * test de static ComptabiliteDaoImpl getInstance()
	 */
	@Test
	public void getInstance() {
		Assert.assertTrue("Test ComptabiliteDaoImpl.getInstance() : L'instance retourné n'est pas la même", ComptabiliteDaoImpl.getInstance() == comptabiliteDao);
	}

	/**
	 * test de List<CompteComptable> getListCompteComptable()
	 * 
	 * @throws SQLException
	 */
	@Test
	public void getListCompteComptable() throws SQLException {
		creerListCompteComptableAttendu();

		ComptabiliteDao vComptabiliteDaoImpl = ComptabiliteDaoImpl.getInstance();

		List<CompteComptable> vListCompteComptable = vComptabiliteDaoImpl.getListCompteComptable();

		// 1. vérification qu'il y a bien le bon nombre d'élément :
		Assert.assertTrue("Test ComptabiliteDaoImpl.getListCompteComptable() : erreur nb de résultat", vListCompteComptable.size() == listCompteComptableAttendu.size());

		// 2. Vérification que tous les éléments attendus sont bien retournés :
		for (CompteComptable compteComptableAttendu : listCompteComptableAttendu) {
			boolean isContain = false;
			for (CompteComptable compteComptable : vListCompteComptable) {
				if (compteComptableAttendu.getNumero().intValue() == compteComptable.getNumero().intValue() && compteComptableAttendu.getLibelle().equals(compteComptable.getLibelle()))
					isContain = true;
			}
			Assert.assertTrue("Test ComptabiliteDaoImpl.getListCompteComptable(), le CompteComptable suivant n'a pas été trouvé : numero :  " + compteComptableAttendu.getNumero()
					+ "\nLa liste retourné est : " + vListCompteComptable, isContain);
		}

	}

	/**
	 * test de List<JournalComptable> getListJournalComptable()
	 * 
	 * @throws SQLException
	 */
	@Test
	public void getListJournalComptable() throws SQLException {
		creerListJournalComptableAttendu();

		ComptabiliteDao vComptabiliteDaoImpl = ComptabiliteDaoImpl.getInstance();

		List<JournalComptable> vListJournalComptable = vComptabiliteDaoImpl.getListJournalComptable();

		// 1. vérification qu'il y a bien le bon nombre d'élément :
		Assert.assertTrue("Test ComptabiliteDaoImpl.getListJournalComptable() : erreur nb de résultat", vListJournalComptable.size() == listJournalComptableAttendu.size());

		// 2. Vérification que tous les éléments attendus sont bien retournés :
		for (JournalComptable vJournalComptableAttendu : listJournalComptableAttendu) {
			boolean isContain = false;
			for (JournalComptable vJournalComptable : vListJournalComptable) {
				if (vJournalComptableAttendu.getCode().equals(vJournalComptable.getCode()) && vJournalComptableAttendu.getLibelle().equals(vJournalComptable.getLibelle())) {
					isContain = true;
				}
			}

			Assert.assertTrue("Test ComptabiliteDaoImpl.getListJournalComptable(), le journalComptalbe suivant erreur Journal n'a pas été trouvé : code : " + vJournalComptableAttendu.getCode()
					+ "\nLa liste retourné est : " + vListJournalComptable, isContain);
		}

	}

	/**
	 * test de List<EcritureComptable> getListEcritureComptable()
	 * 
	 * @throws SQLException
	 */
	@Test
	public void getListEcritureComptable() throws SQLException {
		creerListEcritureComptableAttendu();

		ComptabiliteDao vComptabiliteDaoImpl = ComptabiliteDaoImpl.getInstance();

		List<EcritureComptable> vListEcritureComptable = vComptabiliteDaoImpl.getListEcritureComptable();

		// 1. vérification qu'il y a bien le bon nombre d'élément :
		Assert.assertTrue("Test ComptabiliteDaoImpl.getListEcritureComptable() : erreur nb de résultat", vListEcritureComptable.size() == 5);

		// 2. Vérification que tous les éléments attendus sont bien retournés :
		for (EcritureComptable ecritureComptableAttendu : listEcritureComptableAttendu) {
			boolean isContain = false;
			for (EcritureComptable ecritureComptable : vListEcritureComptable) {
				boolean vTest = ecritureComptable.getId() == ecritureComptableAttendu.getId();
				vTest = ecritureComptableAttendu.getJournal().getCode().equals(ecritureComptable.getJournal().getCode());
				vTest = vTest && ecritureComptableAttendu.getJournal().getLibelle().equals(ecritureComptable.getJournal().getLibelle());

				vTest = vTest && ecritureComptableAttendu.getLibelle().equals(ecritureComptable.getLibelle());
				vTest = vTest && ecritureComptableAttendu.getReference().equals(ecritureComptable.getReference());

				Calendar vTestCalendarAttendu = Calendar.getInstance();
				vTestCalendarAttendu.setTime(ecritureComptableAttendu.getDate());
				Calendar vTestCalendar = Calendar.getInstance();
				vTestCalendar.setTime(ecritureComptable.getDate());
				vTest = vTest && vTestCalendarAttendu.get(Calendar.YEAR) == vTestCalendar.get(Calendar.YEAR);
				vTest = vTest && vTestCalendarAttendu.get(Calendar.MONTH) == vTestCalendar.get(Calendar.MONTH);
				vTest = vTest && vTestCalendarAttendu.get(Calendar.DAY_OF_MONTH) == vTestCalendar.get(Calendar.DAY_OF_MONTH);
				if (vTest)
					isContain = true;
			}
			Assert.assertTrue("Test ComptabiliteDaoImpl.getListEcritureComptable() : erreur EcritureComptable pour l'id : " + ecritureComptableAttendu.getId(), isContain);
		}
	}

	/**
	 * test de EcritureComptable getEcritureComptable(Integer pId)
	 * 
	 * @throws SQLException
	 */
	@Test
	public void getEcritureComptable() throws SQLException {
		creerListEcritureComptableAttendu();

		ComptabiliteDao vComptabiliteDaoImpl = ComptabiliteDaoImpl.getInstance();

		// Utilisation de la méthode à tester
		EcritureComptable vEcritureComptable = null;
		EcritureComptable vEcritureComptableAttendu = listEcritureComptableAttendu.get(0);
		try {
			vEcritureComptable = vComptabiliteDaoImpl.getEcritureComptable(vEcritureComptableAttendu.getId());
		} catch (NotFoundException e) {
			Assert.fail("Test ComptabiliteDaoImpl.getEcritureComptable(Integer pId) : Element non trouvé");
		}

		// Test
		boolean vTest = vEcritureComptableAttendu.getJournal().getCode().equals(vEcritureComptable.getJournal().getCode());

		vTest = vTest && vEcritureComptableAttendu.getJournal().getLibelle().equals(vEcritureComptable.getJournal().getLibelle());

		vTest = vTest && vEcritureComptableAttendu.getLibelle().equals(vEcritureComptable.getLibelle());
		vTest = vTest && vEcritureComptableAttendu.getReference().equals(vEcritureComptable.getReference());

		Calendar vTestCalendarAttendu = Calendar.getInstance();
		vTestCalendarAttendu.setTime(vEcritureComptableAttendu.getDate());
		Calendar vTestCalendar = Calendar.getInstance();
		vTestCalendar.setTime(vEcritureComptable.getDate());
		vTest = vTest && vTestCalendarAttendu.get(Calendar.YEAR) == vTestCalendar.get(Calendar.YEAR);
		vTest = vTest && vTestCalendarAttendu.get(Calendar.MONTH) == vTestCalendar.get(Calendar.MONTH);
		vTest = vTest && vTestCalendarAttendu.get(Calendar.DAY_OF_MONTH) == vTestCalendar.get(Calendar.DAY_OF_MONTH);

		Assert.assertTrue("Test ComptabiliteDaoImpl.getEcritureComptable(Integer pId) : L'objet retourné ne correpond pas", vTest);
	}

	/**
	 * test de EcritureComptable getEcritureComptableByRef(String pReference)
	 * 
	 * @throws SQLException
	 */
	@Test
	public void getEcritureComptableByRef() throws SQLException {
		creerListEcritureComptableAttendu();

		ComptabiliteDao vComptabiliteDaoImpl = ComptabiliteDaoImpl.getInstance();

		EcritureComptable vEcritureComptableAttendu = listEcritureComptableAttendu.get(0);

		// Utilisation de la méthode à tester
		EcritureComptable vEcritureComptable = null;
		try {
			vEcritureComptable = vComptabiliteDaoImpl.getEcritureComptableByRef(vEcritureComptableAttendu.getReference());
		} catch (NotFoundException e) {
			Assert.fail("Test ComptabiliteDaoImpl.getEcritureComptableByRef(String pReference) : Element non trouvé");
		}

		// Test
		boolean vTest = vEcritureComptableAttendu.getJournal().getCode().equals(vEcritureComptable.getJournal().getCode());

		vTest = vTest && vEcritureComptableAttendu.getJournal().getLibelle().equals(vEcritureComptable.getJournal().getLibelle());

		vTest = vTest && vEcritureComptableAttendu.getLibelle().equals(vEcritureComptable.getLibelle());
		vTest = vTest && vEcritureComptableAttendu.getReference().equals(vEcritureComptable.getReference());

		Calendar vTestCalendarAttendu = Calendar.getInstance();
		vTestCalendarAttendu.setTime(vEcritureComptableAttendu.getDate());
		Calendar vTestCalendar = Calendar.getInstance();
		vTestCalendar.setTime(vEcritureComptable.getDate());
		vTest = vTest && vTestCalendarAttendu.get(Calendar.YEAR) == vTestCalendar.get(Calendar.YEAR);
		vTest = vTest && vTestCalendarAttendu.get(Calendar.MONTH) == vTestCalendar.get(Calendar.MONTH);
		vTest = vTest && vTestCalendarAttendu.get(Calendar.DAY_OF_MONTH) == vTestCalendar.get(Calendar.DAY_OF_MONTH);

		Assert.assertTrue("Test ComptabiliteDaoImpl.getEcritureComptableByRef(String pReference) : L'objet retourné ne correpond pas", vTest);
	}

	/**
	 * test de void loadListLigneEcriture(EcritureComptable pEcritureComptable)
	 * 
	 * @throws SQLException
	 * @throws NotFoundException
	 */
	@Test
	public void loadListLigneEcriture() throws SQLException {
		ComptabiliteDao vComptabiliteDaoImpl = ComptabiliteDaoImpl.getInstance();

		creerListLigneEcritureAttendu();

		// Utilisation de la méthode à tester
		EcritureComptable vEcritureComptable = null;
		try {
			vEcritureComptable = vComptabiliteDaoImpl.getEcritureComptable(-1);
		} catch (NotFoundException e) {
			Assert.fail("Test ComptabiliteDaoImpl.loadListLigneEcriture(EcritureComptable pEcritureComptable) : l'id -1 n'existe pas dans la base de données");
		}
		vComptabiliteDaoImpl.loadListLigneEcriture(vEcritureComptable);
		List<LigneEcritureComptable> vListLigneEcriture = vEcritureComptable.getListLigneEcriture();

		// 1. vérification qu'il y a bien le bon nombre d'élément :
		Assert.assertTrue("Test ComptabiliteDaoImpl.loadListLigneEcriture(EcritureComptable pEcritureComptable) : erreur nb de résultat",
				vListLigneEcriture.size() == listLigneEcritureAttendu.size());

		// 2. Vérification que tous les éléments attendus sont bien retournés :
		for (LigneEcritureComptable ligneEcritureComptableAttendu : listLigneEcritureAttendu) {
			boolean isContain = false;
			for (LigneEcritureComptable ligneEcritureComptable : vListLigneEcriture) {
				boolean test;

				if (ligneEcritureComptableAttendu.getCredit() == null) {
					test = ligneEcritureComptable.getCredit() == null;
				} else {
					if (ligneEcritureComptable.getCredit() == null)
						test = false;
					else
						test = ligneEcritureComptableAttendu.getCredit().compareTo(ligneEcritureComptable.getCredit()) == 0;
				}

				if (ligneEcritureComptableAttendu.getDebit() == null) {
					test = test && ligneEcritureComptable.getDebit() == null;
				} else {
					if (ligneEcritureComptable.getDebit() == null)
						test = false;
					else
						test = test && ligneEcritureComptableAttendu.getDebit().compareTo(ligneEcritureComptable.getDebit()) == 0;
				}

				test = test && ligneEcritureComptableAttendu.getLibelle().equals(ligneEcritureComptable.getLibelle())
						&& ligneEcritureComptableAttendu.getCompteComptable().getNumero().intValue() == ligneEcritureComptable.getCompteComptable().getNumero().intValue()
						&& ligneEcritureComptableAttendu.getCompteComptable().getLibelle().equals(ligneEcritureComptable.getCompteComptable().getLibelle());
				if (test)
					isContain = true;
			}
			Assert.assertTrue("Test ComptabiliteDaoImpl.loadListLigneEcriture(EcritureComptable pEcritureComptable) : erreur dans les éléments retournés", isContain);
		}

	}

	/**
	 * test de void insertEcritureComptable(EcritureComptable pEcritureComptable)
	 * 
	 * @throws SQLException
	 */
	@Test
	public void insertEcritureComptable() throws SQLException {
		ComptabiliteDao vComptabiliteDaoImpl = ComptabiliteDaoImpl.getInstance();

		EcritureComptable vEcritureComptableAttendu = new EcritureComptable();

		JournalComptable vJournalComptable = vComptabiliteDaoImpl.getListJournalComptable().get(0);
		vEcritureComptableAttendu.setJournal(vJournalComptable);

		vEcritureComptableAttendu.setLibelle("Libelle");
		vEcritureComptableAttendu.setReference("BQ-2018/00001");

		Calendar vCalendar = Calendar.getInstance();
		vCalendar.set(2018, 8 - 1, 14);
		vEcritureComptableAttendu.setDate(vCalendar.getTime());

		// Création des LigneEcritureComptable
		List<CompteComptable> vListCompteComptable = vComptabiliteDaoImpl.getListCompteComptable();
		LigneEcritureComptable ligne1 = new LigneEcritureComptable();
		for (CompteComptable compteComptable : vListCompteComptable) {
			if (compteComptable.getNumero() == 706)
				ligne1.setCompteComptable(compteComptable);
		}
		ligne1.setLibelle("ligne de test num 1");
		ligne1.setDebit(new BigDecimal("1869.25"));
		ligne1.setCredit(null);

		LigneEcritureComptable ligne2 = new LigneEcritureComptable();
		for (CompteComptable compteComptable : vListCompteComptable) {
			if (compteComptable.getNumero() == 401)
				ligne2.setCompteComptable(compteComptable);
		}
		ligne2.setLibelle("ligne de test num 2");
		ligne2.setDebit(new BigDecimal("5468.54"));
		ligne2.setCredit(null);

		LigneEcritureComptable ligne3 = new LigneEcritureComptable();
		for (CompteComptable compteComptable : vListCompteComptable) {
			if (compteComptable.getNumero() == 512)
				ligne3.setCompteComptable(compteComptable);
		}
		ligne3.setLibelle("ligne de test num 3");
		ligne3.setDebit(null);
		ligne3.setCredit(new BigDecimal("5742.26"));

		ArrayList<LigneEcritureComptable> vListLigneEcritureAttendu = new ArrayList<LigneEcritureComptable>();
		vListLigneEcritureAttendu.add(ligne1);
		vListLigneEcritureAttendu.add(ligne2);
		vListLigneEcritureAttendu.add(ligne3);

		vEcritureComptableAttendu.getListLigneEcriture().addAll(vListLigneEcritureAttendu);

		// Appel de la méthode à tester
		vComptabiliteDaoImpl.insertEcritureComptable(vEcritureComptableAttendu);

		// Vérification de l'ajout de vEcritureComptable
		EcritureComptable vEcritureComptable = null;
		try {
			vEcritureComptable = vComptabiliteDaoImpl.getEcritureComptableByRef("BQ-2018/00001");
		} catch (NotFoundException e) {
			Assert.fail("Test ComptabiliteDaoImpl.insertEcritureComptable(EcritureComptable pEcritureComptable) : l'élément ajouté n'a pas été retrouvé dans la BD");
		}

		if (vEcritureComptable == null) {
			Assert.fail("Test ComptabiliteDaoImpl.insertEcritureComptable(EcritureComptable pEcritureComptable) : retourne null");
		}
		boolean test = vEcritureComptableAttendu.getId() == vEcritureComptable.getId()
				&& vEcritureComptableAttendu.getLibelle().equals(vEcritureComptable.getLibelle())
				&& vEcritureComptableAttendu.getReference().equals(vEcritureComptable.getReference())
				&& vEcritureComptableAttendu.getJournal().getCode().equals(vEcritureComptable.getJournal().getCode())
				&& vEcritureComptableAttendu.getJournal().getLibelle().equals(vEcritureComptable.getJournal().getLibelle());

		Calendar vTestCalendarAttendu = Calendar.getInstance();
		vTestCalendarAttendu.setTime(vEcritureComptableAttendu.getDate());
		Calendar vTestCalendar = Calendar.getInstance();
		vTestCalendar.setTime(vEcritureComptable.getDate());
		test = test && vTestCalendarAttendu.get(Calendar.YEAR) == vTestCalendar.get(Calendar.YEAR);
		test = test && vTestCalendarAttendu.get(Calendar.MONTH) == vTestCalendar.get(Calendar.MONTH);
		test = test && vTestCalendarAttendu.get(Calendar.DAY_OF_MONTH) == vTestCalendar.get(Calendar.DAY_OF_MONTH);

		Assert.assertTrue("Test ComptabiliteDaoImpl.insertEcritureComptable(EcritureComptable pEcritureComptable) : l'objet retourné est différent", test);

		// vérification de la list de LigneEcriture
		vComptabiliteDaoImpl.loadListLigneEcriture(vEcritureComptable);
		List<LigneEcritureComptable> vListLigneEcriture = vEcritureComptable.getListLigneEcriture();

		Assert.assertTrue("Test ComptabiliteDaoImpl.insertEcritureComptable(EcritureComptable pEcritureComptable) : la list de Ligne Ecriture est incorrecte",
				vListLigneEcriture.size() == vListLigneEcritureAttendu.size());

		for (LigneEcritureComptable ligneEcritureComptableAttendu : vListLigneEcritureAttendu) {
			boolean isContain = false;
			for (LigneEcritureComptable ligneEcritureComptable : vListLigneEcriture) {
				boolean test2;

				if (ligneEcritureComptableAttendu.getCredit() == null) {
					test2 = ligneEcritureComptable.getCredit() == null;
				} else {
					if (ligneEcritureComptable.getCredit() == null)
						test2 = false;
					else
						test2 = ligneEcritureComptableAttendu.getCredit().compareTo(ligneEcritureComptable.getCredit()) == 0;
				}

				if (ligneEcritureComptableAttendu.getDebit() == null) {
					test2 = test2 && ligneEcritureComptable.getDebit() == null;
				} else {
					if (ligneEcritureComptable.getDebit() == null)
						test2 = false;
					else
						test2 = test2 && ligneEcritureComptableAttendu.getDebit().compareTo(ligneEcritureComptable.getDebit()) == 0;
				}

				test2 = test2 && ligneEcritureComptableAttendu.getLibelle().equals(ligneEcritureComptable.getLibelle())
						&& ligneEcritureComptableAttendu.getCompteComptable().getNumero().intValue() == ligneEcritureComptable.getCompteComptable().getNumero().intValue()
						&& ligneEcritureComptableAttendu.getCompteComptable().getLibelle().equals(ligneEcritureComptable.getCompteComptable().getLibelle());
				if (test2)
					isContain = true;
			}
			Assert.assertTrue("Test ComptabiliteDaoImpl.loadListLigneEcriture(EcritureComptable pEcritureComptable) : erreur dans les éléments retournés", isContain);
		}

	}

	/**
	 * test de void updateEcritureComptable(EcritureComptable pEcritureComptable)
	 * 
	 * @throws SQLException
	 * @throws NotFoundException
	 */
	@Test
	public void updateEcritureComptable() throws SQLException, NotFoundException {
		ComptabiliteDao vComptabiliteDaoImpl = ComptabiliteDaoImpl.getInstance();

		// Récupération d'une EcritureComptable
		EcritureComptable vEcritureComptableAttendu = null;

		vEcritureComptableAttendu = vComptabiliteDaoImpl.getEcritureComptable(-1);

		// Modification de l'EcritureComptable
		Calendar vCalendarAttendu = Calendar.getInstance();
		vCalendarAttendu.set(2018, 8 - 1, 15);
		vEcritureComptableAttendu.setDate(vCalendarAttendu.getTime());

		vEcritureComptableAttendu.setLibelle("Nouveau Libelle");
		vEcritureComptableAttendu.setReference("Nouvelle Ref");

		vEcritureComptableAttendu.setJournal(vComptabiliteDaoImpl.getListJournalComptable().get(2));

		vEcritureComptableAttendu.getListLigneEcriture().remove(0);
		vEcritureComptableAttendu.getListLigneEcriture().add(new LigneEcritureComptable(vComptabiliteDaoImpl.getListCompteComptable().get(1), "Libelle ligne", new BigDecimal("25.15"), new BigDecimal("15676.36")));

		// Appel de la méthode à tester
		vComptabiliteDaoImpl.updateEcritureComptable(vEcritureComptableAttendu);

		// test
		EcritureComptable vEcritureComptable = null;
		try {
			vEcritureComptable = vComptabiliteDaoImpl.getEcritureComptable(vEcritureComptableAttendu.getId());
		} catch (NotFoundException e) {
			Assert.fail("Test ComptabiliteDaoImpl.updateEcritureComptable(EcritureComptable pEcritureComptable) : non retrouvé dans la base de données aprés modification");
		}

		// Test
		boolean vTest = vEcritureComptableAttendu.getJournal().getCode().equals(vEcritureComptable.getJournal().getCode());

		vTest = vTest && vEcritureComptableAttendu.getJournal().getLibelle().equals(vEcritureComptable.getJournal().getLibelle());

		vTest = vTest && vEcritureComptableAttendu.getLibelle().equals(vEcritureComptable.getLibelle());
		vTest = vTest && vEcritureComptableAttendu.getReference().equals(vEcritureComptable.getReference());

		Calendar vTestCalendarAttendu = Calendar.getInstance();
		vTestCalendarAttendu.setTime(vEcritureComptableAttendu.getDate());
		Calendar vTestCalendar = Calendar.getInstance();
		vTestCalendar.setTime(vEcritureComptable.getDate());
		vTest = vTest && vTestCalendarAttendu.get(Calendar.YEAR) == vTestCalendar.get(Calendar.YEAR);
		vTest = vTest && vTestCalendarAttendu.get(Calendar.MONTH) == vTestCalendar.get(Calendar.MONTH);
		vTest = vTest && vTestCalendarAttendu.get(Calendar.DAY_OF_MONTH) == vTestCalendar.get(Calendar.DAY_OF_MONTH);

		Assert.assertTrue("Test ComptabiliteDaoImpl.getEcritureComptable(Integer pId) : L'objet retourné ne correpond pas", vTest);

		// Test de la list de LigneEcritureComptable
		List<LigneEcritureComptable> vListLigneEcriture = vEcritureComptable.getListLigneEcriture();
		List<LigneEcritureComptable> vListLigneEcritureAttendu = vEcritureComptableAttendu.getListLigneEcriture();

		// 1. vérification qu'il y a bien le bon nombre d'élément :
		Assert.assertTrue("Test ComptabiliteDaoImpl.loadListLigneEcriture(EcritureComptable pEcritureComptable) : erreur nb de résultat",
				vListLigneEcriture.size() == vListLigneEcritureAttendu.size());

		// 2. Vérification que tous les éléments attendus sont bien retournés :
		for (LigneEcritureComptable ligneEcritureComptableAttendu : vListLigneEcritureAttendu) {
			boolean isContain = false;
			for (LigneEcritureComptable ligneEcritureComptable : vListLigneEcriture) {
				boolean test;

				if (ligneEcritureComptableAttendu.getCredit() == null) {
					test = ligneEcritureComptable.getCredit() == null;
				} else {
					if (ligneEcritureComptable.getCredit() == null)
						test = false;
					else
						test = ligneEcritureComptableAttendu.getCredit().compareTo(ligneEcritureComptable.getCredit()) == 0;
				}

				if (ligneEcritureComptableAttendu.getDebit() == null) {
					test = test && ligneEcritureComptable.getDebit() == null;
				} else {
					if (ligneEcritureComptable.getDebit() == null)
						test = false;
					else
						test = test && ligneEcritureComptableAttendu.getDebit().compareTo(ligneEcritureComptable.getDebit()) == 0;
				}

				test = test && ligneEcritureComptableAttendu.getLibelle().equals(ligneEcritureComptable.getLibelle())
						&& ligneEcritureComptableAttendu.getCompteComptable().getNumero().intValue() == ligneEcritureComptable.getCompteComptable().getNumero().intValue()
						&& ligneEcritureComptableAttendu.getCompteComptable().getLibelle().equals(ligneEcritureComptable.getCompteComptable().getLibelle());
				if (test)
					isContain = true;
			}
			Assert.assertTrue("Test ComptabiliteDaoImpl.loadListLigneEcriture(EcritureComptable pEcritureComptable) : erreur dans les éléments retournés", isContain);
		}

	}

	/**
	 * test de void deleteEcritureComptable(Integer pId)
	 * 
	 * @throws SQLException
	 */
	@Test
	public void deleteEcritureComptable() throws SQLException {
		ComptabiliteDao vComptabiliteDaoImpl = ComptabiliteDaoImpl.getInstance();

		// Appel de la méthode à tester
		vComptabiliteDaoImpl.deleteEcritureComptable(-1);

		// Test
		boolean existe = true;
		try {
			vComptabiliteDaoImpl.getEcritureComptable(-1);
		} catch (NotFoundException e) {
			existe = false;
		}
		Assert.assertFalse("Test ComptabiliteDaoImpl.deleteEcritureComptable(Integer pId) : l'élément n'a pas été supprimé de la base de données", existe);

	}

}
