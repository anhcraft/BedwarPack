package dev.anhcraft.bwpack.instructions;

import dev.anhcraft.battle.api.events.PlayerPrePurchaseEvent;
import dev.anhcraft.battle.api.gui.screen.Window;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TransactionInfo {
    private Window window;
    private Player player;
    private PlayerPrePurchaseEvent event;

    public TransactionInfo(@NotNull Window window, @NotNull Player player, @Nullable PlayerPrePurchaseEvent event) {
        this.window = window;
        this.player = player;
        this.event = event;
    }

    @NotNull
    public Window getWindow() {
        return window;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @Nullable
    public PlayerPrePurchaseEvent getEvent() {
        return event;
    }
}
