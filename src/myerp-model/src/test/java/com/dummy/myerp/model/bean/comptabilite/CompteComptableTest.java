package com.dummy.myerp.model.bean.comptabilite;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test Unitaire de {@link CompteComptable}
 * 
 * @author Oltenos
 *
 */
public class CompteComptableTest {

	/**
	 * Test de CompteComptable getByNumero(List<? extends CompteComptable> pList, Integer pNumero)
	 */
	@Test
	public void getByNumero() {
		// Création d'une liste de CompteComptable
		List<CompteComptable> vList = new ArrayList<CompteComptable>();
		vList.add(new CompteComptable(1, "test"));
		CompteComptable vCompteComptable = new CompteComptable(16, "test");
		vList.add(vCompteComptable);
		CompteComptable vCompteComptable2 = new CompteComptable(19, "test");
		vList.add(vCompteComptable2);
		vList.add(new CompteComptable(165, "test"));
		vList.add(new CompteComptable(5034, "test"));

		// Test de deux comptes de la liste
		Assert.assertTrue(vList.toString(), CompteComptable.getByNumero(vList, 16).equals(vCompteComptable));
		Assert.assertTrue(vList.toString(), CompteComptable.getByNumero(vList, 19).equals(vCompteComptable2));

		// Test de deux comptes qui ne sont pas dans la liste avec un numéro au milieu et un autre plus grand que le plus grand numéro de la liste : null attendu
		Assert.assertTrue(vList.toString(), CompteComptable.getByNumero(vList, 17) == null);
		Assert.assertTrue(vList.toString(), CompteComptable.getByNumero(vList, 7598) == null);

		// Test avec de mauvais paramètre : pNumero à null, 0 et négatif et pList vide et null
		Assert.assertTrue(vList.toString(), CompteComptable.getByNumero(vList, null) == null);
		Assert.assertTrue(vList.toString(), CompteComptable.getByNumero(vList, 0) == null);
		Assert.assertTrue(vList.toString(), CompteComptable.getByNumero(vList, -1234) == null);

		Assert.assertTrue(vList.toString(), CompteComptable.getByNumero(new ArrayList<CompteComptable>(), 16) == null);
		Assert.assertTrue(vList.toString(), CompteComptable.getByNumero(null, 16) == null);
	}
}
