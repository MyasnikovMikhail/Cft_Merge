package org.example;

import java.io.File;
import java.util.*;

public class ChecksArgs {

    private static final List<String> tempArgs = new ArrayList<>();
    private static int index = -1;

    private static boolean isCorr = true;

    private static String tempLine = "";

    public static String[] checksParameter(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<String> tempArgsInputFiles = new ArrayList<>();

        args = checkDuplicates(args); //проверка дубликатов

        // проверка количества аргументов

        if (args.length < 2) {
            while (args.length < 3) {
                System.out.println("Количество аргументов меньше 3\n");
                args = inputArgs();
                args = checkDuplicates(args);
                args = checkTypeDataAndSort(args);
            }
        } else {
            args = checkTypeDataAndSort(args);
        }

        while (true) {
            args = checkOutputFile(args);
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
        System.out.println("Окончательный набор парметров: " + Arrays.toString(args)+"\n");
        return args;
    }

    /**
     * Проверка на дублированные параметры
     *
     * @param args - массив аргументов
     * @return массив аргументов без дублирующих значений
     */
    private static String[] checkDuplicates(String[] args) {
        List<String> tempArgsD;
        tempArgsD = Arrays.stream(args).toList();
        args = tempArgsD.stream().distinct().toList().toArray(new String[0]);
        //System.out.println(Arrays.toString(args));
        return args;
    }

    private static String[] checkTypeDataAndSort(String[] args) {
        Collections.addAll(tempArgs, args);
        Scanner scanner = new Scanner(System.in);
        String tempLine = "";

        //проверка на одинаковые типы параметров
        int numType1 = tempArgs.indexOf("-s");
        int numType2 = tempArgs.indexOf("-i");

        if (numType1 < numType2 && numType1 != -1 && numType2 != -1) {
            tempArgs.remove(numType2);
        } else if (numType1 > numType2 && numType1 != -1 && numType2 != -1) {
            tempArgs.remove(numType1);
        }

        int numSort1 = tempArgs.indexOf("-a");
        int numSort2 = tempArgs.indexOf("-d");

        if (numSort1 < numSort2 && numSort1 != -1 && numSort2 != -1) {
            tempArgs.remove(numSort2);
        } else if (numSort1 > numType2 && numSort1 != -1 && numSort2 != -1) {
            tempArgs.remove(numSort1);
        }
        if(!tempArgs.get(0).matches("-([ad])") && !tempArgs.get(0).matches("-([si])")) {
            tempArgs.remove(0);

        }
        if (tempArgs.get(0).matches("-([ad])")) {
            index++;
            if (tempArgs.get(1).matches("-([si])")) {
                index++;
            } else {
                inputReqParameter(scanner, tempLine);
            }
        } else if (tempArgs.get(0).matches("-([si])")) {
            index++;
            if (tempArgs.get(1).matches("-([ad])")) {
                index++;
            }
        } else {
            inputReqParameter(scanner, tempLine);

        }

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
