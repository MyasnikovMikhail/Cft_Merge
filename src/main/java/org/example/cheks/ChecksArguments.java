package org.example.cheks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.example.cheks.ChecksFiles.checkInputFiles;
import static org.example.cheks.ChecksFiles.checkOutputFile;
import static org.example.cheks.ChecksParameters.indexSortAndType;

public class ChecksArguments {

    static List<String> tempArgs = new ArrayList<>();

    public static String[] checksArguments(String[] args) {
        args = filterParameters(Arrays.stream(args).map(String::toLowerCase).toList()).toArray(new String[0]);
        args = checksParameters(args);
        return args;
    }

    public static String[] checksParameters(String[] args) {
        Scanner scanner = new Scanner(System.in);
        tempArgs.addAll(Arrays.stream(args).distinct().toList());
        while(tempArgs.size() < 3) {
            System.out.println("Введите параметры для запуска программы: ");
            tempArgs.addAll(Arrays.stream(scanner.nextLine().split(" ")).distinct().toList());
            filterParameters(tempArgs);
        }
        indexSortAndType();
        checkOutputFile();
        checkInputFiles();
        args = tempArgs.stream().distinct().toList().toArray(new String[0]);
        System.out.println("Параметры после проверки: " + Arrays.toString(args) + "\n");
        return args;
    }

    public static List<String> filterParameters(List<String> argsList) {
        List<Integer> indexArgs = new ArrayList<>();
        for (int i = 2; i < argsList.size(); i++) {
            if (argsList.get(i).startsWith("-")) {
                indexArgs.add(i);
            }
        }
        for (int i = indexArgs.size() - 1; i >= 0; i--) {
            argsList.remove((int) indexArgs.get(i));
        }
        return argsList;
    }
}
