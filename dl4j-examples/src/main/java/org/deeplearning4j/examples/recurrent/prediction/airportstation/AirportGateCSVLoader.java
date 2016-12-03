package org.deeplearning4j.examples.recurrent.prediction.airportstation;

import org.apache.commons.io.IOExceptionWithCause;
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
 * Created by Yukai Ji on 2016/11/20.
 */
public class AirportGateCSVLoader {
    private static Logger log = LoggerFactory.getLogger(AirportGateCSVLoader.class);
    private static Map<String, Integer> result = new HashMap<String, Integer>();

    public static void main(String[] args) {
        try{
            List<String> lines = IOUtils.readLines(new ClassPathResource("/airport/WIFIAggregate.csv").getInputStream());
            int linenumber = 0;

            for(String line : lines) {
                linenumber++;
                if(linenumber == 1)
                    continue;

                String[] parts = line.split(",");
                String key = parts[7] + "%" + parts[1];
                int passengerAmount = Integer.parseInt(parts[2].toString());
                if(result.keySet().contains(key)){
                    result.put(key, result.get(key) + passengerAmount);
                }
                else{
                    result.put(key, passengerAmount);
                }
            }

            List<String> newLines = new ArrayList<String>();
            int linenumber2 = 0;
            for(String line: lines){
                linenumber2++;
                if(linenumber2 == 1)
                    continue;

                String[] parts = line.split(",");
                String key = parts[7] + "%" + parts[1];

                parts[3] = result.get(key).toString();
                String newLine = parts[0] + "," + parts[1] +"," +parts[2]+","+parts[3]+","+parts[4]+","+parts[5]+","+parts[6]+","+parts[7];
                newLines.add(newLine);
            }

            File file = new File("D:/projects/AI/dl4j-examples-master/dl4j-examples/src/main/resources/airport/WIFIAggregateBigGate2.csv");
            FileOutputStream out = new FileOutputStream(file, true);
            OutputStreamWriter osw = new OutputStreamWriter(out, "UTF8");
            BufferedWriter bw = new BufferedWriter(osw);

            newLines.forEach((line) -> {
                try {
                    bw.newLine();
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
        catch (IOException e) {

        }

    }
}
