package com.salathegroup.socialcontagion;

import java.io.BufferedWriter;
import java.io.File;

public class FileWriter {

    private String fileName;
    private BufferedWriter bw;

    public FileWriter(String p_fileName) {
        this.fileName = p_fileName;
        String[] dirs = p_fileName.split("/");
        String path = "";
        for (int i = 0; i < dirs.length-1; i++) {
            path += dirs[i]+"/";
        }
        File baseFile = new File(path);
        try {
            if (!baseFile.exists()) baseFile.mkdirs();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            java.io.FileWriter fw = new java.io.FileWriter(this.fileName);
            this.bw = new BufferedWriter(fw);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write(String p_line) {
        try {
            this.bw.write(p_line);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeln(String p_line) {
        this.write(p_line+"\n");
    }

    public void close() {
        try {
            this.bw.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFileName() {
        return this.fileName;
    }


}
