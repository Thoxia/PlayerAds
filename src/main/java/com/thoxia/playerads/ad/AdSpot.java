package com.thoxia.playerads.ad;

import com.google.common.base.Objects;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

@Data
public class AdSpot {

    private final int slot;
    private final double price;
    private final long duration;
    @Nullable private final AdPreset preset;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdSpot adSpot = (AdSpot) o;
        return slot == adSpot.slot;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(slot);
    }

}
