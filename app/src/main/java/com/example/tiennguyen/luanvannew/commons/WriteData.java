package com.example.tiennguyen.luanvannew.commons;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Quyen Hua on 11/1/2017.
 */

public class WriteData {

    GetFileOutputStream getFileOutputStream;

    public WriteData(GetFileOutputStream getFileOutputStream) {
        this.getFileOutputStream = getFileOutputStream;
    }

    public void saveData(String data) {
        try {
            FileOutputStream out = getFileOutputStream.getFileOutputStream();
            out.write(data.getBytes());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface GetFileOutputStream {
        FileOutputStream getFileOutputStream();
    }
}

