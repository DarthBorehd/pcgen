/*
 * DelegatingDataSet.java
 * Copyright 2014 Connor Petty <cpmeister@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on May 4, 2014, 5:35:52 PM
 */
package pcgen.gui2.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.facade.core.AbilityCategoryFacade;
import pcgen.facade.core.AbilityFacade;
import pcgen.facade.core.AlignmentFacade;
import pcgen.facade.core.BodyStructureFacade;
import pcgen.facade.core.CampaignFacade;
import pcgen.facade.core.ClassFacade;
import pcgen.facade.core.DataSetFacade;
import pcgen.facade.core.DeityFacade;
import pcgen.facade.core.EquipmentFacade;
import pcgen.facade.core.GameModeFacade;
import pcgen.facade.core.GearBuySellFacade;
import pcgen.facade.core.KitFacade;
import pcgen.facade.core.RaceFacade;
import pcgen.facade.core.SizeAdjustmentFacade;
import pcgen.facade.core.SkillFacade;
import pcgen.facade.core.StatFacade;
import pcgen.facade.core.TemplateFacade;
import pcgen.facade.util.event.MapEvent;
import pcgen.facade.util.event.MapListener;
import pcgen.facade.core.generator.StatGenerationFacade;
import pcgen.facade.util.AbstractMapFacade;
import pcgen.facade.util.DelegatingListFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.MapFacade;

