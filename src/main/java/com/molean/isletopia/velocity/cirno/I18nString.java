package com.molean.isletopia.velocity.cirno;

import com.molean.isletopia.shared.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class I18nString {
    private final String node;

    private final List<Pair<String, String>> pairList = new ArrayList<>();

    private final List<Pair<String, Function<Locale, String>>> delayedList = new ArrayList<>();

    public I18nString(String node) {
        this.node = node;
    }

    public static I18nString of(String node) {
        return new I18nString(node);
    }

    public I18nString add(String k, String v) {
        pairList.add(Pair.of(k, v));
        return this;
    }

    public I18nString add(String k, Function<Locale, String> compute) {
        delayedList.add(Pair.of(k, compute));
        return this;
    }

    public String getNode() {
        return node;
    }

    public List<Pair<String, String>> getPairList() {
        return pairList;
    }

    public List<Pair<String, Function<Locale, String>>> getDelayedList() {
        return delayedList;
    }
}
