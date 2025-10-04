package com.polarite.buddingpolar.commands;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import com.polarite.buddingpolar.blocks.BlockBuddingCertusQuartz;
import com.polarite.buddingpolar.integration.AE2Integration;

public class CommandCertusQuartzDebug extends CommandBase {

    @Override
    public String getName() {
        return "certusquartzdebug";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/certusquartzdebug - Shows growth info for the budding certus quartz block you're standing on";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; // Allow all players to use this command
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayer)) {
            sender.sendMessage(new TextComponentString("This command can only be used by players."));
            return;
        }

        EntityPlayer player = (EntityPlayer) sender;
        World world = player.world;

        // Get player's position (block they're standing on)
        BlockPos pos = new BlockPos(player.posX, player.posY - 1, player.posZ);
        Block block = world.getBlockState(pos).getBlock();

        if (!(block instanceof BlockBuddingCertusQuartz)) {
            player.sendMessage(new TextComponentString("§cYou are not standing on a Budding Certus Quartz block!"));
            return;
        }

        // Calculate growth acceleration info
        int acceleratorCount = 0;
        int inactiveAcceleratorCount = 0;
        int additionalAttempts = 0;

        try {
            // Check for AE2 integration
            Class.forName("com.polarite.buddingpolar.integration.AE2Integration");
            acceleratorCount = AE2Integration.countAdjacentAccelerators(world, pos);
            additionalAttempts = AE2Integration.calculateAdditionalGrowthAttempts(acceleratorCount);

            // Count inactive accelerators (blocks that are Growth Accelerators but not powered)
            inactiveAcceleratorCount = countInactiveAccelerators(world, pos);
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
        player.sendMessage(new TextComponentString("§a=== Budding Certus Quartz Debug Info ==="));
        player.sendMessage(new TextComponentString("§bPosition: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()));
        player.sendMessage(new TextComponentString("§bActive Growth Accelerators: §a" + acceleratorCount));

        if (inactiveAcceleratorCount > 0) {
            player.sendMessage(
                new TextComponentString(
                    "§bInactive Growth Accelerators: §c" + inactiveAcceleratorCount + " §7(not powered)"));
        }

        player.sendMessage(new TextComponentString("§bTotal Growth Attempts: §e" + totalAttempts + " per update tick"));

        // Display the variable chance based on accelerator presence
        if (acceleratorCount > 0) {
            player.sendMessage(new TextComponentString("§bChance Per Attempt: §e20.0% (1 in 5) §a[With Accelerators]"));
        } else {
            player.sendMessage(
                new TextComponentString("§bChance Per Attempt: §e4.0% (1 in 25) §c[No Accelerators = 1/5th Speed]"));
        }

        player.sendMessage(
            new TextComponentString(
                "§bOverall Growth Chance: §e" + String.format("%.1f%%", overallGrowthChance) + " per update tick"));
        player.sendMessage(
            new TextComponentString(
                "§bAverage Time Per Stage: §e" + String.format("%.1f", averageTimeSeconds) + " seconds"));

        if (acceleratorCount == 0 && inactiveAcceleratorCount == 0) {
            player.sendMessage(
                new TextComponentString(
                    "§7Tip: Place AE2 Growth Accelerators adjacent to this block for faster growth!"));
        } else if (acceleratorCount == 0 && inactiveAcceleratorCount > 0) {
            player.sendMessage(
                new TextComponentString("§cYour Growth Accelerators need power! Connect them to an AE2 network."));
        } else {
            player.sendMessage(
                new TextComponentString("§aOptimized with " + acceleratorCount + " active Growth Accelerator(s)!"));
        }
    }

    /**
     * Counts inactive (unpowered) Growth Accelerators adjacent to the position
     */
    private int countInactiveAccelerators(World world, BlockPos pos) {
        int total = 0;
        int active = AE2Integration.countAdjacentAccelerators(world, pos);

        // Count total Growth Accelerators (active + inactive)
        if (AE2Integration.isGrowthAccelerator(world.getBlockState(pos.east()).getBlock())) total++;
        if (AE2Integration.isGrowthAccelerator(world.getBlockState(pos.west()).getBlock())) total++;
        if (AE2Integration.isGrowthAccelerator(world.getBlockState(pos.up()).getBlock())) total++;
        if (AE2Integration.isGrowthAccelerator(world.getBlockState(pos.down()).getBlock())) total++;
        if (AE2Integration.isGrowthAccelerator(world.getBlockState(pos.north()).getBlock())) total++;
        if (AE2Integration.isGrowthAccelerator(world.getBlockState(pos.south()).getBlock())) total++;

        return total - active; // inactive = total - active
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        return Collections.emptyList(); // No tab completion needed for this command
    }
}
