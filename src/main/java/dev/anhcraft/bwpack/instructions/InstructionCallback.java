package dev.anhcraft.bwpack.instructions;

import dev.anhcraft.battle.utils.functions.Instruction;
import org.jetbrains.annotations.NotNull;

public interface InstructionCallback<T> {
    void call(@NotNull Instruction instruction, @NotNull T object);
}
