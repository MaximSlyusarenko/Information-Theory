package ru.ifmo.ctddev.slyusarenko.informationtheory.hw3.algorithms;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Maxim Slyusarenko
 * @since 16.11.16
 */
public class AdaptiveCodingAlgorithm implements Algorithm {

    private Map<Character, Integer> symbolsToCount;
    private List<Pair<String, String>> result;
    private int subAlphabetSize;
    private double l;

    private static final int ALPHABET_SIZE = 256;

    public AdaptiveCodingAlgorithm() {
        symbolsToCount = new HashMap<>();
        result = new ArrayList<>();
        subAlphabetSize = ALPHABET_SIZE;
        l = 0.0;
    }

    @Override
    public void solve(String request) {
        for (int i = 0; i < request.length(); i++) {
            if (!symbolsToCount.containsKey(request.charAt(i))) {
                symbolsToCount.put(request.charAt(i), 1);
                result.add(new Pair<>(Character.toString(request.charAt(i)), "1/" + i + " * " + "1/" + subAlphabetSize));
                l += Math.log(1.0 / ((double) i + 1)) / Math.log(2);
                l += Math.log(1.0 / ((double) subAlphabetSize)) / Math.log(2);
                subAlphabetSize--;
            } else {
                symbolsToCount.put(request.charAt(i), symbolsToCount.get(request.charAt(i)) + 1);
                result.add(new Pair<>(Character.toString(request.charAt(i)), symbolsToCount.get(request.charAt(i)) + "/" + i));
                l += Math.log((double) symbolsToCount.get(request.charAt(i)) / (double) i) / Math.log(2);
            }
        }
    }

    @Override
    public String getName() {
        return "адаптивного кодирования с применением арифметического кодирования";
    }

    private void appendAlign(String alignOn, int notAlignedLength, StringBuilder result) {
        for (int i = 0; i < alignOn.length() - notAlignedLength; i++) {
            result.append(" ");
        }
    }

    @Override
    public String getResultAsString() {
        StringBuilder result = new StringBuilder();
        result.append("| t (момент времени) | a (символ) | probability(a) |\n");
        int i = 0;
        for (Pair<String, String> entity: this.result) {
            result.append("| ");
            result.append(i);
            appendAlign("t (момент времени)", Integer.toString(i).length(), result);
            result.append(" | ");
            result.append(entity.getKey());
            appendAlign("a (символ)", 1, result);
            result.append(" | ");
            result.append(entity.getValue());
            appendAlign("probability(a)", entity.getValue().length(), result);
            result.append(" |\n");
            i++;
        }
        result.append("\nИсходя из вспомогательной таблицы, получим L:\nL = -log(G) + 1 = ")
                .append(((int) -l) + 2).append(" бит, где G- произведение чисел в таблице\n\n");
        return result.toString();
    }
}
