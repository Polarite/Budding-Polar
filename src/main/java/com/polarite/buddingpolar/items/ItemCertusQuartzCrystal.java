package com.polarite.buddingpolar.items;

import net.minecraft.item.Item;

import com.polarite.buddingpolar.BuddingPolar;

public class ItemCertusQuartzCrystal extends Item {

    public ItemCertusQuartzCrystal() {
        setUnlocalizedName("certus_quartz_crystal");
        setTextureName("buddingpolar:certus_quartz_crystal");
        setCreativeTab(BuddingPolar.creativeTabs);
        setMaxStackSize(64);
    }
}
