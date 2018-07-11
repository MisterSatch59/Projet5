package com.dummy.myerp.model.bean.comptabilite;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test Unitaire de {@link JournalComptable}
 * @author Oltenos
 *
 */
public class JournalComptableTest {

	/**
	 * Test de JournalComptable getByCode(List<? extends JournalComptable> pList, String pCode)
	 */
	@Test
	public void getByCode() {
		List<JournalComptable> vList = new ArrayList<JournalComptable>();
		
		JournalComptable vJournalComptable1 = new JournalComptable("test", "JournalComptable 1");
		vList.add(vJournalComptable1);
		JournalComptable vJournalComptable2 = new JournalComptable("coDe", "JournalComptable 2");
		vList.add(vJournalComptable2);
		JournalComptable vJournalComptable3 = new JournalComptable("15326", "JournalComptable 3");
		vList.add(vJournalComptable3);
		JournalComptable vJournalComptable4 = new JournalComptable("est12", "JournalComptable 4");
		vList.add(vJournalComptable4);
		
		//Test de trois journaux de la liste avec des code uniquement numérique/uniquement caractère et mélangé
		Assert.assertTrue(vList.toString(), JournalComptable.getByCode(vList, "coDe").equals(vJournalComptable2));
		Assert.assertTrue(vList.toString(), JournalComptable.getByCode(vList, "15326").equals(vJournalComptable3));
		Assert.assertTrue(vList.toString(), JournalComptable.getByCode(vList, "est12").equals(vJournalComptable4));
		
		//Test de deux journaux qui ne sont pas dans la liste
		Assert.assertTrue(vList.toString(), JournalComptable.getByCode(vList, "blabl")==null);
		Assert.assertTrue(vList.toString(), JournalComptable.getByCode(vList, "Argh")==null);
		
		//Test avec de mauvais paramètre : pCode à null et vide et pList vide et null
		Assert.assertTrue(vList.toString(), JournalComptable.getByCode(vList, null) == null);
		Assert.assertTrue(vList.toString(), JournalComptable.getByCode(vList, "")==null);
				
		Assert.assertTrue(vList.toString(), JournalComptable.getByCode(new ArrayList<JournalComptable>(), "coDe")==null);
		Assert.assertTrue(vList.toString(), JournalComptable.getByCode(null, "coDe")==null);
	}

}
