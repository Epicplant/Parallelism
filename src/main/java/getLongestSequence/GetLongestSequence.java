package getLongestSequence;

import hasOver.HasOver;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class GetLongestSequence {
    /**
     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns the length of the longest consecutive sequence of val in arr.
     * For example, if arr is [2, 17, 17, 8, 17, 17, 17, 0, 17, 1], then
     * getLongestSequence(17, arr) == 3 and getLongestSequence(35, arr) == 0.
     *
     * Your code must have O(n) work, O(lg n) span, where n is the length of arr, and actually use the sequentialCutoff
     * argument. We have provided you with an extra class SequenceRange. We recommend you use this class as
     * your return value, but this is not required.
     */

    private static final ForkJoinPool POOL = new ForkJoinPool();
    private static int cutoff = 1;


    public static int getLongestSequence(int val, int[] arr, int sequentialCutoff) {
        /* TODO: Edit this with your code */
        cutoff = sequentialCutoff;
        int test = parallel(arr, val).longest;
        return test;
    }

    /* TODO: Add a sequential method and parallel task here */

    private static SequenceRange sequential(int[] arr, int val, int lo, int hi) {
        int rightMost = 0;
        int longest = 0;
        int leftMost = 0;
        boolean stillLeft = true;

        for (int i = lo; i < hi; i++) {
            if(val == arr[i]) {
                rightMost++;

                if(rightMost > longest) {
                    longest = rightMost;
                }

                if(stillLeft) {
                    leftMost++;
                }


            } else {
                rightMost = 0;
                stillLeft = false;
            }
        }

        SequenceRange returner = new SequenceRange(leftMost, rightMost, longest);
        return returner;
    }

    public static SequenceRange parallel(int[] arr, int val) {
        // TODO: your code here
        return  POOL.invoke(new GetLongestSequenceTask(arr, val, 0, arr.length));
    }



    private static void usage() {
        System.err.println("USAGE: GetLongestSequence <number> <array> <sequential cutoff>");
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
            System.out.println(getLongestSequence(val, arr, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }
    }

    private static class GetLongestSequenceTask extends RecursiveTask<SequenceRange> {
        int[] arr;
        int lo, hi;
        int val;

        public GetLongestSequenceTask(int[] arr, int val, int lo, int hi) {
            this.arr = arr;
            this.lo = lo;
            this.hi = hi;
            this.val = val;
        }

        @Override
        protected SequenceRange compute() {

            if(hi - lo <= cutoff) {
                return sequential(arr, val, lo, hi);
            }
            int mid = lo + (hi-lo)/2;

            GetLongestSequenceTask left = new GetLongestSequenceTask(arr, val, lo, mid);
            GetLongestSequenceTask right = new GetLongestSequenceTask(arr, val, mid, hi);


            left.fork();
            SequenceRange rightVal = right.compute();
            SequenceRange leftVal = left.join();

            int longestReturner = 0;

            //left manages the number in a row from the leftmost part to when it stops
            //right manages the number in a row from the rightmost part to when it stops
            //if right + left > current length than make that the new greater length
            //right vals of the right child becomes the new left and left vals of the left child becomes the new right

            if(leftVal.right + rightVal.left > rightVal.longest && leftVal.right + rightVal.left > leftVal.longest) {
                longestReturner = leftVal.right + rightVal.left;
            }  else if(leftVal.longest > rightVal.longest) {
                longestReturner = leftVal.longest;
            } else {
                longestReturner = rightVal.longest;
            }

            int leftReturner = leftVal.left;
            int rightReturner = rightVal.right;
            if(((hi-lo) == leftVal.left + rightVal.right)) {
                leftReturner = rightVal.right + leftVal.left;
                rightReturner = rightVal.right + leftVal.left;
            } else if(mid-lo == leftVal.left) {
                leftReturner = leftVal.left + rightVal.left;
            } else if(hi-mid == rightVal.right) {
                rightReturner = rightVal.right + leftVal.right;
            }

            SequenceRange returner = new SequenceRange(leftReturner, rightReturner, longestReturner);


            return returner;
        }

    }

}