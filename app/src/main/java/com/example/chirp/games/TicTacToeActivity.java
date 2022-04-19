package com.example.chirp.games;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chirp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TicTacToeActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private String myEmail;
    private static String playerSymbol;
    private EditText otherUserEmailField;
    private static int activePlayer = 1;
    private ArrayList<Integer> playerOneMoves = new ArrayList<Integer>();
    private ArrayList<Integer> playerTwoMoves = new ArrayList<Integer>();
    private String gameId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        myEmail = firebaseUser.getEmail();
        otherUserEmailField = findViewById(R.id.otherUserEmailField);
        databaseReference.child("GameUsers").child(alterEmail(myEmail)).child("Request").setValue(firebaseUser.getUid());
        IncomingRequests();
    }

    public void btnClick(View view){

        Button buttonSelected = (Button)view;
        int btnId = 0;
        switch(buttonSelected.getId()){
            case R.id.btn1:
                btnId = 1;
                break;
            case R.id.btn2:
                btnId = 2;
                break;
            case R.id.btn3:
                btnId = 3;
                break;
            case R.id.btn4:
                btnId = 4;
                break;
            case R.id.btn5:
                btnId = 5;
                break;
            case R.id.btn6:
                btnId = 6;
                break;
            case R.id.btn7:
                btnId = 7;
                break;
            case R.id.btn8:
                btnId = 8;
                break;
            case R.id.btn9:
                btnId = 9;
                break;
        }

        databaseReference.child("Game").child(gameId).child(String.valueOf(btnId)).setValue(alterEmail(myEmail));
    }

    public void btnRequestEvent(View view){
        String otherUserEmail = alterEmail(otherUserEmailField.getText().toString());
        databaseReference.child("GameUsers").child(otherUserEmail).child("Request").push().setValue(alterEmail(myEmail));
        playerMoveListener(alterEmail(myEmail)+otherUserEmail);
        TicTacToeActivity.playerSymbol="X";
    }

    public void btnAcceptEvent(View view){
        String otherUserEmail = alterEmail(otherUserEmailField.getText().toString());
        databaseReference.child("GameUsers").child(otherUserEmail).child("Request").push().setValue(alterEmail(myEmail));
        playerMoveListener(otherUserEmail+alterEmail(myEmail));
        TicTacToeActivity.playerSymbol="O";
    }

    public void playerMoveListener(String gameId){
        this.gameId = gameId;
        databaseReference.child("Game").removeValue();
        databaseReference.child("Game").child(gameId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try{
                            playerOneMoves.clear();
                            playerTwoMoves.clear();

                            for (DataSnapshot datum : snapshot.getChildren()) {
                                if (!(datum.getValue().toString()).equals(alterEmail(myEmail))){
                                    if(TicTacToeActivity.playerSymbol=="X"){
                                        TicTacToeActivity.activePlayer = 1;
                                    }else{
                                        TicTacToeActivity.activePlayer = 2;
                                    }
                                }else{
                                    if(TicTacToeActivity.playerSymbol=="X"){
                                        TicTacToeActivity.activePlayer = 2;
                                    }else{
                                        TicTacToeActivity.activePlayer = 1;
                                    }

                                }

                                autoPlay(Integer.parseInt(datum.getKey()));
                            }
                        }catch (Exception ex){
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void autoPlay(int btnId) {
        Button selectedBtn = null;
        switch (btnId) {
            case 2:
                selectedBtn = findViewById(R.id.btn2);
                break;
            case 3:
                selectedBtn = findViewById(R.id.btn3);
                break;
            case 4:
                selectedBtn = findViewById(R.id.btn4);
                break;
            case 5:
                selectedBtn = findViewById(R.id.btn5);
                break;
            case 6:
                selectedBtn = findViewById(R.id.btn6);
                break;
            case 7:
                selectedBtn = findViewById(R.id.btn7);
                break;
            case 8:
                selectedBtn = findViewById(R.id.btn8);
                break;
            case 9:
                selectedBtn = findViewById(R.id.btn9);
                break;
            default:
                selectedBtn = findViewById(R.id.btn1);
                break;
        }
        playGame(btnId, selectedBtn);
    }

    private void playGame(int btnId, Button selectedBtn) {
        if(TicTacToeActivity.activePlayer == 2){
            Log.d("playGame:player1", Integer.toString(btnId));
            selectedBtn.setText("X");
            selectedBtn.setBackgroundColor(Color.GRAY);
            playerOneMoves.add(btnId);
            TicTacToeActivity.activePlayer = 2;
        }else if(TicTacToeActivity.activePlayer == 1){
            selectedBtn.setText("O");
            selectedBtn.setBackgroundColor(Color.GRAY);
            playerTwoMoves.add(btnId);
            TicTacToeActivity.activePlayer = 1;
        }
        selectedBtn.setEnabled(false);
        Log.d("BtnId", Integer.toString(btnId));
        checkWinner();
    }

    private void checkWinner() {
        int winner = -1;

        // row 1
        if(playerOneMoves.contains(1) && playerOneMoves.contains(2) && playerOneMoves.contains(3)){
            winner=1;
        }
        if(playerTwoMoves.contains(1) && playerTwoMoves.contains(2) && playerTwoMoves.contains(3)){
            winner=2;
        }

        // row 2
        if(playerOneMoves.contains(4) && playerOneMoves.contains(5) && playerOneMoves.contains(6)){
            winner=1;
        }
        if(playerTwoMoves.contains(4) && playerTwoMoves.contains(5) && playerTwoMoves.contains(6)){
            winner=2;
        }

        // row 3
        if(playerOneMoves.contains(7) && playerOneMoves.contains(8) && playerOneMoves.contains(9)){
            winner=1;
        }
        if(playerTwoMoves.contains(7) && playerTwoMoves.contains(8) && playerTwoMoves.contains(9)){
            winner=2;
        }

        // col 1
        if(playerOneMoves.contains(1) && playerOneMoves.contains(4) && playerOneMoves.contains(7)){
            winner=1;
        }
        if(playerTwoMoves.contains(1) && playerTwoMoves.contains(4) && playerTwoMoves.contains(7)){
            winner=2;
        }

        // col 2
        if(playerOneMoves.contains(2) && playerOneMoves.contains(5) && playerOneMoves.contains(8)){
            winner=1;
        }
        if(playerTwoMoves.contains(2) && playerTwoMoves.contains(5) && playerTwoMoves.contains(8)){
            winner=2;
        }

        // col 3
        if(playerOneMoves.contains(3) && playerOneMoves.contains(6) && playerOneMoves.contains(9)){
            winner=1;
        }
        if(playerTwoMoves.contains(3) && playerTwoMoves.contains(6) && playerTwoMoves.contains(9)){
            winner=2;
        }

        //diagonal r->l
        if(playerOneMoves.contains(1) && playerOneMoves.contains(5) && playerOneMoves.contains(9)){
            winner=1;
        }
        if(playerTwoMoves.contains(1) && playerTwoMoves.contains(5) && playerTwoMoves.contains(9)){
            winner=2;
        }

        //diagonal l->r
        if(playerOneMoves.contains(3) && playerOneMoves.contains(5) && playerOneMoves.contains(7)){
            winner=1;
        }
        if(playerTwoMoves.contains(3) && playerTwoMoves.contains(5) && playerTwoMoves.contains(7)){
            winner=2;
        }

        if( winner != -1){

            if (winner==1){
                Toast.makeText(TicTacToeActivity.this, "Player X wins the game", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(TicTacToeActivity.this, "Player O wins the game", Toast.LENGTH_SHORT).show();
                finish();

            }

        }
    }

    public void IncomingRequests(){
        databaseReference.child("GameUsers").child(alterEmail(myEmail)).child("Request")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try{
                            Map<String, String> data = (HashMap<String,String>)snapshot.getValue();
                            if(data != null){
                                String value = "";
                                for (String key : data.keySet()) {
                                    value = (String) data.get(key);
                                    otherUserEmailField.setText(value);
                                    databaseReference.child("GameUsers").child(alterEmail(myEmail)).child("Request").setValue(true);
                                    break;
                                }
                            }

                        }catch (Exception ex){

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    public static String alterEmail(String str){
        String newString = "";
        for(int i=0; i<str.length(); i++){
            if (str.charAt(i) != '.'){
                newString += str.charAt(i);
            }else{
                newString += "(dot)";
            }
        }
        return newString;
    }

}
