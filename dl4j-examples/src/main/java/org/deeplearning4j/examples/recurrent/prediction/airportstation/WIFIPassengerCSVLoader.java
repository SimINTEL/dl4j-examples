package org.deeplearning4j.examples.recurrent.prediction.airportstation;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionContext;
import org.datavec.api.util.ClassPathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yukai Ji on 2016/11/20.
 */
public class WIFIPassengerCSVLoader {
    private static Logger log = LoggerFactory.getLogger(WIFIPassengerCSVLoader.class);
    private static Map<String, Integer> result = new HashMap<String, Integer>();

    public static void main(String[] args){
        try{
            List<String> lines = IOUtils.readLines(new ClassPathResource("/airport/WIFI_AP_Passenger_Records_chusai_1stround.csv").getInputStream());
            //we don't want the loop to be asynchronous since we will aggregate the passenger count. so we will choose the traditional way of loop
            /*lines.forEach(item -> {
                String[] parts = item.split(",");
            });*/

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

            File file = new File("D:/projects/AI/dl4j-examples-master/dl4j-examples/src/main/resources/airport/WIFIAggregate.csv");
            FileOutputStream out = new FileOutputStream(file, true);
            OutputStreamWriter osw = new OutputStreamWriter(out, "UTF8");
            BufferedWriter bw = new BufferedWriter(osw);

            result.forEach((k,v) -> {
                try {
                    bw.newLine();
                    String [] array = k.split("%");
                    String [] dateStrings = array[1].toString().split("-");
                    String date = dateStrings[0] + "-" + dateStrings[1] + "-" + dateStrings[2] + " " + dateStrings[3] + ":" + dateStrings[4] + "0:00";
                    String line = array[0] + "," + date + "," + v;
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
