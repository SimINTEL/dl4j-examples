package org.deeplearning4j.examples.recurrent.prediction.airportstation.minute;

import org.apache.commons.io.IOUtils;
import org.datavec.api.util.ClassPathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yukai Ji on 2016/12/2.
 */
//run order 3
public class MinuteSeparateWIFIPassengerCSVLoader {
    private static Logger log = LoggerFactory.getLogger(MinuteSeparateWIFIPassengerCSVLoader.class);
    private static Map<String, List<String>> result = new HashMap<String, List<String>>();

    public static void main(String[] args) {
        try{
            List<String> lines = IOUtils.readLines(new ClassPathResource("/airport/wifiMinute/MinuteWIFIAggregate.csv").getInputStream());

            for(String line : lines) {
                String[] parts = line.split(",");
                String key = parts[0];
                String value = parts[1] + "," + parts[2];
                if(result.keySet().contains(key)){
                    result.get(key).add(value);
                }
                else{
                    List<String> newValue = new ArrayList<>();
                    newValue.add(value);
                    result.put(key, newValue);
                }
            }

            result.forEach((k,v) -> {
                try {
                    String fileName = k.replace("<","").replace(">","").trim();
                    File file = new File("D:/projects/AI/dl4j-examples/dl4j-examples/src/main/resources/airport/wifiMinute/gates/WIFIGate"+ fileName + ".csv");
                    FileOutputStream out = new FileOutputStream(file, true);
                    OutputStreamWriter osw = new OutputStreamWriter(out, "UTF8");
                    BufferedWriter bw = new BufferedWriter(osw);

                    for (String value : v){
                        bw.newLine();
                        bw.write(k+","+value);
                    }

                    bw.close();
                    osw.close();
                    out.close();
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            });
        }
        catch (IOException e) {

        }

    }
}