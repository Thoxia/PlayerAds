package com.thoxia.playerads.gui.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.Click;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AutoUpdateItem;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class UpdatingItem extends AutoUpdateItem {

    private final Consumer<Click> clickHandler;

    public UpdatingItem(int period, Supplier<? extends ItemProvider> builderSupplier, Consumer<Click> clickHandler) {
        super(period, builderSupplier);

        this.clickHandler = clickHandler;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (clickHandler != null)
            clickHandler.accept(new Click(event));
    }
}
