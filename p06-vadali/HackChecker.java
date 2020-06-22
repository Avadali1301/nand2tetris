import java.io.*;
import java.util.*;
import java.lang.*;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.Path;
public class HackChecker {
  private static byte[] readFileToByteArray(File file){
        FileInputStream fis = null;
        // Creating a byte array using the length of the file
        // file.length returns long which is cast to int
        byte[] bArray = new byte[(int) file.length()];
        try{
            fis = new FileInputStream(file);
            fis.read(bArray);
            fis.close();

        }catch(IOException ioExp){
            ioExp.printStackTrace();
        }
        return bArray;
    }
  public static void main(String[] args) {
    File file1 = new File("mult_test.hack");
    File file2 = new File("mult.hack");
    byte[] f1 = readFileToByteArray(file1);
    byte[] f2 = readFileToByteArray(file2);
    System.out.println(Arrays.equals(f1, f2));
  }
}
