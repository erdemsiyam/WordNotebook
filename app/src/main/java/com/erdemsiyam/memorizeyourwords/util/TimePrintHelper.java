package com.erdemsiyam.memorizeyourwords.util;

import android.content.Context;
import android.text.format.DateFormat;

public abstract class TimePrintHelper {
    public static String getTime(Context context, int hour, int minute){
        if(DateFormat.is24HourFormat(context)){
            return ((hour<10)?"0"+hour:""+hour)+" : "+((minute<10)?"0"+minute:""+minute);
        } else {
            if( 13 <= hour) {
                return ((hour-12<10)?"0"+(hour-12):""+(hour-12))+" : "+((minute<10)?"0"+minute:""+minute) + " PM";
            } else if (hour == 0) {
                return "12 : "+((minute<10)?"0"+minute:""+minute) + " AM";
            } else if (hour == 12) {
                return "12 : "+((minute<10)?"0"+minute:""+minute) + " PM";
            } else {
                return ((hour<10)?"0"+hour:""+hour)+" : "+((minute<10)?"0"+minute:""+minute) + " AM";
            }
        }
    }
}
