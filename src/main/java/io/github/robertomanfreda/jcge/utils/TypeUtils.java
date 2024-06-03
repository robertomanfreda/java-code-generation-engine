package io.github.robertomanfreda.jcge.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class TypeUtils {

    public static TypeName mapType(JsonNode fieldProps) {
        JsonNode customNodeType = fieldProps.get("package");

        JsonNode jsonNodeType = fieldProps.get("type");
        String fieldType = jsonNodeType != null
                ? jsonNodeType.asText()
                : fieldProps.asText();

        log.debug("Mapping field type for {}", fieldProps);

        if (null != customNodeType) {
            log.debug("Mapping field type for custom node type {}", customNodeType);
            String customPackage = customNodeType.asText();

            if (null != customPackage) {
                return mapCustomType(customPackage, fieldType);
            } else {
                throw new RuntimeException("Custom node type must be set");
            }
        }

        if (fieldType.startsWith("List")) {
            return mapParameterizedType(fieldType, List.class);
        } else if (fieldType.startsWith("Set")) {
            return mapParameterizedType(fieldType, Set.class);
        } else if (fieldType.startsWith("Map")) {
            return mapMapTypeFromString(fieldType);
        } else {
            return mapSimpleType(fieldType);
        }
    }

    private static TypeName mapCustomType(String customPackage, String fieldType) {
        ClassName className = ClassName.get(customPackage, fieldType);

        try {
            Class.forName(className.canonicalName());
        } catch (ClassNotFoundException e) {
            log.error("Could not find class {}", className.canonicalName(), e);
            throw new RuntimeException(e);
        }

        return className;
    }

    private static TypeName mapSimpleType(String fieldType) {
        switch (fieldType) {
            case "int":
                return TypeName.INT;
            case "long":
                return TypeName.LONG;
            case "double":
                return TypeName.DOUBLE;
            case "float":
                return TypeName.FLOAT;
            case "boolean":
                return TypeName.BOOLEAN;
            case "byte":
                return TypeName.BYTE;
            case "short":
                return TypeName.SHORT;
            case "char":
                return TypeName.CHAR;
            case "Integer":
                return ClassName.get(Integer.class);
            case "Long":
                return ClassName.get(Long.class);
            case "Double":
                return ClassName.get(Double.class);
            case "Float":
                return ClassName.get(Float.class);
            case "Boolean":
                return ClassName.get(Boolean.class);
            case "Byte":
                return ClassName.get(Byte.class);
            case "Short":
                return ClassName.get(Short.class);
            case "Character":
                return ClassName.get(Character.class);
            case "String":
                return ClassName.get(String.class);
            case "Date":
                return ClassName.get(Date.class);
            case "LocalDate":
                return ClassName.get(LocalDate.class);
            case "LocalDateTime":
                return ClassName.get(LocalDateTime.class);
            case "LocalTime":
                return ClassName.get(LocalTime.class);
            case "Object":
                return ClassName.get(Object.class);
            default:
                throw new IllegalArgumentException("Unknown type: " + fieldType);
        }
    }

    private static TypeName mapParameterizedType(String fieldType, Class<?> rawClass) {
        int startIndex = fieldType.indexOf("<");
        int endIndex = fieldType.lastIndexOf(">");
        if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) {
            throw new IllegalArgumentException(rawClass.getSimpleName() + " type requires parameterized type");
        }
        String itemTypeString = fieldType.substring(startIndex + 1, endIndex).trim();
        TypeName itemType = mapTypeFromString(itemTypeString);
        return ParameterizedTypeName.get(ClassName.get(rawClass), itemType);
    }

    private static TypeName mapTypeFromString(String fieldType) {
        if (fieldType.startsWith("List<")) {
            return mapParameterizedType(fieldType, List.class);
        } else if (fieldType.startsWith("Set<")) {
            return mapParameterizedType(fieldType, Set.class);
        } else if (fieldType.startsWith("Map<")) {
            return mapMapTypeFromString(fieldType);
        } else {
            return mapSimpleType(fieldType);
        }
    }

    private static TypeName mapMapTypeFromString(String fieldType) {
        int startIndex = fieldType.indexOf("<");
        int endIndex = fieldType.lastIndexOf(">");
        if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) {
            throw new IllegalArgumentException("Map type requires parameterized type");
        }
        String[] keyValueTypeStrings = fieldType.substring(startIndex + 1, endIndex).split(",");
        if (keyValueTypeStrings.length != 2) {
            throw new IllegalArgumentException("Map type requires exactly two parameterized types");
        }
        TypeName keyType = mapTypeFromString(keyValueTypeStrings[0].trim());
        TypeName valueType = mapTypeFromString(keyValueTypeStrings[1].trim());
        return ParameterizedTypeName.get(ClassName.get(Map.class), keyType, valueType);
    }
}
