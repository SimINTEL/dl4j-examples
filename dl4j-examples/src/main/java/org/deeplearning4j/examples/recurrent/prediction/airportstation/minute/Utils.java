package org.deeplearning4j.examples.recurrent.prediction.airportstation.minute;

import org.apache.commons.io.IOExceptionWithCause;
import org.apache.commons.io.IOUtils;
import org.datavec.api.util.ClassPathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Yukai Ji on 2016/12/5.
 */
public class Utils {

    private static Logger log = LoggerFactory.getLogger(MinutePassengerCheckinCSVLoader.class);

    public static void main(String[] args) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Date> list = getTimeSegment(2014, 7, 1);
        for(Date date : list){
            System.out.println(fmt.format(date));
        }
    }

    public static Map<String, Map<String, Integer>> getMinuteFlightAgreeate() {
        Map<String, Map<String, Integer>> flightAgreegateMap = new HashMap<String, Map<String, Integer>>();

        try{
            List<String> flighAggregate = IOUtils.readLines(new ClassPathResource("/airport/wifiMinute/MinuteFlightAggregate.csv").getInputStream());
            for(String line : flighAggregate){
                String[] parts = line.split(",");
                String time =parts[1];
                int passengerAmount = Integer.parseInt(parts[2]);
                String gate = parts[0];

                if (flightAgreegateMap.keySet().contains(gate)) {
                    Map<String, Integer> values = flightAgreegateMap.get(gate);
                    values.put(time, passengerAmount);
                } else {
                    Map<String, Integer> values = new HashMap<>();
                    values.put(time, passengerAmount);
                    flightAgreegateMap.put(gate, values);
                }
            }
        }
        catch (IOException e){
            log.error("error when generate getMinuteGateFlight", e);
        }

        return flightAgreegateMap;
    }

    public static  Map<String, Integer> getMinutePassengerMap(String fileName) {
        Map<String, Integer> checkinPassenger = new HashMap<String, Integer>();
        try {
            List<String> lines = IOUtils.readLines(new ClassPathResource(fileName).getInputStream());
            for (String line : lines) {
                String[] parts = line.split(",");
                String key = parts[2] + "%" + parts[0];
                int passengerAmount = Integer.parseInt(parts[1].toString());
                if (checkinPassenger.keySet().contains(key)) {
                    checkinPassenger.put(key, checkinPassenger.get(key) + passengerAmount);
                } else {
                    checkinPassenger.put(key, passengerAmount);
                }
            }
        }
        catch (IOException e){
            log.error("error when generate getMinutePassengerMap", e);
        }

        return checkinPassenger;
    }

    public static  Map<String, Integer> getMinuteFlightAgreeate(String fileName) {
        Map<String, Integer> checkinPassenger = new HashMap<String, Integer>();
        try {
            List<String> lines = IOUtils.readLines(new ClassPathResource(fileName).getInputStream());
            for (String line : lines) {
                String[] parts = line.split(",");
                String key = parts[0] + "%" + parts[1];
                int passengerAmount = Integer.parseInt(parts[2].toString());
                if (checkinPassenger.keySet().contains(key)) {
                    checkinPassenger.put(key, checkinPassenger.get(key) + passengerAmount);
                } else {
                    checkinPassenger.put(key, passengerAmount);
                }
            }
        }
        catch (IOException e){
            log.error("error when generate getMinutePassengerMap", e);
        }

        return checkinPassenger;
    }

    public static Map<String, Map<String, String>> getFlightGateMap(){
        Map<String, Map<String, String>> flightGateMap = new HashMap<String, Map<String, String>>();
        try {
            //load airport_gz_flights_chusai_1stround to get the gate of flight of a certain time
            List<String> flighSchedule = IOUtils.readLines(new ClassPathResource("/airport/airport_gz_flights_chusai_1stround.csv").getInputStream());
            int j = 0;
            for (String line : flighSchedule) {
                j++;
                if (j == 1)
                    continue;

                String[] parts = line.split(",");
                //no gate information for this flight, skip it
                if (parts.length < 4)
                    continue;

                String time = parts[2].toString();
                String flightID = parts[0];
                String bgateID = "";
                if (parts.length > 4) {
                    bgateID = parts[4].replace("\"", "");
                } else {
                    bgateID = parts[3];
                }
                //log.info("key="+key);
                if (flightGateMap.keySet().contains(flightID)) {
                    Map<String, String> values = flightGateMap.get(flightID);
                    values.put(time, bgateID);
                } else {
                    Map<String, String> values = new HashMap<>();
                    values.put(time, bgateID);
                    flightGateMap.put(flightID, values);
                }
            }
            log.info("flightGateMap" + flightGateMap.toString());
        }
        catch (IOException e){
            log.error("error when generate getFlightGateMap", e);
        }

        return flightGateMap;
    }

    public static Map<String, String> getGateAreaMap() {
        Map<String, String> gateAreaMap = new HashMap<String, String>();
        try {
            List<String> gateAreaList = IOUtils.readLines(new ClassPathResource("/airport/airport_gz_gates.csv").getInputStream());
            int i = 0;
            for (String gateArea : gateAreaList) {
                i++;
                if (i == 1)
                    continue;

                String[] parts = gateArea.split(",");
                gateAreaMap.put(parts[0], parts[1]);
            }
            log.info("gateAreaMap" + gateAreaMap.toString());
        }
        catch (IOException e ){
            log.error("error when generate getGateAreaMap", e);
        }

        return gateAreaMap;
    }

    public static List<Date> getTimeSegment(int year, int month, int day){
        Calendar cal = Calendar.getInstance();
        cal.set(year, month-1, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startTime = cal.getTimeInMillis();
        cal.set(year, month-1, day, 23, 59, 59);
        long endTime = cal.getTimeInMillis();
        final int seg = 5*60*1000;//五分钟
        ArrayList<Date> result = new ArrayList<Date>((int)((endTime-startTime)/seg+1));
        for(long time = startTime;time<=endTime;time+=seg){
            result.add(new Date(time));
        }
        return result;
    }
}
