package net.evn.peekaboomod;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SculkShriekerBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.compressors.lz77support.LZ77Compressor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Consumer;

public class StoreOfGameProbabilities {

    // ENUM IDS:
    public static final int SPEC_VAR__ZOMBIE__ENUM_ID = 0;
    public static final int SPEC_VAR__SKELETON__ENUM_ID = 1;
    public static final int SPEC_VAR__CREEPER__ENUM_ID = 2;
    public static final int SPEC_VAR__SPIDER__ENUM_ID = 3;
    public static final int BLOCK_BREAK_EVENT_TYPE__ENUM_ID = 4;
    public static final int SPAWN_MOB_EVENT__ENUM_ID = 5;
    public static final int PLACE_BLOCK_EVENT__ENUM_ID = 6;
    public static final int DO_EVENT__ENUM_ID = 7;
    public static final int MOB_PASSIVE__ENUM_ID = 8;
    public static final int MOB_NEUTRAL__ENUM_ID = 9;
    public static final int MOB_HOSTILE__ENUM_ID = 10;
    public static final int MOB_BOSS_HOSTILE__ENUM_ID = 11;
    public static final int BLOCKS_NORMAL__ENUM_ID = 12;
    public static final int BLOCKS_OBSTRUCTIVE__ENUM_ID = 13;
    public static final int BLOCKS_TREASURE__ENUM_ID = 14;
    public static final int SPECIAL_EVENT__ENUM_ID = 15;
    public static final int SPEC_VAR__PLACE_MOB_SPAWNER__ENUM_ID = 16;
    public static final int SPEC_VAR__PLACE_ANY_LEAVES__ENUM_ID = 17;
    public static final int SPEC_VAR__PLACE_ANY_LOG__ENUM_ID = 18;
    public static final int SPEC_VAR__PLACE_ANY_STONE__ENUM_ID = 19;
    public static final int SPEC_VAR__PLACE_ANY_DIRT__ENUM_ID = 20;
    public static final int MOB_ELITE_HOSTILE__ENUM_ID = 21;

    ////// END OF IDS


    // SPECIFIC VARIATIONS
    public WeightedEnumDetails SPEC_VAR__ZOMBIE__EnumDetails;
    public enum SPEC_VAR__ZOMBIE implements IWeightedEnumSample {
        NORMAL(85, SampleType.NODE),
        PLAYER_MIRRORED(5, SampleType.NODE),
        KNOCKBACK(5, SampleType.NODE),
        SPEED(5, SampleType.NODE);

        public final int weight;
        public final SampleType sample_type;
        public Consumer<BlockEvent.BreakEvent> func_for_summon;
        SPEC_VAR__ZOMBIE(int arg_weight, SampleType arg_sample_type) {
            weight = arg_weight;
            sample_type = arg_sample_type;
        }

        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public SampleType getSampleType() {
            return sample_type;
        }

        @Override
        public int getLinkToEnumDetId() {
            return 0;
        }

        @Override
        public void executeSampleTypeAsNode(BlockEvent.BreakEvent event) {
            switch (this) {
                case NORMAL -> executeBreakEvent__Normal(event);
                case PLAYER_MIRRORED -> executeBreakEvent__PlayerMirrored(event);
                case KNOCKBACK -> executeBreakEvent__Knockback(event);
                case SPEED -> executeBreakEvent__Speed(event);
            }
        }


        // enum specific funcs
        private void executeBreakEvent__Normal(BlockEvent.BreakEvent event) {
            Entity mob = summonMob_BasicTemplate(event, EntityType.ZOMBIE);
        }

        private void executeBreakEvent__PlayerMirrored(BlockEvent.BreakEvent event) {
            Entity mob = summonMob_BasicTemplate(event, EntityType.ZOMBIE);
            Player player = event.getPlayer();
            Iterable<ItemStack> armorSlots = player.getArmorSlots();

            for (ItemStack itemstack: armorSlots) {
                EquipmentSlot equipmentSlot = Player.getEquipmentSlotForItem(itemstack);
                mob.setItemSlot(equipmentSlot, itemstack);
            }

            Iterable<ItemStack> handSlots = player.getHandSlots();
            for (ItemStack itemstack: handSlots) {
                EquipmentSlot equipmentSlot = Player.getEquipmentSlotForItem(itemstack);
                mob.setItemSlot(equipmentSlot, itemstack);
            }

        }

        private void executeBreakEvent__Knockback(BlockEvent.BreakEvent event) {
            Entity mob = summonMob_BasicTemplate(event, EntityType.ZOMBIE);

            ItemStack knockbackTool = new ItemStack(Items.GOLDEN_SHOVEL);
            knockbackTool.enchant(Enchantments.KNOCKBACK, 6);
            mob.setItemSlot(EquipmentSlot.MAINHAND, knockbackTool);
        }

        private void executeBreakEvent__Speed(BlockEvent.BreakEvent event) {
            Zombie mob = (Zombie) summonMob_BasicTemplate(event, EntityType.ZOMBIE);

            Multimap<Attribute, AttributeModifier> map = HashMultimap.create();
            var attributeModifier = new AttributeModifier("attributemodifier",2d, AttributeModifier.Operation.MULTIPLY_TOTAL);
            map.put(Attributes.MOVEMENT_SPEED,  attributeModifier);
            mob.getAttributes().addTransientAttributeModifiers(map);
        }

    }

    public WeightedEnumDetails SPEC_VAR__SKELETON__EnumDetails;
    public enum SPEC_VAR__SKELETON implements IWeightedEnumSample {
        NORMAL(85, SampleType.NODE),
        PLAYER_MIRRORED(6, SampleType.NODE),
        PUNCH(6, SampleType.NODE),
        SPEED(3, SampleType.NODE);

        public final int weight;
        public final SampleType sample_type;
        SPEC_VAR__SKELETON(int arg_weight, SampleType arg_sample_type) {
            weight = arg_weight;
            sample_type = arg_sample_type;
        }

        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public SampleType getSampleType() {
            return sample_type;
        }

        @Override
        public int getLinkToEnumDetId() {
            return 0;
        }

        @Override
        public void executeSampleTypeAsNode(BlockEvent.BreakEvent event) {
            switch (this) {
                case NORMAL -> executeBreakEvent__Normal(event);
                case PLAYER_MIRRORED -> executeBreakEvent__PlayerMirrored(event);
                case PUNCH -> executeBreakEvent__Punch(event);
                case SPEED -> executeBreakEvent__Speed(event);
            }
        }

        // enum specific funcs
        private void executeBreakEvent__Normal(BlockEvent.BreakEvent event) {
            Entity mob = summonMob_BasicTemplate(event, EntityType.SKELETON);
        }

        private void executeBreakEvent__PlayerMirrored(BlockEvent.BreakEvent event) {
            Entity mob = summonMob_BasicTemplate(event, EntityType.SKELETON);
            Player player = event.getPlayer();
            Iterable<ItemStack> armorSlots = player.getArmorSlots();

            for (ItemStack itemstack: armorSlots) {
                EquipmentSlot equipmentSlot = Player.getEquipmentSlotForItem(itemstack);
                mob.setItemSlot(equipmentSlot, itemstack);
            }
        }

