package cofh.thermalexpansion.init;

import cofh.core.util.core.IInitializer;
import cofh.thermalexpansion.plugins.*;
import cofh.thermalexpansion.plugins.forestry.*;
import cofh.thermalexpansion.plugins.top.PluginTOP;

import java.util.ArrayList;

public class TEPlugins {

	private TEPlugins() {

	}

	public static void preInit() {

		pluginTOP = new PluginTOP();

		pluginForestry = new PluginForestry();
		pluginExtraBees = new PluginExtraBees();
		pluginExtraTrees = new PluginExtraTrees();
		pluginGendustry = new PluginGendustry();
		pluginMagicBees = new PluginMagicBees();

		pluginAppliedEnergistics2 = new PluginAppliedEnergistics2();
		pluginAstralSorcery = new PluginAstralSorcery();
		pluginBetweenlands = new PluginBetweenlands();
		pluginChisel = new PluginChisel();
		pluginComputronics = new PluginComputronics();
		pluginEnderIO = new PluginEnderIO();
		pluginEvilCraft = new PluginEvilCraft();
		pluginExtraAlchemy = new PluginExtraAlchemy();
		pluginIC2 = new PluginIC2();
		pluginImmersiveEngineering = new PluginImmersiveEngineering();
		pluginIntegratedDynamics = new PluginIntegratedDynamics();
		pluginMowziesMobs = new PluginMowziesMobs();
		pluginMysticalAgriculture = new PluginMysticalAgriculture();
		pluginMysticalWildlife = new PluginMysticalWildlife();
		pluginPlants = new PluginPlants();
		pluginPrimalCore = new PluginPrimalCore();
		pluginRustic = new PluginRustic();
		pluginTConstruct = new PluginTConstruct();
		pluginThaumcraft = new PluginThaumcraft();
		pluginTwilightForest = new PluginTwilightForest();

		initList.add(pluginTOP);

		initList.add(pluginForestry);
		initList.add(pluginExtraBees);
		initList.add(pluginExtraTrees);
		initList.add(pluginGendustry);
		initList.add(pluginMagicBees);

		initList.add(pluginAppliedEnergistics2);
		initList.add(pluginAstralSorcery);
		initList.add(pluginBetweenlands);
		initList.add(pluginChisel);
		initList.add(pluginComputronics);
		initList.add(pluginEnderIO);
		initList.add(pluginEvilCraft);
		initList.add(pluginExtraAlchemy);
		initList.add(pluginIC2);
		initList.add(pluginImmersiveEngineering);
		initList.add(pluginIntegratedDynamics);
		initList.add(pluginMowziesMobs);
		initList.add(pluginMysticalAgriculture);
		initList.add(pluginMysticalWildlife);
		initList.add(pluginPlants);
		initList.add(pluginPrimalCore);
		initList.add(pluginRustic);
		initList.add(pluginTConstruct);
		initList.add(pluginThaumcraft);
		initList.add(pluginTwilightForest);

		for (IInitializer init : initList) {
			init.preInit();
		}
	}

	public static void initialize() {

		for (IInitializer init : initList) {
			init.initialize();
		}
	}

	private static ArrayList<IInitializer> initList = new ArrayList<>();

	/* REFERENCES */
	private static PluginTOP pluginTOP;

	private static PluginForestry pluginForestry;
	private static PluginExtraBees pluginExtraBees;
	private static PluginExtraTrees pluginExtraTrees;
	private static PluginGendustry pluginGendustry;
	private static PluginMagicBees pluginMagicBees;

	private static PluginAppliedEnergistics2 pluginAppliedEnergistics2;
	private static PluginAstralSorcery pluginAstralSorcery;
	private static PluginBetweenlands pluginBetweenlands;
	private static PluginChisel pluginChisel;
	private static PluginComputronics pluginComputronics;
	private static PluginEnderIO pluginEnderIO;
	private static PluginEvilCraft pluginEvilCraft;
	private static PluginExtraAlchemy pluginExtraAlchemy;
	private static PluginIC2 pluginIC2;
	private static PluginImmersiveEngineering pluginImmersiveEngineering;
	private static PluginIntegratedDynamics pluginIntegratedDynamics;
	private static PluginMowziesMobs pluginMowziesMobs;
	private static PluginMysticalAgriculture pluginMysticalAgriculture;
	private static PluginMysticalWildlife pluginMysticalWildlife;
	private static PluginPlants pluginPlants;
	private static PluginPrimalCore pluginPrimalCore;
	private static PluginRustic pluginRustic;
	private static PluginTConstruct pluginTConstruct;
	private static PluginThaumcraft pluginThaumcraft;
	private static PluginTwilightForest pluginTwilightForest;

}
