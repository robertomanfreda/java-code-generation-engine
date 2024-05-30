package io.github.robertomanfreda.jcge.utils;

public class ClassNameValidator {

    public static boolean isValidClassName(String className) {
        // Check if the string is null or empty
        if (className == null || className.isEmpty()) {
            return false;
        }

        // Check if the string follows the rules for a valid Java class name
        if (!className.matches("[\\p{L}_$][\\p{L}\\p{Digit}_$]*")) {
            return false;
        }

        // Check if the first character is uppercase
        if (!Character.isUpperCase(className.charAt(0))) {
            return false;
        }

        // Check if the string is a Java reserved keyword
        return !isJavaKeyword(className);
    }

    private static boolean isJavaKeyword(String word) {
        // List of reserved keywords in Java
        String[] javaKeywords = {
                "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
                "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally",
                "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface",
                "long", "native", "new", "package", "private", "protected", "public", "return", "short",
                "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
                "transient", "try", "void", "volatile", "while", "true", "false", "null"
        };

        for (String keyword : javaKeywords) {
            if (keyword.equals(word)) {
                return true;
            }
        }

        return false;
    }

}
