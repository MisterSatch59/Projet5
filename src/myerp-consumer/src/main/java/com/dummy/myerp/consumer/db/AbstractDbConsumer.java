package com.dummy.myerp.consumer.db;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.dummy.myerp.consumer.ConsumerHelper;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.technical.exception.TechnicalException;

/**
 * <p>
 * Classe mère des classes de Consumer DB
 * </p>
 */
public abstract class AbstractDbConsumer {

	// ==================== Attributs Static ====================
	/** Logger Log4j pour la classe **/
	private static final Logger LOGGER = LogManager.getLogger(AbstractDbConsumer.class);

	/** Map des DataSources **/
	private static Map<DataSourcesEnum, DataSource> mapDataSource;

	/** Message d'erreur d'initalisation des DataSources **/
	private static String errorInitDatasources = "Les DataSource n'ont pas été initialisées !";

	/** Message d'erreur d'initalisation d'une DataSource **/
	private static String errorInitOneDatasource = "La DataSource suivante n'a pas été initialisée : ";

	// ==================== Constructeurs ====================

	/**
	 * Constructeur.
	 */
	protected AbstractDbConsumer() {
		super();
	}

	// ==================== Getters/Setters ====================
	/**
	 * Renvoie une {@link DaoProxy}
	 *
	 * @return {@link DaoProxy}
	 */
	protected static DaoProxy getDaoProxy() {
		return ConsumerHelper.getDaoProxy();
	}

	// ==================== Méthodes ====================
	/**
	 * Renvoie le {@link DataSource} associé demandée
	 *
	 * @param pDataSourceId
	 *            -
	 * @return SimpleJdbcTemplate
	 */
	protected DataSource getDataSource(DataSourcesEnum pDataSourceId) {
		if (mapDataSource == null) { // ******** Correction Oltenos : ajout du cas où les dataSource n'ont pas été initialisé
			LOGGER.error(errorInitDatasources);
			throw new UnsatisfiedLinkError(errorInitDatasources);
		}
		DataSource vRetour = mapDataSource.get(pDataSourceId);
		if (vRetour == null) {
			throw new UnsatisfiedLinkError(errorInitOneDatasource + pDataSourceId);
		}
		return vRetour;
	}

	// ==================== Méthodes Static ====================
	/**
	 * Méthode de configuration de la classe
	 *
	 * @param pMapDataSource
	 *            -
	 * @throws TechnicalException
	 *             -
	 */
	public static void configure(Map<DataSourcesEnum, DataSource> pMapDataSource) throws TechnicalException {
		if (pMapDataSource == null || pMapDataSource.size() == 0) { // ******** Correction Oltenos : ajout du cas pMapDataSource null ou vide
			LOGGER.error(errorInitDatasources);
			throw new TechnicalException(errorInitDatasources);
		}

		// On pilote l'ajout avec l'Enum et on ne rajoute pas tout à l'aveuglette...
		// ( pas de AbstractDbDao.mapDataSource.putAll(...) )
		Map<DataSourcesEnum, DataSource> vMapDataSource = new HashMap<>(DataSourcesEnum.values().length);
		DataSourcesEnum[] vDataSourceIds = DataSourcesEnum.values();
		for (DataSourcesEnum vDataSourceId : vDataSourceIds) {
			DataSource vDataSource = pMapDataSource.get(vDataSourceId);
			// On test si la DataSource est configurée
			// (NB : elle est considérée comme configurée si elle est dans pMapDataSource mais à null) //******** Correction Oltenos : pMapDataSource ne doit pas être null
			if (vDataSource == null) {
				LOGGER.error(errorInitOneDatasource + vDataSourceId);
				throw new TechnicalException(errorInitOneDatasource + vDataSourceId);		// ******** Correction Oltenos : ajout Exception
			} else {
				vMapDataSource.put(vDataSourceId, vDataSource);
			}
		}
		mapDataSource = vMapDataSource;
	}
}
