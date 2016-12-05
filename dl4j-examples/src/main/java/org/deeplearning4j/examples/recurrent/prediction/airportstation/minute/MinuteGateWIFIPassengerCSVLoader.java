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
 * Created by Yukai Ji on 2016/11/20.
 */
//run order 2
public class MinuteGateWIFIPassengerCSVLoader {
    private static Logger log = LoggerFactory.getLogger(MinuteGateWIFIPassengerCSVLoader.class);
    private static Map<String, Integer> gatePassenger = new HashMap<String, Integer>();
    private static Map<String, Integer> checkinPassenger = new HashMap<String, Integer>();
    private static Map<String, Integer> securityPassenger = new HashMap<String, Integer>();
    private static Map<String, Map<String, Integer>> flightAggregateMap = new HashMap<String, Map<String, Integer>>();

    public static void main(String[] args) {
        try{
            //aggregate the passenger count based on the gate id, so the key would be like "E1%2016-09-13 00:47:00"
            List<String> lines = IOUtils.readLines(new ClassPathResource("/airport/wifiMinute/MinuteWIFIAggregate.csv").getInputStream());
            for(String line : lines) {
                String[] parts = line.split(",");
                String key = parts[0].substring(0,2) + "%" + parts[1];
                int passengerAmount = Integer.parseInt(parts[2].toString());
                if(gatePassenger.keySet().contains(key)){
                    gatePassenger.put(key, gatePassenger.get(key) + passengerAmount);
                }
                else{
                    gatePassenger.put(key, passengerAmount);
                }
            }

            checkinPassenger = Utils.getMinutePassengerMap("/airport/wifiMinute/MinuteCheckinAggregate.csv");
            securityPassenger = Utils.getMinutePassengerMap("/airport/wifiMinute/MinuteSecurityAggregate.csv");
            flightAggregateMap = Utils.getMinuteFlightAgreeate();

            //append the aggregated passenger count to original line, so the key would be like "E1%2016-09-13 00:47:00"
            List<String> newLines = new ArrayList<String>();
            for(String line: lines){
                String[] parts = line.split(",");
                String key = parts[0].substring(0,2) + "%" + parts[1];
                String gatePassengerAmount = gatePassenger.get(key).toString();
                String newLine = parts[0] + "," + parts[1] +"," +parts[2]+","+gatePassengerAmount;
                newLines.add(newLine);
            }

            File file = new File("D:/projects/AI/dl4j-examples/dl4j-examples/src/main/resources/airport/wifiMinute/MinuteGateWIFIPassenger.csv");
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
