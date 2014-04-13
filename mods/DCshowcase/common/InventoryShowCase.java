package mods.DCshowcase.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public class InventoryShowCase implements IInventory {
	
	private static final int[] slots_top = new int[] {1};//購入品
	private static final int[] slots_bottom = new int[] {0};//売却アイテム投入用スロット
	
	public ItemStack itemStacks[] = new ItemStack[1];
	
	private TileShowCase tileentity;
	
	public InventoryShowCase(TileShowCase tile)
	{
		this.tileentity = tile;
	}

	@Override
	public int getSizeInventory() {
		return this.itemStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if (i != 0) i = 0;
		return this.itemStacks[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j)
	{
		if (i < 1 && this.itemStacks[i] != null)
		{
			ItemStack itemstack;
 
			if (this.itemStacks[i].stackSize <= j)
			{
				itemstack = this.itemStacks[i];
				this.itemStacks[i] = null;
				return itemstack;
			}
			else
			{
				itemstack = this.itemStacks[i].splitStack(j);
 
				if (this.itemStacks[i].stackSize == 0)
				{
					this.itemStacks[i] = null;
				}
 
				return itemstack;
			}
		}
		else
		{
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i)
	{
		if (i < 1 && this.itemStacks[i] != null)
		{
			ItemStack itemstack = this.itemStacks[i];
			this.itemStacks[i] = null;
			return itemstack;
		}
		else
		{
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack)
	{
		if (i != 0) i = 0;
		this.itemStacks[i] = itemstack;
		 
		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit())
		{
			itemstack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public String getInvName()
	{
		return ShowCaseCore.showCaseBlock.getLocalizedName();
	}

	@Override
	public boolean isInvNameLocalized() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void onInventoryChanged() {
		this.tileentity.onInventoryChanged();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return this.tileentity.worldObj.getBlockTileEntity(this.tileentity.xCoord, this.tileentity.yCoord, this.tileentity.zCoord) != this.tileentity ?
				false : entityplayer.getDistanceSq((double) this.tileentity.xCoord + 0.5D, (double) this.tileentity.yCoord + 0.5D, (double) this.tileentity.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}
}
