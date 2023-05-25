package com.example.zest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class PollAdmin extends AppCompatActivity {

    private Spinner sportSpinner;
    private Spinner matchSpinner;
    private TextView team1NameTextView;
    private TextView team1ScoreTextView;
    private TextView team2NameTextView;
    private TextView team2ScoreTextView;
    private Button increaseTeam1Button;
    private Button decreaseTeam1Button;
    private Button increaseTeam2Button;
    private Button decreaseTeam2Button;
    private Button updateScoreButton;

    private DatabaseReference databaseReference;
    private List<String> sportList;
    private ArrayAdapter<String> sportAdapter;
    private List<String> matchList;
    private ArrayAdapter<String> matchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_admin);

        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://zest-task-default-rtdb.firebaseio.com/");

        sportSpinner = findViewById(R.id.sportSpinner);
        matchSpinner = findViewById(R.id.matchSpinner);
        team1NameTextView = findViewById(R.id.team1NameTextView);
        team1ScoreTextView = findViewById(R.id.team1ScoreTextView);
        team2NameTextView = findViewById(R.id.team2NameTextView);
        team2ScoreTextView = findViewById(R.id.team2ScoreTextView);
        increaseTeam1Button = findViewById(R.id.increaseTeam1Button);
        decreaseTeam1Button = findViewById(R.id.decreaseTeam1Button);
        increaseTeam2Button = findViewById(R.id.increaseTeam2Button);
        decreaseTeam2Button = findViewById(R.id.decreaseTeam2Button);
        updateScoreButton = findViewById(R.id.updateScoreButton);

        // Initialize sport and match spinners
        sportList = new ArrayList<>();
        sportAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sportList);
        sportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sportSpinner.setAdapter(sportAdapter);

        matchList = new ArrayList<>();
        matchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, matchList);
        matchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        matchSpinner.setAdapter(matchAdapter);

        // Fetch sports from the database
        fetchSports();

        // Set listener for sport spinner selection
        sportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSport = sportList.get(position);
                fetchMatches(selectedSport);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set listener for match spinner selection
        matchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMatch = matchList.get(position);
                updateTeamNames(selectedMatch);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set click listeners for score buttons
        increaseTeam1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseScore(team1ScoreTextView);
            }
        });

        decreaseTeam1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseScore(team1ScoreTextView);
            }
        });

        increaseTeam2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseScore(team2ScoreTextView);
            }
        });

        decreaseTeam2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseScore(team2ScoreTextView);
            }
        });

        // Set click listener for update score button
        updateScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateScoreInDatabase();
            }
        });
    }

    private void fetchSports() {
        databaseReference.child("Sport").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sportList.clear();
                for (DataSnapshot sportSnapshot : snapshot.getChildren()) {
                    String sport = sportSnapshot.getKey();
                    sportList.add(sport);
                }
                sportAdapter.notifyDataSetChanged();

                if (!sportList.isEmpty()) {
                    String selectedSport = sportList.get(0);
                    fetchMatches(selectedSport);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PollAdmin.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMatches(String sport) {
        databaseReference.child("Sport").child(sport).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                matchList.clear();
                for (DataSnapshot matchSnapshot : snapshot.getChildren()) {
                    String match = matchSnapshot.getKey();
                    matchList.add(match);
                }
                matchAdapter.notifyDataSetChanged();

                if (!matchList.isEmpty()) {
                    String selectedMatch = matchList.get(0);
                    updateTeamNames(selectedMatch);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PollAdmin.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTeamNames(String match) {
        String selectedSport = sportSpinner.getSelectedItem().toString();
        databaseReference.child("Sport").child(selectedSport).child(match).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Team 1") && snapshot.hasChild("Team 2")) {
                    String team1Name = snapshot.child("Team 1").child("name").getValue(String.class);
                    String team2Name = snapshot.child("Team 2").child("name").getValue(String.class);
                    team1NameTextView.setText(team1Name);
                    team2NameTextView.setText(team2Name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PollAdmin.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void increaseScore(TextView scoreTextView) {
        int currentScore = Integer.parseInt(scoreTextView.getText().toString());
        int newScore = currentScore + 1;
        scoreTextView.setText(String.valueOf(newScore));
    }

    private void decreaseScore(TextView scoreTextView) {
        int currentScore = Integer.parseInt(scoreTextView.getText().toString());
        if (currentScore > 0) {
            int newScore = currentScore - 1;
            scoreTextView.setText(String.valueOf(newScore));
        }
    }

    private void updateScoreInDatabase() {
        String selectedSport = sportSpinner.getSelectedItem().toString();
        String selectedMatch = matchSpinner.getSelectedItem().toString();
        String team1Score = team1ScoreTextView.getText().toString();
        String team2Score = team2ScoreTextView.getText().toString();

        databaseReference.child("Sport").child(selectedSport).child(selectedMatch).child("Team 1").child("score").setValue(team1Score);
        databaseReference.child("Sport").child(selectedSport).child(selectedMatch).child("Team 2").child("score").setValue(team2Score);

        Toast.makeText(PollAdmin.this, "Scores updated successfully.", Toast.LENGTH_SHORT).show();
    }

    public void gotoCreateMatch(View view) {
        Intent intent = new Intent(PollAdmin.this, Admin.class);
        startActivity(intent);
    }
}
