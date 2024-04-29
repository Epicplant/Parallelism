package filterEmpty;

import hasOver.HasOver;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class FilterEmpty {
    static ForkJoinPool POOL = new ForkJoinPool();

    /**
     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns an array with the lengths of the non-empty strings from arr (in order)
     * For example, if arr is ["", "", "cse", "332", "", "hw", "", "7", "rox"], then
     * main.java.filterEmpty(arr) == [3, 3, 2, 1, 3].
     *
     * A parallel algorithm to solve this problem in O(lg n) span and O(n) work is the following:
     * (1) Do a parallel map to produce a bit set
     * (2) Do a parallel prefix over the bit set
     * (3) Do a parallel map to produce the output
     *
     * In lecture, we wrote parallelPrefix together, and it is included in the gitlab repository.
     * Rather than reimplementing that piece yourself, you should just use it. For the other two
     * parts though, you should write them.
     *
     * Do not bother with a sequential cutoff for this exercise, just have a base case that processes a single element.
     */



    public static int[] filterEmpty(String[] arr) {
        int[] bits = mapToBitSet(arr);

        int[] bitsum = ParallelPrefixSum.parallelPrefixSum(bits);

        return mapToOutput(arr, bits, bitsum);
    }

    public static int[] mapToBitSet(String[] arr) {
        /* TODO: Edit this with your code */
        return toBitSetParallel(arr);
    }

    /* TODO: Add a sequential method and parallel task here */

    public static int[] toBitSetParallel(String[] arr) {

        int[] copy = new int[arr.length];
        POOL.invoke(new toBitSetTask(arr, copy, 0, arr.length));
        return copy;
    }

    private static class toBitSetTask extends RecursiveAction {
        String[] arr;
        int[] copy;
        int lo, hi;

        public toBitSetTask(String[] arr, int[] copy, int lo, int hi) {
            this.arr = arr;
            this.copy = copy;
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        protected void compute() {

            if(hi - lo <= 1) {
                if(arr.length != 0) {
                    if(!arr[lo].isEmpty()) {
                        copy[lo] = 1;
                    } else {
                        copy[lo] = 0;
                    }
                }
                return;
            }

            int mid = lo + (hi-lo)/2;
            toBitSetTask left = new toBitSetTask(arr, copy, lo, mid);
            toBitSetTask right = new toBitSetTask(arr, copy, mid, hi);

            left.fork();
            right.compute();
            left.join();

        }


        }























        public static int[] mapToOutput(String[] input, int[] bits, int[] bitsum) {
            return toOutputParallel(input, bits, bitsum);
        }


    public static int[] toOutputParallel(String[] input, int[] bits, int[] bitsum ) {

        int[] output = new int[0];
        if(bitsum.length != 0) {
            output = new int[bitsum[bitsum.length-1]];
        }

        POOL.invoke(new toOutputTask(input, bits, bitsum, output, 0, input.length));

        return output;
    }

    private static class toOutputTask extends RecursiveAction {
        int[] bits, bitsum, output;
        String[] input;
        int lo, hi;


        public toOutputTask(String[] input, int[] bits, int[] bitsum, int[] output, int lo, int hi) {
            this.input = input;
            this.bits = bits;
            this.bitsum = bitsum;
            this.output = output;
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        protected void compute() {

            if(hi - lo <= 1) {
                if(bits.length != 0 && bits[lo] == 1) {

                    int index = bitsum[lo]-1;

                    int test = input[lo].length();

                    output[index] = test;
                }
                return;
            }

            int mid = lo + (hi-lo)/2;
            toOutputTask left = new toOutputTask(input, bits, bitsum, output, lo, mid);
            toOutputTask right = new toOutputTask(input, bits, bitsum, output, mid, hi);

            left.fork();
            right.compute();
            left.join();

        }
    }














            /* TODO: Add a sequential method and parallel task here */

    private static void usage() {
        System.err.println("USAGE: FilterEmpty <String array>");
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            usage();
        }

        String[] arr = args[0].replaceAll("\\s*", "").split(",");
        System.out.println(Arrays.toString(filterEmpty(arr)));
    }
}