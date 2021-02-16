package org.hisp.dhis.cache;

/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.poi.ss.formula.functions.T;
import org.hisp.dhis.common.ValueType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author Luciano Fiandesio
 */
@Getter
@AllArgsConstructor
public class TrackedEntityAttribute
{
    private String id;

    private ValueType valueType;

    @JsonAdapter(ObjectIdDeserializer.class)
    private String trackedEntityAttribute;

    private boolean generated;

    private boolean unique;

    private String pattern;

    private List<String> options;

    private boolean searchable;

    public TrackedEntityAttribute()
    {
    }

}

class ObjectIdDeserializer
    implements JsonDeserializer<String>
{
    @Override
    public String deserialize( JsonElement json, Type typeOfT, JsonDeserializationContext context )
        throws JsonParseException
    {
        return json.getAsJsonObject().get( "id" ).getAsString();
    }
}

class ObjectIdAdapter extends TypeAdapter<String>
{
    @Override
    public void write( JsonWriter out, String value )
        throws IOException
    {
        JsonObject ob = new JsonObject();
        ob.addProperty( "id", value );
        out.value(
            String.valueOf( ob )
        );
    }

    @Override
    public String read( JsonReader in )
        throws IOException
    {
        in.hasNext();

        return in.nextString();
    }

}