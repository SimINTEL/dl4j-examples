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
public class MinuteAirlineScheduleCSVLoader {

    private static Logger log = LoggerFactory.getLogger(MinuteAirlineScheduleCSVLoader.class);
    private static Map<String, Integer> result = new HashMap<String, Integer>();
    private static Map<String, String> gateAreaMap = new HashMap<String, String>();

    public static void main(String[] args) {
        try{
            List<String> gateAreaList = IOUtils.readLines(new ClassPathResource("/airport/airport_gz_gates.csv").getInputStream());
            int i = 0;
            for(String gateArea : gateAreaList) {
                i++;
                if(i == 1)
                    continue;

                String[] parts = gateArea.split(",");
                gateAreaMap.put(parts[0], parts[1]);
            }
            log.info("gateAreaMap" + gateAreaMap.toString());

            List<String> lines = IOUtils.readLines(new ClassPathResource("/airport/airport_gz_flights_chusai_1stround.csv").getInputStream());
            String startString = "2016/09/10 18:50";
            String endString = "2016/09/14 14:50";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm");
            Date startDate = sdf.parse(startString);
            Date endDate = sdf.parse(endString);

            Calendar cal = Calendar.getInstance();
            int linenumber = 0;
            for(String line : lines) {
                linenumber++;
                if(linenumber == 1)
                    continue;

                String[] parts = line.split(",");
                log.info(parts[4]);
                Date checkinTime =  null;
                try{
                    checkinTime = sdf.parse(parts[4].toString());
                    log.info(checkinTime.toString());
                }
                catch(ParseException e){
                    checkinTime = sdf.parse(parts[5].toString());
                }

                //only handle the time inside wifi time coverage
                if(checkinTime != null && !checkinTime.after(endDate) && !checkinTime.before(startDate)){
                    cal.setTime(checkinTime);
                    int iminute = cal.get(Calendar.MINUTE);
                    log.info("minute=" + iminute);
                    String dateHeader = sdf.format(checkinTime);
                    String time = dateHeader.substring(0,10) + " " + cal.get(Calendar.HOUR_OF_DAY)+":"+iminute;
                    String gate = gateAreaMap.get(parts[3]);
                    log.info("key="+gate + "%" +time);
                    String mapkey = gate + "%" +time;
                    if(result.keySet().contains(mapkey)){
                        result.put(mapkey, result.get(mapkey) + 1);
                    }
                    else{
                        result.put(mapkey, 1);
                    }
                }
            }

            File file = new File("D:/projects/AI/dl4j-examples/dl4j-examples/src/main/resources/airport/wifiMinute/MinuteFlightAggregate.csv");
            FileOutputStream out = new FileOutputStream(file, true);
            OutputStreamWriter osw = new OutputStreamWriter(out, "UTF8");
            BufferedWriter bw = new BufferedWriter(osw);

            result.forEach((k,v) -> {
                try {
                    log.info("Mapkey="+ k);
                    bw.newLine();
                    String [] keys =  k.split("%");
                    String date = keys[1] + ":00";
                    String line = keys[0] + ","+date + "," + v;
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
        catch(ParseException e){
            log.error("data parse error", e);
        }
        catch (Exception e){
            log.error("error when generate xls file", e);
        }
    }


}
