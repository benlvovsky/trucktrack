package com.lvovsky.tracktruck;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.view.Window;

import com.lvovsky.tracktruck.locationBasedAssignment.LocationBasedAssignment;
import com.lvovsky.tracktruck.mileageTracking.MileageTracking;
import com.lvovsky.tracktruck.orderTracking.OrderTracking;
import com.lvovsky.tracktruck.util.BaseActivity;
import com.lvovsky.tracktruck.util.SharedPreferenceStore;
import com.lvovsky.tracktruck.workforceMonitoring.WorkforceMonitoring;


/**
 * Created by piyush on 21/03/17.
 */

public class SplashScreen extends BaseActivity {

    private static int USE_CASE_ORDER_TRACKING = 1;
    private static int USE_CASE_MILEAGE_TRACKING = 2;
    private static int USE_CASE_LOCATION_BASED_ASSIGNMENT = 3;
    private static int USE_CASE_WORKFORCE_MONITORING = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Check if Driver/Sales Person/Delivery Boy is logged in currently
        String driverId = SharedPreferenceStore.getDriverId(this);
        if (TextUtils.isEmpty(driverId)) {

            // Initiate Driver / Sales Person / Delivery Boy Login by starting LoginActivity
            TaskStackBuilder.create(SplashScreen.this)
                    .addNextIntentWithParentStack(new Intent(SplashScreen.this, UseCaseActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    .startActivities();
            finish();

        } else {
            //Check if usecase is already chosen
            int useCase = SharedPreferenceStore.getUseCase(this);
            // Start Driver / Sales Person / Delivery Boy Session by starting MainActivity
            Intent intent = null;
            if(useCase == USE_CASE_ORDER_TRACKING){
                intent =   new Intent(SplashScreen.this, OrderTracking.class);
            }
            else if(useCase == USE_CASE_MILEAGE_TRACKING){
                intent =   new Intent(SplashScreen.this, MileageTracking.class);
            }
            else if(useCase == USE_CASE_WORKFORCE_MONITORING ){
                intent =   new Intent(SplashScreen.this, WorkforceMonitoring.class);
            }
            else if (useCase == USE_CASE_LOCATION_BASED_ASSIGNMENT) {
                intent =   new Intent(SplashScreen.this, LocationBasedAssignment.class);
            }
            TaskStackBuilder.create(SplashScreen.this)
                    .addNextIntentWithParentStack(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    .startActivities();
            finish();
        }
    }
}
