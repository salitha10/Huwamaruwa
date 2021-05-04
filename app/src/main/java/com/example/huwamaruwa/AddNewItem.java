package com.example.huwamaruwa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.huwamaruwa.Models.Post;
import com.example.huwamaruwa.Progress.LoadingProgress;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter;

import java.text.DateFormat;
import java.util.ArrayList;

public class AddNewItem extends AppCompatActivity {

    AutoCompleteTextView autoCompleteTextCat , autoCompleteTextloc;
    EditText ProductName, rentFee, contact, description, deposit;


    private static final int CAMERA_REQEST = 1888;
    Button btnTakePhoto, btnGallery, btnPost;
    ArrayList<Uri> img_list;
    Post post;
    DatabaseReference dbRefe;
    StorageReference sdbRefe;
    LoadingProgress loadingProgress;
    String imgData[];
    RadioButton yes_radio,no_radio;
    int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_iteam);

        btnGallery = (Button) findViewById(R.id.btnGallery);
        btnPost = (Button) findViewById(R.id.btnPost);

        ProductName = (EditText) findViewById(R.id.ProductName1);
        rentFee = (EditText) findViewById(R.id.rentFee);
        contact = (EditText) findViewById(R.id.contact);
        description = (EditText) findViewById(R.id.description);
        deposit = (EditText) findViewById(R.id.deposit);


        img_list = new ArrayList<>();
        imgData = new String[4];
        loadingProgress = new LoadingProgress(AddNewItem.this);


        autoCompleteTextCat = findViewById(R.id.autoCompleteText1);
        autoCompleteTextloc = findViewById(R.id.autoCompleteText2);

        no_radio = findViewById(R.id.no_radio);
        yes_radio = findViewById(R.id.yes_radio);



        String[] category = {"Book", "Kitchenware", "Electronic", "Jewelleries", "Videography equipments",
                "Construction", "Catering equipments", "Camping equipments", "Health equipments",
                "Gaming equipment", "Costumes"};

        String[] Location = {"Colombo", "Kandy", "Galle", "Ampara", "Anuradhapura", "Badulla", "Batticaloa",
                "Gampaha", "Hambantota", "Jaffna", "Kalutara", "Kilinochchi", "Kurunegla", "Mannar", "Matale",
                "Matara", "Monaragala", "Mullative", "Nuwara Eliya", "Polonnaruwa", "Puttalam", "Ratnapura",
                "Trincomalee", "Vavuniya"};

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.dropdown_category, category);
        autoCompleteTextCat.setText(arrayAdapter.getItem(0).toString(), false);
        autoCompleteTextCat.setAdapter(arrayAdapter);

        ArrayAdapter arrayAdapter2 = new ArrayAdapter(this, R.layout.dropdown_category, Location);
        autoCompleteTextloc.setText(arrayAdapter2.getItem(0).toString(), false);
        autoCompleteTextloc.setAdapter(arrayAdapter2);


    }

    @Override
    protected void onResume() {
        super.onResume();
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();

            }
        });


        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbRefe = FirebaseDatabase.getInstance().getReference().child("Product");
                sdbRefe = FirebaseStorage.getInstance().getReference().child("Product Images");

                try {
                    if (TextUtils.isEmpty(ProductName.getText().toString().trim())){
                        Toast.makeText(AddNewItem.this, "Title Required", Toast.LENGTH_SHORT).show();
                    }else if (TextUtils.isEmpty(rentFee.getText().toString().trim())){
                        Toast.makeText(AddNewItem.this, "Price Required", Toast.LENGTH_SHORT).show();

                    }else if (TextUtils.isEmpty(description.getText().toString().trim())){
                        Toast.makeText(AddNewItem.this, "Description Required", Toast.LENGTH_SHORT).show();

                    }else if(img_list.isEmpty()){
                        Toast.makeText(AddNewItem.this, "Image Required", Toast.LENGTH_SHORT).show();
                    } else {

                        loadingProgress.startProgress();
                        imageUploader(0);

                    }
                }catch (Exception e){

                }





            }
        });


    }
    public void onRadioButton(View view) {

        boolean isChecked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.yes_radio:
                showMessage("Premium Item..");
                break;
            case R.id.no_radio:
                showMessage("Non Premium Item..");
                break;
        }

    }



    public void dataUploader(){
        post = new Post();
//        post.setCategoryID(autoCompleteTextCat.);
        int categoryID= Integer.parseInt(autoCompleteTextCat.getText().toString().trim());
        post.setDate(DateFormat.getDateInstance().getCalendar().getTime());
        post.setLocation(autoCompleteTextloc.getText().toString().trim());
        post.setTitle(ProductName.getText().toString().trim());
        post.setRentalFee(rentFee.getText().toString().trim());
        post.setContactNumber(contact.getText().toString().trim());
        post.setDeposit(deposit.getText().toString().trim());
        post.setDescription(description.getText().toString().trim());
        post.setImages1(imgData[0]);
        post.setImages2(imgData[1]);
        post.setImages3(imgData[2]);
        post.setImages4(imgData[3]);

        post.setPostId(dbRefe.push().getKey());
        dbRefe.child(post.getPostId()).setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddNewItem.this, "Successfully Added...", Toast.LENGTH_SHORT).show();
                loadingProgress.dismissProgress();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddNewItem.this, "Unsuccessfully Failed", Toast.LENGTH_SHORT).show();
                loadingProgress.dismissProgress();

            }
        });
    }

    private void imageUploader(int i) {

        if(i >= img_list.size()){
            dataUploader();
        } else{
            StorageReference storageReference  = sdbRefe.child(System.currentTimeMillis() +"."+ GetFileExtension(img_list.get(i)));
            int imgfinal = i;
            storageReference.putFile(img_list.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(AddNewItem.this, "image "+ (imgfinal +1)+" uploaded", Toast.LENGTH_SHORT).show();
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imgData[imgfinal] =  uri.toString();
                            imageUploader(imgfinal+1);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddNewItem.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            imageUploader(imgfinal+1);
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddNewItem.this, "image "+(imgfinal +1 )+" uploading Failed", Toast.LENGTH_SHORT).show();
                    imageUploader(imgfinal+1);
                }
            });

        }

    }

    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }




    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void imageChooser() {
        FishBun.with(this).setImageAdapter(new GlideAdapter()).setMaxCount(4).setRequestCode(100).startAlbum();
    }

//    private void captureImg(){
//        FishBun.with(this).setImageAdapter(new GlideAdapter()).setMaxCount(4).setCamera(true).startAlbum();
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {

            Toast.makeText(this, "Multiple Image selected", Toast.LENGTH_SHORT).show();
            img_list = data.getParcelableArrayListExtra(FishBun.INTENT_PATH);

            initRecycler();
        }
    }

    private void initRecycler() {
        RecyclerView recyclerView = findViewById(R.id.img_prev_recycler);
        NewAddAdapter postAdapter = new NewAddAdapter(this,img_list);
        recyclerView.setAdapter(postAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
    }
}