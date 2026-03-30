public class MergeSort {

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
}