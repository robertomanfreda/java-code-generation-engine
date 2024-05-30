package io.github.robertomanfreda.jcge.generator;

import com.squareup.javapoet.TypeSpec;
import io.github.robertomanfreda.jcge.model.GenerationDetails;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class AbstractGenerator<T> implements IGenerator<T> {

    @Getter
    private final GenerationDetails generationDetails;
    private final TypeSpec.Builder classBuilder;

}