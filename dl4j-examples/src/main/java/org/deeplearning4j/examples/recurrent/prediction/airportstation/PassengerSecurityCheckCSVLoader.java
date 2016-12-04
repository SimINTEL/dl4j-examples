package org.deeplearning4j.examples.recurrent.prediction.airportstation;

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
public class PassengerSecurityCheckCSVLoader {
    private static Logger log = LoggerFactory.getLogger(PassengerSecurityCheckCSVLoader.class);
    private static Map<String, Integer> result = new HashMap<String, Integer>();

    public static void main(String[] args) {
        try{
            List<String> lines = IOUtils.readLines(new ClassPathResource("/airport/airport_gz_security_check_chusai_1stround.csv").getInputStream());

            String startString = "2016-09-10 18:50:00";
            String endString = "2016-09-14 14:50:00";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date startDate = sdf.parse(startString);
            Date endDate = sdf.parse(endString);

            Calendar cal = Calendar.getInstance();
            int linenumber = 0;
            for(String line : lines) {
                linenumber++;
                if(linenumber == 1)
                    continue;

                String[] parts = line.split(",");
                Date securityTime = sdf.parse(parts[1].toString());
                log.info(securityTime.toString());
                //only handle the time inside wifi time coverage
                if(!securityTime.after(endDate) && !securityTime.before(startDate)){
                    cal.setTime(securityTime);
                    Double dminute = Math.floor(cal.get(Calendar.MINUTE)/10);
                    int iminute = dminute.intValue();
                    log.info("minute=" + iminute);
                    String key = parts[1].toString().substring(0,10) + " " + cal.get(Calendar.HOUR_OF_DAY)+":"+iminute;
                    log.info("key="+key);

                    if(result.keySet().contains(key)){
                        result.put(key, result.get(key) + 1);
                    }
                    else{
                        result.put(key, 1);
                    }
                }
            }

            File file = new File("D:/projects/AI/dl4j-examples/dl4j-examples/src/main/resources/airport/SecurityAggregate.csv");
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
