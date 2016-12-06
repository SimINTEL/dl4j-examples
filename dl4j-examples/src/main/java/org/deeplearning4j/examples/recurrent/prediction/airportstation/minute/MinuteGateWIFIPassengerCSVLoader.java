package org.deeplearning4j.examples.recurrent.prediction.airportstation.minute;

import org.apache.commons.io.IOUtils;
import org.datavec.api.util.ClassPathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Yukai Ji on 2016/11/20.
 */
//run order 2
public class MinuteGateWIFIPassengerCSVLoader {
    private static Logger log = LoggerFactory.getLogger(MinuteGateWIFIPassengerCSVLoader.class);
    private static Map<String, Integer> gatePassenger = new HashMap<String, Integer>();
    private static Map<String, Integer> checkinPassenger = new HashMap<String, Integer>();
    private static Map<String, Integer> securityPassenger = new HashMap<String, Integer>();
    private static Map<String, Integer> flightAggregateMap = new HashMap<String, Integer>();

    public static void main(String[] args) {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            //aggregate the passenger count based on the gate id, so the key would be like "E1%2016-09-13 00:47:00"
            List<String> lines = IOUtils.readLines(new ClassPathResource("/airport/wifiMinute/MinuteWIFIAggregate.csv").getInputStream());
            for(String line : lines) {
                String[] parts = line.split(",");
                Date d = sdf.parse(parts[1]);
                String dout = sdf2.format(d);
                String key = parts[0].substring(0,2).toUpperCase() + "%" + dout;
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
            flightAggregateMap = Utils.getMinuteFlightAgreeate("/airport/wifiMinute/MinuteFlightAggregate.csv");

            log.info(gatePassenger.toString());
            log.info(checkinPassenger.toString());
            log.info(securityPassenger.toString());
            log.info(flightAggregateMap.toString());

            Calendar cal = Calendar.getInstance();
            //append the aggregated passenger count to original line, so the key would be like "E1%2016-09-13 00:47:00"
            List<String> newLines = new ArrayList<String>();
            for(String line: lines){
                String[] parts = line.split(",");
                Date d = sdf.parse(parts[1]);
                String dout = sdf2.format(d);

                String key = parts[3] + "%" + dout;
                log.info(key);
                int gatePassengerAmount = 0;
                int checkinPassengerAmount = 0;
                int securityPassengerAmount = 0;
                int flightAmount = 0;

                //loop all the maps, aggregate with the key and get value for each row
                gatePassengerAmount = gatePassenger.get(key) == null ? 0 : gatePassenger.get(key);
                checkinPassengerAmount = checkinPassenger.get(key) == null ? 0 : checkinPassenger.get(key);
                securityPassengerAmount = securityPassenger.get(key) == null ? 0 : securityPassenger.get(key);
                flightAmount =flightAggregateMap.get(key) == null ? 0 : flightAggregateMap.get(key);

                String newLine = parts[0] + "," + dout +"," +parts[2]+","+gatePassengerAmount+","+checkinPassengerAmount+","+securityPassengerAmount+","+flightAmount+","+parts[3];
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
        catch(ParseException e){
            log.error("data parse error", e);
        }

    }
}
