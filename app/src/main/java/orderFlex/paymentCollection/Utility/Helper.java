package orderFlex.paymentCollection.Utility;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Helper {

    private Context context;

    public Helper(Context context) {
        this.context = context;
    }

    public  boolean isInternetAvailable() {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();

    }
    public void showSnakBar(View view, String text){
        Snackbar.make(view,text , Snackbar.LENGTH_LONG).show();
    }

    public String getDate(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }
    public String getDateTime(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }
    public String getMonthEarlier(){
        Date to = Calendar.getInstance().getTime();
        //this line is supposedly to get the date that is 30 days ago
        Date from = new Date(to.getTime()+(1000*60*60*24*30));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date=sdf.format(from);
        return date;
    }

    public String getCalenderDate(final TextView setPosition){
        final Calendar calendar;
        int month, year, day;
        final String[] date = {""};
        //calender
        calendar= Calendar.getInstance();
        year=calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH);
        day=calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                        calendar.set(i,i1,i2);
                        date[0] = sdf.format(calendar.getTime());
                        //Log.i(TAG,"At pick date: "+ date[0]);
                        setPosition.setText(date[0]);
                    }
                }, year, month, day);

        datePickerDialog.show();
        return date[0];
    }

    public String dateConversion(String date){
        String formatedate="";
        SimpleDateFormat mdyFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat svrFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date forDate=mdyFormat.parse(date);
            formatedate= svrFormat.format(forDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatedate;
    }

    public String makeUniqueID(){
        Date date= new Date();
        long time = date.getTime();
        return String.valueOf(time);
    }

    public void showAlert(String title, String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle(title);
        builder1.setMessage(message);
        //builder1.setCancelable(true);
        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }
    public String dateParchYMD(String dateData){
        Date date;
        String stringDate="";
        try {
            date= new SimpleDateFormat("yyyy-MM-dd").parse(dateData);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            stringDate = df.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return stringDate;
    }
}
