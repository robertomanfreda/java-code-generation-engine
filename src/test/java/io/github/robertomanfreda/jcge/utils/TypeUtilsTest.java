package io.github.robertomanfreda.jcge.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TypeUtilsTest {
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    @Test
    void testPrimitiveTypes() throws IOException {
        JsonNode primitiveTypesNode = mapper.readTree(new File("src/test/resources/types/primitive-types.yaml"));
        assertEquals(TypeName.INT, TypeUtils.mapType(primitiveTypesNode.get("properties").get("intField")));
        assertEquals(TypeName.LONG, TypeUtils.mapType(primitiveTypesNode.get("properties").get("longField")));
        assertEquals(TypeName.DOUBLE, TypeUtils.mapType(primitiveTypesNode.get("properties").get("doubleField")));
        assertEquals(TypeName.FLOAT, TypeUtils.mapType(primitiveTypesNode.get("properties").get("floatField")));
        assertEquals(TypeName.BOOLEAN, TypeUtils.mapType(primitiveTypesNode.get("properties").get("booleanField")));
        assertEquals(TypeName.BYTE, TypeUtils.mapType(primitiveTypesNode.get("properties").get("byteField")));
        assertEquals(TypeName.SHORT, TypeUtils.mapType(primitiveTypesNode.get("properties").get("shortField")));
        assertEquals(TypeName.CHAR, TypeUtils.mapType(primitiveTypesNode.get("properties").get("charField")));
    }

    @Test
    void testWrapperTypes() throws IOException {
        JsonNode wrapperTypesNode = mapper.readTree(new File("src/test/resources/types/wrapper-types.yaml"));
        assertEquals(ClassName.get(Integer.class), TypeUtils.mapType(wrapperTypesNode.get("properties").get("IntegerField")));
        assertEquals(ClassName.get(Long.class), TypeUtils.mapType(wrapperTypesNode.get("properties").get("LongField")));
        assertEquals(ClassName.get(Double.class), TypeUtils.mapType(wrapperTypesNode.get("properties").get("DoubleField")));
        assertEquals(ClassName.get(Float.class), TypeUtils.mapType(wrapperTypesNode.get("properties").get("FloatField")));
        assertEquals(ClassName.get(Boolean.class), TypeUtils.mapType(wrapperTypesNode.get("properties").get("BooleanField")));
        assertEquals(ClassName.get(Byte.class), TypeUtils.mapType(wrapperTypesNode.get("properties").get("ByteField")));
        assertEquals(ClassName.get(Short.class), TypeUtils.mapType(wrapperTypesNode.get("properties").get("ShortField")));
        assertEquals(ClassName.get(Character.class), TypeUtils.mapType(wrapperTypesNode.get("properties").get("CharacterField")));
    }

    @Test
    void testOtherTypes() throws IOException {
        JsonNode otherTypesNode = mapper.readTree(new File("src/test/resources/types/other-types.yaml"));
        assertEquals(ClassName.get(String.class), TypeUtils.mapType(otherTypesNode.get("properties").get("stringField")));
        assertEquals(ClassName.get(Date.class), TypeUtils.mapType(otherTypesNode.get("properties").get("dateField")));
        assertEquals(ClassName.get(LocalDate.class), TypeUtils.mapType(otherTypesNode.get("properties").get("localDateField")));
        assertEquals(ClassName.get(LocalDateTime.class), TypeUtils.mapType(otherTypesNode.get("properties").get("localDateTimeField")));
        assertEquals(ClassName.get(LocalTime.class), TypeUtils.mapType(otherTypesNode.get("properties").get("localTimeField")));
        assertEquals(ClassName.get(Object.class), TypeUtils.mapType(otherTypesNode.get("properties").get("objectField")));
    }

    @Test
    void testListType() throws IOException {
        JsonNode listTypeNode = mapper.readTree(new File("src/test/resources/types/list-type.yaml"));
        assertEquals(ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(String.class)),
                TypeUtils.mapType(listTypeNode.get("properties").get("listField")));
    }

    @Test
    void testSetType() throws IOException {
        JsonNode setTypeNode = mapper.readTree(new File("src/test/resources/types/set-type.yaml"));
        assertEquals(ParameterizedTypeName.get(ClassName.get(Set.class), ClassName.get(String.class)),
                TypeUtils.mapType(setTypeNode.get("properties").get("listField")));
    }

    @Test
    void testMapType() throws IOException {
        JsonNode mapTypeNode = mapper.readTree(new File("src/test/resources/types/map-type.yaml"));
        assertEquals(ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class), ClassName.get(Integer.class)),
                TypeUtils.mapType(mapTypeNode.get("properties").get("mapField")));
    }
}
