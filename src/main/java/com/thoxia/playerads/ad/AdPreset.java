package com.thoxia.playerads.ad;

import lombok.Data;

import java.util.List;

@Data
public class AdPreset {

    private final String presetName;
    private final String presetMessage;
    private final List<String> lore;

}
