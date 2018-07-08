package com.example.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.example.myapplication.fragment.ConnectionFragment;

import utils.Log;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onCreate");
        setContentView(R.layout.activity_main);
        showFragment();
    }

    private void showFragment() {
        Fragment fragment = ConnectionFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, fragment, ConnectionFragment.FRAGMENT_TAG);
        transaction.commit();
    }

}
