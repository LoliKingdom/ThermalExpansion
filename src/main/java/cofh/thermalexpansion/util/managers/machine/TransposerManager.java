package cofh.thermalexpansion.util.managers.machine;

import cofh.core.init.CoreProps;
import cofh.core.inventory.ComparableItemStack;
import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.core.inventory.OreValidator;
import cofh.core.util.ItemWrapper;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermalexpansion.block.storage.BlockCell;
import cofh.thermalexpansion.item.ItemFrame;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import zone.rong.zairyou.api.fluid.FluidType;
import zone.rong.zairyou.api.material.Material;
import zone.rong.zairyou.api.material.type.ItemMaterialType;
import zone.rong.zairyou.objects.Materials;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.Arrays.asList;

public class TransposerManager {

	private static Map<List<Integer>, TransposerRecipe> recipeMapFill = new Object2ObjectOpenHashMap<>();
	private static Map<ComparableItemStackValidatedNBT, TransposerRecipe> recipeMapExtract = new Object2ObjectOpenHashMap<>();
	private static Map<ItemWrapper, ContainerOverride> containerOverrides = new Object2ObjectOpenHashMap<>();
	private static Set<ComparableItemStackValidatedNBT> validationSet = new ObjectOpenHashSet<>();
	private static final OreValidator oreValidator = new OreValidator();

	static {
		oreValidator.addPrefix(ComparableItemStack.BLOCK);
		oreValidator.addPrefix(ComparableItemStack.ORE);
		oreValidator.addPrefix(ComparableItemStack.DUST);
		oreValidator.addPrefix(ComparableItemStack.INGOT);
		oreValidator.addPrefix(ComparableItemStack.NUGGET);
		oreValidator.addPrefix(ComparableItemStack.GEM);
		oreValidator.addPrefix("seed");
		oreValidator.addPrefix("crop");
	}

	public static final int DEFAULT_ENERGY = 400;

	public static TransposerRecipe getFillRecipe(ItemStack input, FluidStack fluid) {

		return input.isEmpty() || fluid == null ? null : recipeMapFill.get(asList(convertInput(input).hashCode(), FluidHelper.getFluidHash(fluid)));
	}

	public static TransposerRecipe getExtractRecipe(ItemStack input) {

		return input.isEmpty() ? null : recipeMapExtract.get(convertInput(input));
	}

	public static ContainerOverride getContainerOverride(ItemStack input) {

		return input.isEmpty() ? null : containerOverrides.get(new ItemWrapper(input));
	}

	public static boolean fillRecipeExists(ItemStack input, FluidStack fluid) {

		return getFillRecipe(input, fluid) != null;
	}

	public static boolean extractRecipeExists(ItemStack input) {

		return getExtractRecipe(input) != null;
	}

	public static boolean containerOverrideExists(ItemStack input) {

		return getContainerOverride(input) != null;
	}

	public static TransposerRecipe[] getFillRecipeList() {

		return recipeMapFill.values().toArray(new TransposerRecipe[recipeMapFill.size()]);
	}

	public static TransposerRecipe[] getExtractRecipeList() {

		return recipeMapExtract.values().toArray(new TransposerRecipe[recipeMapExtract.size()]);
	}

	public static boolean isItemValid(ItemStack input) {

		return !input.isEmpty() && validationSet.contains(convertInput(input));
	}

