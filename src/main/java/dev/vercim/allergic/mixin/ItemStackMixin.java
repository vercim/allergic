package dev.vercim.allergic.mixin;

import dev.vercim.allergic.allergy.AllergyConsumptionHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "finishUsing", at = @At("RETURN"))
    private void allergic$afterFinishUsing(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> callbackInfo) {
        AllergyConsumptionHandler.onFinishedUsing((ItemStack) (Object) this, world, user);
    }
}
