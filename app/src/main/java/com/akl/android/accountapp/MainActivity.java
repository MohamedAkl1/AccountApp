package com.akl.android.accountapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    EditText nameEditText, numberEditText, emailEditText, addressEditText;
    Button submitButton;
    TextView nameTextView, numberTextView, emailTextView, addressTextView;
    Context mContext;
    DatabaseReference mDatabaseReference;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(Objects.requireNonNull(mFirebaseAuth.getUid()))){
                    displayUserData();
                }else {
                    takeDataFromUser();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void displayUserData() {
        setContentView(R.layout.activity_display_data);

        nameTextView = findViewById(R.id.name_textview);
        numberTextView = findViewById(R.id.number_textview);
        emailTextView = findViewById(R.id.email_textview);
        addressTextView = findViewById(R.id.address_textview);

        mDatabaseReference.child(Objects.requireNonNull(mFirebaseAuth.getUid())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                displayValues(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayValues(User user) {
        nameTextView.setText(user.getName());
        numberTextView.setText(user.getNumber());
        emailTextView.setText(user.getEmail());
        addressTextView.setText(user.getAddress());
    }

    private void takeDataFromUser() {
        setContentView(R.layout.activity_get_user_data);

        nameEditText = findViewById(R.id.user_name);
        numberEditText = findViewById(R.id.user_number);
        emailEditText = findViewById(R.id.user_email);
        addressEditText = findViewById(R.id.user_address);
        submitButton = findViewById(R.id.submit_button);

        mContext = getApplicationContext();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString().trim();
                String number = numberEditText.getText().toString().trim().replaceFirst("^0+(?!$)","");
                String email = emailEditText.getText().toString().trim().toLowerCase();
                String address = addressEditText.getText().toString().trim();
                if(name.length() == 0 || !name.matches("[a-zA-Z]+")){
                    Toast.makeText(mContext,"please Enter your name with no numbers or special characters", Toast.LENGTH_SHORT).show();
                }else if (number.length() == 0){
                    Toast.makeText(mContext,"Please enter a valid number",Toast.LENGTH_SHORT).show();
                }else if(!EmailValidator.getInstance().isValid(email) || email.length() == 0){
                    Toast.makeText(mContext,"Please enter a valid Email",Toast.LENGTH_SHORT).show();
                }else if(address.length() == 0){
                    Toast.makeText(mContext,"Please enter an Address",Toast.LENGTH_SHORT).show();
                }else{
                    User user = new User(name,number,address,email);
                    String uid = Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid();
                    mDatabaseReference.child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(mContext,"Successfully uploaded",Toast.LENGTH_SHORT).show();
                            displayUserData();
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                mFirebaseAuth.signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(this,LauncherActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
