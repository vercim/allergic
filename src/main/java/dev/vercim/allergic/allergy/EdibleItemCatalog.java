package dev.vercim.allergic.allergy;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public final class EdibleItemCatalog {
    private EdibleItemCatalog() {
    }

    public static Set<Identifier> findAll() {
        Set<Identifier> identifiers = new TreeSet<>();
        for (Item item : Registries.ITEM) {
            ItemStack stack = item.getDefaultStack();
            if (isEdible(stack)) {
                identifiers.add(Registries.ITEM.getId(item));
            }
        }
        return Collections.unmodifiableSet(new LinkedHashSet<>(identifiers));
    }

    public static boolean isEdible(ItemStack stack) {
        return stack.get(DataComponentTypes.FOOD) != null || stack.getUseAction() == UseAction.EAT;
    }

}
