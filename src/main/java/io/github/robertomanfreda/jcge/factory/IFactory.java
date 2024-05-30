package io.github.robertomanfreda.jcge.factory;

public interface IFactory<T> {

    T generate() throws Exception;
}