        private void executeBreakEvent__Punch(BlockEvent.BreakEvent event) {
            Entity mob = summonMob_BasicTemplate(event, EntityType.SKELETON);

            ItemStack knockbackTool = new ItemStack(Items.BOW);
            knockbackTool.enchant(Enchantments.PUNCH_ARROWS, 4);
            mob.setItemSlot(EquipmentSlot.MAINHAND, knockbackTool);
        }

        private void executeBreakEvent__Speed(BlockEvent.BreakEvent event) {
            Skeleton mob = (Skeleton) summonMob_BasicTemplate(event, EntityType.SKELETON);

            Multimap<Attribute, AttributeModifier> map = HashMultimap.create();
            var attributeModifier = new AttributeModifier("attributemodifier",2d, AttributeModifier.Operation.MULTIPLY_TOTAL);
            map.put(Attributes.MOVEMENT_SPEED,  attributeModifier);
            mob.getAttributes().addTransientAttributeModifiers(map);
        }

    }

    public WeightedEnumDetails SPEC_VAR__CREEPER__EnumDetails;
    public enum SPEC_VAR__CREEPER implements IWeightedEnumSample {
        NORMAL(95, SampleType.NODE),
        CHARGED(5, SampleType.NODE);

        public final int weight;
        public final SampleType sample_type;
        SPEC_VAR__CREEPER(int arg_weight, SampleType arg_sample_type) {
            weight = arg_weight;
            sample_type = arg_sample_type;
        }

        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public SampleType getSampleType() {
            return sample_type;
        }

        @Override
        public int getLinkToEnumDetId() {
            return 0;
        }

        @Override
        public void executeSampleTypeAsNode(BlockEvent.BreakEvent event) {

            switch (this) {
                case NORMAL -> executeBreakEvent__Normal(event);
                case CHARGED -> executeBreakEvent__Charged(event);
            }

        }

        // enum specific funcs

        private void executeBreakEvent__Normal(BlockEvent.BreakEvent event) {
            Entity mob = summonMob_BasicTemplate(event, EntityType.CREEPER);
        }

        private void executeBreakEvent__Charged(BlockEvent.BreakEvent event) {
            Creeper mob = (Creeper) summonMob_BasicTemplate(event, EntityType.CREEPER);
            CompoundTag tag = mob.serializeNBT();
            tag.putBoolean("powered", true);
            mob.deserializeNBT(tag);
        }

    }

    public WeightedEnumDetails SPEC_VAR__SPIDER__EnumDetails;
    public enum SPEC_VAR__SPIDER implements IWeightedEnumSample {

        NORMAL(95, SampleType.NODE),
        WITH_POTION_EFFECT(5, SampleType.NODE);

        public final int weight;
        public final SampleType sample_type;
        SPEC_VAR__SPIDER(int arg_weight, SampleType arg_sample_type) {
            weight = arg_weight;
            sample_type = arg_sample_type;
        }

        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public SampleType getSampleType() {
            return sample_type;
        }

        @Override
        public int getLinkToEnumDetId() {
            return 0;
        }

        @Override
        public void executeSampleTypeAsNode(BlockEvent.BreakEvent event) {
            switch (this) {
                case NORMAL -> executeBreakEvent__Normal(event);
                case WITH_POTION_EFFECT -> executeBreakEvent__WithPotionEffect(event);
            }
        }

        // enum specific funcs

        private void executeBreakEvent__Normal(BlockEvent.BreakEvent event) {
            Entity mob = summonMob_BasicTemplate(event, EntityType.SPIDER);
        }

        private void executeBreakEvent__WithPotionEffect(BlockEvent.BreakEvent event) {
            Spider mob = (Spider) summonMob_BasicTemplate(event, EntityType.SPIDER);

            MobEffect randMobEffect = StoreOfGameProbabilities.getRandomFromArrayList(StoreOfGameProbabilities.benefitialMobEffects);
            var effectInstance = new MobEffectInstance(randMobEffect, 5000);
            mob.addEffect(effectInstance);
        }

    }

    ///////////////////// EVENT TYPES

    public WeightedEnumDetails BLOCK_BREAK_EVENT_TYPE__EnumDetails;
    public enum BLOCK_BREAK_EVENT_TYPE implements IWeightedEnumSample {

        SPAWN_MOB(90, SPAWN_MOB_EVENT__ENUM_ID),
        PLACE_BLOCK(5, PLACE_BLOCK_EVENT__ENUM_ID),
        DO_EVENT(5, DO_EVENT__ENUM_ID);


        public final int weight;
        public final SampleType sample_type;
        public int link_to_enum_det_id;
        BLOCK_BREAK_EVENT_TYPE(int arg_weight, SampleType arg_sample_type) {
            weight = arg_weight;
            sample_type = arg_sample_type;
        }
        BLOCK_BREAK_EVENT_TYPE(int arg_weight) {
            weight = arg_weight;
            sample_type = SampleType.LINK;
        }
        BLOCK_BREAK_EVENT_TYPE(int arg_weight, int arg_link_to_enum_det_id) {
            weight = arg_weight;
            sample_type = SampleType.LINK;
            link_to_enum_det_id = arg_link_to_enum_det_id;
        }

        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public SampleType getSampleType() {
            return sample_type;
        }

        @Override
        public int getLinkToEnumDetId() {
            return link_to_enum_det_id;
        }

        @Override
        public void executeSampleTypeAsNode(BlockEvent.BreakEvent event) {

        }

        // enum specific funcs



    }

    public WeightedEnumDetails SPAWN_MOB_EVENT__EnumDetails;
    public enum SPAWN_MOB_EVENT implements IWeightedEnumSample {


        SPAWN_PASSIVE_MOB(34 * 5, MOB_PASSIVE__ENUM_ID),
        SPAWN_NEUTRAL_MOB((32 * 5) + 4, MOB_NEUTRAL__ENUM_ID),
        SPAWN_HOSTILE_MOB(32 * 5, MOB_HOSTILE__ENUM_ID),
        SPAWN_ELITE_HOSTILE_MOB(1*5, MOB_ELITE_HOSTILE__ENUM_ID),
        SPAWN_BOSS_HOSTILE_MOB(1, MOB_BOSS_HOSTILE__ENUM_ID);

        public final int weight;
        public final SampleType sample_type;
        public int link_to_enum_det_id;
        SPAWN_MOB_EVENT(int arg_weight, SampleType arg_sample_type) {
            weight = arg_weight;
            sample_type = arg_sample_type;
        }
        SPAWN_MOB_EVENT(int arg_weight) {
            weight = arg_weight;
            sample_type = SampleType.LINK;
        }
        SPAWN_MOB_EVENT(int arg_weight, int arg_link_to_enum_det_id) {
            weight = arg_weight;
            sample_type = SampleType.LINK;
            link_to_enum_det_id = arg_link_to_enum_det_id;
        }

        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public SampleType getSampleType() {
            return sample_type;
        }

