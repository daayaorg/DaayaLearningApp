package org.daaya.daayalearningapp.exo.utils;

/**
 * A triple consisting of three elements.
 * <p>
 */
public class Triple<F, S, T> {

    public Triple(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public F first;
    public S second;
    public T third;
}