package com.unitn.disi.lpsmt.happypuppy.api.adapter.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {
    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_INSTANT));
    }
}
