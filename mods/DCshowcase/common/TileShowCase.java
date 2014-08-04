package mods.DCshowcase.common;

import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

public class TileShowCase extends TileEntity
{
	
	/**売却アイテム*/
    protected int price = 0;
    protected int priceSet = 0;
    
    /**売却プレイヤー*/
    protected String sellerName = "None";
    protected int sellerID = 0;
    
    /**設置者*/
    protected String ownerName = "None";
    /**
     * このブロックのモードは3種類あり、うち1つはマルチプレイ用。
     * <br>0: public mode デフォルト。誰でもアイテムを設置・購入できる。
     * <br>1: private mode 設置はTileの設置者かOP権限者のみ。購入は誰でもできる。
     * <br>2: village mode プレイヤーによる購入は出来ず、村の判定内部にあると時々中身が消費される。 
     * <br>3: display only mode 購入はできず、見せるためだけのモード。
     * */
    protected int mode = 0;
    
    
    public InventoryShowCase inventory;
    
    public TileShowCase()
    {
    	this.inventory = new InventoryShowCase(this);
    }

    //NBT
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        
        NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items");
		this.inventory.itemStacks = new ItemStack[this.inventory.getSizeInventory()];
 
		for (int i = 0; i < nbttaglist.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
			byte b0 = nbttagcompound1.getByte("Slot");
 
			if (b0 >= 0 && b0 < this.inventory.itemStacks.length)
			{
				this.inventory.itemStacks[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}
        
        this.price = par1NBTTagCompound.getShort("Price");
        this.priceSet = par1NBTTagCompound.getShort("PriceSet");
        this.mode = par1NBTTagCompound.getByte("Mode");
        this.sellerName = par1NBTTagCompound.getString("SellerName");
        this.ownerName = par1NBTTagCompound.getString("OwnerName");
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setShort("Price", (short) this.price);
        par1NBTTagCompound.setShort("PriceSet", (short) this.priceSet);
        par1NBTTagCompound.setByte("Mode", (byte) this.mode);
        par1NBTTagCompound.setString("SellerName", this.sellerName);
        par1NBTTagCompound.setString("OwnerName", this.ownerName);
        
        NBTTagList nbttaglist = new NBTTagList();
        
		for (int i = 0; i < this.inventory.itemStacks.length; ++i)
		{
			if (this.inventory.itemStacks[i] != null)
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte)i);
				this.inventory.itemStacks[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
 
		par1NBTTagCompound.setTag("Items", nbttaglist);
    }
    
    @Override
	public Packet getDescriptionPacket() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        this.writeToNBT(nbtTagCompound);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, nbtTagCompound);
	}
 
	@Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
        this.readFromNBT(pkt.data);
    }
    
    public boolean hasSellItem()
    {
    	return this.getSellItem() != null;
    }
    
    public void setSellItem(ItemStack itemstack)
    {
    	this.inventory.setInventorySlotContents(0, itemstack);
    }
    
    public ItemStack getSellItem()
    {
    	return this.inventory.itemStacks[0];
    }

    public int getPrice()
    {
        return this.price;
    }
    
    @SideOnly(Side.CLIENT)
    public int getPriceClient()
    {
        return this.price;
    }
    
    public void setPrice(int par1)
    {
    	this.price = par1;
    }
    
    public int getPriceSet()
    {
        return this.priceSet;
    }
    
    public void setPriceSet(int par1)
    {
    	int p = par1;
    	if (p > 10000) p = 10000;
    	if (p < 0) p = 0;
    	
    	this.priceSet = par1;
    }
    
    public int getMode()
    {
        return this.mode;
    }
    
    public void setMode(int par1)
    {
    	int p = par1;
    	if (p > 3) p = 3;
    	if (p < 0) p = 0;
    	
    	this.mode = par1;
    }
    
    @SideOnly(Side.CLIENT)
    public int getModeClient()
    {
        return this.mode;
    }
    
    public String getSellerName()
    {
        return this.sellerName;
    }
    
    public void setSellerName(String par1)
    {
    	String name = "None";
    	if (par1 != null && !par1.equalsIgnoreCase("None")) {
    		name = par1;
    	}
    	
    	this.sellerName = name;
    }
    
    @SideOnly(Side.CLIENT)
    public String getSellerClient()
    {
        return this.sellerName;
    }
    
    public void setOwnerName(String par1)
    {
    	String name = "None";
    	if (par1 != null && !par1.equalsIgnoreCase("None")) {
    		name = par1;
    	}
    	
    	this.ownerName = name;
    }
    
    public String getOwner()
    {
        return this.ownerName;
    }
    
    @SideOnly(Side.CLIENT)
    public String getOwnerClient()
    {
        return this.ownerName;
    }
    
    public int getMetadata()
    {
    	return this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
    }
    
    public short getLocation()
    {
    	return (short)this.worldObj.getBlockId(xCoord, yCoord - 1, zCoord);
    }
}
