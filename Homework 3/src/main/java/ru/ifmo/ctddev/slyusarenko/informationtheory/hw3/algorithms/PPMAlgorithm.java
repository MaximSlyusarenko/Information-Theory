package ru.ifmo.ctddev.slyusarenko.informationtheory.hw3.algorithms;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

/**
 * @author Maxim Slyusarenko
 * @since 16.11.16
 */
public class PPMAlgorithm implements Algorithm {

    private Map<Character, List<Integer>> symbolsToPositions;
    private List<ResultElement> result;
    private int currentNumber;
    private int fullSize;

    public PPMAlgorithm() {
        symbolsToPositions = new HashMap<>();
        result = new ArrayList<>();
        currentNumber = 256;
        fullSize = 0;
    }

    @Override
    public void solve(String request) {
        for (int i = 0; i < request.length(); i++) {
            ResultElement resultElement;
            if (i == 0 || symbolsToPositions.get(request.charAt(i - 1)).size() == 1) {
                if (!symbolsToPositions.containsKey(request.charAt(i))) {
                    resultElement = new ResultElement(i + 1, request.charAt(i), "empty",
                            Integer.toString(i), "1/" + (i + 1), "1/" + currentNumber);
                    currentNumber--;
                } else {
                    resultElement = new ResultElement(i + 1, request.charAt(i), "empty",
                            Integer.toString(i), "", symbolsToPositions.get(request.charAt(i)).size() + "/" + (i + 1));
                }
            } else {
                List<Integer> positions = symbolsToPositions.get(request.charAt(i - 1));
                int maxLengthOfContext = 1;
                StringBuilder currentTau = new StringBuilder();
                String maxContext = "empty";
                for (Integer position : positions) {
                    if (position.equals(i - 1)) {
                        break;
                    }
                    int j = position;
                    int q = i - 1;
                    while (j > 0 && request.charAt(j) == request.charAt(q)) {
                        j--;
                        q--;
                    }
                    if (i - q > maxLengthOfContext) {
                        maxLengthOfContext = i - q;
                        maxContext = request.substring(q + 1, i);
                    }
                }
                Map<Character, Integer> tmp = getSymbolsAfterContext(request, maxContext, i);
                currentTau.append(tmp.values().stream().reduce(0, (a, b) -> a + b)).append(", ");
                StringBuilder escProbability = new StringBuilder();
                int subtract = 0;
                String currentContext = maxContext;
                while (true) {
                    Map<Character, Integer> symbolsAfterContext = getSymbolsAfterContext(request, currentContext, i);
                    int allSymbols = symbolsAfterContext.values().stream().reduce(0, (a, b) -> a + b);
                    if (!symbolsAfterContext.containsKey(request.charAt(i))) {
                        if (subtract == 0) {
                            currentTau = new StringBuilder();
                        }
                        escProbability.append("1/").append(allSymbols + 1 - subtract);
                        currentTau.append(allSymbols).append(", ");
                        if (currentContext.equals("empty")) {
                            break;
                        } else {
                            escProbability.append("*");
                        }
                    } else {
                        escProbability.append(symbolsAfterContext.get(request.charAt(i))).append("/")
                                .append(allSymbols + 1 - subtract);
                        if (subtract != 0) {
                            currentTau.append(allSymbols).append(", ");
                        }
                        break;
                    }
                    currentContext = currentContext.substring(1);
                    if (currentContext.length() == 0) {
                        currentContext = "empty";
                    }
                    subtract += allSymbols - subtract;
                }
                currentTau.delete(currentTau.length() - 2, currentTau.length());
                if (currentContext.equals("empty")) {
                    if (!symbolsToPositions.containsKey(request.charAt(i))) {
                        resultElement = new ResultElement(i + 1, request.charAt(i), maxContext,
                                currentTau.toString(), escProbability.toString(), "1/" + currentNumber);
                        currentNumber--;
                    } else {
                        String escProbabilityString = escProbability.toString();
                        String probabilityString = "";
                        if (escProbabilityString.lastIndexOf("*") == -1) {
                            probabilityString = escProbabilityString;
                            escProbabilityString = "";
                        } else {
                            probabilityString = escProbabilityString.substring(escProbabilityString.lastIndexOf("*") + 1);
                            escProbabilityString = escProbabilityString.substring(0, escProbabilityString.lastIndexOf("*"));
                        }
                        resultElement = new ResultElement(i + 1, request.charAt(i), maxContext,
                                currentTau.toString(), escProbabilityString, probabilityString);
                    }
                } else {
                    String escProbabilityString = escProbability.toString();
                    String probabilityString = "";
                    if (escProbabilityString.lastIndexOf("*") == -1) {
                        probabilityString = escProbabilityString;
                        escProbabilityString = "";
                    } else {
                        probabilityString = escProbabilityString.substring(escProbabilityString.lastIndexOf("*") + 1);
                        escProbabilityString = escProbabilityString.substring(0, escProbabilityString.lastIndexOf("*"));
                    }
                    resultElement = new ResultElement(i + 1, request.charAt(i), maxContext,
                            currentTau.toString(), escProbabilityString, probabilityString);
                }
            }
            symbolsToPositions.putIfAbsent(request.charAt(i), new ArrayList<>());
            symbolsToPositions.get(request.charAt(i)).add(i);
            result.add(resultElement);
        }
        int firstBits = 0;
        int secondBits = 0;
        for (ResultElement resultElement : result) {
            firstBits += -Math.log(stringToLogarithmInSum(resultElement.getConditionalProbability())) / Math.log(2);
            if (resultElement.getProbability().length() > 0) {
                secondBits += -Math.log(stringToLogarithmInSum(resultElement.getProbability())) / Math.log(2);
            }
        }
        fullSize = firstBits + secondBits;
    }

