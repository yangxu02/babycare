package com.linkx.babycare.data;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SerializerProvider {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ObjectMapper getInstance() {
        return MAPPER;
    }
}