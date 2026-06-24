package com.mod.caca.event;

import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Petit registre en mémoire qui retient, pour chaque position de bloc de
 * CACA posé, s'il s'agit d'un CACA normal ou d'un CACA DORÉ.
 *
 * Pourquoi ce mécanisme : le bloc CACA_BLOCK est un seul et même bloc
 * Minecraft (une seule loot table possible par défaut). Pour qu'un même
 * bloc puisse parfois donner un CACA et parfois un CACA DORÉ selon le
 * tirage effectué à la pose, on mémorise ici le résultat du tirage par
 * position, et on l'utilise au moment du cassage (voir BlockBreakHandler)
 * avant de nettoyer l'entrée.
 *
 * Remarque : ce registre est volontairement simple (pas de persistance
 * disque). Si le serveur redémarre avant que le bloc soit cassé, le bloc
 * droppera un CACA normal par défaut (comportement de repli sûr).
 */
public class CacaBlockDropRegistry {

    private static final Map<BlockPos, Boolean> PENDING_DORE = new ConcurrentHashMap<>();

    /** Enregistre le résultat du tirage pour une position donnée. */
    public static void registerPendingDrop(BlockPos pos, boolean isDore) {
        PENDING_DORE.put(pos.toImmutable(), isDore);
    }

    /**
     * Consomme (lit puis supprime) l'information pour une position donnée.
     * Retourne true si un CACA DORÉ doit être donné, false sinon (par défaut).
     */
    public static boolean consumeIsDore(BlockPos pos) {
        Boolean value = PENDING_DORE.remove(pos.toImmutable());
        return value != null && value;
    }
}
