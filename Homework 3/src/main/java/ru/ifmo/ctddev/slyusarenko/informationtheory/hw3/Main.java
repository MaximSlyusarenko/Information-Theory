package ru.ifmo.ctddev.slyusarenko.informationtheory.hw3;

import ru.ifmo.ctddev.slyusarenko.informationtheory.hw3.algorithms.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Maxim Slyusarenko
 * @since 16.11.16
 */
public class Main {

    public static void main(String[] args) {
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader("Homework 3/src/main/resources/input.in"))) {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        line = line.replaceAll(" ", "_").toUpperCase();
        System.out.println("Заменим все пробелы в строке на символ нижнего подчеркивания и" +
                " переведем строку в верхний регистр. Получим следующую строку:");
        System.out.println(line);
        List<Algorithm> algorithms = createAlgorithms();
        for (Algorithm algorithm : algorithms) {
            algorithm.solve(line);
            System.out.println("Запустили алгоритм " + algorithm.getName() + " и получили следующий результат: ");
            System.out.println(algorithm.getResultAsString());
        }
    }

    private static List<Algorithm> createAlgorithms() {
        List<Algorithm> algorithms = new ArrayList<>();
        algorithms.add(new HuffmanAlgorithm());
        algorithms.add(new AdaptiveCodingAlgorithm());
        algorithms.add(new NumerationCodingAlgorithm());
        algorithms.add(new ZivLempel77Algorithm());
        algorithms.add(new ZivLempel78Algorithm());
        algorithms.add(new PPMAlgorithm());
        algorithms.add(new BarrowsWillerAlgorithm());
        return algorithms;
    }
}
