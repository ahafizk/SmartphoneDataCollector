package mpsc.smartar;

import android.content.Intent;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.StreamCorruptedException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import mpsc.smartar.data.Sensor;
import mpsc.smartar.data.SensorDataPoint;

/**
 * Created by hafiz on 2/26/16.
 */
public class Utils {

    private static float []bandSensorVals=null; //this variable stands to save previous data points for filtering
    private static float []smartSensorVals = null;
    public static Map strClassLabels;
    public static Map intClassLabels;

    public Utils()
    {

    }
    public static String getRootDir()
    {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }
    public static void initializeStringClassLabels()
    {
        intClassLabels = new HashMap();
        intClassLabels.put("sitting",1);
        intClassLabels.put("standing",2);
        intClassLabels.put("walking",3);
        intClassLabels.put("brushing",4);
        intClassLabels.put("combing",5);
        intClassLabels.put("cooking",6);
        intClassLabels.put("eating",7);
        intClassLabels.put("uselaptop",8);

        //initialize int label to activity

        strClassLabels = new HashMap();
        strClassLabels.put(1,"sitting");
        strClassLabels.put(2,"standing");
        strClassLabels.put(3,"walking");
        strClassLabels.put(4,"brushing");
        strClassLabels.put(5,"combing");
        strClassLabels.put(6,"cooking");
        strClassLabels.put(7,"eating");
        strClassLabels.put(8,"uselaptop");

    }

//    public static String getStrClassLabel(int intKey)
//    {
//        return (String)strClassLabels.get(intKey);
//    }

    public static String getStrClassLabel(int intKey)
    {
        return (String)strClassLabels.get(intKey);
    }

    public static int getIntClassLabel(String key)
    {
        return (int)intClassLabels.get(key); //
    }

    public static List<String> getSubFolderList(String dir)
    {
        List<String> results = new ArrayList<String>();
        File file = new File(dir);
        String[] names = file.list();

        for(String name : names)
        {
            if (new File(dir+"/" + name).isDirectory())
            {
                results.add(name);
            }
        }
        return results;
    }


