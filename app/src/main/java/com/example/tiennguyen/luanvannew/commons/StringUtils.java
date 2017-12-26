package com.example.tiennguyen.luanvannew.commons;

import android.content.Context;
import android.text.TextUtils;

import com.example.tiennguyen.luanvannew.models.PersonItem;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by Quyen Hua on 11/12/2017.
 */

public class StringUtils {
    public static String convertedToUnsigned(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replaceAll("đ", "d").replaceAll(" ", "+");
    }

    public static String getArtists(ArrayList<PersonItem> artist) {
        String artistsString = "";
        ArrayList<String> listArtist = new ArrayList<>();
        for (int i = 0; i < artist.size(); i++) {
            listArtist.add(artist.get(i).getName());
        }
        artistsString = TextUtils.join(", ", listArtist);
//        if (artistsString == "") {
//            artistsString = "Đang cập nhật";
//        }
        return artistsString;
    }

    public String readData(String fileName, Context ctx) {
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = ctx.openFileInput(fileName);
            BufferedReader br= new BufferedReader(new InputStreamReader(fis));
            String s = null;
            while((s = br.readLine())!= null)  {
                sb.append(s).append("\n");
            }
        } catch (FileNotFoundException e) {
            return "";
        } catch (IOException e) {
            return "";
        }
        return String.valueOf(sb);
    }

    public static String newText(String text, int length){
        String newText = "";
        if(text.length() > length + 3){
            for(int i = 0; i < length; i++){
                newText = newText + text.charAt(i);
            }
            newText = newText + " ...";
        }
        else{
            newText = text;
        }
        return newText;
    }
}
