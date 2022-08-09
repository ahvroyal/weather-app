package org.ahvroyal.weatherapp.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleDateConverter {

    public static Date toDate(Long timeStamp) {
        return timeStamp == null ? null : new Date(timeStamp);
    }

    public static String[] datesToDisplay(Long timeStamp) {
        Date currentDate = new Date();
        Date date = toDate(timeStamp);

        StringBuilder fDateToDisplay = new StringBuilder();
        StringBuilder fDateDetailsToDisplay = new StringBuilder();

        if ((date.getDate()) - (currentDate.getDate()) == -1)
            fDateToDisplay.append("Yesterday");
        else if ((date.getDate()) - (currentDate.getDate()) == 0)
            fDateToDisplay.append("Today");
        else if ((date.getDate()) - (currentDate.getDate()) == 1)
            fDateToDisplay.append("Tomorrow");
        else {
            String weekDay = "";
            switch (date.getDay()) {
                case 0:
                    weekDay = "Sunday";
                    break;
                case 1:
                    weekDay = "Monday";
                    break;
                case 2:
                    weekDay = "Tuesday";
                    break;
                case 3:
                    weekDay = "Wednesday";
                    break;
                case 4:
                    weekDay = "Thursday";
                    break;
                case 5:
                    weekDay = "Friday";
                    break;
                case 6:
                    weekDay = "Saturday";
                    break;
                default:
                    break;

            }
            fDateToDisplay.append(weekDay);
        }

        String month = "";
        switch (date.getMonth()) {
            case 0:
                month = "January";
                break;
            case 1:
                month = "February";
                break;
            case 2:
                month = "March";
                break;
            case 3:
                month = "April";
                break;
            case 4:
                month = "May";
                break;
            case 5:
                month = "June";
                break;
            case 6:
                month = "July";
                break;
            case 7:
                month = "August";
                break;
            case 8:
                month = "September";
                break;
            case 9:
                month = "October";
                break;
            case 10:
                month = "November";
                break;
            case 11:
                month = "December";
                break;
            default:
                break;

        }
        fDateDetailsToDisplay.append(month).append(" ");
        fDateDetailsToDisplay.append(date.getDate()).append(" ");
        fDateDetailsToDisplay.append(date.getHours()).append(":").append(date.getMinutes());

        return new String[]{String.valueOf(fDateToDisplay), String.valueOf(fDateDetailsToDisplay)};
    }

}
