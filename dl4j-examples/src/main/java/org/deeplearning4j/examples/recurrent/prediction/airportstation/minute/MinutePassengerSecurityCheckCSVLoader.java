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
//run order 1
public class MinutePassengerSecurityCheckCSVLoader {
    private static Logger log = LoggerFactory.getLogger(MinutePassengerSecurityCheckCSVLoader.class);
    private static Map<String, Integer> result = new HashMap<String, Integer>();
    private static Map<String, String> gateAreaMap = new HashMap<String, String>();
    private static Map<String, Map<String, String>> flightGateMap = new HashMap<String, Map<String, String>>();

    public static void main(String[] args) {
        try{
            gateAreaMap = Utils.getGateAreaMap();
            flightGateMap = Utils.getFlightGateMap();

            //fixed start time and end time
            String startString = "2016/09/10 18:50:00";
            String endString = "2016/09/14 14:50:00";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm");
            Date startDate = sdf.parse(startString);
            Date endDate = sdf.parse(endString);
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd hh:mm");
            Calendar cal = Calendar.getInstance();
            //read security passenger data, aggregate passenger amount for the same gate
            List<String> lines = IOUtils.readLines(new ClassPathResource("/airport/airport_gz_security_check_chusai_1stround.csv").getInputStream());
            int linenumber = 0;
            for(String line : lines) {
                linenumber++;
                if(linenumber == 1)
                    continue;

                String[] parts = line.split(",");
                Date securityTime = sdf.parse(parts[1].toString());
                //log.info(securityTime.toString());
                //only handle the time inside wifi time coverage
                if(!securityTime.after(endDate) && !securityTime.before(startDate)){
                    String flightID = parts[2];
                    String time = parts[1];
                    String gateName="";

                    if(flightGateMap.get(flightID) != null){
                        Map<String, String> values = flightGateMap.get(flightID);
                        if(values.keySet().contains(time)){
                            gateName = values.get(time);
                        }
                        else{
                            for(String key : values.keySet()){
                                Date scheduleTime = sdf2.parse(key);
                                Date flightTime = sdf2.parse(time);
                                if(Math.abs(scheduleTime.getTime() - flightTime.getTime())/(1000*60*60) < 23){
                                    gateName = values.get(key);
                                }
                            }
                        }
                    }

                    //log.info("gate="+gateName);
                    String areaName = gateAreaMap.get(gateName);
                    if(areaName == null){
                        log.info("flightID=" + flightID + ", time=" + time);
                    }
                    else {
                        Date chkTimeDate =  sdf.parse(time);
                        cal.setTime(chkTimeDate);
                        Double dminute = Math.floor(cal.get(Calendar.MINUTE)/10);
                        int iminute = dminute.intValue();
                        String dateHeader = sdf.format(chkTimeDate);
                        String chkTimeStr = dateHeader.substring(0,13) +":"+iminute;

                        String key = areaName + "%" + chkTimeStr;
                        //log.info("key="+key);
                        if(result.keySet().contains(key)){
                            result.put(key, result.get(key) + 1);
                        }
                        else{
                            result.put(key, 1);
                        }
                    }
                }
            }

            File file = new File("D:/projects/AI/dl4j-examples/dl4j-examples/src/main/resources/airport/wifiMinute/MinuteSecurityAggregate.csv");
            FileOutputStream out = new FileOutputStream(file, true);
            OutputStreamWriter osw = new OutputStreamWriter(out, "UTF8");
            BufferedWriter bw = new BufferedWriter(osw);

            result.forEach((k,v) -> {
                try {
                    String[] keys = k.split("%");
                    bw.newLine();
                    String date = keys[1] + "0:00";
                    String line = date + "," + v + "," + keys[0];
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
            log.error("error when generate xls file", e);
        }
    }
}
