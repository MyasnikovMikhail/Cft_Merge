package org.example;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static org.example.CheckArgs.checksParameter;


public class Main {

    private static final Pattern patternInt = Pattern.compile("\\d+");

    private static final Logger logger = Logger.getAnonymousLogger();

    private static final ExecutorService pool = Executors.newCachedThreadPool();

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        System.out.println("Введенные параметры автоматически будут переведены в нижний регистр\n");
        args = Arrays.stream(args).map(String::toLowerCase).toList().toArray(new String[0]);

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
            if(tempFiles.size() != 0) {
                newFilesToProcess.forEach(tempFiles::remove);
                tempFiles.remove(args[args.length-1]);
                deleteTempFiles(tempFiles);
                tempFiles.clear();
            }
            tempFiles.addAll(newFilesToProcess);
            logger.info("РАЗМЕР НОВОГО ПУЛА ФАЙЛОВ ДЛЯ ОБРАБОТКИ: " + newFilesToProcess.size());
            filesToProcess = newFilesToProcess;
        }
        pool.shutdown();
        finishMerge(filesToProcess.get(0), outputFilename);
        boolean fileTemp = new File(tempFile).delete();
    }

    private static String mergeFilesToTemp(String file1, String file2, boolean isAscending, DataType dataType) {
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
        } catch (IOException e) {
            System.err.println("Возникла ошибка при чтении файла: " + e.getMessage());
        }
        return tempFileName;
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

    private static void finishMerge(String s, String outputFilename) {
        File result = new File(s);
        File target = new File(outputFilename);
        boolean success = result.renameTo(target);
        logger.info("СЛИВАЕМ " + s + " в " + outputFilename + " , удачно:  " + success);
    }

    enum DataType {
        STRING,
        INTEGER
    }
}