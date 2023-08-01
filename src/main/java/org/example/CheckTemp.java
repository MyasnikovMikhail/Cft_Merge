package org.example;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CheckTemp {
    private static List<String> tempArgs = new ArrayList<>();
    private static int index = -1;

    private static boolean isCorrTypeAndSort = false;

    private static boolean isCorrCounterParameters = false;

    private static boolean isCorrFileOut = false;

    private static boolean isCorrFilesIn = false;

    private static String tempLine = "";

    public static String[] checksParameter2(String[] args) {
        Scanner scanner = new Scanner(System.in);
        tempArgs.addAll(Arrays.stream(args).distinct().toList());
        while(tempArgs.size() < 3) {
            tempArgs.addAll(Arrays.stream(scanner.nextLine().split(" ")).toList());
        }
        indexSortAndType();
        checkOutputFile();
        return args;
    }

    private static void indexSortAndType() {
        boolean isSort = false;
        boolean isTypeData = false;
        boolean isCorrFirstElem = false;
        boolean isCorrSecondElem = false;
        while(true) {
            String firstElem = tempArgs.get(0);
            String secondElem = tempArgs.get(1);
            if (firstElem.matches("-([ad])") && !isSort) {
                isSort = true;
                isCorrFirstElem = true;
                index = index ==-1 ? 1 : index;
            } else if (firstElem.matches("-([si])") && !isTypeData) {
                isTypeData = true;
                isCorrFirstElem = true;
                index = index ==-1 ? 1 : index;
            } else if(!isCorrFirstElem){
                System.out.println("Первый параметр не относится к режиму сортировки (-a или -d) или типу данных (-s или -i)");
            }
            if (secondElem.startsWith("-")) {
                if (secondElem.matches("-([ad])") && !isSort) {
                    isSort = true;
                    isCorrSecondElem = true;
                    index = index ==-1 ? 2 : index;
                } else if (secondElem.matches("-([si])") && !isTypeData) {
                    isTypeData = true;
                    isCorrSecondElem = true;
                    index = index ==-1 ? 2 : index;
                } else if(!isCorrSecondElem){
                    System.out.println("Второй параметр не относится к режиму сортировки (-a или -d) или типу данных (-s или -i)");
                }
            } else {
                isCorrSecondElem = true;
                index = index ==-1 ? 1 : index;
            }

            if(!isCorrFirstElem && !isCorrSecondElem){
                System.out.println("Не корректные параметры режима сортировки (-a или -d) и типа данных (-s или -i)");
                checkInputParameter(0, "-([ad])", "Введите параметр режима сортировки (-a или -d)");
                checkInputParameter(1, "-([si])", "Введите параметр типа данных (-s или -i)");
            } else if (!isCorrFirstElem && !isSort) {
                System.out.println("Введите параметр режима сортировки (-a или -d)");
                checkInputParameter(0, "-([ad])", "Введите параметр режима сортировки (-a или -d)");
            } else if (!isCorrFirstElem && !isTypeData) {
                checkInputParameter(0, "-([si])", "Введите параметр типа данных (-s или -i)");
            } else if (!isCorrSecondElem && !isSort) {
                checkInputParameter(1, "-([ad])", "Введите параметр режима сортировки (-a или -d)");
            }else if (!isCorrSecondElem && !isTypeData) {
                checkInputParameter(1, "-([si])", "Введите параметр типа данных (-s или -i)");
            } else {
                isCorrTypeAndSort=true;
                break;
            }
        }

    }

    private static void checkInputParameter(int index,String regex, String msg){
        while (true){
            Scanner scanner = new Scanner(System.in);
            System.out.println(msg);
            String parameter = scanner.nextLine();
            if(parameter.matches(regex)) {
                tempArgs.set(index, parameter);
                break;
            }
        }
    }

    private static boolean checkOutputFile() {
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
            for (int k = 1; k <= 100; k++) {
                if (!new File(tempLine + "(" + k + ")" + ".txt").isFile()) {
                    System.out.println("Файл с именем " + tempArgs.get(index) +
                            " существует. Новое имя выходного файла " + tempLine + "(" + k + ")" + ".txt\n");
                    tempArgs.set(index, tempLine + "(" + k + ")" + ".txt");
                    tempLine = "";
                    index++;
                    isCorrFileOut = true;
                    return true;
                }
            }
        }  else {
            index++;
            isCorrFileOut = true;
            return true;
        }
        isCorrFileOut = false;
        return false;
    }

    private static boolean checkInputFiles(){
        int sizeList = tempArgs.size();

        List<Integer> idElementDelete = new ArrayList<>();

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
                    System.out.println(tempArgs);
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
        if (!idElementDelete.isEmpty()){
            for (int z = idElementDelete.size()-1; z >= 0; z--) {
                tempArgs.remove((int)idElementDelete.get(z));
            }
            idElementDelete.clear();
            System.out.println("Аргументы после удаления: " + tempArgs.toString() + "\n");
        }
        //остались ли файлы с данными
        if (tempArgs.size() - (index + 1) >= 0){
            isCorrFilesIn = true;
            return true;
        }
        //System.out.println(Arrays.toString(args));
        isCorrFilesIn = false;
        return false;
    }
}
