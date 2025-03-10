package com.example.weatherdemo.utils

import android.util.Log
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

open class DateUtils {
    companion object {
         val FORMAT_YYYY_MM_DD_T_HH_MM = "yyyy-MM-dd'T'HH:mm"
         fun getDayOfMonth(dateStr: String, format: String): String {
             try {
                 val dateFormat = DateTimeFormatter.ofPattern(format);
                 val dateTime = LocalDateTime.parse(dateStr, dateFormat);
                 return dateTime.dayOfMonth.toString();
             } catch (error: Exception) {
                 Log.e("DateUtils", "getDayOfMonth failed:${error.message}");
                 return dateStr;
             }
         }

         fun getMonth(dateStr: String, format: String): String {
             try {
                 val dateFormat = DateTimeFormatter.ofPattern(format);
                 val dateTime = LocalDateTime.parse(dateStr, dateFormat);
                 return dateTime.monthValue.toString();
             } catch (error: Exception) {
                 Log.e("DateUtils", "getMonth failed:${error.message}");
                 return dateStr;
             }
         }

        fun getMonthShortName(month: String): String {
            return when(month) {
                "01", "1" -> "Jan";
                "02", "2" -> "Feb";
                "03", "3" -> "Mar";
                "04", "4" -> "Apr";
                "05", "5" -> "May";
                "06", "6" -> "Jun";
                "07", "7" -> "Jul";
                "08", "8" -> "Aug";
                "09", "9" -> "Sept";
                "10" -> "Oct";
                "11" -> "Nov";
                "12" -> "Dec";
                else -> month;
            }
        }
    }
}