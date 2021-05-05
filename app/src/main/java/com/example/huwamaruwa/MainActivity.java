package com.example.huwamaruwa;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.huwamaruwa.R;
import com.example.huwamaruwa.addProduct.addProduct;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //define variables
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Fragment fragment;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;
    StorageReference storageReference;
    FloatingActionButton floatingActionButton;

    //Login Auth Variables
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    TextView loginName, loginSellerType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get values by id
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        //Login Ids
        loginName = findViewById(R.id.LoginName);
        loginSellerType = findViewById(R.id.LoginSellerType);


        floatingActionButton = findViewById(R.id.floating_add_product);
        //set app name to toolbar
       // setSupportActionBar(toolbar);

        //set toggle event
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //set navigation view clickable
        navigationView.setNavigationItemSelectedListener(this);

        currentUser = firebaseAuth.getInstance().getCurrentUser();
//        loginName.setText(currentUser.getEmail().toString());
        Query LoginQuery = FirebaseDatabase.getInstance().getReference("Users").child("Seller").orderByChild("userId").equalTo(currentUser.getUid());
        LoginQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    loginName.setText(snapshot.child("userId").getValue().toString());
                    loginSellerType.setText(snapshot.child("name").getValue().toString());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }
    //set when click back then close the nav drawer


    @Override
    protected void onResume() {
        super.onResume();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),addProduct.class);
                startActivity(intent);
            }
        });
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
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getInstance().getCurrentUser();
        // Check if user is signed in (non-null) and update UI accordingly.
        if(currentUser != null){
            Toast.makeText(getApplicationContext(), "User logged", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
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
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void LogoutMethod(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, Login.class));
    }
}