        @Override
        public int getLinkToEnumDetId() {
            return link_to_enum_det_id;
        }

        @Override
        public void executeSampleTypeAsNode(BlockEvent.BreakEvent event) {

        }

        // enum specific funcs


    }

    public WeightedEnumDetails PLACE_BLOCK_EVENT__EnumDetails;
    public enum PLACE_BLOCK_EVENT implements IWeightedEnumSample{

        PLACE_NORMAL_BLOCK(94, BLOCKS_NORMAL__ENUM_ID),
        PLACE_OBSTRUCTIVE_BLOCK(3, BLOCKS_OBSTRUCTIVE__ENUM_ID),
        PLACE_TREASURE_BLOCK(3, BLOCKS_TREASURE__ENUM_ID);

        public final int weight;
        public final SampleType sample_type;
        public int link_to_enum_det_id;
        PLACE_BLOCK_EVENT(int arg_weight, SampleType arg_sample_type) {
            weight = arg_weight;
            sample_type = arg_sample_type;
        }
        PLACE_BLOCK_EVENT(int arg_weight) {
            weight = arg_weight;
            sample_type = SampleType.LINK;
        }
        PLACE_BLOCK_EVENT(int arg_weight, int arg_link_to_enum_det_id) {
            weight = arg_weight;
            sample_type = SampleType.LINK;
            link_to_enum_det_id = arg_link_to_enum_det_id;
        }


        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public SampleType getSampleType() {
            return sample_type;
        }

        @Override
        public int getLinkToEnumDetId() {
            return link_to_enum_det_id;
        }

        @Override
        public void executeSampleTypeAsNode(BlockEvent.BreakEvent event) {

        }

        // enum specific funcs



    }

    public WeightedEnumDetails DO_EVENT__EnumDetails;
    public enum DO_EVENT implements IWeightedEnumSample{

        SPECIAL_EVENT(100, SPECIAL_EVENT__ENUM_ID);

        public final int weight;
        public final SampleType sample_type;
        public int link_to_enum_det_id;
        DO_EVENT(int arg_weight, SampleType arg_sample_type) {
            weight = arg_weight;
            sample_type = arg_sample_type;
        }
        DO_EVENT(int arg_weight) {
            weight = arg_weight;
            sample_type = SampleType.LINK;
        }
        DO_EVENT(int arg_weight, int arg_link_to_enum_det_id) {
            weight = arg_weight;
            sample_type = SampleType.LINK;
            link_to_enum_det_id = arg_link_to_enum_det_id;
        }

        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public SampleType getSampleType() {
            return sample_type;
        }

        @Override
        public int getLinkToEnumDetId() {
            return link_to_enum_det_id;
        }

        @Override
        public void executeSampleTypeAsNode(BlockEvent.BreakEvent event) {

        }

        // enum specific funcs


    }

    //// MOB DEFINITIONS

    public WeightedEnumDetails MOBS_PASSIVE__EnumDetails;
    public enum MOBS_PASSIVE implements IWeightedEnumSample {
        ALLAY(EntityType.ALLAY),
        ARMOR_STAND(EntityType.ARMOR_STAND),
        AXOLOTL(EntityType.AXOLOTL),
        BAT(EntityType.BAT),
        BOAT(EntityType.BOAT),
        CAMEL(EntityType.CAMEL),
        CAT(EntityType.CAT),
        CHICKEN(EntityType.CHICKEN),
        COD(EntityType.COD),
        COW(EntityType.COW),
        DONKEY(EntityType.DONKEY),
        FOX(EntityType.FOX),
        FROG(EntityType.FROG),
        GLOW_SQUID(EntityType.GLOW_SQUID),
        HORSE(EntityType.HORSE),
        MINECART(EntityType.MINECART),
        MOOSHROOM(EntityType.MOOSHROOM),
        MULE(EntityType.MULE),
        OCELOT(EntityType.OCELOT),
        PARROT(EntityType.PARROT),
        PIG(EntityType.PIG),
        PUFFERFISH(EntityType.PUFFERFISH),
        RABBIT(EntityType.RABBIT),
        SALMON(EntityType.SALMON),
        SHEEP(EntityType.SHEEP),
        SNIFFER(EntityType.SNIFFER),
        SNOW_GOLEM(EntityType.SNOW_GOLEM),
        SQUID(EntityType.SQUID),
        STRIDER(EntityType.STRIDER),
        TADPOLE(EntityType.TADPOLE),
        TROPICAL_FISH(EntityType.TROPICAL_FISH),
        TURTLE(EntityType.TURTLE),
        VILLAGER(EntityType.VILLAGER),
        WANDERING_VILLAGER(EntityType.WANDERING_TRADER);

        public final int weight;
        public final SampleType sample_type;
        public EntityType<?> entity_type_to_use;
        MOBS_PASSIVE(int arg_weight, SampleType arg_sample_type) {
            weight = arg_weight;
            sample_type = arg_sample_type;
        }
        MOBS_PASSIVE(int arg_weight) {
            weight = arg_weight;
            sample_type = SampleType.NODE;
        }
        MOBS_PASSIVE() {
            weight = 1;
            sample_type = SampleType.NODE;
        }
        MOBS_PASSIVE(SampleType arg_sample_type) {
            weight = 1;
            sample_type = arg_sample_type;
        }
        MOBS_PASSIVE(EntityType<?> arg_entity_type_to_use) {
            weight = 1;
            sample_type = SampleType.NODE;
            entity_type_to_use = arg_entity_type_to_use;
        }

        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public SampleType getSampleType() {
            return sample_type;
        }

        @Override
        public int getLinkToEnumDetId() {
            return 0;
        }

        @Override
        public void executeSampleTypeAsNode(BlockEvent.BreakEvent event) {
            summonMob_BasicTemplate(event, entity_type_to_use);
        }

        // enum specific funcs


    }

    public WeightedEnumDetails MOBS_NEUTRAL__EnumDetails;
    public enum MOBS_NEUTRAL implements IWeightedEnumSample {
        BEE(EntityType.BEE),
        DOLPHIN(EntityType.DOLPHIN),
        ENDERMAN(EntityType.ENDERMAN),
        GOAT(EntityType.GOAT),
        IRON_GOLEM(EntityType.IRON_GOLEM),
        LLAMA(EntityType.LLAMA),
        MINECART_TNT(EntityType.TNT_MINECART),
        PANDA(EntityType.PANDA),
        PIGLIN(EntityType.PIGLIN),
        POLAR_BEAR(EntityType.POLAR_BEAR),
        WOLF(EntityType.WOLF),
        ZOMBIFIED_PIGLIN(EntityType.ZOMBIFIED_PIGLIN);



        public final int weight;
        public final SampleType sample_type;
        public EntityType<?> entity_type_to_use;
        MOBS_NEUTRAL(int arg_weight, SampleType arg_sample_type) {
            weight = arg_weight;
            sample_type = arg_sample_type;
        }
        MOBS_NEUTRAL(int arg_weight) {
            weight = arg_weight;
            sample_type = SampleType.NODE;
        }
        MOBS_NEUTRAL() {
            weight = 1;
            sample_type = SampleType.NODE;
        }
        MOBS_NEUTRAL(SampleType arg_sample_type) {
            weight = 1;
            sample_type = arg_sample_type;
        }
        MOBS_NEUTRAL(EntityType<?> arg_entity_type_to_use) {
            weight = 1;
            sample_type = SampleType.NODE;
            entity_type_to_use = arg_entity_type_to_use;
        }


        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public SampleType getSampleType() {
            return sample_type;
        }

