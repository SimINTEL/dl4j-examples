package org.deeplearning4j.examples.recurrent.prediction.airportstation.minute;

import org.apache.commons.io.IOUtils;
import org.datavec.api.util.ClassPathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Yukai Ji on 2016/11/20.
 */
//run order 1
public class MinuteWIFIPassengerCSVLoader {
    private static Logger log = LoggerFactory.getLogger(MinuteWIFIPassengerCSVLoader.class);
    private static Map<String, Integer> result = new HashMap<String, Integer>();

    public static void main(String[] args){
        try{
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm");

            List<String> lines = IOUtils.readLines(new ClassPathResource("/airport/WIFI_AP_Passenger_Records_chusai_1stround.csv").getInputStream());
            for(String line : lines) {
                String[] parts = line.split(",");
                String [] times =  parts[2].toString().split("-");

                try {
                    String timeHeader = parts[2].toString().substring(0, 14);
                    Double dminute = Math.floor(Integer.parseInt(times[4])/10);
                    int iminute = dminute.intValue();

                    String key = parts[0] + "%" +timeHeader+ iminute;
                    int value = Integer.parseInt(parts[1]);

                    if(result.keySet().contains(key)){
                        result.put(key, result.get(key) + value);
                    }
                    else{
                        result.put(key, value);
                    }
                }
                catch (StringIndexOutOfBoundsException e) {
                    log.error("date length is not enough :" + parts[2].toString());
                }
            }

            File file = new File("D:/projects/AI/dl4j-examples/dl4j-examples/src/main/resources/airport/wifiMinute/MinuteWIFIAggregate.csv");
            FileOutputStream out = new FileOutputStream(file, true);
            OutputStreamWriter osw = new OutputStreamWriter(out, "UTF8");
            BufferedWriter bw = new BufferedWriter(osw);

            result.forEach((k,v) -> {
                try {
                    bw.newLine();
                    String [] array = k.split("%");
                    String [] dateStrings = array[1].toString().split("-");
                    String date = dateStrings[0] + "-" + dateStrings[1] + "-" + dateStrings[2] + " " + dateStrings[3] + ":" + dateStrings[4] + "0:00";
                    String line = array[0] + "," + date + "," + v + "," + array[0].substring(0,2).toUpperCase();
                    bw.write(line);
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            });

            bw.close();
            osw.close();
            out.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
