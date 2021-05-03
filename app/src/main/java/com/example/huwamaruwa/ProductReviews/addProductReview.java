package com.example.huwamaruwa.ProductReviews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.huwamaruwa.Models.productReview;
import com.example.huwamaruwa.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class addProductReview extends AppCompatActivity {

    //Declare variables
    TextView seller, product;
    EditText comments;
    Button done, cancel;
    RatingBar quality, usability, price;
    ImageView thumbnail;
    productReview pr;
    DatabaseReference dbfProduct, dbfReview;

    String productID;  //Get this from intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_review);

        //Initialize components
        product = (TextView) findViewById(R.id.txtproduct);
        seller = (TextView) findViewById(R.id.txtseller);
        comments = (EditText) findViewById(R.id.editTextProductComments);
        done = (Button) findViewById(R.id.btnDone);
        cancel = (Button) findViewById(R.id.btnCancel);
        quality = (RatingBar) findViewById(R.id.pQualityRatingBar);
        usability = (RatingBar) findViewById(R.id.pUsabilityRatingBar);
        price = (RatingBar) findViewById(R.id.pPriceRatingBar);
        thumbnail = (ImageView) findViewById(R.id.productThumbnail);

        pr = new productReview();


    }

    public void onResume() {
        super.onResume();

        //Display product details
        productID = "MZi84P5g9N1mXLXXbbL";
        dbfProduct = FirebaseDatabase.getInstance().getReference().child("Product").child(productID);

        //Get data from product
        dbfProduct.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void Save(View view) {

        //Get product ID from intent


    }


}