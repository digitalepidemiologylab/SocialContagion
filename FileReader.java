package SocialContagion;

import java.io.BufferedReader;
import java.io.FileNotFoundException;


public class FileReader {

    String absolutePath;

    public FileReader(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public BufferedReader getBufferedReader() {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new java.io.FileReader(absolutePath));
        }
        catch (FileNotFoundException e) {
//            System.out.println("Error while trying to read file at location " + absolutePath);
            e.printStackTrace();
        }
        return in;
    }



}
