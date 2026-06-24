package com.mod.caca.event;

import com.mod.caca.block.ModBlocks;
import com.mod.caca.item.ModItems;
import com.mod.caca.sound.ModSounds;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Gère le cassage du bloc CACA_BLOCK :
 * - donne l'item CACA ou CACA DORÉ selon le tirage effectué à la pose,
 * - joue le son de cassage spécifique,
 * - empêche le drop par défaut du BlockItem (puisqu'on gère le drop nous-mêmes).
 */
public class CacaBlockBreakHandler {

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (state.getBlock() == ModBlocks.CACA_BLOCK) {
                onCacaBlockBroken(world, pos);
            }
        });
    }

    private static void onCacaBlockBroken(World world, BlockPos pos) {
        boolean isDore = CacaBlockDropRegistry.consumeIsDore(pos);

        ItemStack drop = isDore
                ? new ItemStack(ModItems.CACA_DORE)
                : new ItemStack(ModItems.CACA);

        // On fait apparaître l'item dans le monde nous-mêmes (le bloc n'a
        // pas de loot table par défaut pointant vers CACA, donc pas de
        // double-drop possible).
        if (!world.isClient) {
            ItemEntity itemEntity = new ItemEntity(
                    world,
                    pos.getX() + 0.5,
                    pos.getY() + 0.2,
                    pos.getZ() + 0.5,
                    drop
            );
            world.spawnEntity(itemEntity);

            world.playSound(
                    null,
                    pos,
                    ModSounds.CACA_BREAK,
                    SoundCategory.BLOCKS,
                    1.0f,
                    1.0f
            );
        }
    }
}
