package com.mod.caca.network;

import com.mod.caca.CacaMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * Paquet réseau vide envoyé du client vers le serveur lorsque le joueur
 * appuie sur la touche "Faire caca".
 *
 * On utilise un paquet réseau (et pas une action 100% client) car :
 * - faire apparaître un bloc dans le monde doit être autoritaire côté serveur,
 * - le son de pet doit être entendu par TOUS les joueurs proches (pas juste
 *   celui qui appuie sur la touche), ce qui nécessite une diffusion serveur.
 */
public record FaireCacaPayload() implements CustomPayload {

    public static final CustomPayload.Id<FaireCacaPayload> ID =
            new CustomPayload.Id<>(Identifier.of(CacaMod.MOD_ID, "faire_caca"));

    public static final PacketCodec<PacketByteBuf, FaireCacaPayload> CODEC =
            PacketCodec.unit(new FaireCacaPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
