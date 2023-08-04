package org.example.cheks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import static org.example.cheks.ChecksArguments.tempArgs;

public class ChecksFiles {
    private static String tempLine = "";

    private static int index = -1;

    static void checkOutputFile() {

        int indexDot = tempArgs.get(index).lastIndexOf(".");
        //проверка выходного файла (на имя и существование)

        if (!tempArgs.get(index).endsWith(".txt")) {
            if (indexDot > 0) {
                tempLine = tempArgs.get(index).substring(0, indexDot);
            } else {
                tempLine = tempArgs.get(index);
            }
            tempArgs.set(index, tempLine + ".txt");

        }

        if (new File(tempArgs.get(index)).isFile()) {
            indexDot = tempArgs.get(index).lastIndexOf(".");
            if (indexDot > 0) {
                tempLine = tempArgs.get(index).substring(0, indexDot);
            } else {
                tempLine = tempArgs.get(index);
            }

            while(true){
                Random random = new Random();
                int i = random.nextInt(1000 + 1);
                if (!new File(tempLine + "_" + i + ".txt").isFile()) {
                    System.out.println("Файл с именем " + tempArgs.get(index) +
                            " существует. Новое имя выходного файла " + tempLine + "_" + i + ".txt\n");
                    tempArgs.set(index, tempLine + "_" + i + ".txt");
                    tempLine = "";
                    index++;
                    break;
                }
            }


        }  else {
            index++;
        }
    }

    static void checkInputFiles(){
        Scanner scanner = new Scanner(System.in);
        int sizeList = tempArgs.size();
        boolean isCorrFilesIn = false;
        List<Integer> idElementDelete = new ArrayList<>();
        while(true) {
            for (int i = index; i < sizeList; i++) {//проверка файлов на существование
                int indexDot = tempArgs.get(i).lastIndexOf(".");
                if (!tempArgs.get(i).endsWith(".txt")) {
                    if (indexDot > 0) {
                        tempLine = tempArgs.get(i).substring(0, indexDot);
                    } else {
                        tempLine = tempArgs.get(i);
                    }
                    boolean temp = new File(tempLine + ".txt").isFile();
                    if (temp) {
                        tempArgs.set(i, tempLine + ".txt");
                        isCorrFilesIn = true;
                        //System.out.println(tempArgs);
                    } else {
                        idElementDelete.add(i);
                        System.out.println("Имя файла " + tempArgs.get(i) + " будет удалено из аргументов, так как файл не был найден\n");
                    }

                } else if (!new File(tempArgs.get(i)).isFile()) {
                    System.out.println("Файл с именем: " + tempArgs.get(i) + " не найден\n");
                    idElementDelete.add(i);
                    System.out.println("Имя файла " + tempArgs.get(i) + " будет удалено из аргументов, так как файл не был найден\n");
                }
            }
            //удаление не найденных фалов
            if (!idElementDelete.isEmpty()) {
                for (int z = idElementDelete.size() - 1; z >= 0; z--) {
                    tempArgs.remove((int) idElementDelete.get(z));
                }
                idElementDelete.clear();
                System.out.println("Аргументы после удаления: " + tempArgs + "\n");
            }
            //остались ли файлы с данными

            if (tempArgs.size() - (index + 1) > 0) {
                isCorrFilesIn = true;
                break;
            }
            //System.out.println(Arrays.toString(args));

            if (!isCorrFilesIn) {
                System.out.println("Некорректно указаны имена входящих файлов. Введите новые, через пробел: ");
                List<String> files = List.of(scanner.nextLine().split(" "));
                if (files.get(0).equals(":q"))
                    System.exit(0);
                tempArgs.addAll(files);
            } else {
                break;
            }
        }
    }
}
