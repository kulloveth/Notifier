package kulloveth.developer.com.notifier;

import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import static androidx.work.ExistingWorkPolicy.REPLACE;
import static com.google.android.material.snackbar.Snackbar.LENGTH_LONG;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static kulloveth.developer.com.notifier.NotifyWork.NOTIFICATION_ID;
import static kulloveth.developer.com.notifier.NotifyWork.NOTIFICATION_WORK;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final CoordinatorLayout coordinatorLayout = findViewById(R.id.cordinate);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                Calendar customCalendar = GregorianCalendar.getInstance();
                DatePicker dp = findViewById(R.id.date_picker);
                TimePicker picker = findViewById(R.id.time_picker);
                customCalendar.set(
                        dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), picker.getHour(), picker.getMinute(), 0);

                long customTime = customCalendar.getTimeInMillis();
                SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.notification_schedule_pattern), Locale.getDefault());
                long currentTime = System.currentTimeMillis();

                Log.d("time", "cistomTime " + customTime);
                Log.d("time", "cistomTime " + currentTime);
                if (customTime > currentTime) {
                    Data data = new Data.Builder().putInt(NOTIFICATION_ID, 0).build();
                    int delay = (int) (customTime - currentTime);

                    scheduleNotification(delay, data);
                    String titleNotificationSchedule = getString(R.string.notification_schedule_title);
                    Snackbar.make(
                            view,
                            titleNotificationSchedule + sdf
                                    .format(customCalendar.getTime()),
                            LENGTH_LONG).show();
//        Snackbar.make(coordinatorLayout, "Reminder set", LENGTH_LONG)
//                .setAction("Action", null).show();
                } else {
                    String errorNotificationSchedule = "Error occured";
                    Snackbar.make(coordinatorLayout, errorNotificationSchedule, LENGTH_LONG).show();
                }
            }


        });
    }

    private void scheduleNotification(long delay, Data data) {
        OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotifyWork.class)
                .setInitialDelay(delay, MILLISECONDS).setInputData(data).build();

        WorkManager instanceWorkManager = WorkManager.getInstance(getApplicationContext());
        instanceWorkManager.beginUniqueWork(NOTIFICATION_WORK, REPLACE, notificationWork).enqueue();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setUp() {

        // String titleNotification = "Reminder";
        //collapsing_toolbar_l.title = titleNotification
    }
}
