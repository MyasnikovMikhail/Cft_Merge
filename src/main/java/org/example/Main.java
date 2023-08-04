package org.example;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;
import static org.example.CheckArgs.checksParameter;


public class Main {

    private static final Pattern patternInt = Pattern.compile("\\d+");

    private static final Logger logger = Logger.getAnonymousLogger();

    private static final ExecutorService pool = Executors.newCachedThreadPool();

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        System.out.println("Введенные параметры будут переведены в нижний регистр\n");
        List<String> argsList = new ArrayList<>(Arrays.stream(args).map(String::toLowerCase).toList());
        args = checkParameters(argsList).toArray(new String[0]);
        args = checksParameter(args);

        String outputFilename = null;
        List<String> filesToProcess = new LinkedList<>();
        boolean isAscending = true;
        DataType dataType = null;

        for (String arg : args) {
            switch (arg) {
                case "-a":
                    break;
                case "-d":
                    isAscending = false;
                    break;
                case "-i":
                    dataType = DataType.INTEGER;
                    break;
                case "-s":
                    dataType = DataType.STRING;
                    break;
                default:
                    if (outputFilename == null) {
                        outputFilename = arg;
                    } else {
                        filesToProcess.add(arg);
                    }
                    break;
            }
        }

        Set<String> tempFiles = new HashSet<>();
        String tempFile = "temp_" + UUID.randomUUID() + ".txt";
        if (filesToProcess.size() == 1) {
            boolean file = new File(tempFile).createNewFile();
            filesToProcess.add(tempFile);
        }
        while (filesToProcess.size() > 1) {
            List<String> newFilesToProcess = new LinkedList<>();
            List<Future<String>> tasks = new ArrayList<>();
            for (int i = 0; i < filesToProcess.size(); i = i + 2) {
                if (i < filesToProcess.size() - 1) {
                    int finalI = i;
                    boolean finalIsAscending = isAscending;
                    DataType finalDataType = dataType;
                    List<String> finalFilesToProcess = filesToProcess;
                    tasks.add(pool.submit(() ->
                            mergeFilesToTemp(finalFilesToProcess.get(finalI), finalFilesToProcess.get(finalI + 1), finalIsAscending, finalDataType)));
                } else {
                    newFilesToProcess.add(filesToProcess.get(i));
                }
            }
            for (Future<String> task : tasks) {
                newFilesToProcess.add(task.get());
            }
            if (!tempFiles.isEmpty()) {
                newFilesToProcess.forEach(tempFiles::remove);
                tempFiles.remove(args[args.length - 1]);
                deleteTempFiles(tempFiles);
                tempFiles.clear();
            }
            tempFiles.addAll(newFilesToProcess);
            logger.info("РАЗМЕР НОВОГО ПУЛА ФАЙЛОВ ДЛЯ ОБРАБОТКИ: " + newFilesToProcess.size());
            filesToProcess = newFilesToProcess;
        }
        pool.shutdown();
        for (int attempt = 0; attempt < 3; attempt++) {
            if (finishMerge(filesToProcess.get(0), outputFilename)) {
                boolean fileTemp = new File(tempFile).delete();
                break;
            } else {
                sleep(10000);
            }
        }
        /*for (int t = 0; t <= 3; t++) {
            if (checkFile(filesToProcess.get(0))) {
                finishMerge(filesToProcess.get(0), outputFilename);
                boolean fileTemp = new File(tempFile).delete();
                break;
            } else {
                logger.info("Проблема с файлом " + filesToProcess.get(0) + ". Повторная попытка прочтения будет через 10 сек. Всего 3 попытки.");
                sleep(10000);
            }
        }*/
    }

    private static boolean checkFile(String nameFile) {
        return new File(nameFile).isFile();
    }

    private static String mergeFilesToTemp(String file1, String file2, boolean isAscending, DataType dataType) throws IOException {
        String tempFileName = "temp_" + UUID.randomUUID() + ".txt";
        logger.info("Соединяем: " + file1 + " и " + file2 + " в " + tempFileName);
        try (
                FileReader fileReader1 = new FileReader(file1);
                FileReader fileReader2 = new FileReader(file2);
                FileWriter writer = new FileWriter(tempFileName);
                BufferedReader br1 = new BufferedReader(fileReader1);
                BufferedReader br2 = new BufferedReader(fileReader2)
        ) {
            String line1 = searchTypeLine(br1, dataType);
            String line2 = searchTypeLine(br2, dataType);

            while (line1 != null || line2 != null) {
                if (line1 == null) {
                    writer.write(line2 + "\n");
                    line2 = searchTypeLine(br2, dataType);
                } else if (line2 == null) {
                    writer.write(line1 + "\n");
                    line1 = searchTypeLine(br1, dataType);
                } else {
                    int compare = compareDueToType(line1, line2, dataType);
                    if (isAscending) {
                        if (compare <= 0) {
                            writer.write(line1 + "\n");
                            line1 = searchTypeLine(br1, dataType);
                        } else {
                            writer.write(line2 + "\n");
                            line2 = searchTypeLine(br2, dataType);
                        }
                    } else {
                        if (compare <= 0) {
                            writer.write(line2 + "\n");
                            line2 = searchTypeLine(br2, dataType);
                        } else {
                            writer.write(line1 + "\n");
                            line1 = searchTypeLine(br1, dataType);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Файл не найден: " + e.getMessage());
            String nameFIle = e.getLocalizedMessage().substring(0, e.getLocalizedMessage().indexOf(".txt") + 4);
            mergeFileToTemp(nameFIle.equals(file1) ? file1 : file2, tempFileName, dataType);
        } catch (IOException e) {
            System.err.println("Возникла ошибка при работе с файлом: " + e.getMessage());
            if (!new File(tempFileName).isFile()) {
                boolean tempFIle = new File(tempFileName).createNewFile();
            }
        }
        return tempFileName;
    }

    private static void mergeFileToTemp(String file, String tempFileName, DataType dataType) throws IOException {
        try (
                FileReader fileReader = new FileReader(file);
                FileWriter writer = new FileWriter(tempFileName);
                BufferedReader br = new BufferedReader(fileReader);
        ) {
            String line1 = searchTypeLine(br, dataType);
            while (line1 != null) {
                writer.write(line1);
                line1 = searchTypeLine(br, dataType);
            }
        } catch (IOException e) {
            System.err.println("Возникла ошибка при работе с файлом: " + e.getMessage());
        }
    }

    private static String searchTypeLine(BufferedReader br, DataType dataType) throws IOException {
        while (true) {
            String line = br.readLine();
            if (line == null) return null;
            if (isLineValid(line, dataType)) return line;
        }
    }

    private static boolean isLineValid(String line, DataType dataType) {
        switch (dataType) {
            case STRING -> {
                return !(line.contains(" ") || line.contains("\t") || line.isEmpty());
            }
            case INTEGER -> {
                return patternInt.matcher(line).matches();
            }
        }
        return false;
    }

    private static int compareDueToType(String line1, String line2, DataType dataType) {
        try {
            if (dataType == DataType.STRING) {
                return line1.compareTo(line2);
            } else {
                return Integer.compare(Integer.parseInt(line1), Integer.parseInt(line2));
            }
        } catch (NumberFormatException e) {
            System.out.println("Строка не может быть преобразована\n");
            throw new NumberFormatException(e.getMessage());
        }
    }

    private static void deleteTempFiles(Set<String> tempFiles) {
        tempFiles.forEach(f -> {
            boolean delete = new File(f).delete();
        });
    }

    private static boolean finishMerge(String s, String outputFilename) throws InterruptedException {
        File result = new File(s);
        if (!result.isFile() || !result.canWrite()) {
            logger.info("Файл " + result + " отсутствует или невозможно отредактировать");
        }
        File target = new File(outputFilename);
        boolean success = result.renameTo(target);
        if (success) {
            logger.info("СЛИВАЕМ " + s + " в " + outputFilename + " , удачно:  " + true);
        } else {
            sleep(10000);
            success = result.renameTo(target);
            logger.info("СЛИВАЕМ " + s + " в " + outputFilename + " , удачно:  " + success);
        }
        return success;
    }

    static List<String> checkParameters(List<String> argsList) {
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

    enum DataType {
        STRING,
        INTEGER
    }
}