package com.mod.caca;

import com.mod.caca.block.ModBlocks;
import com.mod.caca.event.CacaBlockBreakHandler;
import com.mod.caca.event.CacaEvent;
import com.mod.caca.item.ModItems;
import com.mod.caca.network.FaireCacaPayload;
import com.mod.caca.sound.ModSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Point d'entrée principal du mod CACA (commun client + serveur).
 *
 * C'est ici que tout est enregistré dans l'ordre :
 * sons -> items -> blocs -> réseau -> events -> groupe créatif.
 */
public class CacaMod implements ModInitializer {

    public static final String MOD_ID = "cacamod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Groupe créatif dédié au mod, pour retrouver facilement les items
    // dans l'inventaire créatif.
    public static final RegistryKey<ItemGroup> CACA_GROUP_KEY = RegistryKey.of(
            RegistryKeys.ITEM_GROUP, Identifier.of(MOD_ID, "caca_group")
    );

    @Override
    public void onInitialize() {
        LOGGER.info("[CacaMod] Initialisation du CACA System...");

        // 1. Sons
        ModSounds.init();

        // 2. Items (CACA, CACA DORÉ)
        ModItems.init();

        // 3. Blocs (CACA_BLOCK) + son BlockItem
        ModBlocks.init();

        // 4. Enregistrement du type de paquet réseau (obligatoire des deux côtés)
        PayloadTypeRegistry.playC2S().register(FaireCacaPayload.ID, FaireCacaPayload.CODEC);

        // 5. Réception du paquet côté serveur + logique de jeu
        CacaEvent.registerServerReceiver();

        // 6. Gestion du cassage du bloc CACA pour donner le bon item
        CacaBlockBreakHandler.register();

        // 7. Groupe créatif
        registerItemGroup();

        LOGGER.info("[CacaMod] CACA System prêt. Que la fête commence.");
    }

    private void registerItemGroup() {
        Registry.register(
                Registries.ITEM_GROUP,
                CACA_GROUP_KEY,
                FabricItemGroup.builder()
                        .icon(() -> new ItemStack(ModItems.CACA))
                        .displayName(Text.translatable("itemGroup.cacamod.caca_group"))
                        .build()
        );

        ItemGroupEvents.modifyEntriesEvent(CACA_GROUP_KEY).register(entries -> {
            entries.add(ModItems.CACA);
            entries.add(ModItems.CACA_DORE);
            entries.add(ModBlocks.CACA_BLOCK.asItem());
        });
    }
}
