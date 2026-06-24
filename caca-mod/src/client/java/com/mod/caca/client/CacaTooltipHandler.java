package com.mod.caca.client;

import com.mod.caca.item.ModItems;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Ajoute une ligne de tooltip humoristique sous le nom des items CACA et
 * CACA DORÉ, via l'event client ItemTooltipCallback de Fabric API.
 *
 * Les textes proviennent des clés de traduction :
 * - item.cacamod.caca.tooltip
 * - item.cacamod.caca_dore.tooltip
 * définies dans lang/fr_fr.json et lang/en_us.json.
 *
 * Cette classe vit dans le package "client" (et non "event") car
 * ItemTooltipCallback fait partie de l'API client de Fabric et ne doit
 * jamais être chargée sur un serveur dédié.
 */
public class CacaTooltipHandler {

    public static void register() {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            if (stack.getItem() == ModItems.CACA) {
                lines.add(Text.translatable("item.cacamod.caca.tooltip").formatted(Formatting.GRAY, Formatting.ITALIC));
            } else if (stack.getItem() == ModItems.CACA_DORE) {
                lines.add(Text.translatable("item.cacamod.caca_dore.tooltip").formatted(Formatting.GOLD, Formatting.ITALIC));
            }
        });
    }
}
