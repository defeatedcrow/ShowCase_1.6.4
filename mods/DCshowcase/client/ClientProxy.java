package mods.DCshowcase.client;

import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.DCshowcase.common.CommonProxy;
import mods.DCshowcase.common.TileShowCase;

public class ClientProxy extends CommonProxy{
	
	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}
	
	@Override
	public void registerTileEntity()
    {
		ClientRegistry.registerTileEntity(TileShowCase.class, "TileShowCase", new RenderShowCase());
    }
	
	@Override
	public int getRenderID()
	{
		return RenderingRegistry.getNextAvailableRenderId();
	}
	
	@Override
	public void registerRenderers()
	{
		RenderingRegistry.registerBlockHandler(new RenderBlockDummy());
	}

}
