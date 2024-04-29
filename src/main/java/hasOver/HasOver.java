package hasOver;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class HasOver {
    /**
     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns true if arr has any elements strictly larger than val.
     * For example, if arr is [21, 17, 35, 8, 17, 1], then
     * main.java.hasOver(21, arr) == true and main.java.hasOver(35, arr) == false.
     *
     * Your code must have O(n) work, O(lg n) span, where n is the length of arr, and actually use the sequentialCutoff
     * argument.
     */


    private static final ForkJoinPool POOL = new ForkJoinPool();
    private static int cutoff = 1;

    public static boolean hasOver(int val, int[] arr, int sequentialCutoff) {
        /* TODO: Edit this with your code */
        cutoff = sequentialCutoff;
        return parallel(arr, val);
    }

    /* TODO: Add a sequential method and parallel task here */

    public static boolean parallel(int[] arr, int val) {
        return  POOL.invoke(new HasOverTask(arr, val, 0, arr.length));
    }



    private static boolean sequential(int[] arr, int val, int lo, int hi) {
        for (int i = lo; i < hi; i++) {
            if(arr[i] > val) {
                return true;
            }
        }
        return false;
    }

    private static void usage() {
        System.err.println("USAGE: HasOver <number> <array> <sequential cutoff>");
        System.exit(2);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            usage();
        }

        int val = 0;
        int[] arr = null;

        try {
            val = Integer.parseInt(args[0]);
            String[] stringArr = args[1].replaceAll("\\s*", "").split(",");
            arr = new int[stringArr.length];
            for (int i = 0; i < stringArr.length; i++) {
                arr[i] = Integer.parseInt(stringArr[i]);
            }
            System.out.println(hasOver(val, arr, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }

    }






    private static class HasOverTask extends RecursiveTask<Boolean> {
        int[] arr;
        int lo, hi;
        int val;

        public HasOverTask(int[] arr, int val, int lo, int hi) {
            this.arr = arr;
            this.lo = lo;
            this.hi = hi;
            this.val = val;
        }

        @Override
        protected Boolean compute() {
            // TODO: your code here
            if(hi - lo <= cutoff) {
                return sequential(arr, val, lo, hi);
            }
            int mid = lo + (hi-lo)/2;
            HasOverTask left = new HasOverTask(arr, val, lo, mid);
            HasOverTask right = new HasOverTask(arr, val, mid, hi);

            left.fork();
            boolean rightVal = right.compute();
            boolean leftVal = left.join();

            return (leftVal || rightVal);
        }
    }




}
