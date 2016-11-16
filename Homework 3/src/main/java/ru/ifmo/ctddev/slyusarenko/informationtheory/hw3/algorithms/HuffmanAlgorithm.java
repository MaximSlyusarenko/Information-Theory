package ru.ifmo.ctddev.slyusarenko.informationtheory.hw3.algorithms;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Maxim Slyusarenko
 * @since 16.11.16
 */
public class HuffmanAlgorithm implements Algorithm {

    private Map<Character, Integer> symbolsToCounts;
    private TreeSet<Node> nodes;
    private Map<Character, String> huffmanCode;
    private Map<String, Character> regularHuffmanCode;
    private int huffmanBitCount;
    private Map<Integer, LevelInfo> levelInfo;
    private int naiveBits;

    public HuffmanAlgorithm() {
        symbolsToCounts = new HashMap<>();
        nodes = new TreeSet<>();
        huffmanCode = new HashMap<>();
        regularHuffmanCode = new TreeMap<>((o1, o2) -> {
            int res = Integer.compare(o1.length(), o2.length());
            return res == 0 ? 1 : res;
        });
        levelInfo = new TreeMap<>();
    }

    private void firstRun(String request) {
        for (int i = 0; i < request.length(); i++) {
            symbolsToCounts.putIfAbsent(request.charAt(i), 0);
            symbolsToCounts.put(request.charAt(i), symbolsToCounts.get(request.charAt(i)) + 1);
        }
    }

    private void initiateNodes() {
        nodes.addAll(symbolsToCounts.entrySet().stream().map(symbolToCount ->
                new Node(symbolToCount.getValue(), symbolToCount.getKey())).collect(Collectors.toList()));
    }

    private Node createTree() {
        while (nodes.size() > 1) {
            Node first = nodes.pollFirst();
            Node second = nodes.pollFirst();
            nodes.add(new Node(first, second));
        }
        return nodes.first();
    }

    private void findCodes(Node currentNode, String currentCode) {
        if (currentNode.isLeaf()) {
            huffmanCode.put(currentNode.getSymbol(), currentCode);
            return;
        }
        findCodes(currentNode.getLeft(), currentCode + "0");
        findCodes(currentNode.getRight(), currentCode + "1");
    }

    private Node createCode() {
        Node tree = createTree();
        findCodes(tree, "");
        return tree;
    }

    private void computeBitCount(String request) {
        huffmanBitCount = 0;
        for (int i = 0; i < request.length(); i++) {
            huffmanBitCount += huffmanCode.get(request.charAt(i)).length();
        }
    }

    private void createRegularHuffmanCode() {
        for (Map.Entry<Character, String> entry : huffmanCode.entrySet()) {
            regularHuffmanCode.put(entry.getValue(), entry.getKey());
        }
    }

    private void addVertexes(Node tree, int level) {
        if (tree == null) {
            return;
        }
        LevelInfo info = levelInfo.getOrDefault(level, new LevelInfo());
        info.incrementVertexes();
        levelInfo.put(level, info);
        addVertexes(tree.getLeft(), level + 1);
        addVertexes(tree.getRight(), level + 1);
    }

    private void createLevelInfo(Node tree) {
        Map<Integer, Integer> leafsPerLevel = new HashMap<>();
        for (Map.Entry<String, Character> entry : regularHuffmanCode.entrySet()) {
            int level = entry.getKey().length();
            leafsPerLevel.putIfAbsent(level, 0);
            leafsPerLevel.put(level, leafsPerLevel.get(level) + 1);
        }
        addVertexes(tree, 0);
        for (Map.Entry<Integer, Integer> levelLeafs : leafsPerLevel.entrySet()) {
            LevelInfo info = levelInfo.get(levelLeafs.getKey());
            info.setNumberOfLeafs(levelLeafs.getValue());
            levelInfo.put(levelLeafs.getKey(), info);
        }
    }

    @Override
    public void solve(String request) {
        firstRun(request);
        initiateNodes();
        Node tree = createCode();
        computeBitCount(request);
        createRegularHuffmanCode();
        createLevelInfo(tree);
        naiveBits = request.length() * 8;
    }

    @Override
    public String getName() {
        return "двухпроходного кодирования с использованием кода Хаффмена";
    }

    private void appendAlign(String alignOn, int notAlignedLength, StringBuilder result) {
        for (int i = 0; i < alignOn.length() - notAlignedLength; i++) {
            result.append(" ");
        }
    }

