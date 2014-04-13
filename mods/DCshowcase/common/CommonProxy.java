package mods.DCshowcase.common;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.EdibleTest.common.TileEntityEdible;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CommonProxy{
	

	public World getClientWorld() {
		
		return null;
	}
	
	public void registerTileEntity()
    {
		GameRegistry.registerTileEntity(TileShowCase.class, "TileShowCase");
    }
	
	public int getRenderID()
	{
		return RenderingRegistry.getNextAvailableRenderId();
	}
	
	public void registerRenderers()
	{
		
	}

}
