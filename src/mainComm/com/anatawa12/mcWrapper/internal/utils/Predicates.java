package com.anatawa12.mcWrapper.internal.utils;

import java.util.function.Predicate;

@SuppressWarnings({"rawtypes", "NullableProblems"})
public class Predicates {
    private final static Predicate ALWAYS_TRUE = new AlwaysTrue();
    private final static Predicate ALWAYS_FALSE = new AlwaysFalse();

    public static <T> Predicate<T> alwaysTrue() {
        //noinspection unchecked
        return ALWAYS_TRUE;
    }

    public static <T> Predicate<T> alwaysFalse() {
        //noinspection unchecked
        return ALWAYS_FALSE;
    }

    private static class AlwaysFalse implements Predicate {
        @Override
        public boolean test(Object t) {
            return false;
        }

        @Override
        public Predicate and(Predicate other) {
            return ALWAYS_FALSE;
        }

        @Override
        public Predicate negate() {
            return ALWAYS_TRUE;
        }

        @Override
        public Predicate or(Predicate other) {
            return other;
        }
    }

    private static class AlwaysTrue implements Predicate {
        @Override
        public boolean test(Object t) {
            return true;
        }

        @Override
        public Predicate and(Predicate other) {
            return other;
        }

        @Override
        public Predicate negate() {
            return ALWAYS_FALSE;
        }

        @Override
        public Predicate or(Predicate other) {
            return ALWAYS_TRUE;
        }
    }
}
