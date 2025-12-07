package com.polarite.buddingpolar.mixin;

import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Collections;
import java.util.List;

/**
 * Late mixin loader for Budding Polar.
 * This class is discovered by MixinBooter via ServiceLoader and registers our mixin config
 * after Forge mods are loaded, allowing us to mixin into AE2 classes.
 */
public class BuddingPolarLateMixinLoader implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList("mixins.buddingpolar.json");
    }
}
