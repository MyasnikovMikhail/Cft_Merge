package org.example.cheks;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.example.cheks.ChecksArguments.tempArgs;

public class ChecksParameters {

    private static int index = -1;
    static void indexSortAndType() {
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
                System.out.println("Первый параметр не относится к режиму сортировки (-a или -d) или типу данных (-s или -i)\n");
            }
            if (secondElem.startsWith("-")) {
                if (secondElem.matches("-([ad])") && !isSort) {
                    isSort = true;
                    isCorrSecondElem = true;
                    index = index ==-1 ? 2 : index + 1;
                } else if (secondElem.matches("-([si])") && !isTypeData) {
                    isTypeData = true;
                    isCorrSecondElem = true;
                    index = index ==-1 ? 2 : index + 1;
                } else if(!isCorrSecondElem){
                    System.out.println("Второй параметр не относится к режиму сортировки (-a или -d) или типу данных (-s или -i)\n");
                }
            } else {
                isCorrSecondElem = true;
                index = index ==-1 ? 1 : index;
            }

            if(!isCorrFirstElem && !isCorrSecondElem){
                System.out.println("Не корректные параметры режима сортировки (-a или -d) и типа данных (-s или -i)\n");
                checkInputParameter(0, "-([ad])", "Введите параметр режима сортировки (-a или -d): ");
                checkInputParameter(1, "-([si])", "Введите параметр типа данных (-s или -i): ");
            } else if (!isCorrFirstElem && !isSort) {
                System.out.println("Введите параметр режима сортировки (-a или -d): ");
                checkInputParameter(0, "-([ad])", "Введите параметр режима сортировки (-a или -d): ");
            } else if (!isCorrFirstElem && !isTypeData) {
                checkInputParameter(0, "-([si])", "Введите параметр типа данных (-s или -i): ");
            } else if (!isCorrSecondElem && !isSort) {
                checkInputParameter(1, "-([ad])", "Введите параметр режима сортировки (-a или -d): ");
            }else if (!isCorrSecondElem && !isTypeData) {
                checkInputParameter(1, "-([si])", "Введите параметр типа данных (-s или -i): ");
            } else {
                break;
            }
        }
    }

    private static void checkInputParameter(int index,String regex, String msg){
        while (true){
            Scanner scanner = new Scanner(System.in);
            System.out.println(msg);
            String parameter = scanner.nextLine();
            if (parameter.equals(":q"))
                System.exit(0);
            if(parameter.matches(regex)) {
                tempArgs.set(index, parameter);
                break;
            }
        }
    }
}
