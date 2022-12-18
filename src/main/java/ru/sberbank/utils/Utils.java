package ru.sberbank.utils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;

public final class Utils {

    private Utils() {

    }

    public static <T> T firstValueOrNull(Collection<T> values) {
        return values == null || values.isEmpty() ? null : first(values);
    }

    public static <T> T first(Collection<T> values) {
        return values.iterator().next();
    }

    public static <T> List<T> filter(Collection<T> collection, Predicate<? super T> predicate) {
        return collection.stream().filter(predicate).collect(toList());
    }

    public static <K, V, W> Map<K, W> reMap(Map<K, V> sourceMap, BiFunction<K, V, W> valueConverter) {
        return reMap(sourceMap, (k, v) -> k, valueConverter);
    }

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return map == null || map.isEmpty();
    }

    public static <K, V> Stream<Map.Entry<K, V>> toEntryStream(Map<K, V> sourceMap) {
        return sourceMap.entrySet().stream();
    }


    public static <T, K, U> Collector<T, ?, Map<K, U>> toLinkedMap(Function<? super T, ? extends K> keyMapper,
                                                                   Function<? super T, ? extends U> valueMapper) {
        return Collectors.toMap(keyMapper, valueMapper, throwingMerger(), LinkedHashMap::new);
    }

    public static <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> {
            throw new IllegalStateException(format("Duplicate key %s", u));
        };
    }

    public static <T, R> R applyOrNull(Function<T, R> func, T value) {
        return applyIfNotNull(func, value, null);
    }

    public static <T, R> R applyIfNotNull(Function<T, R> func, T value, R defaultResult) {
        return value != null ? func.apply(value) : defaultResult;
    }

    public static <K, V, Q, W> Map<Q, W> reMap(Map<K, V> sourceMap, BiFunction<K, V, Q> keyConverter, BiFunction<K, V, W> valueConverter) {
        if (isEmpty(sourceMap)) return emptyMap();
        return toEntryStream(sourceMap).collect(toLinkedMap(
                e -> keyConverter.apply(e.getKey(), e.getValue()),
                e -> valueConverter.apply(e.getKey(), e.getValue()) ));
    }
}