    public static synchronized void writeResults(String results, String filename, boolean append)
    {
        String rootPath = Utils.getRootDir()+Units.rootDir+Units.resultDir;
        File file=new File(rootPath);
        if(!file.exists()){
            file.mkdirs();

        }

        FileOutputStream fOut = null;
        try {

            File savedfile = new File(rootPath+filename);
            savedfile.createNewFile();//create file if not exists
            fOut = new FileOutputStream(savedfile, append); //append results
            OutputStreamWriter outWriter = new OutputStreamWriter(fOut);

            outWriter.write(results);

            fOut.flush();
            outWriter.flush();

            fOut.close();
            outWriter.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static List<String> getAllFileLists(String dir)
    {
        List<String> results = new ArrayList<String>();


        File[] files = new File(dir).listFiles();
        //If this pathname does not denote a directory, then listFiles() returns null.

        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getName());
            }
        }
        return  results;
    }

    public static float[] lowPass( float[] input, float[] output ) {
        float ALPHA = 0.25f; //lower 25 Hz frequency to filter data points
        if ( output == null ) return input;

        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    public static String getDateTime(long t)
    {
        Timestamp timestamp = new Timestamp(t/1000000); //make it miliseconds from nanoseconds
        return timestamp.toString();
    }

    public static synchronized void exportFile( Sensor sensor,String filename) {

//        Sensor sensor = getSensor(sensorType);
        if (sensor!=null) {
            LinkedList<SensorDataPoint> dataPoints = sensor.getDataPoints();


            final int total_row = dataPoints.size(); //get total number of data points

            //make string for filtered and raw sensor data to pass it to write to file
            String rawText = "";
            String filteredDataText = "";
            for (int i = 1; i < total_row; i++) {

                SensorDataPoint dp = dataPoints.get(i);

                StringBuffer sb = new StringBuffer();
                float[] values = dp.getValues();


                long milisec = dp.getTimestamp();
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
                Date resultdate = new Date(milisec);
                String times=(sdf.format(resultdate));
                rawText += getDateTime(dp.getTimestamp())+ "," +times+","+ String.valueOf(values[0]) + "," + String.valueOf(values[1])
                        + "," + String.valueOf(values[2]) + "," + String.valueOf(dp.getAccuracy())+"\n";


            }
            sensor.removeDataPoints();

            //saving data to fi
//            String filename = "acc_data.csv";
            final String rootPath = Utils.getRootDir() + Units.rootDir;

            Utils.writeToFile(rootPath, filename, rawText);


        }


    }

    public static synchronized void writeToFile( String rootPath, String filename, String data)
    {

        File file=new File(rootPath);
        if(!file.exists()){
            file.mkdirs();

        }

        FileOutputStream fOut = null;
        try {

            File savedfile = new File(rootPath+filename);
            savedfile.createNewFile();//create file if not exists
            fOut = new FileOutputStream(savedfile, true); //append content to the end of file
            OutputStreamWriter outWriter = new OutputStreamWriter(fOut);

            outWriter.write(data);

            fOut.flush();
            outWriter.flush();

            fOut.close();
            outWriter.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    public String[] doubleToString(double []d)
    {


        // create a string representation like [1.0, 2.0, 3.0, 4.0]
        String s = Arrays.toString(d);

        // cut off the square brackets at the beginning and at the end
        s = s.substring(1, s.length() - 1);

        // split the string with delimiter ", " to produce an array holding strings
        String[] s_array = s.split(", ");
        return s_array;
    }

    public static  double[] convertStringTodouble(String[]s_array){
        double [] a= new double[s_array.length];
        for (int i=0;i<s_array.length;i++){
            a[i]=Double.parseDouble(s_array[i].trim());
        }
//        System.out.println(a[4]);
        return a;
    }
    public static  double[] convertStringTodouble(String[]s_array, int pos){
        double [] a= new double[s_array.length-1];
        for (int i=0;i<s_array.length;i++){
            if (i!=pos && i>0){
                a[i-1]=Double.parseDouble(s_array[i].trim());
            }
            else {
                a[i]=Double.parseDouble(s_array[i].trim());
            }
        }
//        System.out.println(a[4]);
        return a;
    }

    public static  double[] convertStringTodouble(String[]s_array, int pos, double cls){
        Map states_map = new HashMap();
        states_map.put("sitting",1);
        states_map.put("standing",2);
        states_map.put("walking",3);
        double [] a= new double[s_array.length];
        for (int i=0;i<s_array.length;i++){
            if (i==pos) {
//        		int v = (int) cls;
                a[i] =  cls;
            }
            else {
                a[i]=Double.parseDouble(s_array[i].trim());
            }
        }
//        System.out.println(a[4]);
        return a;
    }


    public static  String [] getlines(StringBuilder text)
    {
        String[] lines = text.toString().split("\\n");
        return  lines;
    }

    public String [] getCSVValues(StringBuilder text)
    {
        return text.toString().split(",");
    }

    public ArrayList<double[]> getArrayListFromFile(String filename)
    {
        ObjectInputStream input;
        ArrayList<double[]> x= new ArrayList<double[]>();
        try {
            input = new ObjectInputStream(new FileInputStream(new File( filename)));
            x = (ArrayList<double[]>) input.readObject();
            input.close();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return x;
    }

    public static synchronized ArrayList<double[]> readArrayListFromFile(String filename)
    {
        ObjectInputStream input;
        ArrayList<double[]> x = null;
        try {
            input = new ObjectInputStream(new FileInputStream(new File( filename)));
            Object obj = input.readObject();
            if (obj!=null)
                x = (ArrayList<double[]>) input.readObject();
            input.close();

//            for (double v: x)
//            {
//                System.out.println(v);
//            }
            return x;
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return x;
    }
    public static synchronized void writeArrayListToFile(ArrayList<double []>x, String filename)
    {
//        double  []x  = {1,2,4};
//        String filename = "data.dat";
        ObjectOutput out = null;

        try {
            out = new ObjectOutputStream(new FileOutputStream(new File(filename)));
            out.writeObject(x);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized double[] readDoubleValuesFromFile(String filename)
    {
        ObjectInputStream input;
        double []x = null;
        try {
            input = new ObjectInputStream(new FileInputStream(new File( filename)));
            x = (double[]) input.readObject();
            input.close();

            for (double v: x)
            {
                System.out.println(v);
            }
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return x;
    }
    public synchronized void writeDoubleValuesToFile(double []x, String filename)
    {

        ObjectOutput out = null;

        try {
            out = new ObjectOutputStream(new FileOutputStream(new File(filename)));
            out.writeObject(x);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void writeTempFile(StringBuilder text, String filename)
    {

        FileInputStream fis = null;
        File tempFile = new File(filename) ;
        FileWriter writer=null;
//        StringBuilder text = new StringBuilder();
        try {
            writer = new FileWriter(tempFile);

            /** Saving the contents to the file*/
            writer.write(text.toString());

            /** Closing the writer object */
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized boolean deleteFile(String filename)
    {
        File file = new File(filename);
        return  file.delete();
    }

    public static synchronized StringBuilder readTempFile(String filename)
    {
        File tempFile;
        FileInputStream is = null;

        tempFile = new File(filename) ;

        String strLine="";
        StringBuilder text = new StringBuilder();

        /** Reading contents of the temporary file, if already exists */
        try {
            is = new FileInputStream(tempFile);
//            FileReader fReader = new FileReader(tempFile);
            BufferedReader bReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            /** Reading the contents of the file , line by line */
            while( (strLine=bReader.readLine()) != null  ){
                text.append(strLine+"\n");
            }

            bReader.close(); //close bufferedreader
            is.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }

        return text;

    }

    public static synchronized ArrayList<String> readFile(String filename) {
        BufferedReader buffreader;
        ArrayList<String> lines=null;
        FileInputStream is;
        BufferedReader reader;
        File file = new File(filename);

        if (file.exists()) {
            lines = new ArrayList<String>();
            try {
                is = new FileInputStream(file);
                reader = new BufferedReader(new InputStreamReader(is));
                String line = reader.readLine();
                int i=0;
                while(line != null){
                    lines.add(line);
//			    	System.out.println(line);
                    line = reader.readLine();


                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return lines;


    }

}
