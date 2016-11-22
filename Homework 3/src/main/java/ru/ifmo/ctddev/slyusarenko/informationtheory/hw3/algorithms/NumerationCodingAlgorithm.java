package ru.ifmo.ctddev.slyusarenko.informationtheory.hw3.algorithms;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author Maxim Slyusarenko
 * @since 16.11.16
 */
public class NumerationCodingAlgorithm implements Algorithm {

    private Map<Character, Integer> symbolsToCount;
    private List<Integer> tau;
    private List<Integer> tau2;
    private int l1;
    private int l2;

    private static final int ALPHABET_SIZE = 256;

    public NumerationCodingAlgorithm() {
        symbolsToCount = new HashMap<>();
        tau = new ArrayList<>();
        tau2 = new ArrayList<>();
    }

    @Override
    public void solve(String request) {
        for (int i = 0; i < request.length(); i++) {
            symbolsToCount.putIfAbsent(request.charAt(i), 0);
            symbolsToCount.put(request.charAt(i), symbolsToCount.get(request.charAt(i)) + 1);
        }
        computeTau();
        computeTau2();
        l1 = countL1(request);
        l2 = countL2(request);
    }

    private double factorial(int value) {
        double result = 1.0;
        for (int i = 2; i < value; i++) {
            result *= i;
        }
        return result;
    }

    private int countL1(String request) {
        double result1 = request.length();
        for (Integer count : tau) {
            result1 *= count;
        }
        double result2 = 1.0;
        for (int i = ALPHABET_SIZE - tau.size() + 1; i < ALPHABET_SIZE; i++) {
            result2 *= i;
        }
        List<Integer> tmp = new ArrayList<>(tau2);
        tmp.remove(0);
        for (Integer count : tmp) {
            result2 /= factorial(count);
        }
        return (int) (Math.log(result1) / Math.log(2)) + (int) (Math.log(result2) / Math.log(2));
    }

    private int countL2(String request) {
        double result = factorial(request.length());
        for (Integer count : tau) {
            result /= factorial(count);
        }
        return (int) (Math.log(result) / Math.log(2));
    }

    private void computeTau() {
        tau = new ArrayList<>(symbolsToCount.values());
        tau.sort(Comparator.reverseOrder());
    }

    private void computeTau2() {
        Map<Integer, Integer> countOfCount = new HashMap<>();
        for (Integer count : tau) {
            countOfCount.putIfAbsent(count, 0);
            countOfCount.put(count, countOfCount.get(count) + 1);
        }
        tau2 = new ArrayList<>(countOfCount.values());
        tau2.add(ALPHABET_SIZE - symbolsToCount.size());
        tau2.sort(Comparator.reverseOrder());
    }

    @Override
    public String getName() {
        return "нумерационного кодирования";
    }

    @Override
    public String getResultAsString() {
        StringBuilder result = new StringBuilder();
        result.append("tau = (");
        for (Integer count : tau) {
            result.append(count).append(", ");
        }
        result.append("0 [x ").append(ALPHABET_SIZE - symbolsToCount.size()).append("])\ntau' = (");
        for (Integer count : tau2) {
            result.append(count).append(", ");
        }
        result.delete(result.length() - 2, result.length()).append(")\nl1 = ").append(l1);
        result.append(" бит\nl2 = ").append(l2);
        result.append(" бит\nL = l1 + l2 = ").append(l1 + l2).append(" бит\n\n");
        return result.toString();
    }
}
