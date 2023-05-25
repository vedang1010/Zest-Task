package com.example.zest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class AddSportActivity extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://zest-task-default-rtdb.firebaseio.com/");

    private LinearLayout optionsLayout;
    private Button addOptionButton;
    private Button createPollButton;

    public Integer generatePollId() {
        Random random = new Random();
        int minRange = 100_000;  // Minimum 6-digit number
        int maxRange = 999_999;  // Maximum 6-digit number
        return random.nextInt(maxRange - minRange + 1) + minRange;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_sport);

        optionsLayout = findViewById(R.id.optionsLayout);
        addOptionButton = findViewById(R.id.addOptionButton);
        createPollButton = findViewById(R.id.createPollButton);

        addOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOption();
            }
        });

        createPollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPoll();
            }
        });
    }

    private void addOption() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout optionLayout = new LinearLayout(this);
        optionLayout.setLayoutParams(layoutParams);
        optionLayout.setOrientation(LinearLayout.HORIZONTAL);

        EditText optionEditText = new EditText(this);
        optionEditText.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        optionEditText.setHint("Enter Team name");

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsLayout.removeView(optionLayout);
            }
        });

        optionLayout.addView(optionEditText);
        optionLayout.addView(deleteButton);

        optionsLayout.addView(optionLayout);
    }

    private void createPoll() {
        String question = ((EditText) findViewById(R.id.questionEditText)).getText().toString();
        String question2 = ((EditText) findViewById(R.id.questionEditText2)).getText().toString();
        int optionCount = optionsLayout.getChildCount();
        int check = 0;

        for (int i = 0; i < optionCount; i++) {
            LinearLayout optionLayout = (LinearLayout) optionsLayout.getChildAt(i);
            EditText optionEditText = (EditText) optionLayout.getChildAt(0);
            String option = optionEditText.getText().toString();
            if (option.isEmpty()) {
                check = 1;
                break;
            }
        }

        if (check == 1) {
            Toast.makeText(AddSportActivity.this, "Fill all Team name boxes or Delete them", Toast.LENGTH_SHORT).show();
        } else if (optionCount < 2) {
            Toast.makeText(AddSportActivity.this, "Add at least 2 Teams", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseReference sportReference = databaseReference.child("Sport").child(question);
            String matchNode = String.format("Match %s", question2);
            DatabaseReference matchReference = sportReference.child(matchNode);
            int pollId = generatePollId();
            // Set pollId or other properties if needed

            for (int i = 0; i < optionCount; i++) {
                LinearLayout optionLayout = (LinearLayout) optionsLayout.getChildAt(i);
                EditText optionEditText = (EditText) optionLayout.getChildAt(0);
                String option = optionEditText.getText().toString();
                String teamNode = String.format("Team %d", i + 1);
                int score = 0;
                DatabaseReference teamReference = matchReference.child(teamNode);
                teamReference.child("name").setValue(option);
                teamReference.child("score").setValue(score);
            }

            Toast.makeText(AddSportActivity.this, "Match Created successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddSportActivity.this, PollAdmin.class));
            finish();
        }
    }
}
