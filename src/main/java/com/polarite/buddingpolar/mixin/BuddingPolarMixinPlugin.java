package com.polarite.buddingpolar.mixin;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.spongepowered.asm.lib.ClassReader;
import org.spongepowered.asm.lib.ClassVisitor;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * Mixin config plugin that dynamically selects which meteorite mixin to load
 * based on which AE2 fork is installed (standard AE2 vs GTMEGA fork).
 */
public class BuddingPolarMixinPlugin implements IMixinConfigPlugin {

    private boolean isGTMEGA = false;

    @Override
    public void onLoad(String mixinPackage) {
        // Detect which AE2 fork is installed
        this.isGTMEGA = detectGTMEGAAE2();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // Allow all mixins by default, we control which ones are added in getMixins()
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        // No-op
    }

    @Override
    public List<String> getMixins() {
        List<String> mixins = new ArrayList<>();

        if (isGTMEGA) {
            mixins.add("MixinMeteoritePlacerGTMEGA");
        } else {
            mixins.add("MixinMeteoritePlacer");
        }

        return mixins;
    }

    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // No-op
    }

    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // No-op
    }

    /**
     * Detects if the GTMEGA Applied Energistics 2 Unofficial fork is installed.
     * The GTMEGA fork has a different MeteoritePlacer implementation that doesn't have
     * the spawnMeteoriteCenter() method - instead, it places the sky chest directly in
     * placeMeteorite().
     *
     * Uses ASM bytecode inspection to avoid loading the class, which would prevent mixin
     * transformation.
     *
     * @return true if GTMEGA AE2 fork is detected, false for standard AE2
     */
    private boolean detectGTMEGAAE2() {
        try {
            // Use ASM to inspect the bytecode without loading the class
            String classPath = "appeng/worldgen/MeteoritePlacer.class";
            InputStream classStream = getClass().getClassLoader()
                .getResourceAsStream(classPath);

            if (classStream == null) {
                return false;
            }

            // Track whether we found the spawnMeteoriteCenter method
            final boolean[] foundSpawnMeteoriteCenter = { false };

            ClassReader classReader = new ClassReader(classStream);
            classReader.accept(new ClassVisitor(Opcodes.ASM5) {

                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
                    String[] exceptions) {
                    if ("spawnMeteoriteCenter".equals(name)) {
                        foundSpawnMeteoriteCenter[0] = true;
                    }
                    return null;
                }
            }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

            classStream.close();

            // Standard AE2 has spawnMeteoriteCenter, GTMEGA fork does not
            return !foundSpawnMeteoriteCenter[0];
        } catch (IOException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
