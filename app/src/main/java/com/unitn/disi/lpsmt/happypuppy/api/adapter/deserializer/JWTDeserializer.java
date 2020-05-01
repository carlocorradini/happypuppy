package com.unitn.disi.lpsmt.happypuppy.api.adapter.deserializer;

import com.auth0.android.jwt.JWT;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public final class JWTDeserializer implements JsonDeserializer<JWT> {

    @Override
    public JWT deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new JWT(json.getAsJsonPrimitive().getAsString());
    }
}
