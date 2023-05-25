package com.example.zest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
    }
    public void gotoAdmin(View view) {
        Intent intent = new Intent(this, AdminLogin.class);
        startActivity(intent);
    }

    public void gotoShowPolls(View view) {
        Intent intent2 = new Intent(this, ShowPollsActivity.class);
        startActivity(intent2);
    }
    public void gotoLOGIN(View view) {
        Intent intent3 = new Intent(this, MainActivity.class);
        startActivity(intent3);
    }

//    public void gotoProfile(View view) {
//        Intent intent3 = new Intent(this, Profile.class);
//        startActivity(intent3);
//    }
}
//    @Override
//    public void onBackPressed() {
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

