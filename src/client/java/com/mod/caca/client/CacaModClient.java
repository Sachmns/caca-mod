package com.mod.caca.client;

import com.mod.caca.CacaMod;
import com.mod.caca.keybind.ModKeybinds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

/**
 * Point d'entrée client du mod CACA.
 *
 * Responsable de :
 * - l'enregistrement de la touche "Faire caca" (visible et réassignable
 *   dans Options > Contrôles > Raccourcis clavier),
 * - l'écoute du tick client pour détecter les appuis sur cette touche,
 * - l'ajout des tooltips humoristiques sur les items.
 */
public class CacaModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CacaMod.LOGGER.info("[CacaMod] Initialisation côté client...");

        // Enregistre la touche "Faire caca"
        ModKeybinds.register();

        // Ajoute les tooltips humoristiques sur les items CACA / CACA DORÉ
        CacaTooltipHandler.register();

        // À chaque tick client, on vérifie si la touche a été pressée
        ClientTickEvents.END_CLIENT_TICK.register(ModKeybinds::tick);
    }
}