    @Override
    public String getResultAsString() {
        StringBuilder result = new StringBuilder();
        result.append("Код Хаффмена для текста:\n| Буква | Число появлений | Длина кодового слова | Кодовое слово |\n");
        for (Map.Entry<Character, String> entity : huffmanCode.entrySet()) {
            result.append("| ");
            result.append(entity.getKey());
            appendAlign("Буква", 1, result);
            result.append(" | ");
            result.append(symbolsToCounts.get(entity.getKey()));
            appendAlign("Число появлений", symbolsToCounts.get(entity.getKey()).toString().length(), result);
            result.append(" | ");
            result.append(entity.getValue().length());
            appendAlign("Длина кодового слова", Integer.toString(entity.getValue().length()).length(), result);
            result.append(" | ");
            result.append(entity.getValue());
            appendAlign("Кодовое слово", entity.getValue().length(), result);
            result.append(" |\n");
        }
        result.append("\nЧисло бит, которое мы затратим на передачу кодового слова без служебной информации," +
                " если закодируем его, пользуясь данным алгоритмом: ")
                .append(huffmanBitCount).append(" бит\n");
        result.append("\nРегулярный код Хаффмена:\n| Буква | Номер яруса (длина кодового слова) | Кодовое слово |\n");
        for (Map.Entry<String, Character> entity : regularHuffmanCode.entrySet()) {
            result.append("| ");
            result.append(entity.getValue());
            appendAlign("Буква", 1, result);
            result.append(" | ");
            result.append(entity.getKey().length());
            appendAlign("Номер яруса (длина кодового слова)", Integer.toString(entity.getKey().length()).length(), result);
            result.append(" | ");
            result.append(entity.getKey());
            appendAlign("Кодовое слово", entity.getKey().length(), result);
            result.append(" |\n");
        }
        result.append("\nПодсчет числа бит на передачу регулярного кода:\n| Ярус | Общее число вершин | " +
                "Число концевых вершин | Диапазон значений | Затраты в битах |\n");
        int sumBits = 0;
        for (Map.Entry<Integer, LevelInfo> entity : levelInfo.entrySet()) {
            result.append("| ");
            result.append(entity.getKey());
            appendAlign("Ярус", entity.getKey().toString().length(), result);
            result.append(" | ");
            result.append(entity.getValue().getNumberOfVertexes());
            appendAlign("Общее число вершин", Integer.toString(entity.getValue().getNumberOfVertexes()).length(), result);
            result.append(" | ");
            result.append(entity.getValue().getNumberOfLeafs());
            appendAlign("Число концевых вершин", Integer.toString(entity.getValue().getNumberOfLeafs()).length(), result);
            result.append(" | 0..");
            result.append(entity.getValue().getNumberOfVertexes());
            appendAlign("Диапазон значений", Integer.toString(entity.getValue().getNumberOfVertexes()).length() + 3, result);
            result.append(" | ");
            int bits = (int) ((Math.log(entity.getValue().getNumberOfVertexes())) / Math.log(2)) + 1;
            result.append(bits);
            appendAlign("Затраты в битах", Integer.toString(bits).length(), result);
            result.append(" |\n");
            sumBits += bits;
        }
        result.append("\nИтого затратим на передачу информации о коде: ").append(sumBits).append(" бит.\n");
        result.append("Всего на передачу информации в случае кодирования потратим: ").append(huffmanBitCount + sumBits).append(" бит\n");
        result.append("В случае, если передаем сообщение, не прибегая к кодированию (8 бит на символ) потратим: ")
                .append(naiveBits).append(" бит\n\n");
        return result.toString();
    }

    @Getter
    private class Node implements Comparable {

        Integer weight;
        Character symbol;
        Node left;
        Node right;

        Node(Integer weight, Character symbol) {
            this.weight = weight;
            this.symbol = symbol;
        }

        Node (Node left, Node right) {
            this.weight = left.getWeight() + right.getWeight();
            this.left = left;
            this.right = right;
        }

        boolean isLeaf() {
            return symbol != null;
        }

        @Override
        public int compareTo(Object o) {
            if (!(o instanceof Node)) {
                throw new UnsupportedOperationException("Incomparable types");
            }
            Node to = (Node) o;
            int weightsCompare = weight.compareTo(to.getWeight());
            if (weightsCompare != 0) {
                return weightsCompare;
            }
            if (this.equals(o)) {
                return 0;
            }
            return 1;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Node)) {
                return false;
            }
            Node to = (Node) o;
            return Objects.equals(weight, to.getWeight()) && Objects.equals(symbol, to.getSymbol()) &&
                    Objects.equals(left, to.getLeft()) && Objects.equals(right, to.getRight());
        }
    }

    @Getter
    private class LevelInfo {
        int numberOfVertexes;
        @Setter
        int numberOfLeafs;

        LevelInfo() {
            this.numberOfVertexes = 0;
            this.numberOfLeafs = 0;
        }

        void incrementVertexes() {
            numberOfVertexes++;
        }
    }
}
