package com.mod.caca.sound;

import com.mod.caca.CacaMod;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

/**
 * Enregistrement de tous les sons personnalisés du mod CACA.
 * Chaque son doit avoir un fichier .ogg correspondant dans :
 * src/main/resources/assets/cacamod/sounds/
 * et une entrée dans sounds.json
 */
public class ModSounds {

    // Son joué quand le joueur fait caca (le "bruit de pet")
    public static final SoundEvent CACA_FART = register("caca_fart");

    // Son joué quand le bloc de CACA apparaît au sol
    public static final SoundEvent CACA_PLOP = register("caca_plop");

    // Son joué quand on casse le bloc de CACA
    public static final SoundEvent CACA_BREAK = register("caca_break");

    // Son joué quand le joueur mange du CACA
    public static final SoundEvent CACA_EAT = register("caca_eat");

    // Son spécial joué quand le joueur obtient un CACA DORÉ
    public static final SoundEvent CACA_GOLDEN_JINGLE = register("caca_golden_jingle");

    private static SoundEvent register(String path) {
        Identifier id = Identifier.of(CacaMod.MOD_ID, path);
        return Registry.register(
                Registries.SOUND_EVENT,
                id,
                SoundEvent.of(id)
        );
    }

    public static void init() {
        CacaMod.LOGGER.info("[CacaMod] Enregistrement des sons...");
    }
}
