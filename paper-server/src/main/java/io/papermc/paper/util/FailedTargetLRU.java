package io.papermc.paper.util;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Small bounded LRU cache for "failed targets" with expiry ticks.
 * Key is a packed block position (x,y,z) into a long.
 * Not thread-safe (must be used from the owning region thread only).
 */
public final class FailedTargetLRU {

    private final int maxSize;
    private final LinkedHashMap<Long, Long> map;

    public FailedTargetLRU(final int maxSize) {
        this.maxSize = Math.max(1, maxSize);
        this.map = new LinkedHashMap<>(this.maxSize, 0.75f, true);
    }

    public void noteFailed(final long packedPos, final long expiryTick) {
        this.map.put(packedPos, expiryTick);
        this.trimToSize();
    }

    public boolean isFailed(final long packedPos, final long nowTick) {
        final Long expiry = this.map.get(packedPos);
        if (expiry == null) return false;
        if (expiry <= nowTick) {
            this.map.remove(packedPos);
            return false;
        }
        return true;
    }

    public void purgeExpired(final long nowTick) {
        final Iterator<Map.Entry<Long, Long>> it = this.map.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry<Long, Long> e = it.next();
            if (e.getValue() <= nowTick) it.remove();
        }
    }

    public void clear() {
        this.map.clear();
    }

    public int size() {
        return this.map.size();
    }

    private void trimToSize() {
        while (this.map.size() > this.maxSize) {
            final Iterator<Long> it = this.map.keySet().iterator();
            if (!it.hasNext()) break;
            it.next();
            it.remove();
        }
    }

    /** Pack x,y,z into a long (vanilla-style). */
    public static long packBlockPos(final int x, final int y, final int z) {
        // Same bit layout as vanilla BlockPos#asLong uses in many versions:
        // x: 26 bits, z: 26 bits, y: 12 bits (sign-extended)
        return ((long)(x & 0x3FFFFFF) << 38) | ((long)(z & 0x3FFFFFF) << 12) | (long)(y & 0xFFF);
    }
}
