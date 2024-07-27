package com.thoxia.playerads.ad;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Ad {

    private final UUID uniqueId = UUID.randomUUID();
    private final String playerName;
    private final String message;
    private AdSpot spot;
    private final String skinTexture;

    private final long creationTime = System.currentTimeMillis();

}
