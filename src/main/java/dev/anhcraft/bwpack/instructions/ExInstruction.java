package dev.anhcraft.bwpack.instructions;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

@SuppressWarnings("rawtypes")
public enum ExInstruction {
    LOCAL_GEN_UPGRADE("generator", "upgrade_local", LocalGenUpgrade::new),
    SHARED_GEN_UPGRADE("generator", "upgrade_shared", SharedGenUpgrade::new),
    SET_POTION_POOL("pool", "set_potion", PotionPoolSet::new),
    REMOVE_POTION_POOL("pool", "remove_potion", PotionPoolRemove::new);

    private int hash;
    private Supplier<InstructionCallback> callbackSupplier;

    ExInstruction(String a, String b, @NotNull Supplier<InstructionCallback> callbackSupplier) {
        hash = Objects.hash(a, b);
        this.callbackSupplier = callbackSupplier;
    }

    public int getHash() {
        return hash;
    }

    @NotNull
    public Supplier<InstructionCallback> getCallbackSupplier() {
        return callbackSupplier;
    }
}
