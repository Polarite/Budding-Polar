package com.polarite.buddingpolar.commands;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.polarite.buddingpolar.blocks.BlockBuddingCertusQuartz;
import com.polarite.buddingpolar.integration.AE2Integration;

public class CommandCertusQuartzDebug extends CommandBase {

    @Override
    public String getCommandName() {
        return "certusquartzdebug";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/certusquartzdebug - Shows growth info for the budding certus quartz block you're standing on";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; // Allow all players to use this command
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) {
            sender.addChatMessage(new ChatComponentText("This command can only be used by players."));
            return;
        }

        EntityPlayer player = (EntityPlayer) sender;
        World world = player.worldObj;

        // Get player's position (block they're standing on)
        int x = MathHelper.floor_double(player.posX);
        int y = MathHelper.floor_double(player.posY) - 1; // Block under the player
        int z = MathHelper.floor_double(player.posZ);

        Block block = world.getBlock(x, y, z);

        if (!(block instanceof BlockBuddingCertusQuartz)) {
            player.addChatMessage(new ChatComponentText("§cYou are not standing on a Budding Certus Quartz block!"));
            return;
        }

        // Calculate growth acceleration info
        int acceleratorCount = 0;
        int inactiveAcceleratorCount = 0;
        int additionalAttempts = 0;

        try {
            // Check for AE2 integration
            Class.forName("com.polarite.buddingpolar.integration.AE2Integration");
            acceleratorCount = AE2Integration.countAdjacentAccelerators(world, x, y, z);
            additionalAttempts = AE2Integration.calculateAdditionalGrowthAttempts(acceleratorCount);

            // Count inactive accelerators (blocks that are Growth Accelerators but not powered)
            inactiveAcceleratorCount = countInactiveAccelerators(world, x, y, z);
        } catch (ClassNotFoundException e) {
            // AE2 integration not available
        }

        // Calculate growth chances with the new accelerator-dependent system
        int totalAttempts = 1 + additionalAttempts; // Base 1 + accelerator bonuses

        // Growth chance depends on accelerator presence
        float growthChancePerAttempt;
        if (acceleratorCount > 0) {
            // With accelerators: normal AE2 growth chance (1 in 5 = 20%)
            growthChancePerAttempt = 20.0f;
        } else {
            // Without accelerators: reduced to 1/5th (1 in 25 = 4%)
            growthChancePerAttempt = 4.0f;
        }

        // Probability of at least one success in totalAttempts tries
        // P(at least 1 success) = 1 - P(all failures) = 1 - (1-chance)^attempts
        double failureChancePerAttempt = 1.0 - (growthChancePerAttempt / 100.0);
        double overallFailureProbability = Math.pow(failureChancePerAttempt, totalAttempts);
        float overallGrowthChance = (float) ((1.0 - overallFailureProbability) * 100.0);

        // Calculate average time to grow (in seconds)
        // Each update tick is 10 game ticks = 0.5 seconds
        // Expected value = 1 / probability of success per tick
        float averageTimeSeconds = (10.0f / 20.0f) / (overallGrowthChance / 100.0f);

        // Send debug information to player
        player.addChatMessage(new ChatComponentText("§a=== Budding Certus Quartz Debug Info ==="));
        player.addChatMessage(new ChatComponentText("§bPosition: " + x + ", " + y + ", " + z));
        player.addChatMessage(new ChatComponentText("§bActive Growth Accelerators: §a" + acceleratorCount));

        if (inactiveAcceleratorCount > 0) {
            player.addChatMessage(
                new ChatComponentText(
                    "§bInactive Growth Accelerators: §c" + inactiveAcceleratorCount + " §7(not powered)"));
        }

        player
            .addChatMessage(new ChatComponentText("§bTotal Growth Attempts: §e" + totalAttempts + " per update tick"));

        // Display the variable chance based on accelerator presence
        if (acceleratorCount > 0) {
            player
                .addChatMessage(new ChatComponentText("§bChance Per Attempt: §e20.0% (1 in 5) §a[With Accelerators]"));
        } else {
            player.addChatMessage(
                new ChatComponentText("§bChance Per Attempt: §e4.0% (1 in 25) §c[No Accelerators = 1/5th Speed]"));
        }

        player.addChatMessage(
            new ChatComponentText(
                "§bOverall Growth Chance: §e" + String.format("%.1f%%", overallGrowthChance) + " per update tick"));
        player.addChatMessage(
            new ChatComponentText(
                "§bAverage Time Per Stage: §e" + String.format("%.1f", averageTimeSeconds) + " seconds"));

        if (acceleratorCount == 0 && inactiveAcceleratorCount == 0) {
            player.addChatMessage(
                new ChatComponentText(
                    "§7Tip: Place AE2 Growth Accelerators adjacent to this block for faster growth!"));
        } else if (acceleratorCount == 0 && inactiveAcceleratorCount > 0) {
            player.addChatMessage(
                new ChatComponentText("§cYour Growth Accelerators need power! Connect them to an AE2 network."));
        } else {
            player.addChatMessage(
                new ChatComponentText("§aOptimized with " + acceleratorCount + " active Growth Accelerator(s)!"));
        }
    }

    /**
     * Counts inactive (unpowered) Growth Accelerators adjacent to the position
     */
    private int countInactiveAccelerators(World world, int x, int y, int z) {
        int total = 0;
        int active = AE2Integration.countAdjacentAccelerators(world, x, y, z);

        // Count total Growth Accelerators (active + inactive)
        if (AE2Integration.isGrowthAccelerator(world.getBlock(x + 1, y, z))) total++;
        if (AE2Integration.isGrowthAccelerator(world.getBlock(x - 1, y, z))) total++;
        if (AE2Integration.isGrowthAccelerator(world.getBlock(x, y + 1, z))) total++;
        if (AE2Integration.isGrowthAccelerator(world.getBlock(x, y - 1, z))) total++;
        if (AE2Integration.isGrowthAccelerator(world.getBlock(x, y, z + 1))) total++;
        if (AE2Integration.isGrowthAccelerator(world.getBlock(x, y, z - 1))) total++;

        return total - active; // inactive = total - active
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return null; // No tab completion needed for this command
    }
}
