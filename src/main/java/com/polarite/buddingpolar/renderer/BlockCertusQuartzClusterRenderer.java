package com.polarite.buddingpolar.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockCertusQuartzClusterRenderer implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        // No inventory rendering for now
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        renderBlockCluster(block, x, y, z, renderer);
        return true;
    }

    private boolean renderBlockCluster(Block block, int x, int y, int z, RenderBlocks renderer) {
        Tessellator tessellator = Tessellator.instance;

        IBlockAccess world = renderer.blockAccess;
        tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
        float f = 1.0F;
        int l = block.colorMultiplier(world, x, y, z);
        float f1 = (float) (l >> 16 & 255) / 255.0F;
        float f2 = (float) (l >> 8 & 255) / 255.0F;
        float f3 = (float) (l & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable) {
            float renX = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            float f5 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
            float renY = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
            f1 = renX;
            f2 = f5;
            f3 = renY;
        }

        tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
        double renX1 = (double) x;
        double renY1 = (double) y;
        double renZ = (double) z;

        int meta1 = world.getBlockMetadata(x, y, z);
        IIcon icon = renderer.getBlockIconFromSideAndMetadata(block, 0, meta1);
        if (renderer.hasOverrideBlockTexture()) {
            icon = renderer.overrideBlockTexture;
        }

        byte renderType = 0;
        double verTop = renY1 + 1.0D;
        double verBot = renY1 + 0.0D;
        int dir = world.getBlockMetadata(x, y, z);

        // Handle the 6 directional orientations
        switch (dir % 6) {
            case 0: // Down (hanging from ceiling)
                renderType = 0;
                verTop = renY1 + 0.0D;
                verBot = renY1 + 1.0D;
                break;
            case 1: // Up (growing from floor)
                renderType = 0;
                verTop = renY1 + 1.0D;
                verBot = renY1 + 0.0D;
                break;
            case 2: // North (growing from south wall)
                renderType = 2;
                verTop = renZ + 0.0D;
                verBot = renZ + 1.0D;
                break;
            case 3: // South (growing from north wall)
                renderType = 2;
                verTop = renZ + 1.0D;
                verBot = renZ + 0.0D;
                break;
            case 4: // West (growing from east wall)
                renderType = 1;
                verTop = renX1 + 0.0D;
                verBot = renX1 + 1.0D;
                break;
            case 5: // East (growing from west wall)
                renderType = 1;
                verTop = renX1 + 1.0D;
                verBot = renX1 + 0.0D;
                break;
        }

        double fix = 0.45D; // Size of the cluster
        double minU = (double) icon.getMinU();
        double minV = (double) icon.getMinV();
        double maxU = (double) icon.getMaxU();
        double maxV = (double) icon.getMaxV();
        double yMin;
        double yMax;
        double xMin;
        double xMax;

        // Render based on orientation
        if (renderType == 0) { // Up/Down orientation
            yMin = renX1 + 0.5D - fix;
            yMax = renX1 + 0.5D + fix;
            xMin = renZ + 0.5D - fix;
            xMax = renZ + 0.5D + fix;
            // Add vertices for X-shape cross
            tessellator.addVertexWithUV(yMin, verTop, xMin, minU, minV);
            tessellator.addVertexWithUV(yMin, verBot, xMin, minU, maxV);
            tessellator.addVertexWithUV(yMax, verBot, xMax, maxU, maxV);
            tessellator.addVertexWithUV(yMax, verTop, xMax, maxU, minV);
            tessellator.addVertexWithUV(yMax, verTop, xMax, minU, minV);
            tessellator.addVertexWithUV(yMax, verBot, xMax, minU, maxV);
            tessellator.addVertexWithUV(yMin, verBot, xMin, maxU, maxV);
            tessellator.addVertexWithUV(yMin, verTop, xMin, maxU, minV);
            tessellator.addVertexWithUV(yMin, verTop, xMax, minU, minV);
            tessellator.addVertexWithUV(yMin, verBot, xMax, minU, maxV);
            tessellator.addVertexWithUV(yMax, verBot, xMin, maxU, maxV);
            tessellator.addVertexWithUV(yMax, verTop, xMin, maxU, minV);
            tessellator.addVertexWithUV(yMax, verTop, xMin, minU, minV);
            tessellator.addVertexWithUV(yMax, verBot, xMin, minU, maxV);
            tessellator.addVertexWithUV(yMin, verBot, xMax, maxU, maxV);
            tessellator.addVertexWithUV(yMin, verTop, xMax, maxU, minV);
        }

        if (renderType == 1) { // East/West orientation
            yMin = renZ + 0.5D - fix;
            yMax = renZ + 0.5D + fix;
            xMin = renY1 + 0.5D - fix;
            xMax = renY1 + 0.5D + fix;
            tessellator.addVertexWithUV(verTop, xMin, yMin, minU, minV);
            tessellator.addVertexWithUV(verBot, xMin, yMin, minU, maxV);
            tessellator.addVertexWithUV(verBot, xMax, yMax, maxU, maxV);
            tessellator.addVertexWithUV(verTop, xMax, yMax, maxU, minV);
            tessellator.addVertexWithUV(verTop, xMax, yMax, minU, minV);
            tessellator.addVertexWithUV(verBot, xMax, yMax, minU, maxV);
            tessellator.addVertexWithUV(verBot, xMin, yMin, maxU, maxV);
            tessellator.addVertexWithUV(verTop, xMin, yMin, maxU, minV);
            tessellator.addVertexWithUV(verTop, xMax, yMin, minU, minV);
            tessellator.addVertexWithUV(verBot, xMax, yMin, minU, maxV);
            tessellator.addVertexWithUV(verBot, xMin, yMax, maxU, maxV);
            tessellator.addVertexWithUV(verTop, xMin, yMax, maxU, minV);
            tessellator.addVertexWithUV(verTop, xMin, yMax, minU, minV);
            tessellator.addVertexWithUV(verBot, xMin, yMax, minU, maxV);
            tessellator.addVertexWithUV(verBot, xMax, yMin, maxU, maxV);
            tessellator.addVertexWithUV(verTop, xMax, yMin, maxU, minV);
        }

        if (renderType == 2) { // North/South orientation
            yMin = renY1 + 0.5D - fix;
            yMax = renY1 + 0.5D + fix;
            xMin = renX1 + 0.5D - fix;
            xMax = renX1 + 0.5D + fix;
            tessellator.addVertexWithUV(xMin, yMin, verTop, minU, minV);
            tessellator.addVertexWithUV(xMin, yMin, verBot, minU, maxV);
            tessellator.addVertexWithUV(xMax, yMax, verBot, maxU, maxV);
            tessellator.addVertexWithUV(xMax, yMax, verTop, maxU, minV);
            tessellator.addVertexWithUV(xMax, yMax, verTop, minU, minV);
            tessellator.addVertexWithUV(xMax, yMax, verBot, minU, maxV);
            tessellator.addVertexWithUV(xMin, yMin, verBot, maxU, maxV);
            tessellator.addVertexWithUV(xMin, yMin, verTop, maxU, minV);
            tessellator.addVertexWithUV(xMax, yMin, verTop, minU, minV);
            tessellator.addVertexWithUV(xMax, yMin, verBot, minU, maxV);
            tessellator.addVertexWithUV(xMin, yMax, verBot, maxU, maxV);
            tessellator.addVertexWithUV(xMin, yMax, verTop, maxU, minV);
            tessellator.addVertexWithUV(xMin, yMax, verTop, minU, minV);
            tessellator.addVertexWithUV(xMin, yMax, verBot, minU, maxV);
            tessellator.addVertexWithUV(xMax, yMin, verBot, maxU, maxV);
            tessellator.addVertexWithUV(xMax, yMin, verTop, maxU, minV);
        }

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return RenderIDs.CERTUS_QUARTZ_CLUSTER;
    }
}
