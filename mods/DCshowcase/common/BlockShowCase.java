package mods.DCshowcase.common;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mceconomy.api.MCEconomyAPI;
import mods.applemilk.common.DCsAppleMilk;
import mods.applemilk.common.tile.TileCupHandle;
import mods.applemilk.common.tile.TileWipeBox2;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockShowCase extends BlockContainer {
	
	protected Random rand = new Random();
	
	private String[] modeName = new String[] {"Public", "Private", "Villager", "Display-only"};
	
	public BlockShowCase(int blockid)
	{
		super(blockid, Material.iron);
		this.setHardness(2.0F);
		this.setResistance(2.0F);
	}
	
	//右クリックされた時の処理
	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
	{
		if (par5EntityPlayer.isSneaking() && par5EntityPlayer.inventory.getCurrentItem() == null)//モード変更に使用する
		{
			TileShowCase tile = (TileShowCase) par1World.getBlockTileEntity(par2, par3, par4);
			String thisPlayer = par5EntityPlayer.getDisplayName();
			
			if (tile != null)
			{
				String owner = tile.getOwner();
				
				if (owner.equalsIgnoreCase(thisPlayer))//owner以外は変更できない
				{
					int prevMode = tile.mode;
					prevMode++;
					if (prevMode > 3) prevMode = 0;
					par1World.playSoundAtEntity(par5EntityPlayer, "random.pop", 0.4F, 1.8F);
					
					if (!par1World.isRemote)
					{
						switch(prevMode)
						{
						case 0 : 
							par5EntityPlayer.addChatMessage("set public mode.");
							break;
						case 1 : 
							par5EntityPlayer.addChatMessage("set private mode.");
							break;
						case 2 : 
							par5EntityPlayer.addChatMessage("set villager mode.");
							break;
						case 3 : 
							par5EntityPlayer.addChatMessage("set display-only mode.");
							break;
						}
					}
					
					tile.setMode(prevMode);
					return true;
				}
				else//設置者以外が使った場合は、中身の情報確認を行う。
				{
					String seller = tile.getSellerName();
					ItemStack saleItem = tile.getSellItem();
					int price = tile.getPrice();
					int mode = tile.getMode();
					
					if (!par1World.isRemote)
					{
						//ブロックのオーナー
						par5EntityPlayer.addChatMessage("Owner : " + owner + ", " + "Mode : " + this.modeName[mode]);
						
						if (saleItem != null && !seller.equalsIgnoreCase("None"))
						{
							//販売者とアイテム
							par5EntityPlayer.addChatMessage("Current sale item by " + seller + " : " + saleItem.getDisplayName() + "x" + saleItem.stackSize + ", " + price + "MP");
						}
					}
				}
			}
			return false;
		}
		else
		{
			ItemStack current = par5EntityPlayer.inventory.getCurrentItem();
			TileShowCase tile = (TileShowCase) par1World.getBlockTileEntity(par2, par3, par4);
			
			String blockOwner = "None";
			String seller = "None";
			String thisPlayer = par5EntityPlayer.getDisplayName();
			
			
			if (tile != null)
			{
				//ひと通りのパラメータを取得
				blockOwner = tile.getOwner();
				seller = tile.sellerName;
				int price = tile.getPrice();
				int mode = tile.mode;
				
				//ここからメイン処理開始
				if (tile.hasSellItem())//売却アイテムが入っている
				{
					if (seller.equalsIgnoreCase(thisPlayer))//売主が回収する場合
					{
						ItemStack sellItem = tile.getSellItem();
						
						if (!par5EntityPlayer.inventory.addItemStackToInventory(sellItem))
						{
							par5EntityPlayer.dropPlayerItem(sellItem);
						}
						
						//お知らせ
						if (!par1World.isRemote)
						{
							par5EntityPlayer.addChatMessage(ShowCaseCore.CANCELL_MESSAGE + " : " + sellItem.getDisplayName());
							par1World.playSoundAtEntity(par5EntityPlayer, "random.pop", 0.4F, 1.8F);
						}
						
						//tileのリセット
						tile.setSellerName("None");
						tile.setPrice(0);
						tile.setSellItem((ItemStack)null);
						tile.onInventoryChanged();
						par5EntityPlayer.inventory.onInventoryChanged();
						
						return true;
					}
					else//他プレイヤーが購入する
					{
						ItemStack sellItem = tile.getSellItem();
						EntityPlayer sellerPlayer = par1World.getPlayerEntityByName(seller);
//						int currentMP = MCEconomyAPI.getPlayerMP(par5EntityPlayer);
						int currentMP = 200;//デバッグ用
						
						if (mode < 2 && currentMP > price)//お金が足りているか
						{
							if (!par5EntityPlayer.inventory.addItemStackToInventory(sellItem))
							{
								par5EntityPlayer.dropPlayerItem(sellItem);
							}
							
							//お金のやりとり
//							MCEconomyAPI.reducePlayerMP(par5EntityPlayer, price);
//							if (sellerPlayer != null) MCEconomyAPI.addPlayerMP(sellerPlayer, price);
							
							//お知らせ
							if (!par1World.isRemote)
							{
								if (sellerPlayer != null) sellerPlayer.addChatMessage(par5EntityPlayer.getDisplayName() + ShowCaseCore.MP_GET_MESSAGE + " : " + sellItem.getDisplayName() + ", " + tile.price + "MP");
								par5EntityPlayer.addChatMessage(ShowCaseCore.SELE_MESSAGE + " : " + sellItem.getDisplayName() + ", " + tile.price + "MP");
								par1World.playSoundAtEntity(par5EntityPlayer, "random.pop", 0.4F, 1.8F);
							}
							
							//tileのリセット
							tile.setSellerName("None");
							tile.setPrice(0);
							tile.setSellItem((ItemStack)null);
							tile.onInventoryChanged();
							par5EntityPlayer.inventory.onInventoryChanged();
							
							return true;
						}
					}
				}
				else//アイテムを投入する
				{
					if (mode == 1)//privateモード時
					{
						if (blockOwner.equalsIgnoreCase(thisPlayer) && current != null)//owner以外入れられない
						{
//							int currentPrice = MCEconomyAPI.getPurchase(current);//MP売却額の取得
							int currentPrice = 100;
							
							if (currentPrice > 0)//0や-1の物は除外する
							{
								tile.setSellItem(current);
								tile.setPrice(currentPrice);
								tile.setSellerName(thisPlayer);
								tile.onInventoryChanged();
								
								par5EntityPlayer.inventory.setInventorySlotContents(par5EntityPlayer.inventory.currentItem, (ItemStack)null);
								par5EntityPlayer.inventory.onInventoryChanged();
								
								//お知らせ
								if (!par1World.isRemote)
								{
									par5EntityPlayer.addChatMessage(ShowCaseCore.PUT_MESSAGE + " : " + current.getDisplayName() + ", " + tile.price + "MP");
									par1World.playSoundAtEntity(par5EntityPlayer, "random.pop", 0.4F, 1.8F);
								}
							}
							return true;
						}
					}
					else if (current != null)//他のモードならだれでも入れられる
					{
//						int currentPrice = MCEconomyAPI.getPurchase(current);//MP売却額の取得
						int currentPrice = 100;
						
						if (currentPrice > 0)//0や-1の物は除外する
						{
							tile.setSellItem(current);
							tile.setPrice(currentPrice);
							tile.setSellerName(thisPlayer);
							tile.onInventoryChanged();
							
							par5EntityPlayer.inventory.setInventorySlotContents(par5EntityPlayer.inventory.currentItem, (ItemStack)null);
							par5EntityPlayer.inventory.onInventoryChanged();
							
							//お知らせ
							if (!par1World.isRemote)
							{
								par5EntityPlayer.addChatMessage(ShowCaseCore.PUT_MESSAGE + " : " + current.getDisplayName() + ", " + tile.price + "MP");
								par1World.playSoundAtEntity(par5EntityPlayer, "random.pop", 0.4F, 1.8F);
							}
						}
						return true;
					}
				}
				
			}
			return false;
		}
	}
	
	//周辺に中に入っていたアイテムは販売者のインベントリに突っ込まれる
	@Override
	public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6)
	{
		TileShowCase tileentity = (TileShowCase) par1World.getBlockTileEntity(par2, par3, par4);
	 
		if (tileentity != null)
		{
			for (int j1 = 0; j1 < tileentity.inventory.getSizeInventory(); ++j1)
			{
				ItemStack itemstack = tileentity.inventory.getStackInSlot(j1);
				EntityPlayer seller = par1World.getPlayerEntityByName(tileentity.sellerName);
	 
				if (itemstack != null)
				{
					float f = this.rand.nextFloat() * 0.8F + 0.1F;
					float f1 = this.rand.nextFloat() * 0.8F + 0.1F;
					float f2 = this.rand.nextFloat() * 0.8F + 0.1F;
	 
					if (itemstack.stackSize > 0)
					{
						int k1 = itemstack.stackSize;
	 
						EntityItem entityitem = new EntityItem(par1World, (double)((float)par2 + f), (double)((float)par3 + f1), (double)((float)par4 + f2), new ItemStack(itemstack.itemID, k1, itemstack.getItemDamage()));
	 
						if (itemstack.hasTagCompound())
						{
							entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
						}
	 
						float f3 = 0.05F;
						entityitem.motionX = (double)((float)this.rand.nextGaussian() * f3);
						entityitem.motionY = (double)((float)this.rand.nextGaussian() * f3 + 0.2F);
						entityitem.motionZ = (double)((float)this.rand.nextGaussian() * f3);
						
						if (seller != null)
						{
							if (!seller.inventory.addItemStackToInventory(itemstack))
							{
								seller.dropPlayerItem(itemstack);
							}
							seller.addChatMessage(ShowCaseCore.CANCELL_MESSAGE + " : " + itemstack.getDisplayName());
						}
						else
						{
							par1World.spawnEntityInWorld(entityitem);
						}
					}
				}
			}
	 
			par1World.func_96440_m(par2, par3, par4, par5);
		}
	 
		super.breakBlock(par1World, par2, par3, par4, par5, par6);
	}
	
	//設置時の処理
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack)
	{
		int meta = 0;
		TileShowCase tile = (TileShowCase) par1World.getBlockTileEntity(par2, par3, par4);
		EntityPlayer owner = (EntityPlayer) par5EntityLivingBase;
		
		if (tile != null && owner != null)
		{
			tile.setOwnerName(owner.getDisplayName());
			tile.onInventoryChanged();
			owner.addChatMessage(owner.getDisplayName());
			owner.addChatMessage(tile.getOwner());
		}
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		super.onBlockAdded(world, x, y, z);
		this.setDefaultDirection(world, x, y, z);
	}
	
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
		this.setDefaultDirection(par1World, par2, par3, par4);
    }
	
	private void setDefaultDirection(World world, int x, int y, int z)
	{
		int var4 = world.getBlockId(x, y - 1, z);
		int var5 = world.getBlockId(x, y, z - 1);
		int var6 = world.getBlockId(x, y, z + 1);
		int var7 = world.getBlockId(x - 1, y, z);
		int var8 = world.getBlockId(x + 1, y, z);
		int var9 = 0;
		
		if (var5 == this.blockID)
		{
			var9 = (var9 | 1);
		}

		if (var6 == this.blockID)
		{
			var9 = (var9 | 2);
		}

		if (var7 == this.blockID)
		{
			var9 = (var9 | 4);
		}

		if (var8 == this.blockID)
		{
			var9 = (var9 | 8);
		}

		world.setBlockMetadataWithNotify(x, y, z, var9, 3);
	}
	 
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileShowCase();
	}
		
	@Override
	public int idDropped(int metadata, Random rand, int fortune)
	{
		return this.blockID;
	}
	
	public boolean isOpaqueCube()
	{
		return false;
	}
 
	public boolean renderAsNormalBlock() 
	{
		return false;
	}
	
	@Override
	public int getRenderType()
	{
		return ShowCaseCore.renderShowCase;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon("showcase:showcaseblock");
		
	}

}
