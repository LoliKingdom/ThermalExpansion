package thermalexpansion.block.ender;

import cofh.api.core.ISecurable;
import cofh.api.tileentity.IRedstoneControl.ControlMode;
import cofh.render.IconRegistry;
import cofh.util.ServerHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.ShapedOreRecipe;

import thermalexpansion.ThermalExpansion;
import thermalexpansion.block.BlockTEBase;
import thermalexpansion.block.simple.BlockFrame;
import thermalexpansion.core.TEProps;

public class BlockEnder extends BlockTEBase {

	public BlockEnder() {

		super(Material.iron);
		setHardness(15.0F);
		setResistance(2000.0F);
		setBlockName("thermalexpansion.ender");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {

		return new TileTesseract();
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {

		list.add(new ItemStack(item, 1, 0));
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack) {

		if (ServerHelper.isServerWorld(world) && stack.stackTagCompound != null && stack.stackTagCompound.hasKey("Owner")) {
			TileTesseract tile = (TileTesseract) world.getTileEntity(x, y, z);
			tile.removeFromRegistry();

			tile.setOwnerName(stack.stackTagCompound.getString("Owner"));
			tile.setAccess(ISecurable.AccessMode.values()[stack.stackTagCompound.getByte("Access")]);

			tile.modeItem = stack.stackTagCompound.getByte("ModeItems");
			tile.modeFluid = stack.stackTagCompound.getByte("ModeFluid");
			tile.modeEnergy = stack.stackTagCompound.getByte("ModeEnergy");

			tile.setControl(ControlMode.values()[stack.stackTagCompound.getByte("rsMode")]);

			tile.frequency = stack.stackTagCompound.getInteger("Frequency");
			tile.isActive = tile.frequency != -1;

			tile.addToRegistry();
			tile.sendDescPacket();
		}
		super.onBlockPlacedBy(world, x, y, z, living, stack);
	}

	@Override
	public int getRenderBlockPass() {

		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() {

		return TEProps.renderIdTesseract;
	}

	@Override
	public boolean canRenderInPass(int pass) {

		renderPass = pass;
		return pass < 2;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {

		return true;
	}

	@Override
	public IIcon getIcon(int side, int metadata) {

		return IconRegistry.getIcon("Tesseract");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {

		IconRegistry.addIcon("Tesseract", "thermalexpansion:tesseract/Tesseract", ir);
		IconRegistry.addIcon("TesseractInner", "thermalexpansion:tesseract/Tesseract_Inner", ir);
		IconRegistry.addIcon("TesseractActive", "thermalexpansion:tesseract/Tesseract_Active", ir);
		IconRegistry.addIcon("TesseractInnerActive", "thermalexpansion:tesseract/Tesseract_Inner_Active", ir);
		IconRegistry.addIcon("SkyEnder", "thermalexpansion:tesseract/Sky_Ender", ir);
	}

	@Override
	public NBTTagCompound getItemStackTag(World world, int x, int y, int z) {

		NBTTagCompound tag = super.getItemStackTag(world, x, y, z);
		TileTesseract tile = (TileTesseract) world.getTileEntity(x, y, z);

		if (tile != null) {
			if (tag == null) {
				tag = new NBTTagCompound();
			}
			tag.setString("Owner", tile.getOwnerName());
			tag.setInteger("Frequency", tile.frequency);

			tag.setByte("ModeItems", tile.modeItem);
			tag.setByte("ModeFluid", tile.modeFluid);
			tag.setByte("ModeEnergy", tile.modeEnergy);

			tag.setByte("Access", (byte) tile.getAccess().ordinal());
			tag.setByte("rsMode", (byte) tile.getControl().ordinal());
		}
		return tag;
	}

	/* IDismantleable */
	@Override
	public ItemStack dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnBlock) {

		TileTesseract tile = (TileTesseract) world.getTileEntity(x, y, z);
		if (tile != null) {
			tile.removeFromRegistry();
			tile.inventory = new ItemStack[0];
		}
		return super.dismantleBlock(player, getItemStackTag(world, x, y, z), world, x, y, z, returnBlock, false);
	}

	/* IInitializer */
	@Override
	public boolean initialize() {

		TileTesseract.initialize();

		tesseract = new ItemStack(this, 1, 0);

		return true;
	}

	@Override
	public boolean postInit() {

		if (enable) {
			GameRegistry.addRecipe(new ShapedOreRecipe(tesseract, new Object[] { "BIB", "ICI", "BIB", 'C', BlockFrame.frameTesseractFull, 'I', "ingotSilver",
					'B', "ingotBronze" }));
		}
		return true;
	}

	public static boolean enable;

	static {
		String category = "block.feature";
		enable = ThermalExpansion.config.get(category, "Tesseract.Enable", true);
	}

	public static ItemStack tesseract;

}