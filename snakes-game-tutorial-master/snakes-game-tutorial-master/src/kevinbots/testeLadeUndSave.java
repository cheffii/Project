package kevinbots;

import java.io.File;
import java.io.FileNotFoundException;

public class testeLadeUndSave {
    public static void main(String[] args) {
        Jerry_Kevin j = new Jerry_Kevin();
        try {
            j.save(new File("testJerry.txt"));
            Jerry_Kevin x = new Jerry_Kevin(new File("testJerry.txt"));

            if (x.equals(j))
                System.out.println("all fine");
            else
                System.out.println("dammit");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static boolean compare(float[][] a, float[][] b) {
        if (a.length != b.length)
            return false;

        for (int i = 0; i < a.length; i++) {
            if (a[i].length != b[i].length)
                return false;

            for (int k = 0; k < a[i].length; k++) {
                if (a[i][k] != b[i][k])
                    return false;
            }
        }
        return true;
    }
}
