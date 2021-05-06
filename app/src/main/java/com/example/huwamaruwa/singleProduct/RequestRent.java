package com.example.huwamaruwa.singleProduct;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.bumptech.glide.Glide;
import com.example.huwamaruwa.Models.Product;
import com.example.huwamaruwa.Models.RequestRentModel;
import com.example.huwamaruwa.PaymentOption;
import com.example.huwamaruwa.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class RequestRent extends AppCompatActivity {

    Button btn,btnPay,btnReq;
    ImageView imgMain;
    TextView textView,txtTitle,txtDescription,txtPrice,txtTotal,txtDeposit;
    Product product;
    RequestRentModel requestRent;
    EditText edtAddress,edtContactNumber;
    MaterialDatePicker<Pair<Long, Long>> pickerRange;
    private String duration;
    private int dateDif = 0;
    private double deposit = 0;
    private double total = 0;
    public static final String RS="RS. ";
     DatabaseReference dbRef;
    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_rent);

        btn = findViewById(R.id.date_range);
        btnPay = findViewById(R.id.btnRequestRent_form_pay);
        btnReq = findViewById(R.id.btnRequestRent_form_send_request);

        textView = findViewById(R.id.date_view);
        txtPrice = findViewById(R.id.txtRequest_rent_form_price);
        txtDescription = findViewById(R.id.txtRequest_rent_form_description);
        txtTitle = findViewById(R.id.txtRequest_rent_form_title);
        txtTotal = findViewById(R.id.txtRequestRent__form_total);
        txtDeposit = findViewById(R.id.txtRequestRent__form_deposit);

        edtAddress = findViewById(R.id.edtRequesrtRent_form_address);
        edtContactNumber = findViewById(R.id.edtRequesrtRent_form_contactnumber);

        imgMain = findViewById(R.id.imgRequest_rent_form_main);



        product = getIntent().getParcelableExtra(PremiumProduct.REQUEST_RENT_TAG);


        Glide.with(this).load(product.getImages1()).into(imgMain);
        txtTitle.setText(product.getTitle());
        txtDescription.setText(product.getDescription());
        txtPrice.setText(RS.concat(String.valueOf(product.getPrice())).concat(product.isPerHour() ?" /Per Hour" : " /Per Day") );
        if (product.getIsPremium()){
            btnReq.setVisibility(View.GONE);
        }else{
            btnPay.setVisibility(View.GONE);
        }

    btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            MaterialDatePicker.Builder<Pair<Long, Long>> builderRange = MaterialDatePicker.Builder.dateRangePicker();
            builderRange.setCalendarConstraints(limitRange().build());
            builderRange.setTitleText("Select Date Range");
             pickerRange = builderRange.build();
            pickerRange.show(getSupportFragmentManager(), pickerRange.toString());
            pickerRange.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                @Override
                public void onPositiveButtonClick(Pair<Long, Long> selection) {
                    textView.setText(pickerRange.getHeaderText());
                    Long startDate = selection.first;
                    Long endDate = selection.second;

                    dateDif = (int) TimeUnit.MILLISECONDS.toDays(endDate - startDate)+1;
                    if (product.isPerHour()){
                        total = Double.parseDouble(String.valueOf(product.getPrice())) * (24 * dateDif);
                    }else {
                        total = Double.parseDouble(String.valueOf(product.getPrice())) * dateDif;
                    }
                    txtTotal.setText("Rs .".concat(Double.toString(total)));

                     deposit = total * 10/100.0;
                    txtDeposit.setText("Rs .".concat(Double.toString(deposit)));
                }
            });
        }
    });
    btnPay.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dbRef = FirebaseDatabase.getInstance().getReference().child("RequestRent");
            try {
                if (TextUtils.isEmpty(edtAddress.getText())){
                    Toast.makeText(RequestRent.this, "Address Required", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(edtContactNumber.getText())){
                    Toast.makeText(RequestRent.this, "Contact number Required", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(textView.getText())){
                    Toast.makeText(RequestRent.this, "Duration required", Toast.LENGTH_SHORT).show();
                }else {
                    requestRent = new RequestRentModel();
                    requestRent.setAddress(edtAddress.getText().toString());
                    requestRent.setContactNumber(edtContactNumber.getText().toString());
                    requestRent.setDuration(textView.getText().toString());
                    requestRent.setInitialDeposit(deposit);
                    requestRent.setTotal(total);
                    requestRent.setIsPremium(String.valueOf(product.getIsPremium()));
                    requestRent.setProductId(product.getId());
                    requestRent.setDateDif(Integer.toString(dateDif));
                    requestRent.setStatus("Pending");
                    String id = dbRef.push().getKey();
                    requestRent.setId(id);
                    dbRef.child(requestRent.getId()).setValue(requestRent).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(RequestRent.this, "Data added successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), PaymentOption.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RequestRent.this, "Request failed", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }catch (Exception e){
                Toast.makeText(RequestRent.this, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    });
    }


    private CalendarConstraints.Builder limitRange() {

        CalendarConstraints.Builder constraintsBuilderRange = new CalendarConstraints.Builder();

        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();

        int year = Calendar.getInstance().get(Calendar.YEAR);
        int startMonth = Calendar.getInstance().get(Calendar.MONTH);
        int startDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int endMonth = startMonth;
        int endDate = startDate + product.getMaxRentalTime()+1;


        calendarStart.set(year, startMonth , startDate );
        calendarEnd.set(year, endMonth , endDate);

        long minDate = calendarStart.getTimeInMillis();
        long maxDate = calendarEnd.getTimeInMillis();


        constraintsBuilderRange.setStart(minDate);
        constraintsBuilderRange.setEnd(maxDate);
        constraintsBuilderRange.setValidator(new RangeValidator(minDate, maxDate));

        return constraintsBuilderRange;
    }




    static class RangeValidator implements CalendarConstraints.DateValidator {

        long minDate, maxDate;

        RangeValidator(long minDate, long maxDate) {
            this.minDate = minDate;
            this.maxDate = maxDate;
        }

        RangeValidator(Parcel parcel) {
            minDate = parcel.readLong();
            maxDate = parcel.readLong();
        }

        @Override
        public boolean isValid(long date) {
            return !(minDate > date || maxDate < date);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeLong(minDate);
            parcel.writeLong(maxDate);
        }


        public static final Parcelable.Creator<RangeValidator> CREATOR = new Parcelable.Creator<RangeValidator>() {

            @Override
            public RangeValidator createFromParcel(Parcel parcel) {
                return new RangeValidator(parcel);
            }

            @Override
            public RangeValidator[] newArray(int size) {
                return new RangeValidator[size];
            }
        };


    }
}