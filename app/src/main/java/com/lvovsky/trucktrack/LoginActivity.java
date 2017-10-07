package com.lvovsky.trucktrack;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hypertrack.lib.HyperTrack;
import com.hypertrack.lib.callbacks.HyperTrackCallback;
import com.hypertrack.lib.models.ErrorResponse;
import com.hypertrack.lib.models.SuccessResponse;
import com.hypertrack.lib.models.User;
import com.lvovsky.trucktrack.locationBasedAssignment.LocationBasedAssignment;
import com.lvovsky.trucktrack.mileageTracking.MileageTracking;
import com.lvovsky.trucktrack.orderTracking.OrderTracking;
import com.lvovsky.trucktrack.util.BaseActivity;
import com.lvovsky.trucktrack.util.SharedPreferenceStore;
import com.lvovsky.trucktrack.workforceMonitoring.WorkforceMonitoring;
import com.hypertrack.usecases.R;

/**
 * This class can be used to enable Driver/Sales Person/Delivery Boy's Login flow in your app.
 * This Activity consists of optional input fields for Driver/Sales Person/Delivery Boy's Name
 * and his phone number.
 * Once the Location Services are enabled and Location Permission has been granted,
 * the Driver/Sales Person/Delivery Boy can proceed with the login. This will be when a User entity
 * will be created on HyperTrack API Server and the SDK will be configured for this created User.
 *
 * Once a User has been created, you can start your Driver/Sales Person/Delivery Boy's session
 * in the app and navigate to MainActivity where actions can be created and assigned to him.
 */
public class LoginActivity extends BaseActivity {

    private EditText nameText, phoneNumberText;
    private LinearLayout loginBtnLoader;
    private int USE_CASE;
    private static int USE_CASE_ORDER_TRACKING = 1;
    private static int USE_CASE_MILEAGE_TRACKING = 2;
    private static int USE_CASE_LOCATION_BASED_ASSIGNMENT = 3;
    private static int USE_CASE_WORKFORCE_MONITORING = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        USE_CASE = getIntent().getIntExtra("use_case",0);
        // Initialize Toolbar
        initToolbar();

