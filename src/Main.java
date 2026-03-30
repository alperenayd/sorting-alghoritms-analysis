import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Main {

    static final int[] INPUT_SIZES = {500, 1000, 2000, 4000, 8000, 16000, 32000, 64000, 128000, 250000};
    static final int RUN_COUNT = 10;
    static final String CSV_FILE = "all_stocks_5yr.csv";

    public static void main(String[] args) throws IOException {
        int[] allData = readAllVolumes(CSV_FILE);

        String[] algorithmNames = {"Quick Sort", "Insertion Sort", "Merge Sort", "Shell Sort", "Radix Sort"};
        String[] dataTypes = {"Random", "Sorted", "Reverse Sorted"};

        // [algorithm][dataType][inputSize]
        double[][][] results = new double[5][3][INPUT_SIZES.length];

        for (int j = 0; j < INPUT_SIZES.length; j++) {
            int n = INPUT_SIZES[j];

            int[] base = Arrays.copyOfRange(allData, 0, n);
            int[] randomData = Arrays.copyOf(base, base.length);

            int[] sortedData = Arrays.copyOf(base, base.length);
            Arrays.sort(sortedData);

            int[] reverseData = Arrays.copyOf(sortedData, sortedData.length);
            reverseArray(reverseData);

            // Random
            results[0][0][j] = measureAverageTime("QuickSort", randomData);
            results[1][0][j] = measureAverageTime("InsertionSort", randomData);
            results[2][0][j] = measureAverageTime("MergeSort", randomData);
            results[3][0][j] = measureAverageTime("ShellSort", randomData);
            results[4][0][j] = measureAverageTime("RadixSort", randomData);

            // Sorted
            results[0][1][j] = measureAverageTime("QuickSort", sortedData);
            results[1][1][j] = measureAverageTime("InsertionSort", sortedData);
            results[2][1][j] = measureAverageTime("MergeSort", sortedData);
            results[3][1][j] = measureAverageTime("ShellSort", sortedData);
            results[4][1][j] = measureAverageTime("RadixSort", sortedData);

            // Reverse Sorted
            results[0][2][j] = measureAverageTime("QuickSort", reverseData);
            results[1][2][j] = measureAverageTime("InsertionSort", reverseData);
            results[2][2][j] = measureAverageTime("MergeSort", reverseData);
            results[3][2][j] = measureAverageTime("ShellSort", reverseData);
            results[4][2][j] = measureAverageTime("RadixSort", reverseData);

            System.out.println("Finished n = " + n);
        }

        printResultsTable("RANDOM INPUT", algorithmNames, results, 0);
        printResultsTable("SORTED INPUT", algorithmNames, results, 1);
        printResultsTable("REVERSE SORTED INPUT", algorithmNames, results, 2);

        // 3 comparison charts by input type
        for (int type = 0; type < 3; type++) {
            double[][] yAxis = new double[5][INPUT_SIZES.length];
            for (int alg = 0; alg < 5; alg++) {
                yAxis[alg] = results[alg][type];
            }
            showAndSaveChart(dataTypes[type] + " Input Comparison", INPUT_SIZES, yAxis, algorithmNames);
        }

        // 5 charts by algorithm
        for (int alg = 0; alg < 5; alg++) {
            double[][] yAxis = new double[3][INPUT_SIZES.length];
            yAxis[0] = results[alg][0];
            yAxis[1] = results[alg][1];
            yAxis[2] = results[alg][2];
            showAndSaveChart(algorithmNames[alg], INPUT_SIZES, yAxis, dataTypes);
        }
    }

    public static int[] readAllVolumes(String fileName) throws IOException {
        int capacity = 700000;
        int[] temp = new int[capacity];
        int count = 0;

        BufferedReader br = new BufferedReader(new FileReader(fileName));
        br.readLine(); // header

        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            temp[count] = Integer.parseInt(parts[5]);
            count++;
        }

        br.close();
        return Arrays.copyOf(temp, count);
    }

    public static double measureAverageTime(String algorithm, int[] arr) {
        double total = 0.0;

        for (int i = 0; i < RUN_COUNT; i++) {
            int[] copy = Arrays.copyOf(arr, arr.length);

            long start = System.nanoTime();

            switch (algorithm) {
                case "QuickSort":
                    quickSort(copy, 0, copy.length - 1);
                    break;
                case "InsertionSort":
                    insertionSort(copy);
                    break;
                case "MergeSort":
                    mergeSort(copy);
                    break;
                case "ShellSort":
                    shellSort(copy);
                    break;
                case "RadixSort":
                    copy = radixSort(copy);
                    break;
            }

            long end = System.nanoTime();
            total += (end - start) / 1_000_000.0;
        }

        return total / RUN_COUNT;
    }

    public static void reverseArray(int[] arr) {
        int left = 0;
        int right = arr.length - 1;

        while (left < right) {
            int temp = arr[left];
            arr[left] = arr[right];
            arr[right] = temp;
            left++;
            right--;
        }
    }

    public static void printResultsTable(String title, String[] algorithmNames, double[][][] results, int dataTypeIndex) {
        System.out.println("\n==============================================================");
        System.out.println(title);
        System.out.println("==============================================================");

        System.out.printf("%-18s", "Algorithm");
        for (int size : INPUT_SIZES) {
            System.out.printf("%12d", size);
        }
        System.out.println();

        for (int alg = 0; alg < algorithmNames.length; alg++) {
            System.out.printf("%-18s", algorithmNames[alg]);
            for (int i = 0; i < INPUT_SIZES.length; i++) {
                System.out.printf("%12.2f", results[alg][dataTypeIndex][i]);
            }
            System.out.println();
        }
    }

    public static void showAndSaveChart(String title, int[] xAxis, double[][] yAxis, String[] seriesNames) throws IOException {
        XYChart chart = new XYChartBuilder()
                .width(1000)
                .height(700)
                .title(title)
                .xAxisTitle("Input Size")
                .yAxisTitle("Time (ms)")
                .build();

        double[] doubleX = Arrays.stream(xAxis).asDoubleStream().toArray();

        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        chart.getStyler().setMarkerSize(6);

        // Küçük değerlerin üst üste binmesini azaltır
        chart.getStyler().setYAxisLogarithmic(true);

        for (int i = 0; i < yAxis.length; i++) {
            chart.addSeries(seriesNames[i], doubleX, yAxis[i]);
        }

        BitmapEncoder.saveBitmap(chart, title.replaceAll("[\\\\/:*?\"<>|]", "_"), BitmapEncoder.BitmapFormat.PNG);
        new SwingWrapper<>(chart).displayChart();
    }

    // Quick Sort
    public static void quickSort(int[] A, int low, int high) {
        int stackSize = high - low + 1;
        int[] stack = new int[stackSize];
        int top = -1;

        stack[++top] = low;
        stack[++top] = high;

        while (top >= 0) {
            high = stack[top--];
            low = stack[top--];

            int pivot = partition(A, low, high);

            if (pivot - 1 > low) {
                stack[++top] = low;
                stack[++top] = pivot - 1;
            }

            if (pivot + 1 < high) {
                stack[++top] = pivot + 1;
                stack[++top] = high;
            }
        }
    }

    public static int partition(int[] A, int low, int high) {
        int pivot = A[high];
        int i = low - 1;

        for (int j = low; j <= high - 1; j++) {
            if (A[j] <= pivot) {
                i = i + 1;
                int temp = A[i];
                A[i] = A[j];
                A[j] = temp;
            }
        }

        int temp = A[i + 1];
        A[i + 1] = A[high];
        A[high] = temp;

        return i + 1;
    }

    // Insertion Sort
    public static void insertionSort(int[] A) {
        for (int j = 1; j < A.length; j++) {
            int key = A[j];
            int i = j - 1;

            while (i >= 0 && A[i] > key) {
                A[i + 1] = A[i];
                i = i - 1;
            }

            A[i + 1] = key;
        }
    }

    // Merge Sort
    public static void mergeSort(int[] A) {
        int n = A.length;
        int[] temp = new int[n];
        int currSize = 1;

        while (currSize < n) {
            int leftStart = 0;

            while (leftStart < n - 1) {
                int mid = Math.min(leftStart + currSize - 1, n - 1);
                int rightEnd = Math.min(leftStart + 2 * currSize - 1, n - 1);
                merge(A, temp, leftStart, mid, rightEnd);
                leftStart = leftStart + 2 * currSize;
            }

            currSize = 2 * currSize;
        }
    }

    public static void merge(int[] A, int[] temp, int left, int mid, int right) {
        int i = left;
        int j = mid + 1;
        int k = left;

        while (i <= mid && j <= right) {
            if (A[i] <= A[j]) {
                temp[k] = A[i];
                i = i + 1;
            } else {
                temp[k] = A[j];
                j = j + 1;
            }
            k = k + 1;
        }

        while (i <= mid) {
            temp[k] = A[i];
            i = i + 1;
            k = k + 1;
        }

        while (j <= right) {
            temp[k] = A[j];
            j = j + 1;
            k = k + 1;
        }

        for (i = left; i <= right; i++) {
            A[i] = temp[i];
        }
    }

    // Shell Sort
    public static void shellSort(int[] A) {
        int n = A.length;
        int h = 1;

        while (h < n / 3) {
            h = 3 * h + 1;
        }

        while (h >= 1) {
            for (int i = h; i < n; i++) {
                int j = i;
                while (j >= h && A[j] < A[j - h]) {
                    int temp = A[j];
                    A[j] = A[j - h];
                    A[j - h] = temp;
                    j = j - h;
                }
            }
            h = h / 3;
        }
    }

    // Radix Sort
    public static int[] radixSort(int[] A) {
        int d = findMaxDigitCount(A);
        for (int pos = 1; pos <= d; pos++) {
            A = countingSort(A, pos);
        }
        return A;
    }

    public static int[] countingSort(int[] A, int pos) {
        int[] count = new int[10];
        int[] output = new int[A.length];
        int size = A.length;

        for (int i = 0; i < size; i++) {
            int digit = getDigit(A[i], pos);
            count[digit]++;
        }

        for (int i = 1; i < 10; i++) {
            count[i] += count[i - 1];
        }

        for (int i = size - 1; i >= 0; i--) {
            int digit = getDigit(A[i], pos);
            count[digit]--;
            output[count[digit]] = A[i];
        }

        return output;
    }

    public static int getDigit(int number, int pos) {
        return (number / (int) Math.pow(10, pos - 1)) % 10;
    }

    public static int findMaxDigitCount(int[] arr) {
        int max = 0;

        for (int value : arr) {
            if (value > max) {
                max = value;
            }
        }

        if (max == 0) {
            return 1;
        }

        int digits = 0;
        while (max > 0) {
            digits++;
            max /= 10;
        }

        return digits;
    }
}