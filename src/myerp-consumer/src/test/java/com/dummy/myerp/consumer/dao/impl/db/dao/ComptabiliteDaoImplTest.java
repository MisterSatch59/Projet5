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
import com.dummy.myerp.consumer.ModelTestUtilities;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.consumer.db.AbstractDbConsumer;
import com.dummy.myerp.consumer.db.DataSourcesEnum;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
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

	private static List<CompteComptable> listCompteComptableAttendu;
	private static List<JournalComptable> listJournalComptableAttendu;
	private static EcritureComptable ecritureComptableAttendu; // Il s'agit de l'EcritureComptable d'Id -1 utilisé pour les tests
	private static List<LigneEcritureComptable> listLigneEcritureAttendu;

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

		creerListCompteComptableAttendu();
		creerListJournalComptableAttendu();
		creerListLigneEcritureAttendu();
		creerEcritureComptableAttendu();
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
	 * Création de la list des CompteComptable attendu (même valeurs que la base de données)
	 */
	private static void creerListCompteComptableAttendu() {
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
	 * Création de la list des JournalComptable attendu (même valeurs que la base de données)
	 */
	private static void creerListJournalComptableAttendu() {

		listJournalComptableAttendu = new ArrayList<JournalComptable>();
		listJournalComptableAttendu.add(new JournalComptable("AC", "Achat"));
		listJournalComptableAttendu.add(new JournalComptable("VE", "Vente"));
		listJournalComptableAttendu.add(new JournalComptable("BQ", "Banque"));
		listJournalComptableAttendu.add(new JournalComptable("OD", "Opérations Diverses"));

	}

	/**
	 * Création de l'EcritureComptable attendu (même vealeurs que la base de données) Il s'agit de l'EcritureComptable d'Id -1
	 */
	private static void creerEcritureComptableAttendu() {
		ComptabiliteDao vComptabiliteDaoImpl = ComptabiliteDaoImpl.getInstance();
		List<JournalComptable> vListJounalComptable = vComptabiliteDaoImpl.getListJournalComptable();

		ecritureComptableAttendu = new EcritureComptable();
		ecritureComptableAttendu.setId(-1);
		for (JournalComptable journalComptable : vListJounalComptable) {
			if (journalComptable.getCode().equals("AC"))
				ecritureComptableAttendu.setJournal(journalComptable);
		}
		ecritureComptableAttendu.setReference("AC-2016/00001");

		Calendar vCalendar = Calendar.getInstance();
		vCalendar.set(2016, 12 - 1, 31);
		ecritureComptableAttendu.setDate(vCalendar.getTime());

		ecritureComptableAttendu.setLibelle("Cartouches d’imprimante");

		ecritureComptableAttendu.getListLigneEcriture().addAll(listLigneEcritureAttendu);

	}

	/**
	 * Création de la list des LigneEcriture attendu (même vealeurs que la base de données) UNIQUEMENT POUR L'EcritureComptable d'id -1!!
	 */
	private static void creerListLigneEcritureAttendu() {
		ComptabiliteDao vComptabiliteDaoImpl = ComptabiliteDaoImpl.getInstance();

		List<CompteComptable> vListCompteComptable = vComptabiliteDaoImpl.getListCompteComptable();

		// Création des LigneEcritureComptable attendues

		CompteComptable vCompteComptable = null;
		for (CompteComptable compteComptable : vListCompteComptable) {
			if (compteComptable.getNumero() == 606)
				vCompteComptable = compteComptable;
		}
		String vLibelle = "Cartouches d’imprimante";
		BigDecimal vDebit = new BigDecimal("43.95");
		BigDecimal vCredit = null;
		LigneEcritureComptable ligne1 = new LigneEcritureComptable(vCompteComptable, vLibelle, vDebit, vCredit);

		for (CompteComptable compteComptable : vListCompteComptable) {
			if (compteComptable.getNumero() == 4456)
				vCompteComptable = compteComptable;
		}
		vLibelle = "TVA 20%";
		vDebit = new BigDecimal("8.79");
		vCredit = null;
		LigneEcritureComptable ligne2 = new LigneEcritureComptable(vCompteComptable, vLibelle, vDebit, vCredit);

		for (CompteComptable compteComptable : vListCompteComptable) {
			if (compteComptable.getNumero() == 401)
				vCompteComptable = compteComptable;
		}
		vLibelle = "Facture F110001";
		vDebit = null;
		vCredit = new BigDecimal("52.74");
		LigneEcritureComptable ligne3 = new LigneEcritureComptable(vCompteComptable, vLibelle, vDebit, vCredit);

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
		ComptabiliteDao vComptabiliteDaoImpl = ComptabiliteDaoImpl.getInstance();

		List<CompteComptable> vListCompteComptable = vComptabiliteDaoImpl.getListCompteComptable();

		// 1. vérification qu'il y a bien le bon nombre d'élément :
		Assert.assertTrue("Test ComptabiliteDaoImpl.getListCompteComptable() : erreur nb de résultat", vListCompteComptable.size() == listCompteComptableAttendu.size());

		// 2. Vérification que tous les éléments attendus sont bien retournés :
		for (CompteComptable compteComptableAttendu : listCompteComptableAttendu) {
			boolean isContain = false;
			for (CompteComptable compteComptable : vListCompteComptable) {
				if (ModelTestUtilities.isEqual(compteComptable, compteComptableAttendu))
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
		ComptabiliteDao vComptabiliteDaoImpl = ComptabiliteDaoImpl.getInstance();

		List<JournalComptable> vListJournalComptable = vComptabiliteDaoImpl.getListJournalComptable();

		// 1. vérification qu'il y a bien le bon nombre d'élément :
		Assert.assertTrue("Test ComptabiliteDaoImpl.getListJournalComptable() : erreur nb de résultat", vListJournalComptable.size() == listJournalComptableAttendu.size());

		// 2. Vérification que tous les éléments attendus sont bien retournés :
		for (JournalComptable vJournalComptableAttendu : listJournalComptableAttendu) {
			boolean isContain = false;
			for (JournalComptable vJournalComptable : vListJournalComptable) {
				if (ModelTestUtilities.isEqual(vJournalComptable, vJournalComptableAttendu)) {
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
		ComptabiliteDao vComptabiliteDaoImpl = ComptabiliteDaoImpl.getInstance();

		List<EcritureComptable> vListEcritureComptable = vComptabiliteDaoImpl.getListEcritureComptable();

		// 1. vérification qu'il y a bien le bon nombre d'élément :
		Assert.assertTrue("Test ComptabiliteDaoImpl.getListEcritureComptable() : erreur nb de résultat", vListEcritureComptable.size() == 5);

		// 2. Vérification que l'élément d'Id -1 est bien retourné :
		boolean isContain = false;
		for (EcritureComptable ecritureComptable : vListEcritureComptable) {
			if (ModelTestUtilities.isEqual(ecritureComptable, ecritureComptableAttendu))
				isContain = true;
		}
		Assert.assertTrue("Test ComptabiliteDaoImpl.getListEcritureComptable() : l'EcritureComptable d'Id -1 n'est pas retourné.", isContain);
	}

	/**
	 * test de EcritureComptable getEcritureComptable(Integer pId)
	 * 
	 * @throws SQLException
	 */
	@Test
	public void getEcritureComptable() throws SQLException {
		ComptabiliteDao vComptabiliteDaoImpl = ComptabiliteDaoImpl.getInstance();

		// Utilisation de la méthode à tester
		EcritureComptable vEcritureComptable = null;
		try {
			vEcritureComptable = vComptabiliteDaoImpl.getEcritureComptable(ecritureComptableAttendu.getId());
		} catch (NotFoundException e) {
			Assert.fail("Test ComptabiliteDaoImpl.getEcritureComptable(Integer pId) : Element non trouvé");
		}

		// Test
		Assert.assertTrue("Test ComptabiliteDaoImpl.getEcritureComptable(Integer pId) : L'objet retourné ne correpond pas", ModelTestUtilities.isEqual(ecritureComptableAttendu, vEcritureComptable));
	}

	/**
	 * test de EcritureComptable getEcritureComptableByRef(String pReference)
	 * 
	 * @throws SQLException
	 */
	@Test
	public void getEcritureComptableByRef() throws SQLException {
		ComptabiliteDao vComptabiliteDaoImpl = ComptabiliteDaoImpl.getInstance();

		// Utilisation de la méthode à tester
		EcritureComptable vEcritureComptable = null;
		try {
			vEcritureComptable = vComptabiliteDaoImpl.getEcritureComptableByRef(ecritureComptableAttendu.getReference());
		} catch (NotFoundException e) {
			Assert.fail("Test ComptabiliteDaoImpl.getEcritureComptableByRef(String pReference) : Element non trouvé");
		}

		// Test
		Assert.assertTrue("Test ComptabiliteDaoImpl.getEcritureComptableByRef(String pReference) : L'objet retourné ne correpond pas", ModelTestUtilities.isEqual(ecritureComptableAttendu, vEcritureComptable));
	}

	/**
	 * test de EcritureComptable getEcritureComptableByRef(String pReference) avec une mauvaise référence
	 * EmptyResultDataAccessException attendu
	 * 
	 * @throws SQLException
	 * @throws NotFoundException
	 */
	@Test(expected = NotFoundException.class)
	public void getEcritureComptableByRefMauvaiseRef() throws SQLException, NotFoundException {
		ComptabiliteDao vComptabiliteDaoImpl = ComptabiliteDaoImpl.getInstance();

		// Utilisation de la méthode à tester
		vComptabiliteDaoImpl.getEcritureComptableByRef("Mauvaise Ref");

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
				if (ModelTestUtilities.isEqual(ligneEcritureComptableAttendu, ligneEcritureComptable))
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

		// Créationde l'écritureComptable
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
		// Ligne1
		CompteComptable vCompteComptable = null;
		for (CompteComptable compteComptable : vListCompteComptable) {
			if (compteComptable.getNumero() == 706)
				vCompteComptable = compteComptable;
		}
		String vLibelle = "ligne de test num 1";
		BigDecimal vDebit = new BigDecimal("1869.25");
		BigDecimal vCredit = null;
		LigneEcritureComptable ligne1 = new LigneEcritureComptable(vCompteComptable, vLibelle, vDebit, vCredit);

		// Ligne2
		for (CompteComptable compteComptable : vListCompteComptable) {
			if (compteComptable.getNumero() == 401)
				vCompteComptable = compteComptable;
		}
		vLibelle = "ligne de test num 2";
		vDebit = new BigDecimal("5468.54");
		vCredit = null;
		LigneEcritureComptable ligne2 = new LigneEcritureComptable(vCompteComptable, vLibelle, vDebit, vCredit);

		// Ligne3
		for (CompteComptable compteComptable : vListCompteComptable) {
			if (compteComptable.getNumero() == 512)
				vCompteComptable = compteComptable;
		}
		vLibelle = "ligne de test num 3";
		vDebit = null;
		vCredit = new BigDecimal("5742.26");
		LigneEcritureComptable ligne3 = new LigneEcritureComptable(vCompteComptable, vLibelle, vDebit, vCredit);

		// Ajout des LigneEcritureComptable
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

		Assert.assertTrue("Test ComptabiliteDaoImpl.loadListLigneEcriture(EcritureComptable pEcritureComptable) : l'objet retourné est différent",
				ModelTestUtilities.isEqual(vEcritureComptable, vEcritureComptableAttendu));

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

		// Récupération de l'EcritureComptable après update
		EcritureComptable vEcritureComptable = null;
		try {
			vEcritureComptable = vComptabiliteDaoImpl.getEcritureComptable(vEcritureComptableAttendu.getId());
		} catch (NotFoundException e) {
			Assert.fail("Test ComptabiliteDaoImpl.updateEcritureComptable(EcritureComptable pEcritureComptable) : non retrouvé dans la base de données aprés modification");
		}

		// Test
		Assert.assertTrue("Test ComptabiliteDaoImpl.getEcritureComptable(Integer pId) : L'objet retourné ne correpond pas", ModelTestUtilities.isEqual(vEcritureComptable, vEcritureComptableAttendu));
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

	/**
	 * test de SequenceEcritureComptable getDerniereSequenceEcritureComptable(String pCodeJournal, int pAnnee)
	 */
	@Test
	public void getDerniereSequenceEcritureComptable() {
		ComptabiliteDao vComptabiliteDaoImpl = ComptabiliteDaoImpl.getInstance();

		SequenceEcritureComptable vSequenceEcritureComptable = vComptabiliteDaoImpl.getDerniereSequenceEcritureComptable("AC", 2016);

		Assert.assertTrue("Test ComptabiliteDaoImpl.getDerniereSequenceEcritureComptable(String pCodeJournal, int pAnnee), la SequenceEcritureComptable retourné est incorrecte", vSequenceEcritureComptable.getAnnee().intValue() == 2016 && vSequenceEcritureComptable.getDerniereValeur().intValue() == 40);

		vSequenceEcritureComptable = vComptabiliteDaoImpl.getDerniereSequenceEcritureComptable("AC", 2018);

		Assert.assertTrue("Test ComptabiliteDaoImpl.getDerniereSequenceEcritureComptable(String pCodeJournal, int pAnnee), la SequenceEcritureComptable retourné est incorrecte", vSequenceEcritureComptable == null);
	}

	/**
	 * test de void insertSequenceEcritureComptable(String pCodeJournal, SequenceEcritureComptable pSequenceEcritureComptable)
	 */
	@Test
	public void insertSequenceEcritureComptable() {
		ComptabiliteDao vComptabiliteDaoImpl = ComptabiliteDaoImpl.getInstance();

		SequenceEcritureComptable vSequenceEcritureComptableAttendu = new SequenceEcritureComptable(2018, 35);

		// Appel méthode à tester
		vComptabiliteDaoImpl.insertSequenceEcritureComptable("AC", vSequenceEcritureComptableAttendu);

		SequenceEcritureComptable vSequenceEcritureComptable = vComptabiliteDaoImpl.getDerniereSequenceEcritureComptable("AC", 2018);

		Assert.assertTrue("Test ComptabiliteDaoImpl.insertSequenceEcritureComptable(String pCodeJournal, SequenceEcritureComptable pSequenceEcritureComptable),"
				+ " la SequenceEcritureComptable n'a pas été enregistré en base de données",
				vSequenceEcritureComptable.getAnnee().intValue() == vSequenceEcritureComptableAttendu.getAnnee().intValue()
						&& vSequenceEcritureComptable.getDerniereValeur().intValue() == vSequenceEcritureComptableAttendu.getDerniereValeur().intValue());
	}

	/**
	 * test de void updateSequenceEcritureComptable(String pCodeJournal, SequenceEcritureComptable pSequenceEcritureComptable)
	 */
	@Test
	public void updateSequenceEcritureComptable() {
		ComptabiliteDao vComptabiliteDaoImpl = ComptabiliteDaoImpl.getInstance();

		SequenceEcritureComptable vSequenceEcritureComptableAttendu = new SequenceEcritureComptable(2016, 69);

		// Appel méthode à tester
		vComptabiliteDaoImpl.updateSequenceEcritureComptable("AC", vSequenceEcritureComptableAttendu);

		SequenceEcritureComptable vSequenceEcritureComptable = vComptabiliteDaoImpl.getDerniereSequenceEcritureComptable("AC", 2016);

		Assert.assertTrue("Test ComptabiliteDaoImpl.updateSequenceEcritureComptable(String pCodeJournal, SequenceEcritureComptable pSequenceEcritureComptable),"
				+ " la SequenceEcritureComptable n'a pas été enregistré en base de données",
				vSequenceEcritureComptable.getAnnee().intValue() == vSequenceEcritureComptableAttendu.getAnnee().intValue()
						&& vSequenceEcritureComptable.getDerniereValeur().intValue() == vSequenceEcritureComptableAttendu.getDerniereValeur().intValue());
	}
}
