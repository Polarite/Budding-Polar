package com.polarite.buddingpolar.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

import com.polarite.buddingpolar.BuddingPolar;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;

public class GuiConfigBuddingPolar extends GuiConfig {

    public GuiConfigBuddingPolar(GuiScreen parentScreen) {
        super(
            parentScreen,
            getConfigElements(),
            BuddingPolar.MODID,
            false,
            false,
            GuiConfig.getAbridgedConfigPath(BuddingPolar.config.config.toString()));
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<IConfigElement>();

        // Add mining category
        list.addAll(new ConfigElement(BuddingPolar.config.config.getCategory("mining")).getChildElements());

        // Add growth category
        list.addAll(new ConfigElement(BuddingPolar.config.config.getCategory("growth")).getChildElements());

        return list;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        // Sync configuration values when GUI is closed
        if (BuddingPolar.config != null) {
            BuddingPolar.config.syncConfig();
        }
    }
}
