package com.thoxia.playerads.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.awt.*;
import java.lang.reflect.Type;

public class ColorDeserializer implements JsonDeserializer<Color> {

    @Override
    public Color deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String argb = jsonElement.getAsJsonObject().get("argb").getAsString();
        return new Color(Integer.parseUnsignedInt(argb, 16), true);
    }

}