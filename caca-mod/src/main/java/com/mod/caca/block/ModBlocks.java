package com.mod.caca.block;

import com.mod.caca.CacaMod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

/**
 * Enregistrement du "petit tas de CACA" qui apparaît au sol quand le joueur
 * utilise la touche "Faire caca".
 *
 * C'est volontairement un Block (et non une simple ItemEntity), pour garder
 * un vrai temps de minage configurable via .strength() (voir point 3 de la
 * demande utilisateur) : une ItemEntity se ramasserait instantanément en
 * marchant dessus, sans aucun délai possible.
 *
 * L'aspect "petit tas" (et non "gros cube") est entièrement géré du côté
 * des ressources (voir models/block/caca_block.json, qui utilise un modèle
 * réduit façon "petit tas" plutôt que cube_all), pas du code Java.
 */
public class ModBlocks {

    public static final Block CACA_BLOCK = register(
            "caca_block",
            (settings) -> new Block(settings),
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.DIRT_BROWN)
                    .strength(0.5f, 0.5f) // un peu plus long à casser qu'avant (point 3 de la demande)
                    .sounds(BlockSoundGroup.SLIME) // son "mou" pour rappeler une texture organique
                    .nonOpaque() // pour permettre un modèle non-cube (petit tas)
                    .dropsNothing() // le drop (CACA ou CACA DORÉ) est géré manuellement dans CacaBlockBreakHandler
    );

    /**
     * Interface fonctionnelle simple pour factoriser la création de blocs.
     */
    private interface BlockFactory {
        Block create(AbstractBlock.Settings settings);
    }

    private static Block register(String path, BlockFactory factory, AbstractBlock.Settings settings) {
        RegistryKey<Block> registryKey = keyOf(path);
        Block block = factory.create(settings.registryKey(registryKey));
        return Registry.register(Registries.BLOCK, registryKey, block);
    }

    private static RegistryKey<Block> keyOf(String path) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(CacaMod.MOD_ID, path));
    }

    /**
     * Enregistre l'item correspondant au bloc (le "BlockItem"), permettant
     * de l'avoir dans l'inventaire / la barre de recherche créative.
     */
    private static void registerBlockItem(String path, Block block) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(CacaMod.MOD_ID, path));
        Item.Settings settings = new Item.Settings().registryKey(itemKey).useBlockPrefixedTranslationKey();
        Registry.register(
                Registries.ITEM,
                itemKey,
                new BlockItem(block, settings)
        );
    }

    public static void init() {
        CacaMod.LOGGER.info("[CacaMod] Enregistrement des blocs...");
        registerBlockItem("caca_block", CACA_BLOCK);
    }
}
