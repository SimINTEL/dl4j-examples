package org.deeplearning4j.examples.recurrent.prediction.airportstation;

import org.apache.commons.io.IOUtils;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.examples.dataExamples.BasicCSVClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Yukai Ji on 2016/11/20.
 */
public class AirlineScheduleCSVLoader {

    private static Logger log = LoggerFactory.getLogger(AirlineScheduleCSVLoader.class);
    private static Map<String, Integer> result = new HashMap<String, Integer>();

    public static void main(String[] args) {
        try{
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
                    Double dminute = Math.floor(cal.get(Calendar.MINUTE)/10);
                    int iminute = dminute.intValue();
                    log.info("minute=" + iminute);
                    String dateHeader = sdf.format(checkinTime);
                    String key = dateHeader.substring(0,10) + " " + cal.get(Calendar.HOUR_OF_DAY)+":"+iminute;
                    log.info("key="+key);

                    if(result.keySet().contains(key)){
                        result.put(key, result.get(key) + 1);
                    }
                    else{
                        result.put(key, 1);
                    }
                }
            }

            File file = new File("D:/projects/AI/dl4j-examples/dl4j-examples/src/main/resources/airport/FlightAggregate.csv");
            FileOutputStream out = new FileOutputStream(file, true);
            OutputStreamWriter osw = new OutputStreamWriter(out, "UTF8");
            BufferedWriter bw = new BufferedWriter(osw);

            result.forEach((k,v) -> {
                try {
                    bw.newLine();
                    String date = k + "0:00";
                    String line = date + "," + v;
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
