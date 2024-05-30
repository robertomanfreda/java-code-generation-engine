package io.github.robertomanfreda.jcge.generator;

public interface IGenerator<T> {
    T build();

    void add();
}