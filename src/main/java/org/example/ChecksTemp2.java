package org.example;

import java.io.File;
import java.util.*;

public class ChecksTemp2 {

    private static List<String> tempArgs = new ArrayList<>(1);
    private static int index = -1;

    private static boolean isCorrTypeAndSort = false;

    private static boolean isCorrCounterParameters = false;

    private static boolean isCorrFileOut = false;

    private static boolean isCorrFilesIn = false;

    private static String tempLine = "";

    public static String[] checksParameter(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while(!isCorrCounterParameters) {
            if (tempArgs.size() < 3) {
                System.out.println("Неверное количество аргументов");
                tempArgs.addAll(Arrays.stream(scanner.nextLine().split(" ")).distinct().toList());
            }
            indexSortAndType();


            isCorrCounterParameters = true;

        }



        //counterArgs();
       // проверка количества аргументов
        System.out.println("Окончательный набор параметров: " + tempArgs +"\n");
        return args;
    }

    private static void checkArguments() {
        Scanner scanner = new Scanner(System.in);
        //indexSortAndType();
        while(!checkIndexes("-a", "-d") || !checkIndexes("-s", "-i")) {
            if (index == 1) {
                System.out.println("Введите параметры режима сортировки (-a или -d) и типа данных (-s или -i)");
                tempArgs.set(0, scanner.nextLine());
                tempArgs.set(1, scanner.nextLine());
            } else {
                System.out.println("Введите параметр типа данных");
                tempArgs.set(0, scanner.nextLine());
            }
        }
        isCorrTypeAndSort = true;
        //index = tempArgs.get(1).matches("-([adsi])") ? 1 : 0;

    }

