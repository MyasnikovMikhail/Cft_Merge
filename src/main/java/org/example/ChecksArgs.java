package org.example;

import java.io.File;
import java.util.*;

public class ChecksArgs {

    private static List<String> tempArgs = new ArrayList<>();
    private static int index = -1;

    private static boolean isCorr = true;

    private static String tempLine = "";

    public static String[] checksParameter(String[] args) {
        Scanner scanner = new Scanner(System.in);
        tempArgs.addAll(Arrays.stream(args).distinct().toList());

        List<String> tempArgsInputFiles = new ArrayList<>();

        checkDuplicates(args);//проверка дубликатов и индексов для первых двух параметров (режим сортировки и тип данных)

        // проверка количества аргументов

        if (args.length < 2) {
            while (args.length < 3) {
                System.out.println("Количество аргументов меньше 3\n");
                args = inputArgs();
                checkDuplicates(args);
                checkTypeDataAndSort(args);
            }
        } else {
            args = checkTypeDataAndSort(args);
        }

        while (true) {
            checkOutputFile(args);
            if (isCorr) {
                break;
            }
            System.out.println("Введите новое имя выходного файла (с расширением .txt)\n");
            tempLine = scanner.nextLine();
            args[index + 1] = tempLine;
        }

        while (true) {
            args = checkInputFiles(args);
            if (isCorr) {
                break;
            }
            System.out.println("Введите новые имена входных файлов(с/без расширения .txt) через пробел\n");
            Collections.addAll(tempArgsInputFiles, args);
            tempArgsInputFiles.addAll(Arrays.stream(scanner.nextLine().split(" ")).toList());
            args = tempArgsInputFiles.stream().distinct().toList().toArray(new String[0]);
        }
        System.out.println("Окончательный набор параметров: " + Arrays.toString(args)+"\n");
        return args;
    }

    /**
     * Проверка на дублированные параметры
     *
     * @param args - массив аргументов
     */
    private static void checkDuplicates(String[] args) {

        //tempArgsD = Arrays.stream(args).distinct().toList();
        /*args = tempArgsD.stream().distinct().toList().toArray(new String[0]);
        */

        checkIndexes("-a", "-d", tempArgs);
        checkIndexes("-s", "-i", tempArgs);
        /*if(tempArgsD.indexOf("-a") < 2 || tempArgsD.indexOf("-d") < 2) {
            int indexA = tempArgsD.indexOf("-a");
            int indexD = tempArgsD.indexOf("-d");
            if (tempArgsD.get(0).equals("-a")) {
                tempArgsD.remove("-d");
            } else if (tempArgsD.get(0).equals("-d")) {
                tempArgsD.remove("-a");
            } else {
                System.out.println("Аргумент типа сортировки не найден на первых позициях, автоматически будет выставлен (-а, по возрастанию) ");
                tempArgsD.remove("-d");
                tempArgsD.remove("-a");
            }
        }*/

        /*if(tempArgsD.indexOf("-s") < 2 || tempArgsD.indexOf("-i") < 2) {
            int indexS = tempArgsD.indexOf("-s");
            int indexI = tempArgsD.indexOf("-i");

            if (indexS > 0 && indexS < indexI) {
                tempArgsD.remove("-i");
            } else if (indexI > 0 && indexI < indexS) {
                tempArgsD.remove("-s");
            }
        } else {
            System.out.println("Аргумент сортировки находятся не на своих местах");
            tempArgsD.remove("-i");
            tempArgsD.remove("-s");
        }*/
    }

    private static void checkIndexes(String argOne, String argTwo,List<String> listArg) {
        if(listArg.indexOf(argOne) < 2 || listArg.indexOf(argTwo) < 2) {
            int indexOneElem = listArg.indexOf(argOne);
            int indexTwoElem = listArg.indexOf(argTwo);

            if (indexOneElem > 0 && indexOneElem < indexTwoElem) {
                listArg.remove(argTwo);
            } else if (indexTwoElem > 0 && indexTwoElem < indexOneElem) {
                listArg.remove(argOne);
            }
        } else {
            listArg.remove(argTwo);
            listArg.remove(argOne);
        }


        tempArgs = listArg;
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

    private static String[] checkOutputFile(String[] args) {
        Collections.addAll(tempArgs, args);
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
            args[index + 1] = tempLine + ".txt";
            tempArgs.set(index + 1, tempLine + ".txt");
            if (!temp) {
                tempArgs.clear();
                index++;
                isCorr = true;
                return args;
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
                    args[index + 1] = tempLine + "(" + k + ")" + ".txt";
                    tempLine = "";
                    tempArgs.clear();
                    index++;
                    isCorr = true;
                    return args;
                }
            }
        }  else {
            tempArgs.clear();
            index++;
            isCorr = true;
            return args;
        }
        isCorr = false;
        return args;
    }

    private static String[] checkInputFiles(String[] args){
        Collections.addAll(tempArgs, args);
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
                    args[i] = tempLine + ".txt";
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
            args = tempArgs.toArray(new String[0]);
            idElementDelete.clear();
            System.out.println("Аргументы после удаления: " + Arrays.toString(args) + "\n");
        }
        //остались ли файлы с данными
        if (tempArgs.size() - (index + 2) >= 0){
            tempArgs.clear();
            isCorr = true;
            return args;
        }
        //System.out.println(Arrays.toString(args));
        isCorr = false;
        tempArgs.clear();
        return args;
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

    private static void inputReqParameter(Scanner scanner, String tempLine) {
        boolean isCorrParam = false;
        System.out.println("Отсутствует один из обязательных аргументов -s и -i\n");
        while (!isCorrParam) {
            System.out.println("Введите один из обязательных аргументов -s или -i");
            tempLine = scanner.nextLine();
            isCorrParam = tempLine.matches("-([si])");

        }
        tempArgs.add(index+1, tempLine);
        index++;
    }
}
