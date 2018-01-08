package com.uranusdb.languages.gremlin.structure;

import java.util.Optional;

public final class UranusHelper {

    public static Optional<Object> getOptionalValue(final String key, final Object... keyValues) {
        for (int i = 0; i < keyValues.length; i = i + 2) {
            if (keyValues[i].equals(key))
                return Optional.of(keyValues[i + 1]);
        }
        return Optional.empty();
    }
}
