public class QuickSort {

    public static void quickSort(int[] A, int low, int high) {

        int stackSize = high - low + 1;
        int[] stack = new int[stackSize];

        int top = -1;

        top = top + 1;
        stack[top] = low;

        top = top + 1;
        stack[top] = high;

        while (top >= 0) {

            high = stack[top];
            top = top - 1;

            low = stack[top];
            top = top - 1;

            int pivot = partition(A, low, high);

            if (pivot - 1 > low) {
                top = top + 1;
                stack[top] = low;

                top = top + 1;
                stack[top] = pivot - 1;
            }

            if (pivot + 1 < high) {
                top = top + 1;
                stack[top] = pivot + 1;

                top = top + 1;
                stack[top] = high;
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
}