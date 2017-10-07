package com.lvovsky.trucktrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.hypertrack.usecases.R;

/**
 * Created by Aman Jain on 18/05/17.
 */

public class UseCaseActivity extends AppCompatActivity implements View.OnClickListener {

    Button orderTracking, mileageTracking, locationBasedAssignment, workForceMonitoring;
    private int USE_CASE = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_case);

        initUI();
    }

    //Initialize view for use cases
    private void initUI(){

        orderTracking = (Button) findViewById(R.id.order_tracking);
        mileageTracking = (Button) findViewById(R.id.mileage_tracking);
        locationBasedAssignment = (Button) findViewById(R.id.location_based_assignment);
        workForceMonitoring = (Button) findViewById(R.id.work_force_monitoring);

        orderTracking.setOnClickListener(this);
        mileageTracking.setOnClickListener(this);
        locationBasedAssignment.setOnClickListener(this);
        workForceMonitoring.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

            if(v == orderTracking){
                USE_CASE = 1;
            }
            else if(v == mileageTracking){
                USE_CASE = 2;
            }
            else if(v == locationBasedAssignment){
                USE_CASE = 3;
            }
            else if(v == workForceMonitoring){
                USE_CASE = 4;
            }

        //Start Login Activity with the chosen use case.
        Intent intent = new Intent(UseCaseActivity.this,LoginActivity.class);
        intent.putExtra("use_case",USE_CASE);
        startActivity(intent);
    }
}
