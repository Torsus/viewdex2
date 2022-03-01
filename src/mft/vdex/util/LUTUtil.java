/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mft.vdex.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 *
 * @author sunsv1
 */
public class LUTUtil {

    /**
     * Check if the lut file exist.
     * @return boolean true if history-object exist else false.
     *  NOT IN USE
     */
    public void fileRead2(String str) {
        String userHome = System.getProperty("user.home");
        String userDir = System.getProperty("user.dir");

        String path = userDir + File.separator + "lut" + File.separator + str + ".lut";
        File filePath = new File(path);
        Scanner s = null;
        try {
            s = new Scanner(new BufferedReader(new FileReader(path)));
            while (s.hasNext()) {
                int a = s.nextInt();
                int b = 10;
            //System.out.println(s.next());
            }
        } catch (IOException e) {
            System.out.println("I/O exception obtaining a stream!");
            e.printStackTrace();
            System.exit(0);
        }

        if (s != null) {
            s.close();
        }
    }

    /**
     * Put some text here
     */
    public byte[][] fileRead(String str) {
        BufferedReader inputStream = null;
        //PrintWriter outputStream = null;
        String userHome = System.getProperty("user.home");
        String userDir = System.getProperty("user.dir");

        String path = userDir + File.separator + "lut" + File.separator + str + ".lut";
        File filePath = new File(path);
        String charSeq = " ";
        byte[][] lut = new byte[3][256];

        try {
            inputStream = new BufferedReader(new FileReader(path));
            String l;
            int i = 0;
            while ((l = inputStream.readLine()) != null) {
                if (i <= 255) {
                    String[] lineStr = l.split(charSeq);
                    lut[0][i] = new Integer(lineStr[0]).byteValue();
                    lut[1][i] = new Integer(lineStr[1]).byteValue();
                    lut[2][i] = new Integer(lineStr[2]).byteValue();
                    i++;
                }
            //outputStream.println(l);
            }
        } catch (IOException e) {
            System.out.println("I/O exception obtaining a stream!");
            e.printStackTrace();
            System.exit(0);
        }

        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
        return lut;
    }
}
