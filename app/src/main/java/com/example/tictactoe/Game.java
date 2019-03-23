package com.example.tictactoe;

public class Game {

    public boolean gameInProgress;
    public String currentTurn;
    public int currentMove;
    public String currentLetter;

    //dafult constructor required for call to datasnapshot
    public Game(){

    }
    public Game(String currentTurn){
        this.currentTurn = currentTurn;
        currentLetter = "X";
        gameInProgress = true;
        currentMove = -1;
    }
}
