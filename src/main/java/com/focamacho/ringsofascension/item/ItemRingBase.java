package com.focamacho.ringsofascension.item;

import com.focamacho.ringsofascension.RingsOfAscension;
import com.focamacho.ringsofascension.init.ModItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.capability.CuriosCapability;
import top.theillusivec4.curios.api.capability.ICurio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class ItemRingBase extends Item {

    protected String tooltip;

    public ItemRingBase(Properties properties, String name, String tooltip) {
        super(properties.group(RingsOfAscension.tabGroup));
        setRegistryName(name);

        this.tooltip = tooltip;

        ModItems.allItems.add(this);
    }

    public void tickCurio(String identifier, int index, LivingEntity livingEntity){}

    public Multimap<String, AttributeModifier> curioModifiers(ItemStack stack, String identifier){
        Multimap<String, AttributeModifier> modifiers = HashMultimap.create();
        return modifiers;
    }

    public void onEquippedCurio(String identifier, LivingEntity livingEntity){}

    public void onUnequippedCurio(String identifier, LivingEntity livingEntity){}

    public int getTier() {
        return 0;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ICapabilityProvider() {
            private final LazyOptional<ICurio> lazyCurio = LazyOptional.of(()-> new ICurio() {

                @Override
                public void onCurioTick(String identifier, int index, LivingEntity livingEntity) {
                    tickCurio(identifier, index, livingEntity);
                }

                @Override
                public void playEquipSound(LivingEntity livingEntity) {
                    livingEntity.world.playSound(null, livingEntity.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_GOLD, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                }

                @Override
                public void onEquipped(String identifier, LivingEntity livingEntity) {
                    onEquippedCurio(identifier, livingEntity);
                }

                @Nonnull
                @Override
                public DropRule getDropRule(LivingEntity livingEntity) {
                    return DropRule.DEFAULT;
                }

                @Override
                public void onUnequipped(String identifier, LivingEntity livingEntity) {
                    onUnequippedCurio(identifier, livingEntity);
                }

                @Override
                public boolean canRightClickEquip() {
                    return true;
                }

                @Override
                public Multimap<String, AttributeModifier> getAttributeModifiers(String identifier) {
                    return curioModifiers(stack, identifier);
                }

            });

            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side) {
                return CuriosCapability.ITEM.orEmpty(capability, lazyCurio);
            }
        };
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        switch(getTier()) {
            case 0:
                tooltip.add(new StringTextComponent(ChatFormatting.GOLD + new TranslationTextComponent("tooltip.ringsofascension.tier").getFormattedText() + " " + ChatFormatting.GREEN + new TranslationTextComponent("tooltip.ringsofascension.tier.common").getFormattedText()));
                break;
            case 1:
                tooltip.add(new StringTextComponent(ChatFormatting.GOLD + new TranslationTextComponent("tooltip.ringsofascension.tier").getFormattedText() + " " + ChatFormatting.BLUE + new TranslationTextComponent("tooltip.ringsofascension.tier.rare").getFormattedText()));
                break;
            case 2:
                tooltip.add(new StringTextComponent(ChatFormatting.GOLD + new TranslationTextComponent("tooltip.ringsofascension.tier").getFormattedText() + " " + ChatFormatting.LIGHT_PURPLE + new TranslationTextComponent("tooltip.ringsofascension.tier.epic").getFormattedText()));
                break;
             case 3:
                tooltip.add(new StringTextComponent(ChatFormatting.GOLD + new TranslationTextComponent("tooltip.ringsofascension.tier").getFormattedText() + " " + ChatFormatting.RED + new TranslationTextComponent("tooltip.ringsofascension.tier.legendary").getFormattedText()));
        }

        if(this.tooltip == null) return;

        tooltip.add(new StringTextComponent(""));
        tooltip.add(new StringTextComponent(ChatFormatting.GOLD + new TranslationTextComponent("tooltip.ringsofascension.worn").getFormattedText()));
        tooltip.add(new StringTextComponent(ChatFormatting.BLUE + new TranslationTextComponent(this.tooltip).getFormattedText()));
    }
}
