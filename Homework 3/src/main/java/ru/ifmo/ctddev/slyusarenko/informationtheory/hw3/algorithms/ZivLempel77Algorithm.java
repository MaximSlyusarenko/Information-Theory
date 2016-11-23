package ru.ifmo.ctddev.slyusarenko.informationtheory.hw3.algorithms;

import lombok.Getter;

import java.util.*;

/**
 * @author Maxim Slyusarenko
 * @since 16.11.16
 */
public class ZivLempel77Algorithm implements Algorithm {

    private Map<Character, List<Integer>> symbolToPositions;
    private List<ResultElement> result;
    private int fullSize = 0;
    private List<String> monCode;

    public ZivLempel77Algorithm() {
        symbolToPositions = new HashMap<>();
        result = new ArrayList<>();
        monCode = Arrays.asList("", "0", "100", "101", "11000", "11001", "11010", "11011", "1110000", "1110001", "1110010");
    }

    @Override
    public void solve(String request) {
        int step = 0;
        for (int i = 0; i < request.length(); i++) {
            if (!symbolToPositions.containsKey(request.charAt(i))) {
                List<Integer> positions = new ArrayList<>();
                positions.add(i);
                symbolToPositions.put(request.charAt(i), positions);
                result.add(new ResultElement(step, false, request.charAt(i) + "",
                        -1, 0, "0 bin(" + request.charAt(i) + ")", 9));
                fullSize += 9;
            } else {
                List<Integer> positions = symbolToPositions.get(request.charAt(i));
                int maxLength = -1;
                int maxLengthPosition = -1;
                int newIPosition = i;
                for (Integer position : positions) {
                    int j = position;
                    int q = i;
                    while (j < request.length() && request.charAt(j) == request.charAt(q)) {
                        j++;
                        q++;
                    }
                    if (j - position >= maxLength) {
                        maxLength = j - position;
                        maxLengthPosition = position;
                        newIPosition = q;
                    }
                }
                String prefixCode = monCode.get(request.substring(i, newIPosition).length());
                result.add(new ResultElement(step, true, request.substring(i, newIPosition),
                        i - maxLengthPosition, request.substring(i, newIPosition).length(),
                        "1 " + Integer.toBinaryString(i - maxLengthPosition) + " " + prefixCode,
                        1 + Integer.toBinaryString(i - maxLengthPosition).length() + prefixCode.length()));
                i = newIPosition - 1;
                fullSize += 1 + Integer.toBinaryString(i - maxLengthPosition).length() + prefixCode.length();
            }
            step++;
        }
    }

    @Override
    public String getName() {
        return "Зива-Лемпела-77 (метод скользящего словаря)";
    }

    private void appendAlign(String alignOn, int notAlignedLength, StringBuilder result) {
        for (int i = 0; i < alignOn.length() - notAlignedLength; i++) {
            result.append(" ");
        }
    }

    @Override
    public String getResultAsString() {
        StringBuilder result = new StringBuilder();
        result.append("\n| Шаг | Флаг | Последовательность букв | d  | " +
                "Размер совпадения | Кодовая последовательность | Биты |\n");
        for (ResultElement entity : this.result) {
            result.append("| ");
            result.append(entity.getStep());
            appendAlign("Шаг", Integer.toString(entity.getStep()).length(), result);
            result.append(" | ");
            result.append(entity.isFlag() ? "1" : "0");
            appendAlign("Флаг", 1, result);
            result.append(" | ");
            result.append(entity.getSymbolSequence());
            appendAlign("Последовательность букв", entity.getSymbolSequence().length(), result);
            result.append(" | ");
            result.append(entity.getD() == -1 ? "-" : entity.getD());
            appendAlign("d ", entity.getD() == -1 ? 1 : Integer.toString(entity.getD()).length(), result);
            result.append(" | ");
            result.append(entity.getSize());
            appendAlign("Размер совпадения", Integer.toString(entity.getSize()).length(), result);
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
        boolean flag;
        String symbolSequence;
        int d;
        int size;
        String code;
        int bits;

        ResultElement(int step, boolean flag, String symbolSequence, int d, int size, String code, int bits) {
            this.step = step;
            this.flag = flag;
            this.symbolSequence = symbolSequence;
            this.d = d;
            this.size = size;
            this.code = code;
            this.bits = bits;
        }
    }
}
