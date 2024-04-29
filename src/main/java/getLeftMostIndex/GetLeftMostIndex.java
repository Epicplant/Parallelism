package getLeftMostIndex;

import hasOver.HasOver;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class GetLeftMostIndex {
    /**
     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns the index of the left-most occurrence of needle in haystack (think of needle and haystack as
     * Strings) or -1 if there is no such occurrence.
     *
     * For example, main.java.getLeftMostIndex("cse332", "Dudecse4ocse332momcse332Rox") == 9 and
     * main.java.getLeftMostIndex("sucks", "Dudecse4ocse332momcse332Rox") == -1.
     *
     * Your code must actually use the sequentialCutoff argument. You may assume that needle.length is much
     * smaller than haystack.length. A solution that peeks across subproblem boundaries to decide partial matches
     * will be significantly cleaner and simpler than one that does not.
     */










    private static final ForkJoinPool POOL = new ForkJoinPool();
    private static int cutoff = 1;

    public static int getLeftMostIndex(char[] needle, char[] haystack, int sequentialCutoff) {

        cutoff = sequentialCutoff;
        return parallel(needle, haystack);
    }

    /* TODO: Add a sequential method and parallel task here */

    public static int parallel(char[] needle, char[] haystack) {

        int returner = POOL.invoke(new GetLeftMostIndexTask(needle, haystack, 0, haystack.length));
        return  returner;
    }



    private static int sequential(char[] needle, char[] haystack, int lo, int hi) {


        for(int i = lo; i < hi; i++) {
            if(haystack[i] == needle[0]) {
                boolean flag = true;

                for(int j = 0; j < needle.length; j++) {

                    if(i+j >= haystack.length || haystack[i+j] != needle[j]) {
                        flag = false;
                        break;
                    }
                }
                if(flag) {
                    return i;
                }

            }
        }

        return -1;

    }


    private static void usage() {
        System.err.println("USAGE: GetLeftMostIndex <needle> <haystack> <sequential cutoff>");
        System.exit(2);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            usage();
        }

        char[] needle = args[0].toCharArray();
        char[] haystack = args[1].toCharArray();
        try {
            System.out.println(getLeftMostIndex(needle, haystack, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }
    }



    private static class GetLeftMostIndexTask extends RecursiveTask<Integer> {
        char[] needle, haystack;
        int lo, hi;


        public GetLeftMostIndexTask(char[] needle, char[] haystack, int lo, int hi) {
            this.needle = needle;
            this.haystack = haystack;
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        protected Integer compute() {

            if(hi - lo <= cutoff) {
                return sequential(needle, haystack, lo, hi);
            }
            int mid = lo + (hi-lo)/2;
            GetLeftMostIndexTask left = new GetLeftMostIndexTask(needle, haystack, lo, mid);
            GetLeftMostIndexTask right = new GetLeftMostIndexTask(needle, haystack, mid, hi);

            left.fork();
            int rightVal = right.compute();
            int leftVal = left.join();


            if(leftVal != -1) {
                return leftVal;
            } else {
                return rightVal;
            }

        }
    }





}