        @Override
        public int getLinkToEnumDetId() {
            return 0;
        }

        @Override
        public void executeSampleTypeAsNode(BlockEvent.BreakEvent event) {
            summonMob_BasicTemplate(event, entity_type_to_use);
        }

        // enum specific funcs


    }

    public WeightedEnumDetails MOBS_HOSTILE__EnumDetails;
    public enum MOBS_HOSTILE implements IWeightedEnumSample {


        BLAZE(EntityType.BLAZE),
        CAVE_SPIDER(EntityType.CAVE_SPIDER),
        CREEPER(SampleType.LINK, SPEC_VAR__CREEPER__ENUM_ID),
        DROWNED(EntityType.DROWNED),
        ENDERMITE(EntityType.ENDERMITE),
        GHAST(EntityType.GHAST),
        GUARDIAN(EntityType.GUARDIAN),
        HOGLIN(EntityType.HOGLIN),
        HUSK(EntityType.HUSK),
        //KILLER_BUNNY(EntityType.BU),
        MAGMA_CUBE(EntityType.MAGMA_CUBE),
        PHANTOM(EntityType.PHANTOM),
        PILLAGER(EntityType.PILLAGER),
        SHULKER(EntityType.SHULKER),
        SILVERFISH(EntityType.SILVERFISH),
        SKELETON(SampleType.LINK, SPEC_VAR__SKELETON__ENUM_ID),
        SLIME(EntityType.SLIME),
        SPIDER(SampleType.LINK, SPEC_VAR__SPIDER__ENUM_ID),
        STRAY(EntityType.STRAY),
        VEX(EntityType.VEX),
        WITCH(EntityType.WITCH),
        WITHER_SKELETON(EntityType.WITHER_SKELETON),
        ZOGLIN(EntityType.ZOGLIN),
        ZOMBIE(SampleType.LINK, SPEC_VAR__ZOMBIE__ENUM_ID);

        //SPIDER(SampleType.LINK, SPEC_VAR__SPIDER__ENUM_ID);

        public final int weight;
        public final SampleType sample_type;
        public int link_to_enum_det_id;
        public EntityType<?> entity_type_to_use;

        MOBS_HOSTILE(int arg_weight, SampleType arg_sample_type) {
            weight = arg_weight;
            sample_type = arg_sample_type;
        }
        MOBS_HOSTILE(int arg_weight) {
            weight = arg_weight;
            sample_type = SampleType.NODE;
        }
        MOBS_HOSTILE(SampleType arg_sample_type) {
            weight = 1;
            sample_type = arg_sample_type;
        }
        MOBS_HOSTILE(int arg_weight, int arg_link_to_enum_det_id) {
            weight = arg_weight;
            sample_type = SampleType.LINK;
            link_to_enum_det_id = arg_link_to_enum_det_id;
        }
        MOBS_HOSTILE(SampleType arg_sample_type, int arg_link_to_enum_det_id) {
            weight = 1;
            sample_type = arg_sample_type;
            link_to_enum_det_id = arg_link_to_enum_det_id;
        }
        MOBS_HOSTILE(EntityType<?> arg_entity_type_to_use) {
            weight = 1;
            sample_type = SampleType.NODE;
            entity_type_to_use = arg_entity_type_to_use;
        }


        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public SampleType getSampleType() {
            return sample_type;
        }

        @Override
        public int getLinkToEnumDetId() {
            return link_to_enum_det_id;
        }

        @Override
        public void executeSampleTypeAsNode(BlockEvent.BreakEvent event) {
            Entity mob = summonMob_BasicTemplate(event, entity_type_to_use);
        }

        // enum specific funcs


    }



    public WeightedEnumDetails MOBS_ELITE_HOSTILE__EnumDetails;
    public enum MOBS_ELITE_HOSTILE implements IWeightedEnumSample {

        ELDER_GUARDIAN(EntityType.ELDER_GUARDIAN),
        EVOKER(EntityType.EVOKER),
        ILLUSIONER(EntityType.ILLUSIONER),
        PIGLIN_BRUTE(EntityType.PIGLIN_BRUTE),
        RAVANGER(EntityType.RAVAGER),
        VINDICATOR(EntityType.VINDICATOR);


        //SPIDER(SampleType.LINK, SPEC_VAR__SPIDER__ENUM_ID);

        public final int weight;
        public final SampleType sample_type;
        public int link_to_enum_det_id;
        public EntityType<?> entity_type_to_use;

        MOBS_ELITE_HOSTILE(int arg_weight, SampleType arg_sample_type) {
            weight = arg_weight;
            sample_type = arg_sample_type;
        }
        MOBS_ELITE_HOSTILE(int arg_weight) {
            weight = arg_weight;
            sample_type = SampleType.NODE;
        }
        MOBS_ELITE_HOSTILE(SampleType arg_sample_type) {
            weight = 1;
            sample_type = arg_sample_type;
        }
        MOBS_ELITE_HOSTILE(int arg_weight, int arg_link_to_enum_det_id) {
            weight = arg_weight;
            sample_type = SampleType.LINK;
            link_to_enum_det_id = arg_link_to_enum_det_id;
        }
        MOBS_ELITE_HOSTILE(SampleType arg_sample_type, int arg_link_to_enum_det_id) {
            weight = 1;
            sample_type = arg_sample_type;
            link_to_enum_det_id = arg_link_to_enum_det_id;
        }
        MOBS_ELITE_HOSTILE(EntityType<?> arg_entity_type_to_use) {
            weight = 1;
            sample_type = SampleType.NODE;
            entity_type_to_use = arg_entity_type_to_use;
        }


        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public SampleType getSampleType() {
            return sample_type;
        }

        @Override
        public int getLinkToEnumDetId() {
            return link_to_enum_det_id;
        }

        @Override
        public void executeSampleTypeAsNode(BlockEvent.BreakEvent event) {
            Entity mob = summonMob_BasicTemplate(event, entity_type_to_use);
        }

        // enum specific funcs


    }


    public WeightedEnumDetails MOBS_BOSS_HOSTILE__EnumDetails;
    public enum MOBS_BOSS_HOSTILE implements IWeightedEnumSample{
        WARDEN(50, EntityType.WARDEN),
        ENDER_DRAGON(0, EntityType.ENDER_DRAGON),
        WITHER(50, EntityType.WITHER);


