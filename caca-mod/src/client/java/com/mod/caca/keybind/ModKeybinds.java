package com.mod.caca.keybind;

import com.mod.caca.CacaMod;
import com.mod.caca.network.FaireCacaPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

/**
 * Déclare la touche personnalisable "Faire caca".
 *
 * Cette touche utilise le système officiel Fabric KeyBinding, ce qui veut
 * dire qu'elle apparaît automatiquement dans :
 * Options Minecraft > Contrôles > Raccourcis clavier > catégorie "CACA Mod"
 * et peut y être réassignée librement par le joueur.
 *
 * Touche par défaut : P (libre par défaut dans Minecraft vanilla).
 */
public class ModKeybinds {

    // Catégorie de raccourcis dans laquelle la touche apparaîtra dans le menu
    public static final KeyBinding.Category CACA_CATEGORY =
            KeyBinding.Category.create(Identifier.of(CacaMod.MOD_ID, "main"));

    public static final KeyBinding FAIRE_CACA_KEY = new KeyBinding(
            "key.cacamod.faire_caca",      // clé de traduction (voir lang/fr_fr.json et en_us.json)
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_P,                // touche par défaut : P
            CACA_CATEGORY
    );

    /**
     * Enregistre la touche auprès du KeyBindingHelper de Fabric API.
     * Doit être appelé uniquement côté client (dans le ClientModInitializer).
     */
    public static void register() {
        CacaMod.LOGGER.info("[CacaMod] Enregistrement de la touche 'Faire caca'...");
        net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(FAIRE_CACA_KEY);
    }

    /**
     * À appeler à chaque tick client pour détecter les appuis sur la touche.
     * wasPressed() consomme les appuis en file d'attente : si le joueur
     * spam la touche, chaque appui sera traité (aucun cooldown, comme demandé).
     *
     * On se contente d'envoyer un paquet réseau vide vers le serveur :
     * c'est le serveur qui fait foi pour le monde (bloc, son, particules),
     * voir com.mod.caca.event.CacaEvent côté serveur.
     */
    public static void tick(MinecraftClient client) {
        while (FAIRE_CACA_KEY.wasPressed()) {
            if (client.player != null) {
                ClientPlayNetworking.send(new FaireCacaPayload());
            }
        }
    }
}