    private Map<Character, Integer> getSymbolsAfterContext(String request, String context, int i) {
        if (context.equals("empty")) {
            Map<Character, Integer> result = new HashMap<>();
            for (int j = 0; j < i; j++) {
                result.putIfAbsent(request.charAt(j), 0);
                result.put(request.charAt(j), result.get(request.charAt(j)) + 1);
            }
            return result;
        }
        Map<Character, Integer> symbolsAfterContext = new HashMap<>();
        for (int j = 0; j < i - context.length(); j++) {
            if (request.substring(j).startsWith(context)) {
                symbolsAfterContext.putIfAbsent(request.charAt(j + context.length()), 0);
                symbolsAfterContext.put(request.charAt(j + context.length()),
                        symbolsAfterContext.get(request.charAt(j + context.length())) + 1);
            }
        }
        return symbolsAfterContext;
    }

    private double stringToLogarithmInSum(String str) {
        if (str.length() == 0) {
            return 0.0;
        }
        if (!str.contains("*")) {
            return parseRational(str);
        }
        String[] strings = str.split("\\*");
        double res = 1.0;
        for (String string : strings) {
            res *= parseRational(string);
        }
        return res;
    }

    private Double parseRational(String rational) {
        String[] strings = rational.split("/");
        return Double.parseDouble(strings[0]) / Double.parseDouble(strings[1]);
    }

    @Override
    public String getName() {
        return "PPM";
    }

    private void appendAlign(String alignOn, int notAlignedLength, StringBuilder result) {
        for (int i = 0; i < alignOn.length() - notAlignedLength; i++) {
            result.append(" ");
        }
    }

    @Override
    public String getResultAsString() {
        StringBuilder result = new StringBuilder();
        result.append("\n| Шаг | Буква | Контекст s           | " +
                "tau(s)                                    | probability(esc|s)                                        | probability(a|s) |\n");
        for (ResultElement entity : this.result) {
            result.append("| ");
            result.append(entity.getStep());
            appendAlign("Шаг", Integer.toString(entity.getStep()).length(), result);
            result.append(" | ");
            result.append(entity.getSymbol());
            appendAlign("Буква", 1, result);
            result.append(" | ");
            result.append(entity.getContext());
            appendAlign("Контекст s          ", entity.getContext().length(), result);
            result.append(" | ");
            result.append(entity.getTau());
            appendAlign("tau(s)                                   ", entity.getTau().length(), result);
            result.append(" | ");
            result.append(entity.getProbability());
            appendAlign("probability(esc|s)                                       ", entity.getProbability().length(), result);
            result.append(" | ");
            result.append(entity.getConditionalProbability());
            appendAlign("probability(a|s)", entity.getConditionalProbability().length(), result);
            result.append(" |\n");
        }
        result.append("\n\n");
        result.append("Суммарный размер: ").append(fullSize).append(" бит\n\n");
        return result.toString();
    }

    @Getter
    @AllArgsConstructor
    private class ResultElement {
        private int step;
        private char symbol;
        private String context;
        private String tau;
        private String probability;
        private String conditionalProbability;
    }
}