        public final int weight;
        public final SampleType sample_type;
        public EntityType<?> entity_type_to_use;
        MOBS_BOSS_HOSTILE(int arg_weight, SampleType arg_sample_type) {
            weight = arg_weight;
            sample_type = arg_sample_type;
        }
        MOBS_BOSS_HOSTILE(int arg_weight, EntityType<?> arg_entity_type_to_use) {
            weight = arg_weight;
            sample_type = SampleType.NODE;
            entity_type_to_use = arg_entity_type_to_use;
        }
        MOBS_BOSS_HOSTILE() {
            weight = 1;
            sample_type = SampleType.NODE;
        }
        MOBS_BOSS_HOSTILE(SampleType arg_sample_type) {
            weight = 1;
            sample_type = arg_sample_type;
        }
        MOBS_BOSS_HOSTILE(EntityType<?> arg_entity_type_to_use) {
            weight = 1;
            sample_type = SampleType.NODE;
            entity_type_to_use = arg_entity_type_to_use;
        }

        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public SampleType getSampleType() {
            return sample_type;
        }

        @Override
        public int getLinkToEnumDetId() {
            return 0;
        }

        @Override
        public void executeSampleTypeAsNode(BlockEvent.BreakEvent event) {
            summonMob_BasicTemplate(event, entity_type_to_use);
        }

        // enum specific funcs


    }


    ///// BLOCK DEFINITIONS

    public WeightedEnumDetails BLOCKS_NORMAL__EnumDetails;
    public enum BLOCKS_NORMAL implements IWeightedEnumSample {
        ANY_LEAVES(SampleType.LINK, SPEC_VAR__PLACE_ANY_LEAVES__ENUM_ID),
        ANY_LOG(SampleType.LINK, SPEC_VAR__PLACE_ANY_LOG__ENUM_ID),
        ANY_STONE(SampleType.LINK, SPEC_VAR__PLACE_ANY_STONE__ENUM_ID),
        ANY_DIRT(SampleType.LINK, SPEC_VAR__PLACE_ANY_DIRT__ENUM_ID);
        //MAGMA_BLOCK(Blocks.MAGMA_BLOCK),
        //NOTE_BLOCK(Blocks.NOTE_BLOCK),
        //ICE(Blocks.ICE);



        public final int weight;
        public final SampleType sample_type;
        public int link_to_enum_det_id;
        public Block block_type;

        BLOCKS_NORMAL(int arg_weight, SampleType arg_sample_type) {
            weight = arg_weight;
            sample_type = arg_sample_type;
        }
        BLOCKS_NORMAL(int arg_weight, Block arg_block_type) {
            weight = arg_weight;
            sample_type = SampleType.NODE;
            block_type = arg_block_type;
        }
        BLOCKS_NORMAL(Block arg_block_type) {
            weight = 1;
            sample_type = SampleType.NODE;
            block_type = arg_block_type;
        }
        BLOCKS_NORMAL(SampleType arg_sample_type, int arg_link_to_enum_det_id) {
            weight = 1;
            sample_type = arg_sample_type;
            link_to_enum_det_id = arg_link_to_enum_det_id;
        }

        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public SampleType getSampleType() {
            return sample_type;
        }

        @Override
        public int getLinkToEnumDetId() {
            return link_to_enum_det_id;
        }

        @Override
        public void executeSampleTypeAsNode(BlockEvent.BreakEvent event) {
            placeBlock_BasicTemplate(event, block_type);
        }

        // enum specific funcs


    }

    public WeightedEnumDetails BLOCKS_OBSTRUCTIVE__EnumDetails;
    public enum BLOCKS_OBSTRUCTIVE implements IWeightedEnumSample {

        BEDROCK(Blocks.BEDROCK),
        MOB_SPAWNER(SampleType.LINK, SPEC_VAR__PLACE_MOB_SPAWNER__ENUM_ID),
        OBSIDIAN(Blocks.OBSIDIAN);
        //SCULK_SHRIEKER(Blocks.SCULK_SHRIEKER);


        public final int weight;
        public final SampleType sample_type;
        public int link_to_enum_det_id;
        public Block block_type;
        BLOCKS_OBSTRUCTIVE(int arg_weight, SampleType arg_sample_type) {
            weight = arg_weight;
            sample_type = arg_sample_type;
        }
        BLOCKS_OBSTRUCTIVE(int arg_weight, Block arg_block_type) {
            weight = arg_weight;
            sample_type = SampleType.NODE;
            block_type = arg_block_type;
        }
        BLOCKS_OBSTRUCTIVE(Block arg_block_type) {
            weight = 1;
            sample_type = SampleType.NODE;
            block_type = arg_block_type;
        }
        BLOCKS_OBSTRUCTIVE(SampleType arg_sample_type, int arg_link_to_enum_det_id) {
            weight = 1;
            sample_type = arg_sample_type;
            link_to_enum_det_id = arg_link_to_enum_det_id;
        }

        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public SampleType getSampleType() {
            return sample_type;
        }

        @Override
        public int getLinkToEnumDetId() {
            return link_to_enum_det_id;
        }

        @Override
        public void executeSampleTypeAsNode(BlockEvent.BreakEvent event) {
            placeBlock_BasicTemplate(event, block_type);

            /*
            if (this == SCULK_SHRIEKER) {
                placeSculkShrieker(event);
            } else {
                placeBlock_BasicTemplate(event, block_type);
            }

             */
        }


    }

    public WeightedEnumDetails BLOCKS_TREASURE__EnumDetails;
    public enum BLOCKS_TREASURE implements IWeightedEnumSample {

        DIAMOND_BLOCK(Blocks.DIAMOND_BLOCK),
        BEACON(Blocks.BEACON),
        GOLD_BLOCK(Blocks.GOLD_BLOCK),
        ENCHANTMENT_TABLE(Blocks.ENCHANTING_TABLE),
        ANVIL(Blocks.ANVIL),
        ENDER_CHEST(Blocks.ENDER_CHEST);


        public final int weight;
        public final SampleType sample_type;
        public int link_to_enum_det_id;
        public Block block_type;
        BLOCKS_TREASURE(int arg_weight, SampleType arg_sample_type) {
            weight = arg_weight;
            sample_type = arg_sample_type;
        }
        BLOCKS_TREASURE(int arg_weight, Block arg_block_type) {
            weight = arg_weight;
            sample_type = SampleType.NODE;
            block_type = arg_block_type;
        }
        BLOCKS_TREASURE(Block arg_block_type) {
            weight = 1;
            sample_type = SampleType.NODE;
            block_type = arg_block_type;
        }
        BLOCKS_TREASURE(SampleType arg_sample_type, int arg_link_to_enum_det_id) {
            weight = 1;
            sample_type = arg_sample_type;
            link_to_enum_det_id = arg_link_to_enum_det_id;
        }

        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public SampleType getSampleType() {
            return sample_type;
        }

        @Override
        public int getLinkToEnumDetId() {
            return link_to_enum_det_id;
        }

        @Override
        public void executeSampleTypeAsNode(BlockEvent.BreakEvent event) {
            placeBlock_BasicTemplate(event, block_type);
        }

        // enum specific funcs


    }


    // EVENT

    public WeightedEnumDetails SPECIAL_EVENT__EnumDetails;
    public enum SPECIAL_EVENT implements IWeightedEnumSample {
        SPAWN_PRIMED_TNT,
        SPAWN_LIGHTNING,
        //PUBLIC_CHAT_EVN_ROCKS,
        SPAWN_LOTS_OF_XP;


