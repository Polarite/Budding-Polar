package com.polarite.buddingpolar.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.polarite.buddingpolar.BuddingPolar;
import com.polarite.buddingpolar.blocks.BlockCertusQuartzCluster;
import com.polarite.buddingpolar.tileentity.TileEntityCertusQuartzCluster;

import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileEntityCertusQuartzClusterRenderer extends TileEntitySpecialRenderer<TileEntityCertusQuartzCluster> {

    @Override
    public void render(TileEntityCertusQuartzCluster te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te == null || te.getWorld() == null) return;
        
        BlockCertusQuartzCluster block = (BlockCertusQuartzCluster) te.getBlockType();
        if (block == null) return;

        // Get the facing direction from block state
        EnumFacing facing = te.getWorld().getBlockState(te.getPos()).getValue(BlockCertusQuartzCluster.FACING);
        
        // Get texture for this cluster type
        String textureName = getTextureForCluster(block);
        ResourceLocation textureLocation = new ResourceLocation(BuddingPolar.MODID, "blocks/" + textureName);
        
        // Render the cross-shaped cluster with brightness based on growth stage
        renderClusterCross(x, y, z, facing, textureLocation, getClusterSize(block), getClusterBrightness(block));
    }

    private String getTextureForCluster(BlockCertusQuartzCluster block) {
        String registryName = block.getRegistryName().getPath();
        return registryName; // This should match the texture file names
    }

    private float getClusterSize(BlockCertusQuartzCluster block) {
        String registryName = block.getRegistryName().getPath();
        switch (registryName) {
            case "small_certus_quartz_bud":
                return 0.25f;
            case "medium_certus_quartz_bud":
                return 0.35f;
            case "large_certus_quartz_bud":
                return 0.45f;
            case "certus_quartz_cluster":
                return 0.55f;
            default:
                return 0.45f;
        }
    }

    private float getClusterBrightness(BlockCertusQuartzCluster block) {
        String registryName = block.getRegistryName().getPath();
        switch (registryName) {
            case "small_certus_quartz_bud":
                return 200f; // Dimmer
            case "medium_certus_quartz_bud":
                return 210f; // Medium brightness
            case "large_certus_quartz_bud":
                return 220f; // Brighter
            case "certus_quartz_cluster":
                return 240f; // Full brightness
            default:
                return 200f;
        }
    }

    private void renderClusterCross(double x, double y, double z, EnumFacing facing, ResourceLocation texture, float size, float brightness) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
        
        // Enable polygon offset to reduce z-fighting
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(-1.0f, -1.0f);
        
        // Enable full brightness and disable lighting for emissive effect
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();
        
        // Set brightness based on cluster growth stage (emissive crystals)
        net.minecraft.client.renderer.OpenGlHelper.setLightmapTextureCoords(net.minecraft.client.renderer.OpenGlHelper.lightmapTexUnit, brightness, brightness);
        
        // Rotate based on facing direction
        switch (facing) {
            case DOWN:
                GlStateManager.rotate(180, 1, 0, 0);
                break;
            case UP:
                // No rotation needed
                break;
            case NORTH:
                GlStateManager.rotate(90, 1, 0, 0);
                GlStateManager.rotate(180, 0, 0, 1);
                break;
            case SOUTH:
                GlStateManager.rotate(90, 1, 0, 0);
                break;
            case WEST:
                GlStateManager.rotate(90, 0, 0, 1);
                break;
            case EAST:
                GlStateManager.rotate(-90, 0, 0, 1);
                break;
        }

        // Bind texture
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture.toString());

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        // Render cross-shaped quads with offset to prevent z-fighting
        renderCrossQuads(buffer, sprite, size);

        tessellator.draw();

        // Restore state
        GlStateManager.disablePolygonOffset();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void renderCrossQuads(BufferBuilder buffer, TextureAtlasSprite sprite, float size) {
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        float minU = sprite.getMinU();
        float maxU = sprite.getMaxU();
        float minV = sprite.getMinV();
        float maxV = sprite.getMaxV();

        // Larger offset to prevent z-fighting between quad faces
        float offset1 = 0.005f;
        float offset2 = -0.005f;
        float offset3 = 0.01f;
        float offset4 = -0.01f;

        // First cross quad (diagonal) - front faces
        buffer.pos(-size, -0.5, -size + offset1).tex(minU, maxV).endVertex();
        buffer.pos(-size, 0.5, -size + offset1).tex(minU, minV).endVertex();
        buffer.pos(size, 0.5, size + offset1).tex(maxU, minV).endVertex();
        buffer.pos(size, -0.5, size + offset1).tex(maxU, maxV).endVertex();

        // First cross quad (diagonal) - back faces
        buffer.pos(size, -0.5, size + offset2).tex(minU, maxV).endVertex();
        buffer.pos(size, 0.5, size + offset2).tex(minU, minV).endVertex();
        buffer.pos(-size, 0.5, -size + offset2).tex(maxU, minV).endVertex();
        buffer.pos(-size, -0.5, -size + offset2).tex(maxU, maxV).endVertex();

        // Second cross quad (other diagonal) - front faces  
        buffer.pos(size, -0.5, -size + offset3).tex(minU, maxV).endVertex();
        buffer.pos(size, 0.5, -size + offset3).tex(minU, minV).endVertex();
        buffer.pos(-size, 0.5, size + offset3).tex(maxU, minV).endVertex();
        buffer.pos(-size, -0.5, size + offset3).tex(maxU, maxV).endVertex();

        // Second cross quad (other diagonal) - back faces
        buffer.pos(-size, -0.5, size + offset4).tex(minU, maxV).endVertex();
        buffer.pos(-size, 0.5, size + offset4).tex(minU, minV).endVertex();
        buffer.pos(size, 0.5, -size + offset4).tex(maxU, minV).endVertex();
        buffer.pos(size, -0.5, -size + offset4).tex(maxU, maxV).endVertex();
    }
}