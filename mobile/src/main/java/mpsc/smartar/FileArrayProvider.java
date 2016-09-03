package mpsc.smartar;
import android.content.Context;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileArrayProvider {
	 public String[] readLines(String filename) throws IOException {
	        FileReader fileReader = new FileReader(filename);
	        BufferedReader bufferedReader = new BufferedReader(fileReader);
	        List<String> lines = new ArrayList<String>();
	        String line = null;
	        while ((line = bufferedReader.readLine()) != null) {
	            lines.add(line);
	        }
	        bufferedReader.close();
	        return lines.toArray(new String[lines.size()]);
	    }
    //read file lines from the given file with context
    public String[] readLines(Context context, String fileName) throws IOException {
        InputStream fIn = context.getResources().getAssets().open(fileName, Context.MODE_WORLD_READABLE);
        InputStreamReader isr = new InputStreamReader(fIn);
//        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(isr);
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        bufferedReader.close();
        return lines.toArray(new String[lines.size()]);
    }
}
