package ca.tnoah.bteterramapfabric.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NullValidator {

    public static <T> T get(@Nullable T value, @NotNull T defaultValue) {
        return value == null ? defaultValue : value;
    }

}
