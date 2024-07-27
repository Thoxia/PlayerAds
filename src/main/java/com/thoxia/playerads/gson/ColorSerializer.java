package com.thoxia.playerads.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.awt.*;
import java.lang.reflect.Type;

public class ColorSerializer implements JsonSerializer<Color> {

    @Override
    public JsonElement serialize(Color color, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("argb", Integer.toHexString(color.getRGB()));
        return jsonObject;
    }

}