        // Initialize UI Views
        initUIViews();
    }

    //Set toolbar title according to use case
    private void initToolbar(){
        if(USE_CASE == USE_CASE_ORDER_TRACKING){
            initToolbar(getString(R.string.order_tracking), false);
        }
        else if(USE_CASE == USE_CASE_MILEAGE_TRACKING){
            initToolbar(getString(R.string.mileage_tracking), false);
        }
        else if(USE_CASE == USE_CASE_WORKFORCE_MONITORING ){
            initToolbar(getString(R.string.work_force_monitoring), false);
        }
        else if (USE_CASE == USE_CASE_LOCATION_BASED_ASSIGNMENT) {
            initToolbar(getString(R.string.location_based_assignment), false);
        }
        else{
            initToolbar(getString(R.string.login_activity_title),false);
        }
    }

    /**
     * Call this method to initialize UI views and handle listeners for these views
     */
    private void initUIViews() {
        // Initialize Driver/Sales Person/Delivery Boy Name Views
        nameText = (EditText) findViewById(R.id.login_name);

        // Initialize PhoneNumber Views
        phoneNumberText = (EditText) findViewById(R.id.login_phone_number);

        // Initialize Login Btn Loader
        loginBtnLoader = (LinearLayout) findViewById(R.id.login_btn_loader);
    }

    /**
     * Call this method when Driver/Sales Person/Delivery Boy Login button has been clicked.
     * Note that this method is linked with the layout file (content_login.xml)
     * using this button's layout's onClick attribute. So no need to invoke this
     * method or handle login button's click listener explicitly.
     *
     * @param view
     */
    public void onLoginButtonClick(View view) {
        // Check if Location Settings are enabled,
        // if yes then attempt Driver/Sales Person/Delivery Boy Login
        checkForLocationSettings();
    }

    /**
     * Call this method to check Location Settings before
     * proceeding for Driver/Sales Person/Delivery Boy Login
     */
    private void checkForLocationSettings() {
        // Check for Location permission
        if (!HyperTrack.checkLocationPermission(this)) {
            HyperTrack.requestPermissions(this);
            return;
        }

        // Check for Location settings
        if (!HyperTrack.checkLocationServices(this)) {
            HyperTrack.requestLocationServices(this);
        }

        // Location Permissions and Settings have been enabled
        // Proceed with your app logic here
        // i.e Driver/Sales Person/Delivery Boy Login in this case
        attemptLogin();
    }

    /**
     * Call this method to attempt Driver/Sales Person/Delivery Boy login.
     * This method will create a User on HyperTrack Server
     * and configure the SDK using this generated UserId.
     */
    private void attemptLogin() {
        // Show Login Button loader
        loginBtnLoader.setVisibility(View.VISIBLE);

        // Get Driver/Sales Person/Delivery Boy details, if provided
        final String name = nameText.getText().toString();
        final String phoneNumber = phoneNumberText.getText().toString();

        // PhoneNumber is used as the lookup_id here but you can specify any
        // other entity as the lookup_id.
        final String lookupId = phoneNumber;

        /**
         * Create a User on HyperTrack Server here to login your Driver/Sales Person/Delivery Boy
         * & configure HyperTrack SDK with this generated HyperTrack UserId.
         * OR
         * Implement your API call for Driver/Sales Person/Delivery Boy Login and get back a
         * HyperTrack UserId from your API Server to be configured in the HyperTrack SDK.
         *
         * @NOTE:
         * Specify Driver/Sales Person/Delivery Boy name, phone number and a lookup_id denoting
         * your Driver/Sales Person/Delivery Boy's internal id.
         * PhoneNumber is used as the lookup_id here but you can specify any other entity
         * as the lookup_id.
         */
        HyperTrack.getOrCreateUser(name, phoneNumber, lookupId, new HyperTrackCallback() {
            @Override
            public void onSuccess(@NonNull SuccessResponse successResponse) {
                // Hide Login Button loader
                loginBtnLoader.setVisibility(View.GONE);

                User user = (User) successResponse.getResponseObject();
                // Handle createUser success here, if required
                // HyperTrack SDK auto-configures UserId on createUser API call, so no need to call
                // HyperTrack.setUserId() API

                // On Driver/Sales Person/Delivery Boy Login success
                onLoginSuccess();
            }

            @Override
            public void onError(@NonNull ErrorResponse errorResponse) {
                // Hide Login Button loader
                loginBtnLoader.setVisibility(View.GONE);

                Toast.makeText(LoginActivity.this, R.string.login_error_msg + " " +
                                errorResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Call this method when Driver/Sales Person/Delivery Boy has successfully logged in
     */
    private void onLoginSuccess() {

        // To start tracking your Driver/Sales Person/Delivery Boy, call HyperTrack's
        // startTracking API
        HyperTrack.startTracking(new HyperTrackCallback() {
            @Override
            public void onSuccess(@NonNull SuccessResponse successResponse) {

                Toast.makeText(LoginActivity.this, R.string.login_success_msg,
                        Toast.LENGTH_SHORT).show();

                // Start Driver/Sales Person/Delivery Boy Session
                startSession();
            }

            @Override
            public void onError(@NonNull ErrorResponse errorResponse) {
                // Hide Login Button loader
                loginBtnLoader.setVisibility(View.GONE);

                Toast.makeText(LoginActivity.this, R.string.login_error_msg + " " +
                                errorResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void startSession(){

        // Hide Login Button loader
        loginBtnLoader.setVisibility(View.GONE);

        // Handle startTracking API response
        SharedPreferenceStore.setDriverId(LoginActivity.this, HyperTrack.getUserId());

        if(USE_CASE == USE_CASE_ORDER_TRACKING){
            Intent intent = new Intent(LoginActivity.this, OrderTracking.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            TaskStackBuilder.create(LoginActivity.this)
                    .addNextIntentWithParentStack(intent)
                    .startActivities();
            finish();
        }
        else if(USE_CASE == USE_CASE_MILEAGE_TRACKING){
            Intent intent = new Intent(LoginActivity.this, MileageTracking.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            TaskStackBuilder.create(LoginActivity.this)
                    .addNextIntentWithParentStack(intent)
                    .startActivities();
            finish();
        }
        else if(USE_CASE == USE_CASE_WORKFORCE_MONITORING ){
            Intent intent = new Intent(LoginActivity.this, WorkforceMonitoring.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            TaskStackBuilder.create(LoginActivity.this)
                    .addNextIntentWithParentStack(intent)
                    .startActivities();
            finish();
        }
        else if (USE_CASE == USE_CASE_LOCATION_BASED_ASSIGNMENT) {
            Intent intent = new Intent(LoginActivity.this, LocationBasedAssignment.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            TaskStackBuilder.create(LoginActivity.this)
                    .addNextIntentWithParentStack(intent)
                    .startActivities();
            finish();
        }
    }
    //Re-select use case before login
    public void onChangeUseCaseClick(View view){
        TaskStackBuilder.create(LoginActivity.this)
                .addNextIntentWithParentStack(new Intent(LoginActivity.this, UseCaseActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                .startActivities();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == HyperTrack.REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location Permission granted successfully, proceed with Login flow
                checkForLocationSettings();

            } else {
                // Handle Location Permission denied error
                Toast.makeText(this, "Location Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == HyperTrack.REQUEST_CODE_LOCATION_SERVICES) {
            if (resultCode == Activity.RESULT_OK) {
                // Location Services enabled successfully, proceed with Login flow
                checkForLocationSettings();

            } else {
                // Handle Enable Location Services request denied error
                Toast.makeText(this, R.string.enable_location_settings,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
