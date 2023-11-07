package com.demo.antizha.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class ResponseDataTypeAdaptor extends TypeAdapter<ResponseData> {

    ResponseDataTypeAdaptor(Gson gson) {
        this.gson = gson;
    }

    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() { // from class: network.gson.ResponseDataTypeAdaptor.1
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            if (typeToken.getRawType() == ResponseData.class) {
                return (TypeAdapter<T>) new ResponseDataTypeAdaptor(gson);
            }
            return null;
        }
    };
    private final Gson gson;

    public static class AnonymousClass2 {
        static final int[] $SwitchMap$com$google$gson$stream$JsonToken = new int[JsonToken.values().length];

        static {
            try {
                $SwitchMap$com$google$gson$stream$JsonToken[JsonToken.BEGIN_ARRAY.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$google$gson$stream$JsonToken[JsonToken.BEGIN_OBJECT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$google$gson$stream$JsonToken[JsonToken.STRING.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$google$gson$stream$JsonToken[JsonToken.NUMBER.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$google$gson$stream$JsonToken[JsonToken.BOOLEAN.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$google$gson$stream$JsonToken[JsonToken.NULL.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
        }
    }


    public static Gson buildGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapterFactory(FACTORY);
        return gsonBuilder.create();
    }

    private Object readInternal(JsonReader jsonReader) throws IOException {
        switch (AnonymousClass2.$SwitchMap$com$google$gson$stream$JsonToken[jsonReader.peek().ordinal()]) {
            case 1:
                ArrayList arrayList = new ArrayList();
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    arrayList.add(readInternal(jsonReader));
                }
                jsonReader.endArray();
                return arrayList;
            case 2:
                LinkedTreeMap linkedTreeMap = new LinkedTreeMap();
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    linkedTreeMap.put(jsonReader.nextName(), readInternal(jsonReader));
                }
                jsonReader.endObject();
                return linkedTreeMap;
            case 3:
                return jsonReader.nextString();
            case 4:
                String nextString = jsonReader.nextString();
                if (nextString.contains(".") || nextString.contains("e") || nextString.contains("E")) {
                    return Double.valueOf(Double.parseDouble(nextString));
                }
                if (Long.parseLong(nextString) <= 2147483647L) {
                    return Integer.valueOf(Integer.parseInt(nextString));
                }
                return Long.valueOf(Long.parseLong(nextString));
            case 5:
                return Boolean.valueOf(jsonReader.nextBoolean());
            case 6:
                jsonReader.nextNull();
                return null;
            default:
                throw new IllegalStateException();
        }
    }

    public ResponseData read(JsonReader jsonReader) throws IOException {
        ResponseData responseData = new ResponseData();
        Map map = (Map) readInternal(jsonReader);
        responseData.setStatus((Integer) map.get("status"));
        responseData.setMsg((String) map.get("msg"));
        responseData.setData(map.get("data"));
        return responseData;
    }

    public void write(JsonWriter jsonWriter, ResponseData responseData) throws IOException {
        if (responseData == null) {
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.beginObject();
        jsonWriter.name("status");
        this.gson.getAdapter(Integer.class).write(jsonWriter, responseData.getStatus());
        jsonWriter.name("msg");
        this.gson.getAdapter(String.class).write(jsonWriter, responseData.getMsg());
        jsonWriter.name("data");
        this.gson.getAdapter(Object.class).write(jsonWriter, responseData.getData());
        jsonWriter.endObject();
    }
}