        public final int weight;
        public final SampleType sample_type;
        SPECIAL_EVENT(int arg_weight, SampleType arg_sample_type) {
            weight = arg_weight;
            sample_type = arg_sample_type;
        }
        SPECIAL_EVENT(int arg_weight) {
            weight = arg_weight;
            sample_type = SampleType.NODE;
        }
        SPECIAL_EVENT() {
            weight = 1;
            sample_type = SampleType.NODE;
        }
        SPECIAL_EVENT(SampleType arg_sample_type) {
            weight = 1;
            sample_type = arg_sample_type;
        }

        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public SampleType getSampleType() {
            return sample_type;
        }

        @Override
        public int getLinkToEnumDetId() {
            return 0;
        }

        @Override
        public void executeSampleTypeAsNode(BlockEvent.BreakEvent event) {
            switch (this) {
                case SPAWN_PRIMED_TNT -> executeSampleType__PrimedTNT(event);
                case SPAWN_LIGHTNING -> executeSampleType__Lightning(event);
                //case PUBLIC_CHAT_EVN_ROCKS -> executeSampleType__ChatEvnRocks(event);
                case SPAWN_LOTS_OF_XP -> executeSampleType__LotsOfXp(event);

            }
        }

        // enum specific funcs

        private void executeSampleType__PrimedTNT(BlockEvent.BreakEvent event) {
            summonMob_BasicTemplate(event, EntityType.TNT);
        }

        private void executeSampleType__Lightning(BlockEvent.BreakEvent event) {
            summonMob_BasicTemplate(event, EntityType.LIGHTNING_BOLT);
        }

        private void executeSampleType__ChatEvnRocks(BlockEvent.BreakEvent event) {

        }

        private void executeSampleType__LotsOfXp(BlockEvent.BreakEvent event) {
            event.setExpToDrop(event.getExpToDrop() + 600);
        }
    }



    public WeightedEnumDetails SPEC_VAR__PLACE_MOB_SPAWNER__EnumDetails;
    public enum SPEC_VAR__PLACE_MOB_SPAWNER implements IWeightedEnumSample {
        ZOMBIE(25, EntityType.ZOMBIE),
        SKELETON(25, EntityType.SKELETON),
        SPIDER(20, EntityType.SPIDER),
        BLAZE(15, EntityType.BLAZE),
        MAGMA_CUBE(15, EntityType.MAGMA_CUBE);

        public final int weight;
        public final SampleType sample_type;
        public final EntityType<?> entity_type_for_spawner;
        SPEC_VAR__PLACE_MOB_SPAWNER(int arg_weight, EntityType<?> arg_entity_type_for_spawner) {
            weight = arg_weight;
            sample_type = SampleType.NODE;
            entity_type_for_spawner = arg_entity_type_for_spawner;
        }

        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public SampleType getSampleType() {
            return sample_type;
        }

        @Override
        public int getLinkToEnumDetId() {
            return 0;
        }

        @Override
        public void executeSampleTypeAsNode(BlockEvent.BreakEvent event) {
            placeMobSpawnerBlock_BasicTemplate(event, entity_type_for_spawner);
        }

        // enum specific funcs


    }



    public WeightedEnumDetails SPEC_VAR__PLACE_ANY_LEAVES__EnumDetails;
    public enum SPEC_VAR__PLACE_ANY_LEAVES implements IWeightedEnumSample {

        OAK(Blocks.OAK_LEAVES),
        SPRUCE(Blocks.SPRUCE_LEAVES),
        BIRCH(Blocks.BIRCH_LEAVES),
        JUNGLE(Blocks.JUNGLE_LEAVES),
        ACACIA(Blocks.ACACIA_LEAVES),
        DARK_OAK(Blocks.DARK_OAK_LEAVES),
        AZALEA(Blocks.AZALEA_LEAVES),
        MANGROVE(Blocks.MANGROVE_LEAVES),
        CHERRY(Blocks.CHERRY_LEAVES);


        public final int weight;
        public final SampleType sample_type;
        public final Block block_type;
        SPEC_VAR__PLACE_ANY_LEAVES(int arg_weight, Block arg_block_type) {
            weight = arg_weight;
            sample_type = SampleType.NODE;
            block_type = arg_block_type;
        }
        SPEC_VAR__PLACE_ANY_LEAVES(Block arg_block_type) {
            weight = 1;
            sample_type = SampleType.NODE;
            block_type = arg_block_type;
        }


        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public SampleType getSampleType() {
            return sample_type;
        }

        @Override
        public int getLinkToEnumDetId() {
            return 0;
        }

        @Override
        public void executeSampleTypeAsNode(BlockEvent.BreakEvent event) {
            placeBlock_BasicTemplate(event, block_type);
        }

        // enum specific funcs


    }


    public WeightedEnumDetails SPEC_VAR__PLACE_ANY_LOG__EnumDetails;
    public enum SPEC_VAR__PLACE_ANY_LOG implements IWeightedEnumSample {

        OAK(Blocks.OAK_LOG),
        SPRUCE(Blocks.SPRUCE_LOG),
        BIRCH(Blocks.BIRCH_LOG),
        JUNGLE(Blocks.JUNGLE_LOG),
        ACACIA(Blocks.ACACIA_LOG),
        DARK_OAK(Blocks.DARK_OAK_LOG),
        AZALEA(Blocks.OAK_LOG),
        MANGROVE(Blocks.MANGROVE_LOG),
        CHERRY(Blocks.CHERRY_LOG);


        public final int weight;
        public final SampleType sample_type;
        public final Block block_type;
        SPEC_VAR__PLACE_ANY_LOG(int arg_weight, Block arg_block_type) {
            weight = arg_weight;
            sample_type = SampleType.NODE;
            block_type = arg_block_type;
        }
        SPEC_VAR__PLACE_ANY_LOG(Block arg_block_type) {
            weight = 1;
            sample_type = SampleType.NODE;
            block_type = arg_block_type;
        }

        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public SampleType getSampleType() {
            return sample_type;
        }

        @Override
        public int getLinkToEnumDetId() {
            return 0;
        }

        @Override
        public void executeSampleTypeAsNode(BlockEvent.BreakEvent event) {
            placeBlock_BasicTemplate(event, block_type);
        }

        // enum specific funcs



    }



    public WeightedEnumDetails SPEC_VAR__PLACE_ANY_STONE__EnumDetails;
    public enum SPEC_VAR__PLACE_ANY_STONE implements IWeightedEnumSample {

        STONE(Blocks.STONE),
        COBBLESTONE(Blocks.COBBLESTONE),
        SANDSTONE(Blocks.SANDSTONE),
        RED_SANDSTONE(Blocks.RED_SANDSTONE),
        ANDESITE(Blocks.ANDESITE),
        GRANITE(Blocks.GRANITE),
        DIORITE(Blocks.DIORITE),
        DEEPSLATE(Blocks.DEEPSLATE),
        COBBLED_DEEPSLATE(Blocks.COBBLED_DEEPSLATE),
        BLACKSTONE(Blocks.BLACKSTONE),
        ENDSTONE(Blocks.END_STONE),
        GLOWSTONE(Blocks.GLOWSTONE),
        NETHERRACK(Blocks.NETHERRACK);

