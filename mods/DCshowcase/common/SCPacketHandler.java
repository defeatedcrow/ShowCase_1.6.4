package mods.DCshowcase.common;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
 

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
 


import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
 


import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
 
public class SCPacketHandler implements IPacketHandler {
 
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
		if (packet.channel.equals("tileShowCase")) {
			ByteArrayDataInput data = ByteStreams.newDataInput(packet.data);
			ItemStack items = null;
 
			try
			{
				int x = data.readInt();
				int y = data.readInt();
				int z = data.readInt();
				
				boolean hasStacks = data.readBoolean();
				if (hasStacks)
				{
					items = packet.readItemStack(data);
				}
				
				short Price = data.readShort();
				byte Mode = data.readByte();
				String Seller = data.readLine();
				String Owner = data.readLine();
				
				
				TileEntity tileentity = null;
				 
				World worldClient = FMLClientHandler.instance().getClient().theWorld;
				World worldServer = ((EntityPlayer) player).worldObj;
	 
				if (worldClient != null && worldServer == null) {
					tileentity = worldClient.getBlockTileEntity(x, y, z);
				}
				if (worldServer != null) {
					tileentity = worldServer.getBlockTileEntity(x, y, z);
				}
	 
				if (tileentity != null)
				{
					if (tileentity instanceof TileShowCase)
					{
						TileShowCase tile = (TileShowCase)tileentity;
						tile.setPrice(Price);
						tile.setMode(Mode);
						tile.setSellerName(Seller);
						tile.setOwnerName(Owner);
						tile.setSellItem(items);
						
					}
				}
			}
			catch (IOException e){}
		}
	}
 
	public static Packet getPacket(TileShowCase tile)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
 
		int x = tile.xCoord;
		int y = tile.yCoord;
		int z = tile.zCoord;
		short Price = (short) tile.getPrice();
		byte Mode = (byte) tile.getMode();
		String Seller = tile.getSellerName();
		String Owner = tile.getOwner();
		boolean hasStacks = tile.hasSellItem();
		ItemStack items = tile.getSellItem();
 
		try
		{
			dos.writeInt(x);
			dos.writeInt(y);
			dos.writeInt(z);
			
			dos.writeBoolean(hasStacks);
			if (hasStacks)
			{
				Packet.writeItemStack(items, dos);
			}
 
			dos.writeShort(Price);
			dos.writeByte(Mode);
			dos.writeChars(Seller);
			dos.writeChars(Owner);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
 
		Packet250CustomPayload packet = new Packet250CustomPayload();
 
		packet.channel = "tileShowCase";
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		packet.isChunkDataPacket = true;
 
		return packet;
	}
 
}
