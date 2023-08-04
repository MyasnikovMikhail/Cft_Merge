package org.example;

import java.io.*;
import java.util.concurrent.ExecutionException;
import static org.example.MergeSort.sortingMerge;
import static org.example.cheks.ChecksArguments.checksArguments;


public class Main {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        System.out.println("Введенные параметры будут переведены в нижний регистр\n");
        args = checksArguments(args);
        sortingMerge(args);

    }
}