    private static boolean checkIndexes(String argOne, String argTwo) {
        if(tempArgs.indexOf(argOne) < 2 || tempArgs.indexOf(argTwo) < 2) {
            int indexOneElem = tempArgs.indexOf(argOne);
            int indexTwoElem = tempArgs.indexOf(argTwo);

            if (indexOneElem > 0 && indexOneElem < indexTwoElem) {
                tempArgs.remove(argTwo);
            } else if (indexTwoElem > 0 && indexTwoElem < indexOneElem) {
                tempArgs.remove(argOne);
            }
            return true;
        } else {
            tempArgs.remove(argTwo);
            tempArgs.remove(argOne);
            return false;
        }
    }
    private static boolean indexSortAndType() {
        for (int i = 0; i < 3; i++) {
            if(tempArgs.get(i).matches("-([adsi])")){
                index = i;
            } else {
                checkArguments();
                i = 0;
            }
        }
        if(index != -1){
            System.out.println("Введены некорректные параметры режима сортировки (-a или -d) и типа данных (-s или -i)");
        }
        checkArguments();
        return true;
    }















/*private static void counterArgs(){
        List<String> tempArgsInputFiles = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        while(!isCorrCounterParameters) {
            if (tempArgs.size() < 3) {
                System.out.println("Неверное количество аргументов");
                tempArgs.addAll(Arrays.stream(scanner.nextLine().split(" ")).distinct().toList());
            } else {
                isCorrCounterParameters = true;
            }
        }
       checkArguments();

        while(!isCorrFileOut) {
            if(!checkOutputFile()) {
                    break;
            }
            System.out.println("Введите новое имя выходного файла (с расширением .txt)\n");
            tempLine = scanner.nextLine();
            tempArgs.remove("-a");
            tempArgs.set(index + 1, tempLine);
        }

        while (!isCorrFilesIn) {
            if (checkInputFiles()) {
                break;
            }
            System.out.println("Введите новые имена входных файлов(с/без расширения .txt) через пробел\n");
            tempArgsInputFiles = Arrays.stream(scanner.nextLine().split(" ")).toList();
            tempArgs.addAll(tempArgsInputFiles);
        }

    }

    private static void checkArguments() {
        Scanner scanner = new Scanner(System.in);
        while(!(checkIndexes("-a", "-d") && checkIndexes("-s", "-i"))) {
            if (index == 1) {
                System.out.println("Введите параметры режима сортировки (-a или -d) и типа данных (-s или -i)");
                tempArgs.set(0, scanner.nextLine());
                tempArgs.set(1, scanner.nextLine());
            } else {
                System.out.println("Введите параметр типа данных");
                tempArgs.set(0, scanner.nextLine());
            }
        }
        isCorrTypeAndSort = true;
        index = tempArgs.get(1).matches("-([adsi])") ? 1 : 0;
    }

    private static boolean checkIndexes(String argOne, String argTwo) {
        if(tempArgs.indexOf(argOne) < 2 || tempArgs.indexOf(argTwo) < 2) {
            int indexOneElem = tempArgs.indexOf(argOne);
            int indexTwoElem = tempArgs.indexOf(argTwo);

            if (indexOneElem > 0 && indexOneElem < indexTwoElem) {
                tempArgs.remove(argTwo);
                if(indexOneElem > index && indexOneElem < 2) {
                    index = indexOneElem;
                }
            } else if (indexTwoElem > 0 && indexTwoElem < indexOneElem) {
                tempArgs.remove(argOne);
                if(indexTwoElem > index && indexTwoElem < 2) {
                    index = indexTwoElem;
                }
            }
            return true;
        } else {
            tempArgs.remove(argTwo);
            tempArgs.remove(argOne);
            return false;
        }
    }

    private static String[] checkTypeDataAndSort(String[] args) {
        Collections.addAll(tempArgs, args);
        for(int v = tempArgs.size() - 1; v > index; v--) {
            if(tempArgs.get(v).startsWith("-")) {
                tempArgs.remove(v);
            }
        }
        args = tempArgs.toArray(new String[0]);
        tempArgs.clear();

        return args;
    }

    private static boolean checkOutputFile() {
        int indexDot = tempArgs.get(index + 1).lastIndexOf(".");
        //проверка выходного файла (на имя и существование)

        if (!tempArgs.get(index + 1).endsWith(".txt")) {
            if (indexDot > 0) {
                tempLine = tempArgs.get(index + 1).substring(0, indexDot);
            } else {
                tempLine = tempArgs.get(index + 1);
            }
            boolean temp = new File(tempLine + ".txt").isFile();

            //Проверка наличия файла после добавления расширения
            tempArgs.set(index + 1, tempLine + ".txt");
            if (!temp) {
                tempArgs.clear();
                index++;
                isCorrFileOut = true;
                return true;
            }
        }

        if (new File(tempArgs.get(index + 1)).isFile()) {
            indexDot = tempArgs.get(index + 1).lastIndexOf(".");
            if (indexDot > 0) {
                tempLine = tempArgs.get(index + 1).substring(0, indexDot);
            } else {
                tempLine = tempArgs.get(index + 1);
            }
            for (int k = 1; k <= 100; k++) {
                if (!new File(tempLine + "(" + k + ")" + ".txt").isFile()) {
                    System.out.println("Файл с именем " + tempArgs.get(index + 1) +
                            " существует. Новое имя выходного файла " + tempLine + "(" + k + ")" + ".txt\n");
                    tempArgs.set(index + 1, tempLine + "(" + k + ")" + ".txt");
                    tempLine = "";
                    tempArgs.clear();
                    index++;
                    isCorrFileOut = true;
                    return false;
                }
            }
        }  else {
            tempArgs.clear();
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

        for (int i = index + 1; i < sizeList; i++) {//проверка файлов на существование
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
        if (tempArgs.size() - (index + 2) >= 0){
            tempArgs.clear();
            isCorrFilesIn = true;
            return true;
        }
        //System.out.println(Arrays.toString(args));
        isCorrFilesIn = false;
        tempArgs.clear();
        return false;
    }



    private static String[] inputArgs() {
        Scanner scanner = new Scanner(System.in);
        String[] args;
        System.out.println("""
                Аргументы не валидны. Введите новые.
                Требования для аргументов (аргументы вводить через пробел):
                1. режим сортировки (-a или -d), необязательный, по умолчанию сортируем по возрастанию;
                2. тип данных (-s или -i), обязательный;
                3. имя выходного файла, обязательное;
                4. остальные параметры – имена входных файлов, не менее одного.
                Пример: -d -s out.txt in1.txt in2.txt in3.txt""");
        args = scanner.nextLine().toLowerCase().split(" ");
        while (args.length <2 ) {
            args = scanner.nextLine().split(" ");
        }
        return args;
    }

    /*private static void inputReqParameter(Scanner scanner, String tempLine) {
        boolean isCorrParam = false;
        System.out.println("Отсутствует один из обязательных аргументов -s и -i\n");
        while (!isCorrParam) {
            System.out.println("Введите один из обязательных аргументов -s или -i");
            tempLine = scanner.nextLine();
            isCorrParam = tempLine.matches("-([si])");

        }
        tempArgs.add(index+1, tempLine);
        index++;
    }*/
}