	public static void preInit() {

		/* BUCKETS */
		{
			addContainerOverride(new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.BUCKET), 100);
			addContainerOverride(new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.BUCKET), 100);
			addContainerOverride(new ItemStack(Items.MILK_BUCKET), new ItemStack(Items.BUCKET), 100);
			addContainerOverride(new ItemStack(ForgeModContainer.getInstance().universalBucket), new ItemStack(Items.BUCKET), 100);
		}
	}

	public static void initialize() {

		/* BLOCKS */
		{
			addFillRecipe(4000, new ItemStack(Blocks.COBBLESTONE), new ItemStack(Blocks.MOSSY_COBBLESTONE), new FluidStack(FluidRegistry.WATER, 250), false);
			addFillRecipe(4000, new ItemStack(Blocks.STONEBRICK), new ItemStack(Blocks.STONEBRICK, 1, 1), new FluidStack(FluidRegistry.WATER, 250), false);
			// addFillRecipe(4000, new ItemStack(Blocks.SANDSTONE), new ItemStack(Blocks.END_STONE), new FluidStack(TFFluids.fluidEnder, 250), false);
			// addFillRecipe(4000, new ItemStack(Items.BRICK), new ItemStack(Items.NETHERBRICK), new FluidStack(FluidRegistry.LAVA, 250), false);

			addExtractRecipe(2400, new ItemStack(Blocks.CACTUS), ItemStack.EMPTY, new FluidStack(FluidRegistry.WATER, 500), 0, false);
			addExtractRecipe(2400, new ItemStack(Blocks.REEDS), new ItemStack(Items.SUGAR, 2), new FluidStack(FluidRegistry.WATER, 250), 0, false);
		}

		/* CONCRETE */
		{
			FluidStack water = new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);

			for (int i = 0; i < 16; i++) {
				addFillRecipe(DEFAULT_ENERGY, new ItemStack(Blocks.CONCRETE_POWDER, 1, i), new ItemStack(Blocks.CONCRETE, 1, i), water, false);
			}
		}

		/* CRYOTHEUM */
		{
			int energy = 2000;

			FluidStack cryoFluid = Materials.CRYOTHEUM.getStack(FluidType.MOLTEN, 200);

			addFillRecipe(energy, Materials.REDSTONE.getItem(ItemMaterialType.CRYSTAL, false), new ItemStack(Items.REDSTONE, 2), cryoFluid, false);
			addFillRecipe(energy, Materials.GLOWSTONE.getItem(ItemMaterialType.CRYSTAL, false), new ItemStack(Items.GLOWSTONE_DUST), cryoFluid, false);
			addFillRecipe(energy, Materials.ENDER_PEARL.getItem(ItemMaterialType.CRYSTAL, false), new ItemStack(Items.ENDER_PEARL), cryoFluid, false);

			energy = 400;

			addFillRecipe(energy, new ItemStack(Blocks.ICE), new ItemStack(Blocks.PACKED_ICE), cryoFluid, false);

			ItemStack cryoDust = Materials.CRYOTHEUM.getItem(ItemMaterialType.DUST, false);

			addFillRecipe(energy, cryoDust, new ItemStack(Blocks.ICE), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME), false);
			addFillRecipe(energy, cryoDust, new ItemStack(Items.REDSTONE, 10), Materials.REDSTONE.getStack(FluidType.MOLTEN, Fluid.BUCKET_VOLUME), false);
			addFillRecipe(energy, cryoDust, new ItemStack(Items.GLOWSTONE_DUST, 4), Materials.GLOWSTONE.getStack(FluidType.MOLTEN, Fluid.BUCKET_VOLUME), false);
			addFillRecipe(energy, cryoDust, new ItemStack(Items.ENDER_PEARL, 4), Materials.ENDER_PEARL.getStack(FluidType.MOLTEN, Fluid.BUCKET_VOLUME), false);
		}

		/* ELEMENTAL */
		{
			FluidStack expFluid = Materials.CRYOTHEUM.getStack(FluidType.MOLTEN, 200);

			addFillRecipe(16000, Materials.SULFUR.getStack(ItemMaterialType.DUST, 2), new ItemStack(Items.BLAZE_POWDER), expFluid, false);
			addFillRecipe(16000, ItemHelper.cloneStack(Items.SNOWBALL, 2), Materials.BLIZZ.getItem(ItemMaterialType.DUST, false), expFluid, false);
			addFillRecipe(16000, Materials.NITER.getStack(ItemMaterialType.DUST, 2), Materials.BLITZ.getItem(ItemMaterialType.DUST, false), expFluid, false);
			addFillRecipe(16000, Materials.OBSIDIAN.getStack(ItemMaterialType.DUST, 2), Materials.BASALZ.getItem(ItemMaterialType.DUST, false), expFluid, false);

			if (FluidRegistry.isFluidRegistered(CoreProps.ESSENCE)) {
				expFluid = FluidRegistry.getFluidStack(CoreProps.ESSENCE, 200);

				addFillRecipe(16000, Materials.SULFUR.getStack(ItemMaterialType.DUST, 2), new ItemStack(Items.BLAZE_POWDER), expFluid, false);
				addFillRecipe(16000, ItemHelper.cloneStack(Items.SNOWBALL, 2), Materials.BLIZZ.getItem(ItemMaterialType.DUST, false), expFluid, false);
				addFillRecipe(16000, Materials.NITER.getStack(ItemMaterialType.DUST, 2), Materials.BLITZ.getItem(ItemMaterialType.DUST, false), expFluid, false);
				addFillRecipe(16000, Materials.OBSIDIAN.getStack(ItemMaterialType.DUST, 2), Materials.BASALZ.getItem(ItemMaterialType.DUST, false), expFluid, false);
			}
			if (FluidRegistry.isFluidRegistered(CoreProps.XPJUICE)) {
				expFluid = FluidRegistry.getFluidStack(CoreProps.XPJUICE, 200);

				addFillRecipe(16000, Materials.SULFUR.getStack(ItemMaterialType.DUST, 2), new ItemStack(Items.BLAZE_POWDER), expFluid, false);
				addFillRecipe(16000, ItemHelper.cloneStack(Items.SNOWBALL, 2), Materials.BLIZZ.getItem(ItemMaterialType.DUST, false), expFluid, false);
				addFillRecipe(16000, Materials.NITER.getStack(ItemMaterialType.DUST, 2), Materials.BLITZ.getItem(ItemMaterialType.DUST, false), expFluid, false);
				addFillRecipe(16000, Materials.OBSIDIAN.getStack(ItemMaterialType.DUST, 2), Materials.BASALZ.getItem(ItemMaterialType.DUST, false), expFluid, false);
			}
		}

		/* CELLS */
		{
			FluidStack redstoneFluid = Materials.REDSTONE.getStack(FluidType.MOLTEN, Fluid.BUCKET_VOLUME * 4);

			if (BlockCell.enableClassicRecipes) {
				addFillRecipe(16000, ItemFrame.frameCell2, ItemFrame.frameCell2Filled, redstoneFluid, false);
				addFillRecipe(16000, ItemFrame.frameCell3, ItemFrame.frameCell3Filled, redstoneFluid, false);
				addFillRecipe(16000, ItemFrame.frameCell4, ItemFrame.frameCell4Filled, redstoneFluid, false);
			}
		}
		addFillRecipe(400, new ItemStack(Blocks.SPONGE, 1, 0), new ItemStack(Blocks.SPONGE, 1, 1), new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME), true);
		addFillRecipe(400, new ItemStack(Items.BOWL), new ItemStack(Items.MUSHROOM_STEW), Materials.MUSHROOM_STEW.getStack(FluidType.LIQUID, 250), true);
		addFillRecipe(400, new ItemStack(Items.GLASS_BOTTLE), new ItemStack(Items.EXPERIENCE_BOTTLE), Materials.EXPERIENCE.getStack(FluidType.LIQUID, 250), false);

		addFillRecipe(1600, Material.BASIC.getItem(ItemMaterialType.FERTILIZER, false), Materials.RICH.getItem(ItemMaterialType.FERTILIZER, false), Materials.SAP.getStack(FluidType.LIQUID, 200), false);
		// TODO
		// addFillRecipe(800, ItemMaterial.dustBiomass, ItemMaterial.dustBiomassRich, Materials.SEED_OIL.getStack(FluidType.LIQUID, 100), false);
		// addFillRecipe(800, ItemMaterial.dustBioblend, ItemMaterial.dustBioblendRich, Materials.SEED_OIL.getStack(FluidType.LIQUID, 100), false);

		/* LOAD POTIONS */
		loadPotions();

		/* LOAD RECIPES */
		loadRecipes();
	}

	public static void loadPotions() {

		for (PotionType type : PotionType.REGISTRY) {

			if (type != PotionTypes.EMPTY) {
				addDefaultPotionRecipes(type);
			}
		}
	}

	public static void loadRecipes() {

		FluidStack cryoStack = Materials.CRYOTHEUM.getStack(FluidType.MOLTEN, 200);

		addFillRecipe(2000, ItemHelper.getOre("oreCinnabar"), Materials.CINNABAR.getStack(ItemMaterialType.CRYSTAL, 4), cryoStack, false);

		if (FluidRegistry.isFluidRegistered(CoreProps.ESSENCE)) {
			addFillRecipe(400, new ItemStack(Items.GLASS_BOTTLE), new ItemStack(Items.EXPERIENCE_BOTTLE), new FluidStack(FluidRegistry.getFluid(CoreProps.ESSENCE), 250), false);
		}
		if (FluidRegistry.isFluidRegistered(CoreProps.XPJUICE)) {
			addFillRecipe(400, new ItemStack(Items.GLASS_BOTTLE), new ItemStack(Items.EXPERIENCE_BOTTLE), new FluidStack(FluidRegistry.getFluid(CoreProps.XPJUICE), 250), false);
		}
		addDefaultSeedOilRecipes();
	}

	public static void refresh() {

		Map<List<Integer>, TransposerRecipe> tempFill = new Object2ObjectOpenHashMap<>(recipeMapFill.size());
		Map<ComparableItemStackValidatedNBT, TransposerRecipe> tempExtract = new Object2ObjectOpenHashMap<>(recipeMapExtract.size());
		Map<ItemWrapper, ContainerOverride> tempOverrides = new Object2ObjectOpenHashMap<>(containerOverrides.size());
		Set<ComparableItemStackValidatedNBT> tempSet = new ObjectOpenHashSet<>();
		TransposerRecipe tempRecipe;
		ContainerOverride tempOverride;

		for (Entry<List<Integer>, TransposerRecipe> entry : recipeMapFill.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackValidatedNBT input = convertInput(tempRecipe.input);
			tempFill.put(asList(input.hashCode(), FluidHelper.getFluidHash(tempRecipe.fluid)), tempRecipe);
			tempSet.add(input);
		}
		for (Entry<ComparableItemStackValidatedNBT, TransposerRecipe> entry : recipeMapExtract.entrySet()) {
			tempRecipe = entry.getValue();
			ComparableItemStackValidatedNBT input = convertInput(tempRecipe.input);
			tempExtract.put(input, tempRecipe);
			tempSet.add(input);
		}
		for (Entry<ItemWrapper, ContainerOverride> entry : containerOverrides.entrySet()) {
			tempOverride = entry.getValue();
			ItemWrapper input = new ItemWrapper(tempOverride.input);
			tempOverrides.put(input, tempOverride);
		}
		recipeMapFill.clear();
		recipeMapExtract.clear();

		recipeMapFill = tempFill;
		recipeMapExtract = tempExtract;

		validationSet.clear();
		validationSet = tempSet;

		containerOverrides.clear();
		containerOverrides = tempOverrides;
	}

	/* ADD RECIPES */
	public static TransposerRecipe addFillRecipe(int energy, ItemStack input, ItemStack output, FluidStack fluid, boolean reversible) {

		if (input.isEmpty() || output.isEmpty() || fluid == null || fluid.amount <= 0 || energy <= 0) {
			return null;
		}
		if (fillRecipeExists(input, fluid)) {
			return null;
		}
		TransposerRecipe recipeFill = new TransposerRecipe(input, output, fluid, energy, 100);
		recipeMapFill.put(asList(convertInput(input).hashCode(), FluidHelper.getFluidHash(fluid)), recipeFill);
		validationSet.add(convertInput(input));

		if (reversible) {
			addExtractRecipe(energy, output, input, fluid, 100, false);
		}
		return recipeFill;
	}

	public static TransposerRecipe addExtractRecipe(int energy, ItemStack input, ItemStack output, FluidStack fluid, int chance, boolean reversible) {

		if (input.isEmpty() || fluid == null || fluid.amount <= 0 || energy <= 0) {
			return null;
		}
		if (extractRecipeExists(input)) {
			return null;
		}
		if (output.isEmpty() && reversible || output.isEmpty() && chance != 0) {
			return null;
		}
		TransposerRecipe recipeExtraction = new TransposerRecipe(input, output, fluid, energy, chance);
		recipeMapExtract.put(convertInput(input), recipeExtraction);
		validationSet.add(convertInput(input));

		if (reversible) {
			addFillRecipe(energy, output, input, fluid, false);
		}
		return recipeExtraction;
	}

	/* REMOVE RECIPES */
	public static TransposerRecipe removeFillRecipe(ItemStack input, FluidStack fluid) {

		return recipeMapFill.remove(asList(convertInput(input).hashCode(), FluidHelper.getFluidHash(fluid)));
	}

	public static TransposerRecipe removeExtractRecipe(ItemStack input) {

		return recipeMapExtract.remove(convertInput(input));
	}

	/* HELPERS */
	public static ComparableItemStackValidatedNBT convertInput(ItemStack stack) {

		return new ComparableItemStackValidatedNBT(stack, oreValidator);
	}

	public static ContainerOverride addContainerOverride(ItemStack input, ItemStack output, int chance) {

		if (input.isEmpty() || output.isEmpty() || chance <= 0) {
			return null;
		}
		if (containerOverrideExists(input)) {
			return null;
		}
		ContainerOverride override = new ContainerOverride(input, output, chance);
		containerOverrides.put(new ItemWrapper(input), override);

		return override;
	}

	public static void addDefaultPotionRecipes(PotionType type) {

		addFillRecipe(DEFAULT_ENERGY * 2, new ItemStack(Items.GLASS_BOTTLE), PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM, 1), type), Materials.Potions.getNormalFluid(type, CoreProps.BOTTLE_VOLUME), true);
		addFillRecipe(DEFAULT_ENERGY * 2, new ItemStack(Items.GLASS_BOTTLE), PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION, 1), type), Materials.Potions.getSplashFluid(type, CoreProps.BOTTLE_VOLUME), true);
		addFillRecipe(DEFAULT_ENERGY * 2, new ItemStack(Items.GLASS_BOTTLE), PotionUtils.addPotionToItemStack(new ItemStack(Items.LINGERING_POTION, 1), type), Materials.Potions.getLingeringFluid(type, CoreProps.BOTTLE_VOLUME), true);
		addFillRecipe(DEFAULT_ENERGY, new ItemStack(Items.ARROW), PotionUtils.addPotionToItemStack(new ItemStack(Items.TIPPED_ARROW), type),  Materials.Potions.getLingeringFluid(type, CoreProps.BOTTLE_VOLUME / 10), false);
	}

	public static void addDefaultSeedOilRecipes() {

		for (String name : OreDictionary.getOreNames()) {
			if (name.startsWith("seed") && !name.startsWith("seeds")) {
				List<ItemStack> seed = OreDictionary.getOres(name, false);

				if (seed.isEmpty()) {
					continue;
				}
				TransposerManager.addExtractRecipe(1600, ItemHelper.cloneStack(seed.get(0), 1), ItemStack.EMPTY, Materials.SEED_OIL.getStack(FluidType.LIQUID, 50), 0, false);
			}
		}
	}

	/* RECIPE CLASS */
	public static class TransposerRecipe {

		final ItemStack input;
		final ItemStack output;
		final FluidStack fluid;
		final int energy;
		final int chance;

		public TransposerRecipe(ItemStack input, ItemStack output, FluidStack fluid, int energy, int chance) {

			this.input = input;
			this.output = output;
			this.fluid = fluid;
			this.energy = energy;
			this.chance = chance;
		}

		public ItemStack getInput() {

			return input;
		}

		public ItemStack getOutput() {

			return output;
		}

		public FluidStack getFluid() {

			return fluid;
		}

		public int getEnergy() {

			return energy;
		}

		public int getChance() {

			return chance;
		}

	}

	/* CONTAINER OVERRIDE CLASS */
	public static class ContainerOverride {

		final ItemStack input;
		final ItemStack output;
		final int chance;

		public ContainerOverride(ItemStack input, ItemStack output, int chance) {

			this.input = input;
			this.output = output;
			this.chance = chance;
		}

		public ItemStack getInput() {

			return input;
		}

		public ItemStack getOutput() {

			return output;
		}

		public int getChance() {

			return chance;
		}

	}

}
