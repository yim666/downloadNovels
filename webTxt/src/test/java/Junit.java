import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Yim on 2023/3/12.
 */
public class Junit {
    @Test
    void A() {
        Random rand = new Random();
        int[] arr = new int[58];
        for (int i = 0; i < 58; i++) {
//            arr[i] = rand.nextInt(1000); // 生成 0 到 999 的随机数
            arr[i] = i;
        }
        int n = 12;
        int[][] subArrays = divideArray(arr, n);
        // 输出子数组
        int i=1;
        for (int[] subArray : subArrays) {
            System.out.println(i++ +"--->"+ subArray.length);
        }
    }


    public static int[][] divideArray(int[] arr, int n) {
        int len = arr.length;
        int[][] subArrays = new int[n][];
        int avg = len / n;
        int remainder = len % n;
        int offset = 0;
        for (int i = 0; i < n; i++) {
            int size = i < remainder ? avg + 1 : avg;
            subArrays[i] = Arrays.copyOfRange(arr, offset, offset + size);
            offset += size;
        }
        return subArrays;
    }
}
