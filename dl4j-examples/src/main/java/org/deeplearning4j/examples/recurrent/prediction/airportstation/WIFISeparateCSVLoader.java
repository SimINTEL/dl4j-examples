package org.deeplearning4j.examples.recurrent.prediction.airportstation;

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
public class WIFISeparateCSVLoader {
    private static Logger log = LoggerFactory.getLogger(WIFISeparateCSVLoader.class);
    private static Map<String, List<String>> result = new HashMap<String, List<String>>();

    public static void main(String[] args) {
        try{
            List<String> lines = IOUtils.readLines(new ClassPathResource("/airport/WIFIAggregateBigGate2.csv").getInputStream());
            int linenumber = 0;

            for(String line : lines) {
                linenumber++;
                if(linenumber == 1)
                    continue;

                String[] parts = line.split(",");
                String key = parts[0];
                String value = parts[1] + "," + parts[2] + "," + parts[3] + "," + parts[4] + "," +parts[5] + "," + parts[6] + ","+parts[7];
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
                    File file = new File("D:/projects/AI/dl4j-examples-master/dl4j-examples/src/main/resources/airport/wifi/WIFIGate"+ fileName + ".csv");
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
