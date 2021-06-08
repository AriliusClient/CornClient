/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: Cornos
# File: ByteSizeViewerHandler
# Created by constantin at 21:34, Mär 05 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package me.zeroX150.cornos.mixin;

import me.zeroX150.cornos.features.module.ModuleRegistry;
import me.zeroX150.cornos.features.module.impl.external.ByteSizeViewer;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.charset.StandardCharsets;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;
import java.util.Objects;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "getTooltip", at = @At("RETURN"), cancellable = true)
    public void getTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        if (!ModuleRegistry.search(ByteSizeViewer.class).isEnabled())
            return;
        List<Text> tl = cir.getReturnValue();
        ItemStack is = (ItemStack) ((Object) this);
        tl.add(Text.of(" "));
        if (!is.hasTag()) {
            tl.add(Text.of("Byte size: No NBT tags, almost 0"));
        } else {
            NbtCompound ct = is.getOrCreateTag();
            if (ct.contains("bytesize")) {
                tl.add(Text.of("Bytes: about " + ct.getString("bytesize")));
                return;
            }
            float fs = 0;
            for (String key : ct.getKeys()) {
                if (ct.get(key) == null)
                    continue;
                fs += key.getBytes(StandardCharsets.UTF_8).length;
                fs += Objects.requireNonNull(ct.get(key)).copy().toString().getBytes(StandardCharsets.UTF_8).length;
            }
            // tl.add(Text.of("Byte size: ~ "+humanReadableByteCountBin(fs)));
            ct.putString("bytesize",
                    humanReadableByteCountBin(fs) + (fs > 2097152 ? " §l§c! §r§cTHIS CAN BOOKBAN§r" : ""));
            is.setTag(ct);
        }
    }

    String humanReadableByteCountBin(float bytes) {
        float absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = (long) absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum((long) bytes);
        return String.format("%.1f %ciB", value / 1024.0, ci.current());
    }
}
