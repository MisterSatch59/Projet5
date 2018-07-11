package com.dummy.myerp.consumer.db.helper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Class de test de {@link ResultSetHelper}
 * 
 * @author Oltenos
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ResultSetHelperTest {

	@Mock
	private ResultSet resultSet;

	/**
	 * Test de static Integer getInteger(ResultSet pRS, String pColName) throws SQLException
	 */
	@Test
	public void getInteger() throws SQLException {
		String colName = "colName";

		// 2 tests avec des entiers comme résultat de la colonne
		Mockito.when(resultSet.getInt(colName)).thenReturn(12);// stubbing méthode getInt
		Mockito.when(resultSet.wasNull()).thenReturn(false);// stubbing méthode wasNull
		Assert.assertTrue("Test ResultSetHelper.getInteger", ResultSetHelper.getInteger(resultSet, colName).intValue() == 12);

		Mockito.when(resultSet.getInt(colName)).thenReturn(6541);
		Mockito.when(resultSet.wasNull()).thenReturn(false);
		Assert.assertTrue("Test ResultSetHelper.getInteger", ResultSetHelper.getInteger(resultSet, colName).intValue() == 6541);

		// Test avec un résultat null (getInt retourne 0 mais wasNull retourne true)
		Mockito.when(resultSet.getInt(colName)).thenReturn(0);
		Mockito.when(resultSet.wasNull()).thenReturn(true);
		Assert.assertTrue("Test ResultSetHelper.getInteger", ResultSetHelper.getInteger(resultSet, colName) == null);

		// Test avec 0 comme résultat (getInt retourne 0 et wasNull retourne false)
		Mockito.when(resultSet.getInt(colName)).thenReturn(0);
		Mockito.when(resultSet.wasNull()).thenReturn(false);
		Assert.assertTrue("Test ResultSetHelper.getInteger", ResultSetHelper.getInteger(resultSet, colName).intValue() == 0);
	}

	/**
	 * Test de static Integer getInteger(ResultSet pRS, String pColName) throws SQLException
	 * avec levée d'une SQLException : vérifie qu'une exeption de {@link ResultSet}.getInt soit remontée
	 * 
	 * @throws SQLException
	 */
	@Test(expected = SQLException.class)
	public void getIntegerException() throws SQLException {
		String colName = "colName";
		Mockito.when(resultSet.getInt(colName)).thenThrow(new SQLException());
		ResultSetHelper.getInteger(resultSet, colName);
	}

	/**
	 * Test de static static Long getLong(ResultSet pRS, String pColName) throws SQLException
	 */
	@Test
	public void getLong() throws SQLException {
		String colName = "colName";

		// 2 tests avec des long comme résultat de la colonne
		Mockito.when(resultSet.getLong(colName)).thenReturn(3543231L);
		Mockito.when(resultSet.wasNull()).thenReturn(false);
		Assert.assertTrue("Test ResultSetHelper.getLong", ResultSetHelper.getLong(resultSet, colName).longValue() == 3543231L);

		Mockito.when(resultSet.getLong(colName)).thenReturn(683L);
		Mockito.when(resultSet.wasNull()).thenReturn(false);
		Assert.assertTrue("Test ResultSetHelper.getLong", ResultSetHelper.getLong(resultSet, colName).longValue() == 683L);

		// Test avec un résultat null (getLong retourne 0L mais wasNull retourne true)
		Mockito.when(resultSet.getLong(colName)).thenReturn(0L);
		Mockito.when(resultSet.wasNull()).thenReturn(true);
		Assert.assertTrue("Test ResultSetHelper.getLong", ResultSetHelper.getLong(resultSet, colName) == null);

		// Test avec 0 en résultat (getLong retourne 0L et wasNull retourne false)
		Mockito.when(resultSet.getLong(colName)).thenReturn(0L);
		Mockito.when(resultSet.wasNull()).thenReturn(false);
		Assert.assertTrue("Test ResultSetHelper.getLong", ResultSetHelper.getLong(resultSet, colName).longValue() == 0L);
	}

	/**
	 * Test de static static Long getLong(ResultSet pRS, String pColName) throws SQLException
	 * avec levée d'une SQLException : vérifie qu'une exeption de {@link ResultSet}.getLong soit remontée
	 * 
	 * @throws SQLException
	 */
	@Test(expected = SQLException.class)
	public void getLongException() throws SQLException {
		String colName = "colName";
		Mockito.when(resultSet.getLong(colName)).thenThrow(new SQLException());
		ResultSetHelper.getLong(resultSet, colName);
	}

	/**
	 * Test de static Date getDate(ResultSet pRS, String pColName) throws SQLException
	 */
	@Test
	public void getDate() throws SQLException {
		String colName = "colName";

		// tests avec la date du jour comme résultat de la colonne
		Date vDate = new Date(Calendar.getInstance().getTime().getTime());
		Mockito.when(resultSet.getDate(colName)).thenReturn(vDate);
		Mockito.when(resultSet.wasNull()).thenReturn(false);

		Calendar resultCalendar = Calendar.getInstance();
		Calendar initCalendar = Calendar.getInstance();
		initCalendar.setTime(vDate);
		resultCalendar.setTime(ResultSetHelper.getDate(resultSet, colName));

		boolean result = (resultCalendar.get(Calendar.YEAR) == initCalendar.get(Calendar.YEAR) 
				&& resultCalendar.get(Calendar.MONTH) == initCalendar.get(Calendar.MONTH) 
				&& resultCalendar.get(Calendar.DAY_OF_MONTH) == initCalendar.get(Calendar.DAY_OF_MONTH) 
				&& resultCalendar.get(Calendar.HOUR_OF_DAY) == 0 && resultCalendar.get(Calendar.MINUTE) == 0 
				&& resultCalendar.get(Calendar.SECOND) == 0 && resultCalendar.get(Calendar.MILLISECOND) == 0);

		Assert.assertTrue("Test ResultSetHelper.getDate", result);

		// Test avec un résultat null
		Mockito.when(resultSet.getDate(colName)).thenReturn(null);
		Mockito.when(resultSet.wasNull()).thenReturn(true);
		Assert.assertTrue("Test ResultSetHelper.getDate", ResultSetHelper.getDate(resultSet, colName) == null);
	}

	/**
	 * Test de static Date getDate(ResultSet pRS, String pColName) throws SQLException
	 * avec levée d'une SQLException : vérifie qu'une exeption de {@link ResultSet}.getLong soit remontée
	 * 
	 * @throws SQLException
	 */
	@Test(expected = SQLException.class)
	public void getDateException() throws SQLException {
		String colName = "colName";
		Mockito.when(resultSet.getDate(colName)).thenThrow(new SQLException());
		ResultSetHelper.getDate(resultSet, colName);
	}
}
