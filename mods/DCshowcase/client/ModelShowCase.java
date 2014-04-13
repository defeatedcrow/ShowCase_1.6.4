package mods.DCshowcase.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelShowCase extends ModelBase
{
  //fields
    ModelRenderer base;
    ModelRenderer boad;
    ModelRenderer maincase;
    ModelRenderer front;
    ModelRenderer back;
    ModelRenderer right;
    ModelRenderer left;
  
  public ModelShowCase()
  {
    textureWidth = 128;
    textureHeight = 64;
    
      base = new ModelRenderer(this, 0, 0);
      base.addBox(-8F, 0F, -8F, 16, 4, 16);
      base.setRotationPoint(0F, 20F, 0F);
      base.setTextureSize(128, 64);
      base.mirror = true;
      setRotation(base, 0F, 0F, 0F);
      boad = new ModelRenderer(this, 64, 0);
      boad.addBox(-5.5F, 0F, -5.5F, 11, 1, 11);
      boad.setRotationPoint(0F, 19F, 0F);
      boad.setTextureSize(128, 64);
      boad.mirror = true;
      setRotation(boad, 0F, 0F, 0F);
      maincase = new ModelRenderer(this, 0, 24);
      maincase.addBox(-6F, 0F, -6F, 12, 16, 12);
      maincase.setRotationPoint(0F, 8F, 0F);
      maincase.setTextureSize(128, 64);
      maincase.mirror = true;
      setRotation(maincase, 0F, 0F, 0F);
      front = new ModelRenderer(this, 0, 24);
      front.addBox(-6F, 0F, -8F, 12, 16, 2);
      front.setRotationPoint(0F, 8F, 0F);
      front.setTextureSize(128, 64);
      front.mirror = true;
      setRotation(front, 0F, 0F, 0F);
      back = new ModelRenderer(this, 0, 24);
      back.addBox(-6F, 0F, 6F, 12, 16, 2);
      back.setRotationPoint(0F, 8F, 0F);
      back.setTextureSize(128, 64);
      back.mirror = true;
      setRotation(back, 0F, 0F, 0F);
      right = new ModelRenderer(this, 0, 24);
      right.addBox(0F, 0F, -6F, 2, 16, 12);
      right.setRotationPoint(-8F, 8F, 0F);
      right.setTextureSize(128, 64);
      right.mirror = true;
      setRotation(right, 0F, 0F, 0F);
      left = new ModelRenderer(this, 0, 24);
      left.addBox(6F, 0F, -6F, 2, 16, 12);
      left.setRotationPoint(0F, 8F, 0F);
      left.setTextureSize(128, 64);
      left.mirror = true;
      setRotation(left, 0F, 0F, 0F);
  }
  
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5, byte b0)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(entity, f, f1, f2, f3, f4, f5);
    boad.render(f5);
    if ((b0 & 1) == 0) base.render(f5);
  }
  
  public void renderGlass(Entity entity, float f, float f1, float f2, float f3, float f4, float f5, byte b0)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(entity, f, f1, f2, f3, f4, f5);
    maincase.render(f5);
    if ((b0 & 1) == 1) back.render(f5);
    if ((b0 >> 1 & 1) == 1) front.render(f5);
    if ((b0 >> 2 & 1) == 1) right.render(f5);
    if ((b0 >> 3 & 1) == 1) left.render(f5);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
  
  public void setRotationAngles(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
  }

}
