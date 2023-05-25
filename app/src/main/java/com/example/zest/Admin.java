package com.example.zest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class Admin extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin);
    }

    public void gotoCreateMatch(View view) {
        Intent intent = new Intent(Admin.this, AddSportActivity.class);
        startActivity(intent);
    }

    public void gotoUpdate(View view) {
        Intent intent2 = new Intent(Admin.this, PollAdmin.class);
        startActivity(intent2);
    }
    public void goBack(View view) {
        Intent intent3 = new Intent(Admin.this, HomeActivity.class);
        startActivity(intent3);
    }

}
