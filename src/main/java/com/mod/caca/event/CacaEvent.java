package com.mod.caca.event;

import com.mod.caca.block.ModBlocks;
import com.mod.caca.network.FaireCacaPayload;
import com.mod.caca.sound.ModSounds;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Contient toute la logique SERVEUR du "CACA System" :
 * - Cooldown de 2 minutes 30 entre deux utilisations, par joueur
 * - Tirage aléatoire du CACA DORÉ (0,5% = 1/200)
 * - Pose du bloc de CACA au sol près du joueur
 * - Particules + son de "plop"
 * - Son de pet entendu uniquement dans un rayon EXACT de 10 blocs
 *
 * IMPORTANT : cette classe ne doit JAMAIS importer de classes client
 * (net.minecraft.client.*), car elle est chargée à la fois côté client et
 * côté serveur dédié (elle vit dans le common ModInitializer).
 */
public class CacaEvent {

    private static final Random RANDOM = new Random();

    // Chance exacte de CACA DORÉ : 0,5% = 1 chance sur 200
    private static final double CHANCE_CACA_DORE = 1.0 / 200.0;

    // Rayon d'écoute exact du bruit de pet, en blocs
    private static final double RAYON_SON_PET = 10.0;

    // Cooldown entre deux utilisations de la touche, en millisecondes (2 minutes 30)
    private static final long COOLDOWN_MS = 150_000L; // 2 minutes 30

    // Dernier moment (epoch ms) où chaque joueur a fait caca, par UUID.
    private static final Map<UUID, Long> LAST_USE = new ConcurrentHashMap<>();

    /**
     * Enregistre le récepteur du paquet côté SERVEUR.
     * À appeler une seule fois dans CacaMod#onInitialize().
     */
    public static void registerServerReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(FaireCacaPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            // On exécute la logique de jeu sur le thread principal du serveur
            context.server().execute(() -> handleFaireCaca(player));
        });
    }

    /**
     * Logique principale exécutée côté serveur quand un joueur fait caca.
     * Un cooldown de 2 minutes 30 par joueur est appliqué : si le joueur
     * appuie sur la touche avant la fin du délai, rien ne se passe (à part
     * un petit message lui indiquant le temps restant).
     */
    private static void handleFaireCaca(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        long now = System.currentTimeMillis();
        Long lastUse = LAST_USE.get(playerId);

        if (lastUse != null) {
            long elapsed = now - lastUse;
            if (elapsed < COOLDOWN_MS) {
                long remainingSeconds = (COOLDOWN_MS - elapsed + 999) / 1000; // arrondi au-dessus
                player.sendMessage(
                        Text.literal("Tu ne peux pas encore faire caca... (" + remainingSeconds + "s restantes)"),
                        true // action bar
                );
                return;
            }
        }
        LAST_USE.put(playerId, now);

        ServerWorld world = player.getEntityWorld();
        BlockPos playerPos = player.getBlockPos();

        // Position juste sous/à côté du joueur pour poser le bloc de CACA
        BlockPos cacaPos = findCacaPosition(world, playerPos);

        // --- Tirage aléatoire : CACA normal ou CACA DORÉ (0,5%) ---
        boolean isDore = RANDOM.nextDouble() < CHANCE_CACA_DORE;

        // --- Pose du bloc de CACA dans le monde ---
        world.setBlockState(cacaPos, ModBlocks.CACA_BLOCK.getDefaultState());

        // --- Particules humoristiques ---
        world.spawnParticles(
                ParticleTypes.POOF,
                cacaPos.getX() + 0.5, cacaPos.getY() + 0.3, cacaPos.getZ() + 0.5,
                12,      // nombre de particules
                0.25, 0.25, 0.25, // dispersion x/y/z
                0.05     // vitesse
        );
        world.spawnParticles(
                ParticleTypes.CRIT,
                cacaPos.getX() + 0.5, cacaPos.getY() + 0.4, cacaPos.getZ() + 0.5,
                6,
                0.2, 0.2, 0.2,
                0.0
        );

        // --- Son "plop" joué localement (audible normalement, distance vanilla) ---
        world.playSound(
                null,
                cacaPos,
                ModSounds.CACA_PLOP,
                SoundCategory.PLAYERS,
                1.0f,
                1.0f
        );

        // --- Le bruit de pet : entendu STRICTEMENT dans un rayon de 10 blocs ---
        playFartSoundInRadius(world, player, RAYON_SON_PET);

        // --- Si CACA DORÉ : son spécial + message dans le chat ---
        if (isDore) {
            world.playSound(
                    null,
                    cacaPos,
                    ModSounds.CACA_GOLDEN_JINGLE,
                    SoundCategory.PLAYERS,
                    1.0f,
                    1.0f
            );
            player.sendMessage(
                    Text.literal("✨ Incroyable ! Tu as fait un CACA DORÉ ! ✨"),
                    false
            );

            // Annonce dans le chat, visible par tous les joueurs connectés, avec le pseudo.
            String playerName = player.getGameProfile().getName();
            Text announcement = Text.literal(playerName + " A FAIT UN CACA DORÉ !");
            for (ServerPlayerEntity onlinePlayer : world.getServer().getPlayerManager().getPlayerList()) {
                onlinePlayer.sendMessage(announcement, false);
            }
        }

        // --- Stocke le type de CACA attendu pour ce bloc, pour le drop au cassage ---
        CacaBlockDropRegistry.registerPendingDrop(cacaPos, isDore);
    }

    /**
     * Trouve une position au sol valide près du joueur pour poser le CACA.
     * On utilise la position des pieds du joueur ; si le bloc est déjà
     * occupé (non remplaçable), on cherche juste à côté.
     */
    private static BlockPos findCacaPosition(ServerWorld world, BlockPos playerPos) {
        if (canPlaceCaca(world, playerPos)) {
            return playerPos;
        }
        BlockPos[] alternatives = new BlockPos[]{
                playerPos.north(),
                playerPos.south(),
                playerPos.east(),
                playerPos.west()
        };
        for (BlockPos alt : alternatives) {
            if (canPlaceCaca(world, alt)) {
                return alt;
            }
        }
        // Par défaut, on pose à la position du joueur même si ce n'est pas idéal
        return playerPos;
    }

    private static boolean canPlaceCaca(ServerWorld world, BlockPos pos) {
        return world.getBlockState(pos).isReplaceable();
    }

    /**
     * Joue le son de pet uniquement pour les joueurs situés à 10 blocs ou
     * moins du joueur qui a fait caca. On envoie le son manuellement à
     * chaque joueur dans le rayon plutôt que d'utiliser l'atténuation
     * vanilla, pour garantir un rayon EXACT et indépendant du volume du son.
     */
    private static void playFartSoundInRadius(ServerWorld world, ServerPlayerEntity source, double radius) {
        Vec3d sourcePos = source.getEntityPos();
        List<ServerPlayerEntity> nearbyPlayers = world.getPlayers();

        for (ServerPlayerEntity target : nearbyPlayers) {
            double distance = target.getEntityPos().distanceTo(sourcePos);
            if (distance <= radius) {
                target.playSoundToPlayer(
                        ModSounds.CACA_FART,
                        SoundCategory.PLAYERS,
                        1.0f,
                        1.0f + (RANDOM.nextFloat() * 0.2f - 0.1f) // pitch légèrement varié pour le fun
                );
            }
        }
    }
}
