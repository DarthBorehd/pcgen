/*
 * Created on 02-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package plugin.pretokens.test;

import java.util.Set;

import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.ObjectContainer;
import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.FactSetKey;
import pcgen.core.Deity;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractDisplayPrereqTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.system.LanguageBundle;

/**
 * @author wardc
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PreDeityTester extends AbstractDisplayPrereqTest implements PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final CharacterDisplay display, CDOMObject source)
		throws PrerequisiteException
	{
		int runningTotal;

		if (prereq.getKey().startsWith("PANTHEON."))//$NON-NLS-1$
		{
			try
			{
				String pantheon = prereq.getKey().substring(9);
				Deity deity = display.getDeity();
				Set<String> charDeityPantheon =
						new WrappedMapSet<String>(CaseInsensitiveMap.class);
				if (deity != null)
				{
					FactSetKey<String> fk = FactSetKey.valueOf("Pantheon");
					for (ObjectContainer<String> oc : deity.getSafeSetFor(fk))
					{
						charDeityPantheon.addAll(oc.getContainedObjects());
					}
				}
				if (prereq.getOperator().equals(PrerequisiteOperator.EQ)
						|| prereq.getOperator().equals(
								PrerequisiteOperator.GTEQ))
				{
					runningTotal = (charDeityPantheon.contains(pantheon)) ? 1
							: 0;
				}
				else if (prereq.getOperator().equals(PrerequisiteOperator.NEQ)
						|| prereq.getOperator().equals(PrerequisiteOperator.LT))
				{
					runningTotal = (charDeityPantheon.contains(pantheon)) ? 0
							: 1;
				}
				else
				{
					throw new PrerequisiteException(
							LanguageBundle
									.getFormattedString(
											"PreDeity.error.bad_coparator", prereq.toString())); //$NON-NLS-1$
				}
			}
			catch (IllegalArgumentException e)
			{
				//This is okay, just indicates the Pantheon asked for can't exist in any PC
				if (prereq.getOperator().equals(PrerequisiteOperator.EQ)
						|| prereq.getOperator().equals(
								PrerequisiteOperator.GTEQ))
				{
					runningTotal = 0;
				}
				else if (prereq.getOperator().equals(PrerequisiteOperator.NEQ)
						|| prereq.getOperator().equals(PrerequisiteOperator.LT))
				{
					runningTotal = 1;
				}
				else
				{
					throw new PrerequisiteException(
							LanguageBundle
									.getFormattedString(
											"PreDeity.error.bad_coparator", prereq.toString())); //$NON-NLS-1$
				}
			}
		}
		else
		{
			final String charDeity =
					display.getDeity() != null ? display.getDeity()
						.getKeyName() : ""; //$NON-NLS-1$
			if (prereq.getOperator().equals(PrerequisiteOperator.EQ)
				|| prereq.getOperator().equals(PrerequisiteOperator.GTEQ))
			{
				runningTotal =
						(charDeity.equalsIgnoreCase(prereq.getKey())) ? 1 : 0;
			}
			else if (prereq.getOperator().equals(PrerequisiteOperator.NEQ)
				|| prereq.getOperator().equals(PrerequisiteOperator.LT))
			{
				runningTotal =
						(charDeity.equalsIgnoreCase(prereq.getKey())) ? 0 : 1;
			}
			else
			{
				throw new PrerequisiteException(LanguageBundle
					.getFormattedString(
						"PreDeity.error.bad_coparator", prereq.toString())); //$NON-NLS-1$
			}
		}

		return countedTotal(prereq, runningTotal);
	}

	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
    @Override
	public String kindHandled()
	{
		return "DEITY"; //$NON-NLS-1$
	}

}
