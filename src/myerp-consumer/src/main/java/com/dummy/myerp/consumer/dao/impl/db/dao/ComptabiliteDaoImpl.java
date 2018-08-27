package com.dummy.myerp.consumer.dao.impl.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite.CompteComptableRM;
import com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite.EcritureComptableRM;
import com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite.JournalComptableRM;
import com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite.LigneEcritureComptableRM;
import com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite.SequenceEcritureComptableRM;
import com.dummy.myerp.consumer.db.AbstractDbConsumer;
import com.dummy.myerp.consumer.db.DataSourcesEnum;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.NotFoundException;

/**
 * Implémentation de l'interface {@link ComptabiliteDao}
 */
public class ComptabiliteDaoImpl extends AbstractDbConsumer implements ComptabiliteDao {

	// ==================== Constructeurs ====================
	/** Instance unique de la classe (design pattern Singleton) */
	private static final ComptabiliteDaoImpl INSTANCE = new ComptabiliteDaoImpl();

	/**
	 * Renvoie l'instance unique de la classe (design pattern Singleton).
	 *
	 * @return {@link ComptabiliteDaoImpl}
	 */
	public static ComptabiliteDaoImpl getInstance() {
		return ComptabiliteDaoImpl.INSTANCE;
	}

	/**
	 * Constructeur.
	 */
	protected ComptabiliteDaoImpl() {
		super();
	}

	// ==================== CompteComptable - GET ====================
	/** SQLgetListCompteComptable */
	private static String sqlGetListCompteComptable;

	public static void setSQLgetListCompteComptable(String pSQLgetListCompteComptable) {
		sqlGetListCompteComptable = pSQLgetListCompteComptable;
	}

	@Override
	public List<CompteComptable> getListCompteComptable() {
		JdbcTemplate vJdbcTemplate = new JdbcTemplate(this.getDataSource(DataSourcesEnum.MYERP));
		CompteComptableRM vRM = new CompteComptableRM();
		return vJdbcTemplate.query(sqlGetListCompteComptable, vRM);
	}

	// ==================== JournalComptable - GET ====================
	/** SQLgetListJournalComptable */
	private static String sqlGetListJournalComptable;

	public static void setSQLgetListJournalComptable(String pSQLgetListJournalComptable) {
		sqlGetListJournalComptable = pSQLgetListJournalComptable;
	}

	@Override
	public List<JournalComptable> getListJournalComptable() {
		JdbcTemplate vJdbcTemplate = new JdbcTemplate(this.getDataSource(DataSourcesEnum.MYERP));
		JournalComptableRM vRM = new JournalComptableRM();
		return vJdbcTemplate.query(sqlGetListJournalComptable, vRM);
	}

	// ==================== EcritureComptable - GET ====================

	/** SQLgetListEcritureComptable */
	private static String sqlGetListEcritureComptable;

	public static void setSQLgetListEcritureComptable(String pSQLgetListEcritureComptable) {
		sqlGetListEcritureComptable = pSQLgetListEcritureComptable;
	}

	@Override
	public List<EcritureComptable> getListEcritureComptable() {
		JdbcTemplate vJdbcTemplate = new JdbcTemplate(this.getDataSource(DataSourcesEnum.MYERP));
		EcritureComptableRM vRM = new EcritureComptableRM();
		return vJdbcTemplate.query(sqlGetListEcritureComptable, vRM);
	}

	/** SQLgetEcritureComptable */
	private static String sqlGetEcritureComptable;

	public static void setSQLgetEcritureComptable(String pSQLgetEcritureComptable) {
		sqlGetEcritureComptable = pSQLgetEcritureComptable;
	}

