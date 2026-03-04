package de.openfabtwin.mapper;

import java.util.List;
import java.util.stream.Collectors;

public class MapperUtils {

    public static List<String> enumToStrings(List<? extends Enum<?>> enums) {
        if (enums == null) return List.of();
        return enums.stream()
                .map(Object::toString)  // toString() returns getValue() for all generated enums
                .collect(Collectors.toList());
    }
}
