package com.example.tictactoe;

public class User {
    public String myId;
    public String opponentId;
    public String name;
    public String email;
    public String opponentEmail;
    public boolean request;
    public String accepted;
    public boolean currentlyPlaying;
    public Game myGame;

    public User(){

    }

    public User(String name,String email,String id){

        this.name = name;
        this.email = email;
        this.myId = id;
        opponentId = "";
        opponentEmail = "";
        accepted = "";
        request = false;
        myGame = null;
        currentlyPlaying = false;


    }
}
