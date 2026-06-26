package com.mod.caca.item;

import com.mod.caca.CacaMod;
import com.mod.caca.sound.ModSounds;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Enregistrement des items du mod CACA :
 * - CACA : l'item de base, comestible, donne des effets négatifs/drôles.
 * - CACA_DORE : la version rare (0,5% de chance), comestible, effet différent.
 *
 * IMPORTANT (API 1.21.5+) : depuis Minecraft 1.21.5, FoodComponent ne porte
 * plus que (nutrition, saturation, canAlwaysEat). Les effets de statut à la
 * consommation se déclarent désormais via un ConsumableComponent séparé,
 * passé en second argument de Item.Settings#food(FoodComponent, ConsumableComponent).
 *
 * On utilise une sous-classe d'Item (CacaFoodItem) qui surcharge
 * finishUsing() afin de jouer un son spécifique et afficher un message
 * drôle dans le chat au moment exact où le joueur termine de manger.
 */
public class ModItems {

    private static final Random RANDOM = new Random();

    // Messages drôles affichés aléatoirement après avoir mangé du CACA normal
    private static final List<String> CACA_EAT_MESSAGES = List.of(
            "Beurk... tu viens vraiment de manger ça ?!",
            "Ton estomac proteste violemment.",
            "C'était... une expérience. Pas une bonne.",
            "Tu sens un goût étrange et persistant.",
            "Pourquoi as-tu fait ça ?",
            "Hmm… fait maison.",
            "Goût… discutable.",
            "Tu regrettes déjà.",
            "Pourquoi ? Juste… pourquoi ?",
            "+1 en dégoût",
            "Est-ce vraiment nécessaire ?",
            "Une expérience… unique.",
            "Tu touches le fond. Littéralement.",
            "100% bio, 100% toi.",
            "Zéro gaspillage.",
            "Même les creepers prennent leurs distances.",
            "Herobrine a vu ça.",
            "Erwan en veut aussi",
            "Antoine adore ça askip",
            "Ça manque d'épices",
            "Même pour 50€ je mange pas",
            "Nan frérot... No troll",
            "Sekolah",
            "Wesh doseeee",
            "Ha c'est la merdeee (littéralement)",
            "Je te parle plus de cette semaine, la semaine prochaine et dans deux semaines"
    );

    // Messages drôles affichés aléatoirement après avoir mangé du CACA DORÉ
    private static final List<String> CACA_DORE_EAT_MESSAGES = List.of(
            "✨ Un goût... étonnamment raffiné, pour du CACA. ✨",
            "Tu te sens étrangement plein d'énergie !",
            "C'est doré, donc forcément meilleur. Logique imparable.",
            "Une saveur presque royale... presque.",
            "Le shampoing de Mr Mercier",
            "Mais ça a le goût du Tasty crousty ?",
            "C'est très gai",
            "Ho le croque monsieur",
            "Ha mais c'est a moi ça ?",
            "Bah oui qui n'aime pas ça ?"
    );

    // ----------------------------------------------------------------
    // CACA normal
    // ----------------------------------------------------------------
    private static final FoodComponent CACA_FOOD = new FoodComponent.Builder()
            .nutrition(1)
            .saturationModifier(0.1f)
            .build();

    private static final ConsumableComponent CACA_CONSUMABLE = ConsumableComponents.food()
            .consumeSeconds(1.6f)
            .consumeEffect(new ApplyEffectsConsumeEffect(
                    new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0), // 10 secondes de nausée
                    1.0f
            ))
            .build();

    // ----------------------------------------------------------------
    // CACA DORÉ : plus nourrissant, effet différent (vitesse au lieu de faim)
    // ----------------------------------------------------------------
    private static final FoodComponent CACA_DORE_FOOD = new FoodComponent.Builder()
            .nutrition(4)
            .saturationModifier(0.6f)
            .build();

    private static final ConsumableComponent CACA_DORE_CONSUMABLE = ConsumableComponents.food()
            .consumeSeconds(1.6f)
            .consumeEffect(new ApplyEffectsConsumeEffect(
                    new StatusEffectInstance(StatusEffects.NAUSEA, 100, 0), // nausée plus courte (5s)
                    1.0f
            ))
            .consumeEffect(new ApplyEffectsConsumeEffect(
                    new StatusEffectInstance(StatusEffects.SPEED, 600, 1), // vitesse II pendant 30s
                    1.0f
            ))
            .build();

    public static final Item CACA = register(
            "caca",
            (settings) -> new CacaFoodItem(settings.food(CACA_FOOD, CACA_CONSUMABLE), ModSounds.CACA_EAT, CACA_EAT_MESSAGES)
    );

    public static final Item CACA_DORE = register(
            "caca_dore",
            (settings) -> new CacaFoodItem(settings.food(CACA_DORE_FOOD, CACA_DORE_CONSUMABLE).rarity(Rarity.EPIC), ModSounds.CACA_EAT, CACA_DORE_EAT_MESSAGES)
    );

    private interface ItemFactory {
        Item create(Item.Settings settings);
    }

    private static Item register(String path, ItemFactory factory) {
        RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(CacaMod.MOD_ID, path));
        Item.Settings settings = new Item.Settings().registryKey(registryKey);
        Item item = factory.create(settings);
        return Registry.register(Registries.ITEM, registryKey, item);
    }

    public static void init() {
        CacaMod.LOGGER.info("[CacaMod] Enregistrement des items...");
    }

    /**
     * Item comestible personnalisé : en plus des effets gérés par
     * FoodComponent/ConsumableComponent, joue un son dédié et affiche un
     * message drôle dans le chat du joueur quand il termine de manger.
     */
    public static class CacaFoodItem extends Item {

        private final SoundEvent eatSound;
        private final List<String> funnyMessages;

        public CacaFoodItem(Item.Settings settings, SoundEvent eatSound, List<String> funnyMessages) {
            super(settings);
            this.eatSound = eatSound;
            this.funnyMessages = funnyMessages;
        }

        @Override
        public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
            // On laisse Minecraft gérer la nutrition / les effets de statut
            // normalement (comportement par défaut de la nourriture).
            ItemStack result = super.finishUsing(stack, world, user);

            if (!world.isClient()) {
                world.playSound(
                        null,
                        user.getBlockPos(),
                        eatSound,
                        SoundCategory.PLAYERS,
                        1.0f,
                        1.0f
                );

                if (user instanceof PlayerEntity player && !funnyMessages.isEmpty()) {
                    String message = funnyMessages.get(RANDOM.nextInt(funnyMessages.size()));
                    player.sendMessage(Text.literal(message), true); // true = message dans l'action bar
                }
            }

            return result;
        }
    }
}
