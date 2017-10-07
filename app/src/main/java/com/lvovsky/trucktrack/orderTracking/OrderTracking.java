package com.lvovsky.trucktrack.orderTracking;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hypertrack.lib.HyperTrack;
import com.hypertrack.lib.callbacks.HyperTrackCallback;
import com.hypertrack.lib.models.Action;
import com.hypertrack.lib.models.ActionParams;
import com.hypertrack.lib.models.ActionParamsBuilder;
import com.hypertrack.lib.models.ErrorResponse;
import com.hypertrack.lib.models.GeoJSONLocation;
import com.hypertrack.lib.models.Place;
import com.hypertrack.lib.models.SuccessResponse;
import com.lvovsky.trucktrack.LoginActivity;
import com.lvovsky.trucktrack.R;
import com.lvovsky.trucktrack.util.BaseActivity;
import com.lvovsky.trucktrack.util.SharedPreferenceStore;
import java.util.Date;
import java.util.UUID;

public class OrderTracking extends BaseActivity {

    private ProgressDialog mProgressDialog;
    private View.OnClickListener createActionButtonListener, completeActionButtonListener;
    private Button createActionButton, completeActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_tracking);

        SharedPreferenceStore.setUseCase(this,1);
        // Initialize UI Views
        initUIViews();
        /**
         * @IMPORTANT:
         * Implement Network call to fetch ORDERS/TRANSACTIONS for the Driver/Delivery Boy here.
         * Once the list of orders/transactions have been fetched, implement
         * assignAction and completeAction calls either with or without user interaction
         * depending on the specific requirements in the workflow of your business and your app.
         */
    }

    private void initUIViews() {
        // Initialize Click Listeners for Action buttons
        initClickListeners();

        // Initialize createAction Button
        createActionButton = (Button) findViewById(R.id.create_action);
        if (createActionButton != null)
            createActionButton.setOnClickListener(createActionButtonListener);

        // Initialize complete Button
        completeActionButton = (Button) findViewById(R.id.complete_action);
        if (completeActionButton != null)
            completeActionButton.setOnClickListener(completeActionButtonListener);

        //Set toolbar title
        initToolbar(getString(R.string.order_tracking), false);

    }

    private void initClickListeners() {
        // Click Listener for create Action Button
        createActionButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Show Progress Dialog
                mProgressDialog = new ProgressDialog(OrderTracking.this);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setMessage("Loading...");
                mProgressDialog.show();

                /**
                 * You can specify a lookup_id to Actions which maps to your internal id of the
                 * order that is going to be tracked. This will help you search for the order on
                 * HyperTrack dashboard, and get custom views for the specific order tracking.
                 *
                 * @NOTE: A randomly generated UUID is used as the lookup_id here. This will be
                 * the actual orderID in your case which will be fetched from either your server
                 * or generated locally.
                 */
                final String orderID = UUID.randomUUID().toString();

                /**
                 * Create Actions for current action for order tracking with a given OrderID
                 */
                createAction(orderID);
            }
        };

        // Click Listener for complete Action Button
        completeActionButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String actionID = SharedPreferenceStore.getActionID(OrderTracking.this);

                if (TextUtils.isEmpty(actionID)) {
                    Toast.makeText(OrderTracking.this, "Action Id is empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Complete action using actionId for the order tracking action that you created
                HyperTrack.completeAction(actionID);

                createActionButton.setEnabled(true);
                completeActionButton.setEnabled(false);

                Toast.makeText(OrderTracking.this, "Action Completed", Toast.LENGTH_SHORT).show();

            }

        };
    }

    /**
     * This method creates and assigns Delivery type actions to the current user on
     * HyperTrack API Server for the current orderID.
     *
     * @param orderID Internal order_id which maps to the current order tracking being performed
     */
    private void createAction(String orderID) {
        /**
         * Construct a place object for Action's expected place
         *
         * @NOTE: Either the coordinates of the expected delivering location
         * {@link Place#setLocation(GeoJSONLocation)} or it's address
         * {@link Place#setAddress(String)} can be used to construct
         * the expected place for the Action.
         */
        Place expectedPlace = new Place()
                .setAddress("HyperTrack, Vasant Vihar")
                .setName("HyperTrack");

        /**
         * Create ActionParams object specifying the Delivery Type Action
         * parameters including ExpectedPlace, ExpectedAt time and Lookup_id.
         */
        //Added 2 hours to current time;
        Date expectedTime = new Date(new Date().getTime() + 2*60*60*1000);
        ActionParams createActionParams = new ActionParamsBuilder()
                .setExpectedPlace(expectedPlace)
                .setExpectedAt(expectedTime)
                .setType(Action.ACTION_TYPE_DELIVERY)
                .setLookupId(orderID)
                .build();

        /**
         * Call createAndAssignAction to assign Delivery action to the current user configured
         * in the SDK using the ActionParams created above.
         */
        HyperTrack.createAndAssignAction(createActionParams, new HyperTrackCallback() {
            @Override
            public void onSuccess(@NonNull SuccessResponse response) {

                // Handle createAndAssignAction API success here
                Action action = (Action) response.getResponseObject();

                /**
                 * The Delivery Action just created has the tracking url which can be shared
                 * with your customers.
                 * This will enable the customer to live track the Delivery Boy / Driver.
                 *
                 * @NOTE You can now share this tracking_url with your customers via an SMS
                 * or via your Customer app using in-app notifications.
                 */
                String trackingUrl = action.getTrackingURL();

                onCreateAndAssignActionSuccess(action);

                Toast.makeText(OrderTracking.this, "Action Created Successfully.",
                        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(@NonNull ErrorResponse errorResponse) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }

                Toast.makeText(OrderTracking.this, "Action assingment failed: " +
                                errorResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onCreateAndAssignActionSuccess(Action action) {
        if(mProgressDialog != null )
            mProgressDialog.cancel();

        SharedPreferenceStore.setActionID(OrderTracking.this, action.getId());

        //Disable Create Action Button and enable complete Action Button
        createActionButton.setEnabled(false);
        completeActionButton.setEnabled(true);

        //Uncomment this code so that action ID will copy in clipboard
        ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).
                setPrimaryClip(ClipData.newPlainText("ActionID", action.getId()));

    }

    /**
     * This method is called when Driver/Delivery Boy clicks on LOGOUT button in the toolbar.
     * On Logout, HyperTrack's stopTracking API is called to stop tracking the Driver/Delivery Boy
     * Note that this method is linked with the menu file (menu_main.xml)
     * using this menu item's onClick attribute. So no need to invoke this
     * method or handle logout button's click listener explicitly.
     *
     * @param menuItem
     */
    public void onLogoutClicked(MenuItem menuItem) {
        Toast.makeText(OrderTracking.this, R.string.main_logout_success_msg, Toast.LENGTH_SHORT).show();

        // Stop tracking a user
        HyperTrack.stopTracking();

        // Clear Driver/Delivery Boy Session here
        SharedPreferenceStore.clearIDs(this);

        // Proceed to LoginActivity for a fresh Login
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        loginIntent.putExtra("use_case",1);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
