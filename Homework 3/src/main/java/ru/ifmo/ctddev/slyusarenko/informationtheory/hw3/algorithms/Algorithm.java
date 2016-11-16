package ru.ifmo.ctddev.slyusarenko.informationtheory.hw3.algorithms;

/**
 * @author Maxim Slyusarenko
 * @since 16.11.16
 */
public interface Algorithm {
    void solve(String request);
    String getName();
    String getResultAsString();
}
