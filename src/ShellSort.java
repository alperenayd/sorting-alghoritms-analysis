public class ShellSort {
    public static void shellSort(int[] A) {
        int n = A.length;
        int h = 1;
        while (h < n/3) {
            h = 3*h + 1;
        }
        while (h >= 1) {
            for(int i=h;i<n;i++) {
                int j = i;
                while(j>=h && A[j]<A[j-h]) {
                    int temp = A[j];
                    A[j] = A[j-h];
                    A[j-h] = temp;
                    j = j-h;
                }
            }
            h = h/3;
        }
    }
}
