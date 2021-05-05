package com.example.huwamaruwa;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.huwamaruwa.Home.Customer_care_fragment;
import com.example.huwamaruwa.Home.Home_fragment;
import com.example.huwamaruwa.RentalRequests.PremiumProductRentalRequestFragment;
import com.example.huwamaruwa.addProduct.AddNewItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //define variables
    int clicker;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Fragment fragment;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;
    StorageReference storageReference;
    FloatingActionButton floatingActionButton;
    FloatingActionButton floatingActionButton_add;
    FloatingActionButton floatingActionButton_req;
    Animation rotateOpenAnim;
    Animation rotateCloseAnim;
    Animation fromBottomAnim;
    Animation toBottomAnim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get values by id
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        clicker = 0;
        floatingActionButton = findViewById(R.id.floating_add_product);
        floatingActionButton_add = findViewById(R.id.floating_add_button);
        floatingActionButton_req = findViewById(R.id.floating_buyer_req_button);
        rotateOpenAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_open_floating_anim);
        rotateCloseAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_closefloating_anim);
        fromBottomAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.from_bottom_floating_anim);
        toBottomAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.to_bottom_floating_anim);
        //set app name to toolbar
       // setSupportActionBar(toolbar);

        //set toggle event
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //set navigation view clickable
        navigationView.setNavigationItemSelectedListener(this);
    }
    //set when click back then close the nav drawer


    @Override
    protected void onResume() {
        super.onResume();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisibility(clicker);
//                Intent intent = new Intent(getApplicationContext(), AddNewItem.class);
//                startActivity(intent);
            }
        });
    }

    private void setVisibility(int cli) {
        if (cli == 0){
            floatingActionButton_add.setAnimation(fromBottomAnim);
            floatingActionButton_req.setAnimation(fromBottomAnim);

            floatingActionButton_add.setVisibility(View.VISIBLE);
            floatingActionButton_req.setVisibility(View.VISIBLE);
            clicker = 1;
            floatingActionButton.setAnimation(rotateOpenAnim);
        }else{

            floatingActionButton_add.setAnimation(toBottomAnim);
            floatingActionButton_req.setAnimation(toBottomAnim);
            floatingActionButton.setAnimation(rotateCloseAnim);
            floatingActionButton_add.setVisibility(View.INVISIBLE);
            floatingActionButton_req.setVisibility(View.INVISIBLE);
            clicker = 0;
        }
    }


    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.nav_home:
                fragment = new Home_fragment();
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentDefault,fragment);
                fragmentTransaction.commit();
                break;
            case R.id.nav_customer_care:
                fragment = new Customer_care_fragment();
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentDefault,fragment);
                fragmentTransaction.commit();
                break;
            case R.id.admin_Rental_requests:
                fragment = new PremiumProductRentalRequestFragment();
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentDefault,fragment);
                fragmentTransaction.commit();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}