	@Override
	public EcritureComptable getEcritureComptable(Integer pId) throws NotFoundException {
		NamedParameterJdbcTemplate vJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource(DataSourcesEnum.MYERP));
		MapSqlParameterSource vSqlParams = new MapSqlParameterSource();
		vSqlParams.addValue("id", pId);
		EcritureComptableRM vRM = new EcritureComptableRM();
		EcritureComptable vBean;
		try {
			vBean = vJdbcTemplate.queryForObject(sqlGetEcritureComptable, vSqlParams, vRM);
		} catch (EmptyResultDataAccessException vEx) {
			throw new NotFoundException("EcritureComptable non trouvée : id=" + pId);
		}
		return vBean;
	}

	/** SQLgetEcritureComptableByRef */
	private static String sqlGetEcritureComptableByRef;

	public static void setSQLgetEcritureComptableByRef(String pSQLgetEcritureComptableByRef) {
		sqlGetEcritureComptableByRef = pSQLgetEcritureComptableByRef;
	}

	@Override
	public EcritureComptable getEcritureComptableByRef(String pReference) throws NotFoundException {
		NamedParameterJdbcTemplate vJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource(DataSourcesEnum.MYERP));
		MapSqlParameterSource vSqlParams = new MapSqlParameterSource();
		vSqlParams.addValue("reference", pReference);
		EcritureComptableRM vRM = new EcritureComptableRM();
		EcritureComptable vBean;
		try {
			vBean = vJdbcTemplate.queryForObject(sqlGetEcritureComptableByRef, vSqlParams, vRM);
		} catch (EmptyResultDataAccessException vEx) {
			throw new NotFoundException("EcritureComptable non trouvée : reference=" + pReference);
		}
		return vBean;
	}

	// ==================== LigneEcritureComptable - LOAD (GET) ====================

	/** SQLloadListLigneEcriture */
	private static String sqlLoadListLigneEcriture;

	public static void setSQLloadListLigneEcriture(String pSQLloadListLigneEcriture) {
		sqlLoadListLigneEcriture = pSQLloadListLigneEcriture;
	}

	@Override
	public void loadListLigneEcriture(EcritureComptable pEcritureComptable) {
		NamedParameterJdbcTemplate vJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource(DataSourcesEnum.MYERP));
		MapSqlParameterSource vSqlParams = new MapSqlParameterSource();
		vSqlParams.addValue("ecriture_id", pEcritureComptable.getId());
		LigneEcritureComptableRM vRM = new LigneEcritureComptableRM();
		List<LigneEcritureComptable> vList = vJdbcTemplate.query(sqlLoadListLigneEcriture, vSqlParams, vRM);
		pEcritureComptable.getListLigneEcriture().clear();
		pEcritureComptable.getListLigneEcriture().addAll(vList);
	}

	// ==================== EcritureComptable - INSERT ====================

	/** SQLinsertEcritureComptable */
	private static String sqlInsertEcritureComptable;

	public static void setSQLinsertEcritureComptable(String pSQLinsertEcritureComptable) {
		sqlInsertEcritureComptable = pSQLinsertEcritureComptable;
	}

	@Override
	public void insertEcritureComptable(EcritureComptable pEcritureComptable) {
		// ===== Ecriture Comptable
		NamedParameterJdbcTemplate vJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource(DataSourcesEnum.MYERP));
		MapSqlParameterSource vSqlParams = new MapSqlParameterSource();
		vSqlParams.addValue("journal_code", pEcritureComptable.getJournal().getCode());
		vSqlParams.addValue("reference", pEcritureComptable.getReference());
		vSqlParams.addValue("date", pEcritureComptable.getDate(), Types.DATE);
		vSqlParams.addValue("libelle", pEcritureComptable.getLibelle());

		KeyHolder keyHolder = new GeneratedKeyHolder(); // *** Modification Oltenos : utilisation du KeyHolder au lieu des sequences pour récupérer l'id en auto_increment
		vJdbcTemplate.update(sqlInsertEcritureComptable, vSqlParams, keyHolder);

		int vId = (int) keyHolder.getKeys().get("id");
		pEcritureComptable.setId(vId);

		// ===== Liste des lignes d'écriture
		this.insertListLigneEcritureComptable(pEcritureComptable);
	}

	/** SQLinsertListLigneEcritureComptable */
	private static String sqlInsertListLigneEcritureComptable;

	public static void setSQLinsertListLigneEcritureComptable(String pSQLinsertListLigneEcritureComptable) {
		sqlInsertListLigneEcritureComptable = pSQLinsertListLigneEcritureComptable;
	}

	/**
	 * Insert les lignes d'écriture de l'écriture comptable
	 * 
	 * @param pEcritureComptable
	 *            l'écriture comptable
	 */
	protected void insertListLigneEcritureComptable(EcritureComptable pEcritureComptable) {
		NamedParameterJdbcTemplate vJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource(DataSourcesEnum.MYERP));
		MapSqlParameterSource vSqlParams = new MapSqlParameterSource();
		vSqlParams.addValue("ecriture_id", pEcritureComptable.getId());

		int vLigneId = 0;
		for (LigneEcritureComptable vLigne : pEcritureComptable.getListLigneEcriture()) {
			vLigneId++;
			vSqlParams.addValue("ligne_id", vLigneId);
			vSqlParams.addValue("compte_comptable_numero", vLigne.getCompteComptable().getNumero());
			vSqlParams.addValue("libelle", vLigne.getLibelle());
			vSqlParams.addValue("debit", vLigne.getDebit());

			vSqlParams.addValue("credit", vLigne.getCredit());

			vJdbcTemplate.update(sqlInsertListLigneEcritureComptable, vSqlParams);
		}
	}

	// ==================== EcritureComptable - UPDATE ====================

	/** SQLupdateEcritureComptable */
	private static String sqlUpdateEcritureComptable;

	public static void setSQLupdateEcritureComptable(String pSQLupdateEcritureComptable) {
		sqlUpdateEcritureComptable = pSQLupdateEcritureComptable;
	}

	@Override
	public void updateEcritureComptable(EcritureComptable pEcritureComptable) {
		// ===== Ecriture Comptable
		NamedParameterJdbcTemplate vJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource(DataSourcesEnum.MYERP));
		MapSqlParameterSource vSqlParams = new MapSqlParameterSource();
		vSqlParams.addValue("id", pEcritureComptable.getId());
		vSqlParams.addValue("journal_code", pEcritureComptable.getJournal().getCode());
		vSqlParams.addValue("reference", pEcritureComptable.getReference());
		vSqlParams.addValue("date", pEcritureComptable.getDate(), Types.DATE);
		vSqlParams.addValue("libelle", pEcritureComptable.getLibelle());

		vJdbcTemplate.update(sqlUpdateEcritureComptable, vSqlParams);

		// ===== Liste des lignes d'écriture
		this.deleteListLigneEcritureComptable(pEcritureComptable.getId());
		this.insertListLigneEcritureComptable(pEcritureComptable);
	}

	// ==================== EcritureComptable - DELETE ====================

	/** SQLdeleteEcritureComptable */
	private static String sqlDeleteEcritureComptable;

	public static void setSQLdeleteEcritureComptable(String pSQLdeleteEcritureComptable) {
		sqlDeleteEcritureComptable = pSQLdeleteEcritureComptable;
	}

	@Override
	public void deleteEcritureComptable(Integer pId) {
		// ===== Suppression des lignes d'écriture
		this.deleteListLigneEcritureComptable(pId);

		// ===== Suppression de l'écriture
		NamedParameterJdbcTemplate vJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource(DataSourcesEnum.MYERP));
		MapSqlParameterSource vSqlParams = new MapSqlParameterSource();
		vSqlParams.addValue("id", pId);
		vJdbcTemplate.update(sqlDeleteEcritureComptable, vSqlParams);
	}

	/** SQLdeleteListLigneEcritureComptable */
	private static String sqlDeleteListLigneEcritureComptable;

	public static void setSQLdeleteListLigneEcritureComptable(String pSQLdeleteListLigneEcritureComptable) {
		sqlDeleteListLigneEcritureComptable = pSQLdeleteListLigneEcritureComptable;
	}

	/**
	 * Supprime les lignes d'écriture de l'écriture comptable d'id
	 * {@code pEcritureId}
	 * 
	 * @param pEcritureId
	 *            id de l'écriture comptable
	 */
	protected void deleteListLigneEcritureComptable(Integer pEcritureId) {
		NamedParameterJdbcTemplate vJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource(DataSourcesEnum.MYERP));
		MapSqlParameterSource vSqlParams = new MapSqlParameterSource();
		vSqlParams.addValue("ecriture_id", pEcritureId);
		vJdbcTemplate.update(sqlDeleteListLigneEcritureComptable, vSqlParams);
	}

	// ==================== SequenceEcritureComptable - GET ====================

	/** SQLgetDerniereSequenceEcritureComptable */
	private static String sqlGetDerniereSequenceEcritureComptable;

	public static void setSQLgetDerniereSequenceEcritureComptable(String pSQLgetDerniereSequenceEcritureComptable) {
		sqlGetDerniereSequenceEcritureComptable = pSQLgetDerniereSequenceEcritureComptable;
	}

	@Override
	public SequenceEcritureComptable getDerniereSequenceEcritureComptable(String pCodeJournal, int pAnnee) {
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(this.getDataSource(DataSourcesEnum.MYERP));

		MapSqlParameterSource vParams = new MapSqlParameterSource();
		vParams.addValue("annee", pAnnee);
		vParams.addValue("journal_code", pCodeJournal);

		SequenceEcritureComptableRM vRM = new SequenceEcritureComptableRM();
		List<SequenceEcritureComptable> vList = namedParameterJdbcTemplate.query(sqlGetDerniereSequenceEcritureComptable, vParams, vRM);

		if (vList.isEmpty())
			return null;
		else
			return vList.get(0);
	}

	// ==================== SequenceEcritureComptable - INSERT ====================

	/** SQLinsertSequenceEcritureComptable */
	private static String sqlInsertSequenceEcritureComptable;

	public static void setSQLinsertSequenceEcritureComptable(String pSQLinsertSequenceEcritureComptable) {
		sqlInsertSequenceEcritureComptable = pSQLinsertSequenceEcritureComptable;
	}

	@Override
	public void insertSequenceEcritureComptable(String pCodeJournal, SequenceEcritureComptable pSequenceEcritureComptable) {
		NamedParameterJdbcTemplate vJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource(DataSourcesEnum.MYERP));
		MapSqlParameterSource vSqlParams = new MapSqlParameterSource();
		vSqlParams.addValue("annee", pSequenceEcritureComptable.getAnnee());
		vSqlParams.addValue("journal_code", pCodeJournal);
		vSqlParams.addValue("derniere_valeur", pSequenceEcritureComptable.getDerniereValeur());

		vJdbcTemplate.update(sqlInsertSequenceEcritureComptable, vSqlParams);

	}

	// ==================== SequenceEcritureComptable - UPDATE ====================

	/** SQLupdateSequenceEcritureComptable */
	private static String sqlUpdateSequenceEcritureComptable;

	public static void setSQLupdateSequenceEcritureComptable(String pSQLupdateSequenceEcritureComptable) {
		sqlUpdateSequenceEcritureComptable = pSQLupdateSequenceEcritureComptable;
	}

	@Override
	public void updateSequenceEcritureComptable(String pCodeJournal, SequenceEcritureComptable pSequenceEcritureComptable) {
		NamedParameterJdbcTemplate vJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource(DataSourcesEnum.MYERP));
		MapSqlParameterSource vSqlParams = new MapSqlParameterSource();
		vSqlParams.addValue("annee", pSequenceEcritureComptable.getAnnee());
		vSqlParams.addValue("journal_code", pCodeJournal);
		vSqlParams.addValue("derniere_valeur", pSequenceEcritureComptable.getDerniereValeur());

		vJdbcTemplate.update(sqlUpdateSequenceEcritureComptable, vSqlParams);

	}

}
