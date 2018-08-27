package com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;

/**
 * {@link RowMapper} de {@link JournalComptable}
 */
public class JournalComptableRM implements RowMapper<JournalComptable> {

	@Override
	public JournalComptable mapRow(ResultSet pRS, int pRowNum) throws SQLException {
		String vCode = pRS.getString("code");
		String vLibelle = pRS.getString("libelle");

		return new JournalComptable(vCode, vLibelle);
	}
}
