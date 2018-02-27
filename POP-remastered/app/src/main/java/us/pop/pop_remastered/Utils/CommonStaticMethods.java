package info.sayederfanarefin.location_sharing.pop_remastered.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by erfan on 9/5/17.
 */

public class CommonStaticMethods {


    public static String convertFromFirebaseStringDate(String datee, String marker){
        //yyyy-MM-dd-hh-mm-ss
        //https://stackoverflow.com/questions/4216745/java-string-to-date-conversion
        DateFormat format = new SimpleDateFormat("MMM dd", Locale.ENGLISH);

        DateFormat format1 = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);

        DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss", Locale.ENGLISH);


        String date = null;
        String time = null;

        Date date2 = null;

        try {
            date2 = format2.parse(datee);

            date = format.format(date2);
            time = format1.format(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date + " "+ marker + " " + time;
    }
}