        public final int weight;
        public final SampleType sample_type;
        public final Block block_type;
        SPEC_VAR__PLACE_ANY_STONE(int arg_weight, Block arg_block_type) {
            weight = arg_weight;
            sample_type = SampleType.NODE;
            block_type = arg_block_type;
        }
        SPEC_VAR__PLACE_ANY_STONE(Block arg_block_type) {
            weight = 1;
            sample_type = SampleType.NODE;
            block_type = arg_block_type;
        }

        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public SampleType getSampleType() {
            return sample_type;
        }

        @Override
        public int getLinkToEnumDetId() {
            return 0;
        }

        @Override
        public void executeSampleTypeAsNode(BlockEvent.BreakEvent event) {
            placeBlock_BasicTemplate(event, block_type);
        }

        // enum specific funcs



    }


    public WeightedEnumDetails SPEC_VAR__PLACE_ANY_DIRT__EnumDetails;
    public enum SPEC_VAR__PLACE_ANY_DIRT implements IWeightedEnumSample {

        DIRT(Blocks.DIRT),
        GRASS_BLOCK(Blocks.GRASS_BLOCK),
        MYCELIUM(Blocks.MYCELIUM),
        PODZOL(Blocks.PODZOL),
        COARSE_DIRT(Blocks.COARSE_DIRT),
        MUD(Blocks.MUD);


        public final int weight;
        public final SampleType sample_type;
        public final Block block_type;
        SPEC_VAR__PLACE_ANY_DIRT(int arg_weight, Block arg_block_type) {
            weight = arg_weight;
            sample_type = SampleType.NODE;
            block_type = arg_block_type;
        }
        SPEC_VAR__PLACE_ANY_DIRT(Block arg_block_type) {
            weight = 1;
            sample_type = SampleType.NODE;
            block_type = arg_block_type;
        }

        @Override
        public int getWeight() {
            return weight;
        }

        @Override
        public SampleType getSampleType() {
            return sample_type;
        }

        @Override
        public int getLinkToEnumDetId() {
            return 0;
        }

        @Override
        public void executeSampleTypeAsNode(BlockEvent.BreakEvent event) {
            placeBlock_BasicTemplate(event, block_type);
        }

        // enum specific funcs



    }

    ///////////// HELPERS and Vars

    //public EnumMap<? extends IWeightedEnumSample, WeightedEnumDetails> enum_to_details_map;
    public HashMap<Integer, WeightedEnumDetails> enum_id_to_details_map = new HashMap<>();

    public static Random rng = new Random();

    public static ArrayList<MobEffect> benefitialMobEffects = new ArrayList<>();

    StoreOfGameProbabilities() {
        //enum_to_details_map = new EnumMap<SPEC_VAR__ZOMBIE, WeightedEnumDetails>();

        SPEC_VAR__ZOMBIE__EnumDetails = new WeightedEnumDetails(SPEC_VAR__ZOMBIE.values(), rng);
        enum_id_to_details_map.put(SPEC_VAR__ZOMBIE__ENUM_ID, SPEC_VAR__ZOMBIE__EnumDetails);

        SPEC_VAR__SKELETON__EnumDetails = new WeightedEnumDetails(SPEC_VAR__SKELETON.values(), rng);
        enum_id_to_details_map.put(SPEC_VAR__SKELETON__ENUM_ID, SPEC_VAR__SKELETON__EnumDetails);

        SPEC_VAR__CREEPER__EnumDetails = new WeightedEnumDetails(SPEC_VAR__CREEPER.values(), rng);
        enum_id_to_details_map.put(SPEC_VAR__CREEPER__ENUM_ID, SPEC_VAR__CREEPER__EnumDetails);

        SPEC_VAR__SPIDER__EnumDetails = new WeightedEnumDetails(SPEC_VAR__SPIDER.values(), rng);
        enum_id_to_details_map.put(SPEC_VAR__SPIDER__ENUM_ID, SPEC_VAR__SPIDER__EnumDetails);

        //

        BLOCK_BREAK_EVENT_TYPE__EnumDetails = new WeightedEnumDetails(BLOCK_BREAK_EVENT_TYPE.values(), rng);
        enum_id_to_details_map.put(BLOCK_BREAK_EVENT_TYPE__ENUM_ID, BLOCK_BREAK_EVENT_TYPE__EnumDetails);

        SPAWN_MOB_EVENT__EnumDetails = new WeightedEnumDetails(SPAWN_MOB_EVENT.values(), rng);
        enum_id_to_details_map.put(SPAWN_MOB_EVENT__ENUM_ID, SPAWN_MOB_EVENT__EnumDetails);

        PLACE_BLOCK_EVENT__EnumDetails = new WeightedEnumDetails(PLACE_BLOCK_EVENT.values(), rng);
        enum_id_to_details_map.put(PLACE_BLOCK_EVENT__ENUM_ID, PLACE_BLOCK_EVENT__EnumDetails);

        DO_EVENT__EnumDetails = new WeightedEnumDetails(DO_EVENT.values(), rng);
        enum_id_to_details_map.put(DO_EVENT__ENUM_ID, DO_EVENT__EnumDetails);

        //

        MOBS_PASSIVE__EnumDetails = new WeightedEnumDetails(MOBS_PASSIVE.values(), rng);
        enum_id_to_details_map.put(MOB_PASSIVE__ENUM_ID, MOBS_PASSIVE__EnumDetails);

        MOBS_NEUTRAL__EnumDetails = new WeightedEnumDetails(MOBS_NEUTRAL.values(), rng);
        enum_id_to_details_map.put(MOB_NEUTRAL__ENUM_ID, MOBS_NEUTRAL__EnumDetails);

        MOBS_HOSTILE__EnumDetails = new WeightedEnumDetails(MOBS_HOSTILE.values(), rng);
        enum_id_to_details_map.put(MOB_HOSTILE__ENUM_ID, MOBS_HOSTILE__EnumDetails);

        MOBS_ELITE_HOSTILE__EnumDetails = new WeightedEnumDetails(MOBS_ELITE_HOSTILE.values(), rng);
        enum_id_to_details_map.put(MOB_ELITE_HOSTILE__ENUM_ID, MOBS_BOSS_HOSTILE__EnumDetails);

        MOBS_BOSS_HOSTILE__EnumDetails = new WeightedEnumDetails(MOBS_BOSS_HOSTILE.values(), rng);
        enum_id_to_details_map.put(MOB_BOSS_HOSTILE__ENUM_ID, MOBS_BOSS_HOSTILE__EnumDetails);


        //

        BLOCKS_NORMAL__EnumDetails = new WeightedEnumDetails(BLOCKS_NORMAL.values(), rng);
        enum_id_to_details_map.put(BLOCKS_NORMAL__ENUM_ID, BLOCKS_NORMAL__EnumDetails);

        BLOCKS_OBSTRUCTIVE__EnumDetails = new WeightedEnumDetails(BLOCKS_OBSTRUCTIVE.values(), rng);
        enum_id_to_details_map.put(BLOCKS_OBSTRUCTIVE__ENUM_ID, BLOCKS_OBSTRUCTIVE__EnumDetails);

        BLOCKS_TREASURE__EnumDetails = new WeightedEnumDetails(BLOCKS_TREASURE.values(), rng);
        enum_id_to_details_map.put(BLOCKS_TREASURE__ENUM_ID, BLOCKS_TREASURE__EnumDetails);


        //

        SPECIAL_EVENT__EnumDetails = new WeightedEnumDetails(SPECIAL_EVENT.values(), rng);
        enum_id_to_details_map.put(SPECIAL_EVENT__ENUM_ID, SPECIAL_EVENT__EnumDetails);


        //

        SPEC_VAR__PLACE_MOB_SPAWNER__EnumDetails = new WeightedEnumDetails(SPEC_VAR__PLACE_MOB_SPAWNER.values(), rng);
        enum_id_to_details_map.put(SPEC_VAR__PLACE_MOB_SPAWNER__ENUM_ID, SPEC_VAR__PLACE_MOB_SPAWNER__EnumDetails);

        SPEC_VAR__PLACE_ANY_LEAVES__EnumDetails = new WeightedEnumDetails(SPEC_VAR__PLACE_ANY_LEAVES.values(), rng);
        enum_id_to_details_map.put(SPEC_VAR__PLACE_ANY_LEAVES__ENUM_ID, SPEC_VAR__PLACE_ANY_LEAVES__EnumDetails);

        SPEC_VAR__PLACE_ANY_LOG__EnumDetails = new WeightedEnumDetails(SPEC_VAR__PLACE_ANY_LOG.values(), rng);
        enum_id_to_details_map.put(SPEC_VAR__PLACE_ANY_LOG__ENUM_ID, SPEC_VAR__PLACE_ANY_LOG__EnumDetails);

        SPEC_VAR__PLACE_ANY_STONE__EnumDetails = new WeightedEnumDetails(SPEC_VAR__PLACE_ANY_STONE.values(), rng);
        enum_id_to_details_map.put(SPEC_VAR__PLACE_ANY_STONE__ENUM_ID, SPEC_VAR__PLACE_ANY_STONE__EnumDetails);

        SPEC_VAR__PLACE_ANY_DIRT__EnumDetails = new WeightedEnumDetails(SPEC_VAR__PLACE_ANY_DIRT.values(), rng);
        enum_id_to_details_map.put(SPEC_VAR__PLACE_ANY_DIRT__ENUM_ID, SPEC_VAR__PLACE_ANY_DIRT__EnumDetails);

    }


