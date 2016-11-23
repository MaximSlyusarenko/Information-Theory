package ru.ifmo.ctddev.slyusarenko.informationtheory.hw3.algorithms;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Maxim Slyusarenko
 * @since 16.11.16
 */
public class ZivLempel78Algorithm implements Algorithm {

    private Map<String, Integer> vocabulary;
    private int fullSize;
    private int addBits;
    private int nextBinaryPower;
    private List<ResultElement> result;

    public ZivLempel78Algorithm() {
        vocabulary = new HashMap<>();
        fullSize = 0;
        addBits = 0;
        nextBinaryPower = 2;
        result = new ArrayList<>();
    }

    @Override
    public void solve(String request) {
        int step = 1;
        for (int i = 0; i < request.length(); i++) {
            if (!vocabulary.containsKey("" + request.charAt(i))) {
                vocabulary.put("" + request.charAt(i), vocabulary.size() + 1);
                StringBuilder code = new StringBuilder();
                for (int j = 0; j < addBits; j++) {
                    code.append("0");
                }
                code.append("bin(").append(request.charAt(i)).append(")");
                result.add(new ResultElement(step, "" + request.charAt(i), 0, code.toString(), addBits + 8));
                fullSize += addBits + 8;
                if (vocabulary.size() == nextBinaryPower) {
                    addBits++;
                    nextBinaryPower *= 2;
                }
            } else {
                int j = i + 1;
                while (j < request.length() && vocabulary.containsKey(request.substring(i, j))) {
                    j++;
                }
                vocabulary.put(request.substring(i, j), vocabulary.size() + 1);
                StringBuilder code = new StringBuilder();
                for (int k = 0; k < addBits; k++) {
                    code.append("0");
                }
                code.append(Integer.toBinaryString(vocabulary.get(request.substring(i, j - 1))));
                result.add(new ResultElement(step, request.substring(i, j), vocabulary.get(request.substring(i, j - 1)), code.toString(),
                        addBits + Integer.toBinaryString(vocabulary.get(request.substring(i, j - 1))).length()));
                fullSize += addBits + Integer.toBinaryString(vocabulary.get(request.substring(i, j - 1))).length();
                if (vocabulary.size() == nextBinaryPower) {
                    addBits++;
                    nextBinaryPower *= 2;
                }
                i = j - 1;
            }
        }
    }

    @Override
    public String getName() {
        return "Зива-Лемпела-78 (Зива-Лемпела-Велча)";
    }

    private void appendAlign(String alignOn, int notAlignedLength, StringBuilder result) {
        for (int i = 0; i < alignOn.length() - notAlignedLength; i++) {
            result.append(" ");
        }
    }

    @Override
    public String getResultAsString() {
        StringBuilder result = new StringBuilder();
        result.append("\n| Шаг | Последовательность букв | Номер слова | " +
                "Кодовая последовательность | Биты |\n");
        result.append("| 0   | esc                     | -           | -                          | 0    |\n");
        for (ResultElement entity : this.result) {
            result.append("| ");
            result.append(entity.getStep());
            appendAlign("Шаг", Integer.toString(entity.getStep()).length(), result);
            result.append(" | ");
            result.append(entity.getSymbolSequence());
            appendAlign("Последовательность букв", entity.getSymbolSequence().length(), result);
            result.append(" | ");
            result.append(entity.getWordNumber());
            appendAlign("Номер слова", Integer.toString(entity.getWordNumber()).length(), result);
            result.append(" | ");
            result.append(entity.getCode());
            appendAlign("Кодовая последовательность", entity.getCode().length(), result);
            result.append(" | ");
            result.append(entity.getBits());
            appendAlign("Биты", Integer.toString(entity.getBits()).length(), result);
            result.append(" |\n");
        }
        result.append("\n\n");
        result.append("Суммарный размер: ").append(fullSize).append(" бит\n\n");
        return result.toString();
    }

    @Getter
    private class ResultElement {
        int step;
        String symbolSequence;
        int wordNumber;
        String code;
        int bits;

        ResultElement(int step, String symbolSequence, int wordNumber, String code, int bits) {
            this.step = step;
            this.symbolSequence = symbolSequence;
            this.wordNumber = wordNumber;
            this.code = code;
            this.bits = bits;
        }
    }
}
