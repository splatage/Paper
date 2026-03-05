package io.papermc.paper.util;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class ItemKey {

    private final @NotNull NamespacedKey key;

    private ItemKey(final @NotNull NamespacedKey key) {
        this.key = key;
    }

    public static @NotNull ItemKey of(final @NotNull NamespacedKey key) {
        return new ItemKey(key);
    }

    public @NotNull NamespacedKey key() {
        return this.key;
    }

    public @NotNull String asString() {
        return this.key.toString();
    }

    public static @Nullable ItemKey tryParse(final @Nullable String id) {
        if (id == null || id.isEmpty()) return null;
        final NamespacedKey key = NamespacedKey.fromString(id);
        return key == null ? null : new ItemKey(key);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemKey other)) return false;
        return this.key.equals(other.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key);
    }

    @Override
    public String toString() {
        return "ItemKey[" + this.key + "]";
    }
}