    //

    static {
        initBeneficialMobEffectsArrList();
    }

    private static void initBeneficialMobEffectsArrList() {
        benefitialMobEffects.add(MobEffects.ABSORPTION);
        benefitialMobEffects.add(MobEffects.DAMAGE_BOOST);
        benefitialMobEffects.add(MobEffects.DAMAGE_RESISTANCE);
        benefitialMobEffects.add(MobEffects.FIRE_RESISTANCE);
        benefitialMobEffects.add(MobEffects.INVISIBILITY);
        benefitialMobEffects.add(MobEffects.JUMP);
        benefitialMobEffects.add(MobEffects.MOVEMENT_SPEED);
        benefitialMobEffects.add(MobEffects.REGENERATION);

    }

    //

    public WeightedEnumDetails getEnumDetailsFromId(int arg_id) {
        return enum_id_to_details_map.get(arg_id);
    }

    public static <E> E getRandomFromArrayList(ArrayList<E> arrList) {
        int randi = rng.nextInt(0, arrList.size());
        return arrList.get(randi);
    }

    // template funcs

    static Entity summonMob_BasicTemplate(BlockEvent.BreakEvent event, EntityType<?> entity_type_factory) {
        Player player = event.getPlayer();
        Level level = player.getCommandSenderWorld();
        ServerLevel serverLevel = level.getServer().getLevel(level.dimension());
        return entity_type_factory.spawn(serverLevel, event.getPos(), MobSpawnType.NATURAL);

    }

    static boolean placeBlock_BasicTemplate(BlockEvent.BreakEvent event, Block block) {
        Player player = event.getPlayer();
        Level level = player.getCommandSenderWorld();
        ServerLevel serverLevel = level.getServer().getLevel(level.dimension());

        var result = serverLevel.setBlock(event.getPos(), block.defaultBlockState(), Block.UPDATE_ALL);
        // prevent break
        event.setCanceled(true);

        // drop loot
        if (!player.isCreative()) {
            Block.dropResources(event.getState(), level, event.getPos());
        }

        return result;
    }

    static void placeMobSpawnerBlock_BasicTemplate(BlockEvent.BreakEvent event, EntityType<?> entityType) {
        Player player = event.getPlayer();
        Level level = player.getCommandSenderWorld();
        ServerLevel serverLevel = level.getServer().getLevel(level.dimension());

        placeBlock_BasicTemplate(event, Blocks.SPAWNER);

        SpawnerBlockEntity spawnerBlockEntity = new SpawnerBlockEntity(event.getPos(), Blocks.SPAWNER.defaultBlockState());
        spawnerBlockEntity.setEntityId(entityType, RandomSource.create());
        serverLevel.setBlockEntity(spawnerBlockEntity);
        //serverLevel.setBlock(event.getPos(), spawnerBlockEntity.getBlockState(), Block.UPDATE_ALL);

    }


    static void placeSculkShrieker(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        Level level = player.getCommandSenderWorld();
        ServerLevel serverLevel = level.getServer().getLevel(level.dimension());

        placeBlock_BasicTemplate(event, Blocks.SCULK_SHRIEKER);

        SculkShriekerBlockEntity sculkBE = new SculkShriekerBlockEntity(event.getPos(), Blocks.SCULK_SHRIEKER.defaultBlockState());

        CompoundTag nbt = sculkBE.serializeNBT();
        nbt.putBoolean("can_summon", true);
        sculkBE.deserializeNBT(nbt);

        serverLevel.setBlockEntity(sculkBE);


        // prevent break
        event.setCanceled(true);

        // drop loot
        if (!player.isCreative()) {
            Block.dropResources(event.getState(), level, event.getPos());
        }

        //serverLevel.setBlock(event.getPos(), spawnerBlockEntity.getBlockState(), Block.UPDATE_ALL);

    }

    /*
    static void publicChat__BasicTemplate(BlockEvent.BreakEvent event, String chat) {
        Player player = event.getPlayer();
        Level level = player.getCommandSenderWorld();

        //player.
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        Minecraft.getInstance().getCurrentServer().
    }
    */

    // accessor funcs

    void rollChanceOnBreak_SummonPlaceOrDoEvent(BlockEvent.BreakEvent event) {
        BLOCK_BREAK_EVENT_TYPE__EnumDetails.getRandomEnumSample_NestUpToNonEnumLinkAndExecute(event, this);
    }

}
