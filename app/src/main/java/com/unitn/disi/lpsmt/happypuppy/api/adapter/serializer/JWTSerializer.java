package com.unitn.disi.lpsmt.happypuppy.api.adapter.serializer;

import com.auth0.android.jwt.JWT;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class JWTSerializer implements JsonSerializer<JWT> {
    @Override
    public JsonElement serialize(JWT src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }
}
