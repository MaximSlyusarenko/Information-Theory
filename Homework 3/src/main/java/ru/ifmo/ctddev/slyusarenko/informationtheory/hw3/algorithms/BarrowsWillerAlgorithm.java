package ru.ifmo.ctddev.slyusarenko.informationtheory.hw3.algorithms;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

/**
 * @author Maxim Slyusarenko
 * @since 16.11.16
 */
public class BarrowsWillerAlgorithm implements Algorithm {

    private List<String> cyclicShifts;
    private List<Integer> newCoding;
    private Map<Integer, Integer> newCodingSymbolToCount;
    private int bitsForNewCoding;
    private int bitsForEscapes;
    private int numberOfSequenceBits;
    private int bitsForStringIndex;

    public BarrowsWillerAlgorithm() {
        cyclicShifts = new ArrayList<>();
        newCoding = new ArrayList<>();
        newCodingSymbolToCount = new HashMap<>();
    }

    private List<String> getAllCyclicShifts(String request) {
        List<String> result = new ArrayList<>();
        result.add(request);
        String prevShift = request;
        prevShift = prevShift.charAt(prevShift.length() - 1) + prevShift.substring(0, prevShift.length() - 1);
        while (!prevShift.equals(request)) {
            result.add(prevShift);
            prevShift = prevShift.charAt(prevShift.length() - 1) + prevShift.substring(0, prevShift.length() - 1);
        }
        return result;
    }

    private String getLastSymbols() {
        StringBuilder result = new StringBuilder();
        for (String cyclicShift : cyclicShifts) {
            result.append(cyclicShift.charAt(cyclicShift.length() - 1));
        }
        return result.toString();
    }

    private List<Integer> getNewCoding(String lastSymbols) {
        List<Integer> result = new ArrayList<>();
        Map<Character, Integer> symbolToLastPosition = new HashMap<>();
        for (int i = 0; i < lastSymbols.length(); i++) {
            if (!symbolToLastPosition.containsKey(lastSymbols.charAt(i))) {
                result.add(-1);
            } else {
                int prevPosition = symbolToLastPosition.get(lastSymbols.charAt(i));
                Set<Character> symbolsBetween = new HashSet<>();
                for (int j = prevPosition; j < i; j++) {
                    symbolsBetween.add(lastSymbols.charAt(j));
                }
                result.add(symbolsBetween.size() - 1);
            }
            symbolToLastPosition.put(lastSymbols.charAt(i), i);
        }
        return result;
    }

    private void countNewSymbolCodingToCount() {
        for (Integer symbol : newCoding) {
            newCodingSymbolToCount.putIfAbsent(symbol, 0);
            newCodingSymbolToCount.put(symbol, newCodingSymbolToCount.get(symbol) + 1);
        }
    }

    private void countBitsForNewCoding(String request) {
        bitsForNewCoding = 0;
        int currentLength = request.length();
        for (Map.Entry<Integer, Integer> entry : newCodingSymbolToCount.entrySet()) {
            bitsForNewCoding += (int) Math.log(currentLength) / Math.log(2) + 1;
            currentLength -= entry.getValue() - 1;
        }
    }

    private void countBitsForEscapeSymbols(String request) {
        bitsForEscapes = newCodingSymbolToCount.get(-1) * 8;
    }

    private double factorial(int value) {
        double result = 1.0;
        for (int i = 2; i < value; i++) {
            result *= i;
        }
        return result;
    }

    private void countNumberOfSequenceBits(String request) {
        double result = factorial(request.length());
        for (Integer value : newCodingSymbolToCount.values()) {
            result /= factorial(value);
        }
        numberOfSequenceBits = (int) (Math.log(result) / Math.log(2));
    }

    private void countBitsForStringIndex() {
        bitsForStringIndex = (int) (Math.log(cyclicShifts.size()) / Math.log(2) + 1);
    }

    @Override
    public void solve(String request) {
        cyclicShifts = getAllCyclicShifts(request);
        cyclicShifts.sort(Comparator.naturalOrder());
        newCoding = getNewCoding(getLastSymbols());
        countNewSymbolCodingToCount();
        countBitsForNewCoding(request);
        countBitsForEscapeSymbols(request);
        countNumberOfSequenceBits(request);
        countBitsForStringIndex();
    }

    @Override
    public String getName() {
        return "на основе преобразования Барроуза-Уиллера";
    }

    private void appendAlign(String alignOn, int notAlignedLength, StringBuilder result) {
        for (int i = 0; i < alignOn.length() - notAlignedLength; i++) {
            result.append(" ");
        }
    }

    @Override
    public String getResultAsString() {
        StringBuilder result = new StringBuilder();
        result.append("\nЦиклические сдвиги: \n\n");
        result.append("\n| Номер | First | Inside                                                 | Last |\n");
        int step = 0;
        for (String cyclicShift : cyclicShifts) {
            result.append("| ");
            result.append(step);
            appendAlign("Номер", Integer.toString(step).length(), result);
            result.append(" | ");
            result.append(cyclicShift.charAt(0));
            appendAlign("First", 1, result);
            result.append(" | ");
            result.append(cyclicShift.substring(1, cyclicShift.length() - 1));
            appendAlign("Inside                                                ", cyclicShift.substring(1, cyclicShift.length() - 1).length(), result);
            result.append(" | ");
            result.append(cyclicShift.charAt(cyclicShift.length() - 1));
            appendAlign("Last", 1, result);
            result.append(" |\n");
            step++;
        }
        result.append("\n\n");
        result.append("Перепишем резльтат прямого преобразования Барроуза-Уиллера (последний столбец предыдущей таблицы) и получим:\n");
        for (Integer element : newCoding) {
            if (element == -1) {
                result.append("esc ");
            } else {
                result.append(element).append(" ");
            }
        }
        result.append("\n");
        result.append("\nКодирование композиции преобразованной последовательности: \n\n");
        result.append("\n| Буква | Количество |\n");
        for (Map.Entry<Integer, Integer> entry : newCodingSymbolToCount.entrySet()) {
            result.append("| ");
            result.append(entry.getKey() == -1 ? "esc" : entry.getKey());
            appendAlign("Буква", entry.getKey() == -1 ? 3 : Integer.toString(entry.getKey()).length(), result);
            result.append(" | ");
            result.append(entry.getValue());
            appendAlign("Количество", entry.getValue().toString().length(), result);
            result.append(" |\n");
        }
        result.append("\n").append("l = ").append(bitsForNewCoding).append(" + ").append(bitsForEscapes)
                .append(" + ").append(numberOfSequenceBits).append(" + ").append(bitsForStringIndex)
                .append(" = ").append(bitsForNewCoding + bitsForEscapes + numberOfSequenceBits + bitsForStringIndex)
                .append(" бит ");
        result.append("\n\n");
        return result.toString();
    }
}