/**
 * This class implements a <code>DataSetFacade</code> by delegating to another
 * DataSetFacade. This class is the DataSetFacade returned by the
 * <code>CharacterFacadeImpl</code> and is necessary to protect outside event
 * listeners from directly listening to the real DataSetFacade. By adding this
 * delegate layer, upon closing a character, the CFI can sever connections
 * between the DelegatingDataSet and the actual DataSetFacade thus preventing an
 * memory leaks that could occur from an outside event listener.
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class DelegatingDataSet implements DataSetFacade
{

	private final DelegatingListFacade<RaceFacade> races;
	private final DelegatingListFacade<ClassFacade> classes;
	private final DelegatingListFacade<DeityFacade> deities;
	private final DelegatingListFacade<SkillFacade> skills;
	private final DelegatingListFacade<TemplateFacade> templates;
	private final DelegatingListFacade<AlignmentFacade> alignments;
	private final DelegatingListFacade<KitFacade> kits;
	private final DelegatingListFacade<StatFacade> stats;
	private final DelegatingAbilitiesMap abilities;
	private final DelegatingListFacade<CampaignFacade> campaigns;
	private final DelegatingListFacade<BodyStructureFacade> bodyStructures;
	private final DelegatingListFacade<EquipmentFacade> equipment;
	private final DelegatingListFacade<String> xpTableNames;
	private final DelegatingListFacade<GearBuySellFacade> gearBuySellSchemes;
	private final DelegatingListFacade<String> characterTypes;
	private final DelegatingListFacade<SizeAdjustmentFacade> sizes;

	private final DataSetFacade delegate;

	public DelegatingDataSet(DataSetFacade delegate)
	{
		this.delegate = delegate;
		this.abilities = new DelegatingAbilitiesMap(delegate.getAbilities());
		this.races = new DelegatingListFacade<RaceFacade>(delegate.getRaces());
		this.classes = new DelegatingListFacade<ClassFacade>(delegate.getClasses());
		this.deities = new DelegatingListFacade<DeityFacade>(delegate.getDeities());
		this.skills = new DelegatingListFacade<SkillFacade>(delegate.getSkills());
		this.templates = new DelegatingListFacade<TemplateFacade>(delegate.getTemplates());
		this.alignments = new DelegatingListFacade<AlignmentFacade>(delegate.getAlignments());
		this.kits = new DelegatingListFacade<KitFacade>(delegate.getKits());
		this.stats = new DelegatingListFacade<StatFacade>(delegate.getStats());
		this.campaigns = new DelegatingListFacade<CampaignFacade>(delegate.getCampaigns());
		this.bodyStructures = new DelegatingListFacade<BodyStructureFacade>(delegate.getEquipmentLocations());
		this.equipment = new DelegatingListFacade<EquipmentFacade>(delegate.getEquipment());
		this.xpTableNames = new DelegatingListFacade<String>(delegate.getXPTableNames());
		this.gearBuySellSchemes = new DelegatingListFacade<GearBuySellFacade>(delegate.getGearBuySellSchemes());
		this.characterTypes = new DelegatingListFacade<String>(delegate.getCharacterTypes());
		this.sizes = new DelegatingListFacade<SizeAdjustmentFacade>(delegate.getSizes());

	}

	public void detachDelegates()
	{
		abilities.detach();
		races.setDelegate(null);
		classes.setDelegate(null);
		deities.setDelegate(null);
		skills.setDelegate(null);
		templates.setDelegate(null);
		alignments.setDelegate(null);
		kits.setDelegate(null);
		stats.setDelegate(null);
		campaigns.setDelegate(null);
		bodyStructures.setDelegate(null);
		equipment.setDelegate(null);
		xpTableNames.setDelegate(null);
		gearBuySellSchemes.setDelegate(null);
		characterTypes.setDelegate(null);
		sizes.setDelegate(null);
	}

	@Override
	public MapFacade<AbilityCategoryFacade, ListFacade<AbilityFacade>> getAbilities()
	{
		return abilities;
	}

	private class DelegatingAbilitiesMap extends AbstractMapFacade<AbilityCategoryFacade, ListFacade<AbilityFacade>>
			implements MapListener<AbilityCategoryFacade, ListFacade<AbilityFacade>>
	{

		private final Map<AbilityCategoryFacade, DelegatingListFacade<AbilityFacade>> abilitiesMap;

		private final MapFacade<AbilityCategoryFacade, ListFacade<AbilityFacade>> abilitiesDelegate;

		public DelegatingAbilitiesMap(MapFacade<AbilityCategoryFacade, ListFacade<AbilityFacade>> abilitiesDelegate)
		{
			this.abilitiesDelegate = abilitiesDelegate;
			this.abilitiesMap = new HashMap<AbilityCategoryFacade, DelegatingListFacade<AbilityFacade>>();
			populateMap();
			abilitiesDelegate.addMapListener(this);
		}

		public void detach()
		{
			abilitiesDelegate.removeMapListener(this);
			for (DelegatingListFacade<AbilityFacade> list : abilitiesMap.values())
			{
				list.setDelegate(null);
			}
		}

		@Override
		public Set<AbilityCategoryFacade> getKeys()
		{
			return abilitiesDelegate.getKeys();
		}

		@Override
		public ListFacade<AbilityFacade> getValue(AbilityCategoryFacade key)
		{
			return abilitiesMap.get(key);
		}

		@Override
		public void keyAdded(MapEvent<AbilityCategoryFacade, ListFacade<AbilityFacade>> e)
		{
			AbilityCategoryFacade key = e.getKey();
			DelegatingListFacade<AbilityFacade> newValue = new DelegatingListFacade<AbilityFacade>(e.getNewValue());
			abilitiesMap.put(key, newValue);
			fireKeyAdded(this, key, newValue);
		}

		@Override
		public void keyRemoved(MapEvent<AbilityCategoryFacade, ListFacade<AbilityFacade>> e)
		{
			AbilityCategoryFacade key = e.getKey();
			DelegatingListFacade<AbilityFacade> oldValue = abilitiesMap.remove(key);
			fireKeyRemoved(this, key, oldValue);
			oldValue.setDelegate(null);
		}

		@Override
		public void keyModified(MapEvent<AbilityCategoryFacade, ListFacade<AbilityFacade>> e)
		{
			fireKeyModified(this, e.getKey(), abilitiesMap.get(e.getKey()), e);
		}

		@Override
		public void valueChanged(MapEvent<AbilityCategoryFacade, ListFacade<AbilityFacade>> e)
		{
			AbilityCategoryFacade key = e.getKey();
			DelegatingListFacade<AbilityFacade> oldValue = abilitiesMap.get(key);
			DelegatingListFacade<AbilityFacade> newValue = new DelegatingListFacade<AbilityFacade>(e.getNewValue());
			abilitiesMap.put(key, newValue);
			fireValueChanged(this, key, oldValue, newValue);
			oldValue.setDelegate(null);
		}

		@Override
		public void valueModified(MapEvent<AbilityCategoryFacade, ListFacade<AbilityFacade>> e)
		{
			fireValueModified(this, e.getKey(), abilitiesMap.get(e.getKey()), e);
		}

		private void populateMap()
		{
			for (AbilityCategoryFacade key : abilitiesDelegate.getKeys())
			{
				DelegatingListFacade<AbilityFacade> value = new DelegatingListFacade<AbilityFacade>(abilitiesDelegate.getValue(key));
				abilitiesMap.put(key, value);
			}
		}

		@Override
		public void keysChanged(MapEvent<AbilityCategoryFacade, ListFacade<AbilityFacade>> e)
		{
			ArrayList<DelegatingListFacade<AbilityFacade>> deadLists = new ArrayList<DelegatingListFacade<AbilityFacade>>(abilitiesMap.values());
			abilitiesMap.clear();
			populateMap();
			for (DelegatingListFacade<AbilityFacade> delegatingListFacade : deadLists)
			{
				delegatingListFacade.setDelegate(null);
			}
		}

	}

	@Override
	public List<AbilityFacade> getPrereqAbilities(AbilityFacade abilityFacade)
	{
		return delegate.getPrereqAbilities(abilityFacade);
	}

	@Override
	public ListFacade<SkillFacade> getSkills()
	{
		return skills;
	}

	@Override
	public ListFacade<RaceFacade> getRaces()
	{
		return races;
	}

	@Override
	public ListFacade<ClassFacade> getClasses()
	{
		return classes;
	}

	@Override
	public ListFacade<DeityFacade> getDeities()
	{
		return deities;
	}

	@Override
	public ListFacade<TemplateFacade> getTemplates()
	{
		return templates;
	}

	@Override
	public ListFacade<CampaignFacade> getCampaigns()
	{
		return campaigns;
	}

	@Override
	public GameModeFacade getGameMode()
	{
		return delegate.getGameMode();
	}

	@Override
	public ListFacade<AlignmentFacade> getAlignments()
	{
		return alignments;
	}

	@Override
	public ListFacade<StatFacade> getStats()
	{
		return stats;
	}

	@Override
	public ListFacade<StatGenerationFacade> getStatGenerators()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public SkillFacade getSpeakLanguageSkill()
	{
		return delegate.getSpeakLanguageSkill();
	}

	@Override
	public ListFacade<EquipmentFacade> getEquipment()
	{
		return equipment;
	}

	@Override
	public void addEquipment(EquipmentFacade equip)
	{
		delegate.addEquipment(equip);
	}

	@Override
	public ListFacade<BodyStructureFacade> getEquipmentLocations()
	{
		return bodyStructures;
	}

	@Override
	public ListFacade<String> getXPTableNames()
	{
		return xpTableNames;
	}

	@Override
	public ListFacade<String> getCharacterTypes()
	{
		return characterTypes;
	}

	@Override
	public ListFacade<GearBuySellFacade> getGearBuySellSchemes()
	{
		return gearBuySellSchemes;
	}

	@Override
	public ListFacade<KitFacade> getKits()
	{
		return kits;
	}

	@Override
	public ListFacade<SizeAdjustmentFacade> getSizes()
	{
		return sizes;
	}

	@Override
	public void refreshEquipment()
	{
		delegate.refreshEquipment();
	}

}
