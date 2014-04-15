package mods.DCshowcase.common;

import java.util.logging.Level;

import mods.DCshowcase.common.*;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(
		modid = "DCsShowCase",
		name = "ShowCaseAddon",
		version = "1.6.2_1.0a",
		dependencies = "required-after:MCEconomy"
		)
@NetworkMod(
		clientSideRequired = true,
		serverSideRequired = false,
		channels = "tileShowCase", packetHandler = SCPacketHandler.class
		)

public class ShowCaseCore{
	
	@SidedProxy(clientSide = "mods.DCshowcase.client.ClientProxy", serverSide = "mods.DCshowcase.common.CommonProxy")
	public static CommonProxy proxy;
	
	@Instance("ShowCaseCore")
    public static ShowCaseCore instance;
	
	public static Block showCaseBlock;
	
	public int blockIdSCase = 800;
	
	public static String[] OPNames = new String[] {"Single"};
	
	public static String SELE_MESSAGE = "Purchased the item ";
	public static String CANCELL_MESSAGE = "Canceled the sale of the item ";
	public static String MP_GET_MESSAGE = " bought your item ";
	public static String PUT_MESSAGE = "Registered sele item ";
	
	public static int renderShowCase = -1;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
		try
		{
			cfg.load();
			Property blockShowCase = cfg.getBlock("ShowCaseBlock", blockIdSCase);
			
			blockIdSCase = blockShowCase.getInt();

		}
		catch (Exception e)
		{
			FMLLog.log(Level.SEVERE, e, "Error Message");

		}
		finally
		{
			cfg.save();
		}
		
		showCaseBlock = (new BlockShowCase(blockIdSCase)).
				setUnlocalizedName("defeatedcrow.showCase").
				setCreativeTab(CreativeTabs.tabDecorations);
		
		GameRegistry.registerBlock(showCaseBlock, "defeatedcrow.showCase");
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		//Registering rrice for sale
		(new RegisterMPForSale()).regster();
		
		//Registering new recipe
		GameRegistry.addRecipe(
	    		  new ItemStack(this.showCaseBlock, 1),
	    		  new Object[]{"XXX","XYX","ZZZ",
	    			  Character.valueOf('X'), Block.glass,
	    			  Character.valueOf('Y'), Block.pressurePlateIron,
	    			  Character.valueOf('Z'), Item.ingotIron
	    			  });
	      
		//TileEntityの登録
		proxy.registerTileEntity();
		
		//レンダー登録
		renderShowCase = proxy.getRenderID();
		proxy.registerRenderers();
		
	    //Registering language
		LanguageRegistry.addName(this.showCaseBlock, "Show Case");
		LanguageRegistry.instance().addNameForObject(this.showCaseBlock, "ja_JP", "ショーケース");
		
		LanguageRegistry.instance().addStringLocalization(SELE_MESSAGE, "Purchased the item ");
		LanguageRegistry.instance().addStringLocalization(SELE_MESSAGE, "ja_JP", "アイテムを購入しました。");
		
		LanguageRegistry.instance().addStringLocalization(CANCELL_MESSAGE, "Canceled the sale of the item ");
		LanguageRegistry.instance().addStringLocalization(CANCELL_MESSAGE, "ja_JP", "アイテムの売却を取り消しました。");
		
		LanguageRegistry.instance().addStringLocalization(MP_GET_MESSAGE, " bought your item ");
		LanguageRegistry.instance().addStringLocalization(MP_GET_MESSAGE, "ja_JP", " があなたのアイテムを購入しました。");
		
		LanguageRegistry.instance().addStringLocalization(PUT_MESSAGE, "Registered sele item ");
		LanguageRegistry.instance().addStringLocalization(PUT_MESSAGE, "ja_JP", "売却アイテムを登録しました。");
	}
	
}
