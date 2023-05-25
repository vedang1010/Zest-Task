package com.example.zest;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowPollsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private DatabaseReference databaseReference;
    private TextView team1ScoreTextView;
    private TextView team2ScoreTextView;
    private Spinner sportSpinner;
    private Spinner matchSpinner;

    private String selectedSport;
    private String selectedMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showpolls);

        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://zest-task-default-rtdb.firebaseio.com/");

        team1ScoreTextView = findViewById(R.id.team1ScoreTextView);
        team2ScoreTextView = findViewById(R.id.team2ScoreTextView);
        sportSpinner = findViewById(R.id.sportSpinner);
        matchSpinner = findViewById(R.id.matchSpinner);

        // Set up the sport spinner
        ArrayAdapter<String> sportAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        sportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sportSpinner.setAdapter(sportAdapter);
        sportSpinner.setOnItemSelectedListener(this);

        // Set up the match spinner
        ArrayAdapter<String> matchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        matchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        matchSpinner.setAdapter(matchAdapter);
        matchSpinner.setOnItemSelectedListener(this);

        loadSports();
    }

    private void loadSports() {
        // Retrieve the list of sports from the Firebase database
        databaseReference.child("Sport").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> sportList = new ArrayList<>();
                for (DataSnapshot sportSnapshot : snapshot.getChildren()) {
                    String sport = sportSnapshot.getKey();
                    sportList.add(sport);
                }

                // Update the sport spinner adapter with the sport list
                ArrayAdapter<String> sportAdapter = (ArrayAdapter<String>) sportSpinner.getAdapter();
                sportAdapter.clear();
                sportAdapter.addAll(sportList);
                sportAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if necessary
            }
        });
    }

    private void loadMatches(String selectedSport) {
        // Retrieve the list of matches for the selected sport from the Firebase database
        databaseReference.child("Sport").child(selectedSport).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> matchList = new ArrayList<>();
                for (DataSnapshot matchSnapshot : snapshot.getChildren()) {
                    String match = matchSnapshot.getKey();
                    matchList.add(match);
                }

                // Update the match spinner adapter with the match list
                ArrayAdapter<String> matchAdapter = (ArrayAdapter<String>) matchSpinner.getAdapter();
                matchAdapter.clear();
                matchAdapter.addAll(matchList);
                matchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if necessary
            }
        });
    }

    private void updateScores() {
        // Retrieve the scores for the selected sport and match from the Firebase database
        databaseReference.child("Sport").child(selectedSport).child(selectedMatch).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String team1Score = snapshot.child("Team 1").child("score").getValue(String.class);
                String team2Score = snapshot.child("Team 2").child("score").getValue(String.class);

                // Update the score TextViews with the retrieved scores
                team1ScoreTextView.setText(team1Score);
                team2ScoreTextView.setText(team2Score);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if necessary
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int viewId = parent.getId();
        if (viewId == R.id.sportSpinner) {
            // A sport is selected
            selectedSport = parent.getItemAtPosition(position).toString();
            loadMatches(selectedSport);
        } else if (viewId == R.id.matchSpinner) {
            // A match is selected
            selectedMatch = parent.getItemAtPosition(position).toString();
            updateScores();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }
}
