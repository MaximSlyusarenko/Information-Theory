package ru.ifmo.ctddev.slyusarenko.informationtheory.hw1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Maxim Slyusarenko
 * @since 29.10.16
 */
public class Main {

    private static final int MAX_BLOCK_SIZE = 4;

    private static Map<String, Integer> getSymbolToCounts(String text, int symbols) {
        Map<String, Integer> symbolToCounts = new HashMap<>();
        for (int i = 0; i < text.length() - symbols + 1; i++) {
            StringBuilder current = new StringBuilder("");
            for (int j = 0; j < symbols; j++) {
                current.append(text.charAt(i + j));
            }
            String currentString = current.toString();
            symbolToCounts.putIfAbsent(currentString, 0);
            symbolToCounts.put(currentString, symbolToCounts.get(currentString) + 1);
        }
        return symbolToCounts;
    }

    private static double calculateTheoreticalMin(String text, int symbols) {
        Map<String, Integer> symbolToCounts = getSymbolToCounts(text, symbols);
        double bits = 0;
        for (Map.Entry<String, Integer> symbolToCount : symbolToCounts.entrySet()) {
            bits += -Math.log(((double) symbolToCount.getValue()) / ((double) (text.length() - symbols + 1))) * symbolToCount.getValue() / symbols;
        }
        return bits / 8.0;
    }

    private static double calculateEntropy(String text, int symbols) {
        Map<String, Integer> symbolToCounts = getSymbolToCounts(text, symbols);
        double entropy = 0;
        for (Map.Entry<String, Integer> symbolToCount : symbolToCounts.entrySet()) {
            double p = ((double) symbolToCount.getValue()) / ((double) (text.length() - symbols + 1));
            entropy += -Math.log(p) * p;
        }
        return entropy / symbols;
    }

    private static double splitAndCountBitsPerSymbol(String text) {
        int halfLength = text.length() / 2;
        Map<String, Integer> symbolToCounts = getSymbolToCounts(text.substring(0, halfLength), 1);
        double sum = 0.0;
        for (int i = halfLength; i < text.length(); i++) {
            if (symbolToCounts.containsKey("" + text.charAt(i))) {
                sum += -Math.log(((double) symbolToCounts.get("" + text.charAt(i)) / ((double) text.length() / 2)));
            } else {
                sum += 8.0;
            }
        }
        sum /= (double) text.length() / 2;
        return sum;
    }

    public static void main(String[] args) {
        StringBuilder textBuilder = new StringBuilder("");
        char[] buffer = new char[1000];
        try (BufferedReader reader = new BufferedReader(new FileReader("Homework 1/src/main/resources/PROGL"))) {
            while (reader.read(buffer, 0, 1000) != -1) {
                textBuilder.append(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 1; i <= MAX_BLOCK_SIZE; i++) {
            System.out.println(String.format("%.2f", calculateEntropy(textBuilder.toString(), i)) +
                    " " + String.format("%.2f", calculateTheoreticalMin(textBuilder.toString(), i)));
        }
        System.out.println(splitAndCountBitsPerSymbol(textBuilder.toString()));
    }
}
