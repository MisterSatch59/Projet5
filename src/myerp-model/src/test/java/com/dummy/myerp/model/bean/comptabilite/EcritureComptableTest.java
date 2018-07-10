package com.dummy.myerp.model.bean.comptabilite;

import java.math.BigDecimal;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Unitaire de {@link EcritureComptable}
 * 
 * @author Oltenos
 *
 */
public class EcritureComptableTest {

	/**
	 * Creation d'une LigneEcritureComptable
	 * 
	 * @param pCompteComptableNumero
	 * @param pDebit
	 * @param pCredit
	 * @return LigneEcritureComptable
	 */
	private LigneEcritureComptable createLigne(Integer pCompteComptableNumero, String pDebit, String pCredit) {
		BigDecimal vDebit = pDebit == null ? null : new BigDecimal(pDebit);
		BigDecimal vCredit = pCredit == null ? null : new BigDecimal(pCredit);
		String vLibelle = ObjectUtils.defaultIfNull(vDebit, BigDecimal.ZERO).subtract(ObjectUtils.defaultIfNull(vCredit, BigDecimal.ZERO)).toPlainString();
		LigneEcritureComptable vRetour = new LigneEcritureComptable(new CompteComptable(pCompteComptableNumero), vLibelle, vDebit, vCredit);
		return vRetour;
	}

	/**
	 * Test de boolean isEquilibree()
	 */
	@Test
	public void isEquilibree() {
		EcritureComptable vEcriture;
		vEcriture = new EcritureComptable();

		// Test Equilibrée
		vEcriture.setLibelle("Equilibrée");
		vEcriture.getListLigneEcriture().add(this.createLigne(1, "200.50", null));
		vEcriture.getListLigneEcriture().add(this.createLigne(1, "100.50", "33"));
		vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "301"));
		vEcriture.getListLigneEcriture().add(this.createLigne(2, "40", "7"));
		Assert.assertTrue("Test de EcritureComptable.isEquilibree() avec équilibre", vEcriture.isEquilibree());

		// Test Non Equilibrée Debit<Credit
		vEcriture.getListLigneEcriture().clear();
		vEcriture.setLibelle("Non équilibrée");
		vEcriture.getListLigneEcriture().add(this.createLigne(1, "10", null));
		vEcriture.getListLigneEcriture().add(this.createLigne(1, "20", "1"));
		vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "30.5"));
		vEcriture.getListLigneEcriture().add(this.createLigne(2, "1", "2"));
		Assert.assertFalse("Test de EcritureComptable.isEquilibree() avec Debit<Credit", vEcriture.isEquilibree());

		// Test Non Equilibrée Debit>Credit
		vEcriture.getListLigneEcriture().clear();
		vEcriture.setLibelle("Non équilibrée");
		vEcriture.getListLigneEcriture().add(this.createLigne(1, "12545.53", null));
		vEcriture.getListLigneEcriture().add(this.createLigne(1, "30", "12542.53"));
		vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "30"));
		vEcriture.getListLigneEcriture().add(this.createLigne(2, "5", "2"));
		Assert.assertFalse("Test de EcritureComptable.isEquilibree() avec Debit>Credit", vEcriture.isEquilibree());
	}

	/**
	 * Test de BigDecimal getTotalDebit()
	 */
	@Test
	public void getTotalDebit() {
		EcritureComptable vEcriture;
		vEcriture = new EcritureComptable();

		// Test de getTotalDebit avec des valeurs null, décimal et entière et un credit
		// égal au débit
		vEcriture.setLibelle("Test getTotalDebit");
		vEcriture.getListLigneEcriture().add(this.createLigne(1, "200.34", null));
		vEcriture.getListLigneEcriture().add(this.createLigne(1, "133", "200.34"));
		vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "40"));
		vEcriture.getListLigneEcriture().add(this.createLigne(2, "40", "133"));

		Assert.assertTrue(vEcriture.toString(), vEcriture.getTotalDebit().compareTo(new BigDecimal("373.34")) == 0);

		// Test de getTotalDebit avec des valeurs null, décimal et entière et un credit
		// différent du débit
		vEcriture.getListLigneEcriture().clear();
		vEcriture.setLibelle("Test getTotalDebit");
		vEcriture.getListLigneEcriture().add(this.createLigne(1, "200.34", null));
		vEcriture.getListLigneEcriture().add(this.createLigne(1, "133", "33"));
		vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "301"));
		vEcriture.getListLigneEcriture().add(this.createLigne(2, "40", "7"));

		Assert.assertTrue(vEcriture.toString(), vEcriture.getTotalDebit().compareTo(new BigDecimal("373.34")) == 0);
	}

	/**
	 * Test de BigDecimal getTotalCredit()
	 */
	@Test
	public void getTotalCredit() {
		EcritureComptable vEcriture;
		vEcriture = new EcritureComptable();

		// Test de getTotalCredit avec des valeurs null, décimal et entière et un credit égal au débit
		vEcriture.setLibelle("Test getTotalCredit");
		vEcriture.getListLigneEcriture().add(this.createLigne(1, "200.34", null));
		vEcriture.getListLigneEcriture().add(this.createLigne(1, "133", "200.34"));
		vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "40"));
		vEcriture.getListLigneEcriture().add(this.createLigne(2, "40", "133"));

		Assert.assertTrue(vEcriture.toString(), vEcriture.getTotalCredit().compareTo(new BigDecimal("373.34")) == 0);

		// Test de getTotagetTotalCreditlDebit avec des valeurs null, décimal et entière et un credit différent du débit
		vEcriture.getListLigneEcriture().clear();
		vEcriture.setLibelle("Test getTotalCredit");
		vEcriture.getListLigneEcriture().add(this.createLigne(1, "456", null));
		vEcriture.getListLigneEcriture().add(this.createLigne(1, "58", "200.34"));
		vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "40"));
		vEcriture.getListLigneEcriture().add(this.createLigne(2, "80.32", "133"));

		Assert.assertTrue(vEcriture.toString(), vEcriture.getTotalCredit().compareTo(new BigDecimal("373.34")) == 0);
	}
}
