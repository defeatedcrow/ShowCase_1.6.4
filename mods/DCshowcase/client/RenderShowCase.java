package mods.DCshowcase.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.DCironchain.entity.TileEntityFloodLight;
import mods.DCshowcase.common.ShowCaseCore;
import mods.DCshowcase.common.TileShowCase;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureCompass;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;


@SideOnly(Side.CLIENT)
public class RenderShowCase extends TileEntitySpecialRenderer
{
    private static final ResourceLocation CASE_TEX = new ResourceLocation("showcase:textures/tiles/showcase.png");
    public static RenderShowCase caseRenderer;
    private ModelShowCase model = new ModelShowCase();

    public void renderTileEntityCaseAt(TileShowCase par1Tile, double par2, double par4, double par6, float par8)
    {
        this.setRotation(par1Tile, (float)par2, (float)par4, (float)par6);
    }

    /**
     * Associate a TileEntityRenderer with this TileEntitySpecialRenderer
     */
    public void setTileEntityRenderer(TileEntityRenderer par1TileEntityRenderer)
    {
        super.setTileEntityRenderer(par1TileEntityRenderer);
        caseRenderer = this;
    }

    public void setRotation(TileShowCase par0Tile, float par1, float par2, float par3)
    {
        ModelShowCase model = this.model;
        byte l = (byte)par0Tile.getBlockMetadata();
        byte id = (byte) (par0Tile.getLocation() == ShowCaseCore.showCaseBlock.blockID ? 1 : 0);

        this.bindTexture(CASE_TEX);
        
        //base
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        
        GL11.glTranslatef((float)par1 + 0.5F, (float)par2 + 1.5F, (float)par3 + 0.5F);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        GL11.glRotatef(0.0F, 0.0F, 0.0F, 0.0F);
        model.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, id);
        
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
        
        //inner
        if (par0Tile.worldObj != null)
        {
        	GL11.glPushMatrix();
            GL11.glTranslatef((float)par1 + 0.5F, (float)par2 + 0.35F, (float)par3 + 0.5F);
            GL11.glRotatef(0.0F, 0.0F, 0.0F, 1.0F);
        	
        	this.renderInner(par0Tile);
        	this.renderFont(par0Tile);
        	
        	GL11.glPopMatrix();
        }
        
        //glass
        this.bindTexture(CASE_TEX);
        
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        GL11.glPolygonOffset(-1, -1);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 1);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        
        GL11.glTranslatef((float)par1 + 0.5F, (float)par2 + 1.5F, (float)par3 + 0.5F);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        GL11.glRotatef(0.0F, 0.0F, 0.0F, 0.0F);
        GL11.glColor4f(2.0F, 2.0F, 2.0F, 0.25F);
        model.renderGlass((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, l);
        
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        
    }
    
    private void renderInner(TileShowCase par0Tile)
    {
    	ItemStack item = par0Tile.getSellItem();

        if (item != null)
        {
            EntityItem entityitem = new EntityItem(par0Tile.worldObj, 0.0D, 0.0D, 0.0D, item);
            entityitem.getEntityItem().stackSize = 1;
            entityitem.hoverStart = 0.0F;
            
            GL11.glScalef(1.5F, 1.5F, 1.5F);
            
            RenderItem.renderInFrame = false;
            RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
            RenderItem.renderInFrame = false;
        }
    }
    
    private void renderFont(TileShowCase par0Tile)
    {
    	ItemStack item = par0Tile.getSellItem();
        String price = par0Tile.getPrice() + "MP";

        if (item != null)
        {
            //render font
            FontRenderer fontrenderer = this.getFontRenderer();
            float f1 = 0.67F;
            float f2 = 0.016666668F * f1;
            
            GL11.glTranslatef(0.0F, 0.35F, 0.2F);
            GL11.glRotatef(0.0F, 0.0F, 0.0F, 1.0F);
            GL11.glScalef(f2, -f2, f2);
            GL11.glNormal3f(0.0F, 0.0F, -1.0F * f2);
            GL11.glDepthMask(false);
            byte b0 = 0;

            String s = price;
            fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, 10 - price.length(), b0);
            
            GL11.glDepthMask(true);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            
        }
    }

    public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8)
    {
        this.renderTileEntityCaseAt((TileShowCase)par1TileEntity, par2, par4, par6, par8);
    }
}
