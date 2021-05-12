package com.example.huwamaruwa.ProductReviews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.huwamaruwa.Models.ProductReviews;
import com.example.huwamaruwa.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddProductReview extends AppCompatActivity {

    private static final String INVALID_DATA = "INVALID_DATA";
    private static final String DB_ERROR = "DATABASE_ERROR";
    //Declare variables
    TextView seller, product;
    EditText comments;
    Button done, cancel;
    RatingBar quality, usability, price;
    ImageView thumbnail;
    ProductReviews pr;
    DatabaseReference dbfProduct, dbfReview, dbfSeller;
    String productID, sellerID, imageURL, buyerComments, reviewerID, date;
    float overallRating, qRating, uRating, pRating;
    FirebaseUser user;


    /**
     * Get buyer id from intents
     * Get product ID from intents
     */


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
        usability = (RatingBar) findViewById(R.id.premium_product_rating_bar);
        price = (RatingBar) findViewById(R.id.pPriceRatingBar);
        thumbnail = (ImageView) findViewById(R.id.productThumbnail);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reviewerID = user.getUid();

        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        pr = new ProductReviews();
    }

    public void onResume() {
        super.onResume();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Display product details
        productID = getIntent().getStringExtra("ProductID");
        //productID = "-M_Rn2Sq6QWGxFcgS2y0";
        dbfProduct = FirebaseDatabase.getInstance().getReference().child("Product").child(productID);

        //Get data from product
        dbfProduct.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    //Product details for card view
                    product.setText(snapshot.child("title").getValue().toString());
                    sellerID = snapshot.child("sellerId").getValue().toString();
                    imageURL = snapshot.child("images1").getValue().toString();
                    Log.d("URL", imageURL);

                    //Set Image
                    Glide.with(getApplicationContext()).load(imageURL).centerCrop().placeholder(R.drawable.ic_launcher_background).into(thumbnail);
                }

                //Seller details for card view
                dbfSeller = FirebaseDatabase.getInstance().getReference().child("Users").child(sellerID);
                dbfSeller.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            seller.setText("By " + snapshot.child("name").getValue().toString());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("Error", "DB Cancelled");
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error", "DB Cancelled");
            }
        });
    }


    public void Save(View view) {

        dbfReview = FirebaseDatabase.getInstance().getReference().child("ProductReviews");

        //Get ratings
        qRating = Float.parseFloat(String.valueOf(quality.getRating()));
        uRating = Float.parseFloat(String.valueOf(usability.getRating()));
        pRating = Float.parseFloat(String.valueOf(price.getRating()));

        //Validation
        if (qRating == 0 || uRating == 0 || pRating == 0) {
            Toast.makeText(getApplicationContext(), "Ratings Can't be empty", Toast.LENGTH_SHORT).show();
        } else {


            //Get comments
            buyerComments = comments.getText().toString().trim();
            Log.d("BComments", buyerComments);


            //Calculate overall rating
            float overall = (float) ((qRating + uRating + pRating) / 3.0);
            overallRating = (float) (Math.round(overall * 2) / 2.0);

            Log.d("OverallRating", String.valueOf(overallRating));


            try {
                //date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                //Set vales in model object
                pr.setReviewerID(reviewerID);
                Log.d("Rev", pr.getReviewerID());
                pr.setProductID(productID);
                pr.setQualityRating(qRating);
                pr.setUsabilityRating(uRating);
                pr.setPriceRating(pRating);
                pr.setAverageRating(overallRating);
                pr.setComment(buyerComments);
                pr.setDate(date);

                //Push to database
                String id = dbfReview.push().getKey();
                pr.setID(id);
                dbfReview.child(id).setValue(pr);

                Toast.makeText(getApplicationContext(), "Review Added", Toast.LENGTH_SHORT).show();


            } catch (Exception e) {
                Log.d(DB_ERROR, "DATA SAVE FAILED - " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Review Not Added", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}