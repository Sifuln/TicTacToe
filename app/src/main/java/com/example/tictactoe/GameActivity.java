package com.example.tictactoe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = GameActivity.class.getSimpleName();

    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mDatabaseRef;

    User loggedInUser;
    private String userId;

    TextView playerText,turn;

    Button buttonGame1,buttonGame2,buttonGame3,
            buttonGame4,buttonGame5,buttonGame6,
            buttonGame7,buttonGame8,buttonGame9,
            choosePlayerScreen;
    ImageView imageView1,imageView2,imageView3,imageView4,
            imageView5,imageView6,imageView7,imageView8,
            imageView9,imageView10,boardView;
    //represnts buttons clicked
    int[] gameBoard;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        turn = (TextView) findViewById(R.id.turnTextView);
        playerText = (TextView) findViewById(R.id.playerTextView);

        buttonGame1 = (Button) findViewById(R.id.gameButton1);
        buttonGame2 = (Button) findViewById(R.id.gameButton2);
        buttonGame3 = (Button) findViewById(R.id.gameButton3);
        buttonGame4 = (Button) findViewById(R.id.gameButton4);
        buttonGame5 = (Button) findViewById(R.id.gameButton5);
        buttonGame6 = (Button) findViewById(R.id.gameButton6);
        buttonGame7 = (Button) findViewById(R.id.gameButton7);
        buttonGame8 = (Button) findViewById(R.id.gameButton8);
        buttonGame9 = (Button) findViewById(R.id.gameButton9);
        choosePlayerScreen = (Button) findViewById(R.id.choosePlayerMenu);

        imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView3 = (ImageView) findViewById(R.id.imageView3);
        imageView4 = (ImageView) findViewById(R.id.imageView4);
        imageView5 = (ImageView) findViewById(R.id.imageView5);
        imageView6 = (ImageView) findViewById(R.id.imageView6);
        imageView7 = (ImageView) findViewById(R.id.imageView8);
        imageView9 = (ImageView) findViewById(R.id.imageView9);
        imageView10 =(ImageView) findViewById(R.id.imageView10);
        boardView = (ImageView) findViewById(R.id.tictactoeView);

        //draws the game board
        drawGameBoard();

        //initializes the game board array to 0
        gameBoard = new int[9];
        for(int i=0;i<gameBoard.length;i++){
            gameBoard[i] = 0;
        }

        buttonGame1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameTurn(0);
            }
        });

        buttonGame2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameTurn(1);
            }
        });
        buttonGame3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameTurn(2);
            }
        });
        buttonGame4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameTurn(3);
            }
        });
        buttonGame5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameTurn(4);
            }
        });
        buttonGame6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameTurn(5);
            }
        });
        buttonGame7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameTurn(6);
            }
        });
        buttonGame8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameTurn(7);
            }
        });
        buttonGame9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameTurn(8);
            }
        });
        //send the user back to main activity
        choosePlayerScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameActivity.this,ChoosePlayerActivity.class));
            }
        });
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mDatabaseRef = mFirebaseInstance.getReference("users");

        final FirebaseUser currentUserLoggedIn = FirebaseAuth.getInstance().getCurrentUser();

        userId = currentUserLoggedIn.getUid();

        addUserChangeListener();


    }
    //user data change listener
    private void addUserChangeListener(){
        mDatabaseRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                loggedInUser = user;

                playerText.setText(loggedInUser.email+"vs. "+loggedInUser.opponentEmail);
                if (user.myGame.gameInProgress == false && user.currentlyPlaying == true){
                    if (loggedInUser.myGame.currentLetter.equals("X")){
                        //populates the gameBoard array,swapping x with y
                        gameBoard[user.myGame.currentMove]=2;
                        printMove(user.myGame.currentMove,"O");
                    }else{
                        gameBoard[user.myGame.currentMove] = 1;
                        printMove(user.myGame.currentMove,"X");
                    }
                    //remove all of the buttons
                    removeAllButtons();

                    //make sure we draw a the line
                    if(checkGameWon() == 0){
                        checkTie();

                        //initialize game values
                        mDatabaseRef.child(loggedInUser.myId).child("myGame").child("gameInProgress").setValue(false);
                        mDatabaseRef.child(loggedInUser.myId).child("myGame").child("currentlyPlaying").setValue(false);

                        mDatabaseRef.child(loggedInUser.opponentId).child("myGame").child("gameInProgress").setValue(false);
                        mDatabaseRef.child(loggedInUser.opponentId).child("myGame").child("currentlyPlaying").setValue(false);
                        //initialize each user opponents
                        mDatabaseRef.child(loggedInUser.myId).child("opponentId").setValue("");
                        mDatabaseRef.child(loggedInUser.myId).child("opponentEmail").setValue("");

                        mDatabaseRef.child(loggedInUser.opponentId).child("opponentId").setValue("");
                        mDatabaseRef.child(loggedInUser.opponentId).child("opponentEmail").setValue("");

                        Toast.makeText(GameActivity.this,"Game Is Over",Toast.LENGTH_LONG).show();


                    }
                    //if my turn,draw board and enable buttons
                    else if(user.myGame.currentTurn.equals(user.myId)){
                        turn.setText("Your Turn to make a move");
                        //make sure there was a move
                        if (user.myGame.currentMove != -1){
                            if (loggedInUser.myGame.currentLetter.equals("X")){
                                //populates the gameboard array
                                gameBoard[user.myGame.currentMove] = 2;
                                printMove(user.myGame.currentMove,"O");
                            }else{
                                gameBoard[user.myGame.currentMove] = 1;
                                printMove(user.myGame.currentMove,"X");
                            }
                            //re-enable buttons
                            updateButtons();
                        }
                    }else//not my turn
                    {
                        turn.setText("Waiting for other player");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //handles the playes turn
    void gameTurn(int move){
        int gameWon = -1;
        boolean gameTie = false;
        //set the board to a move and print it
        if (loggedInUser.myGame.currentLetter.equals("X")){
            gameBoard [move] = 1;
            printMove(move,"X");
        }
        else{
            gameBoard[move] = 2;
            printMove(move,"O");
        }
        //Update the status of the buttons graying out the ones that were played
        updateButtons();

        //checks if the game has been won or not
        gameWon = checkGameWon();

        //check of the game has been tied
        gameTie = checkTie();

        Game game = new Game(loggedInUser.opponentId);
        game.currentMove = move;

        if (loggedInUser.myGame.currentLetter.equals("X")){
            game.currentLetter = "O";
        }else {
            game.currentLetter = "X";
        }

        //if the game hasn't been won and the game hasnt been tied the other playesr turn is made
        if(gameWon != 0 && !gameTie){
            game.gameInProgress = true;
        }
        //if game won
        else if (gameWon != 0){
            //remove all of the buttons
            removeAllButtons();
            game.gameInProgress = false;
        }
        else{
            playerText.setText("The game is tie");
            turn.setText("No one wins");
            game.gameInProgress = false;
        }

        //set data
        mDatabaseRef.child(loggedInUser.myId).child("myGame").setValue(game);
        mDatabaseRef.child(loggedInUser.opponentId).child("myGame").setValue(game);
    }
    //Update status of buttons
    void updateButtons(){
        if (gameBoard[0] != 0){
            buttonGame1.setVisibility(View.INVISIBLE);
        }else {
            buttonGame1.setVisibility(View.VISIBLE);
        }
        if (gameBoard[1] != 0){
            buttonGame2.setVisibility(View.INVISIBLE);
        }else
        {
            buttonGame2.setVisibility(View.VISIBLE);
        }

        if (gameBoard[2] != 0){
            buttonGame3.setVisibility(View.INVISIBLE);
        }else {
            buttonGame3.setVisibility(View.VISIBLE);
        }

        if (gameBoard[3] != 0){
            buttonGame4.setVisibility(View.INVISIBLE);
        }else{
            buttonGame4.setVisibility(View.VISIBLE);
        }

        if (gameBoard[4] != 0){
            buttonGame5.setVisibility(View.INVISIBLE);
        }else{
            buttonGame5.setVisibility(View.VISIBLE);
        }

        if (gameBoard[5] != 0){
            buttonGame6.setVisibility(View.INVISIBLE);
        }else{
            buttonGame6.setVisibility(View.VISIBLE);
        }

        if (gameBoard[6] != 0){
            buttonGame7.setVisibility(View.INVISIBLE);
        }else{
            buttonGame7.setVisibility(View.VISIBLE);
        }

        if (gameBoard[7] != 0){
            buttonGame8.setVisibility(View.INVISIBLE);
        }else{
            buttonGame8.setVisibility(View.VISIBLE);
        }
        if (gameBoard[8] != 0){
            buttonGame9.setVisibility(View.INVISIBLE);
        }else{
            buttonGame9.setVisibility(View.VISIBLE);
        }
    }
    void removeAllButtons(){
        buttonGame1.setVisibility(View.INVISIBLE);
        buttonGame2.setVisibility(View.INVISIBLE);
        buttonGame3.setVisibility(View.INVISIBLE);
        buttonGame4.setVisibility(View.INVISIBLE);
        buttonGame5.setVisibility(View.INVISIBLE);
        buttonGame6.setVisibility(View.INVISIBLE);
        buttonGame7.setVisibility(View.INVISIBLE);
        buttonGame8.setVisibility(View.INVISIBLE);
        buttonGame9.setVisibility(View.INVISIBLE);
    }
    //Draw Code
    //draws an X at a specified location
    void drawX(int startx1,int starty1,int endx1,int endy1,int startx2,int starty2,int endx2,int endy2,ImageView imageView){
        Bitmap bitmap = Bitmap.createBitmap(getWindowManager().getDefaultDisplay().getWidth(),getWindowManager().getDefaultDisplay().getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        imageView.setImageBitmap(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(10);
        canvas.drawLine(startx1,starty1,endx1,endy1,paint);
        canvas.drawLine(startx2,starty2,endx2,endy2,paint);
        imageView.setVisibility(View.VISIBLE);

        //animates the drawing to fadein
        Animation animation = new AlphaAnimation(0.0f,1.0f);
        animation.setDuration(2000);
        imageView.startAnimation(animation);
    }
    //draws an 0 at a specified location
    void drawO(float x,int y,ImageView imageView){
        Bitmap bitmap = Bitmap.createBitmap(getWindowManager().getDefaultDisplay().getWidth(),getWindowManager().getDefaultDisplay().getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        imageView.setImageBitmap(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(x,y,100,paint);
        imageView.setVisibility(View.VISIBLE);

        //animates the drawing to fade int
        Animation animation = new AlphaAnimation(0.0f,1.0f);
        animation.setDuration(2000);
        imageView.startAnimation(animation);
    }
    //draws a line at a specified location,when the game has won
    void drawLine(int startx,int starty,int endx,int endy,ImageView imageView){
        imageView.setVisibility(View.VISIBLE);
        Bitmap bitmap = Bitmap.createBitmap(getWindowManager().getDefaultDisplay().getWidth(),getWindowManager().getDefaultDisplay().getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        imageView.setImageBitmap(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(20);
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawLine(startx,starty,endx,endy,paint);

        //animates the drawing to fade in
        Animation animation = new AlphaAnimation(0.0f,1.0f);
        animation.setDuration(4000);
        imageView.startAnimation(animation);
    }
    //checks to see if the game has been won,if it has it draws a line over the winning squares
    int checkGameWon(){
        int sx,sy,ex,ey;
        Point size = new Point();
        //get the size of current window
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        //win first row
        if (gameBoard[0] != 0 && gameBoard[0] == gameBoard[1] && gameBoard[0] == gameBoard[2]){
            sx = 0;
            sy =(int) (height * .30);

            ex = width;
            ey = (int) (height * .30);
            drawLine(sx,sy,ex,ey,imageView10);
            return gameBoard[0];
        }
        //win second row
        else if (gameBoard[3] != 0 && gameBoard[3] == gameBoard[4] && gameBoard[3] == gameBoard[5]){
            sx = 0;
            sy = (int) (height * .54);
            ex = width;
            ey = (int) (height * .54);

            drawLine(sx,sy,ex,ey,imageView10);
            return gameBoard[3];
        }
        //win third row
        else if (gameBoard[6] != 0 && gameBoard[6] == gameBoard[7] && gameBoard[6] == gameBoard[8]){
            sx = 0;
            sy = (int) (height * .77);

            ex = width;
            ey = (int) (height * .77);

            drawLine(sx,sy,ex,ey,imageView10);
            return gameBoard[6];
        }
        //win first colum
        else if (gameBoard[0] != 0 && gameBoard[0] == gameBoard[3] && gameBoard[0] == gameBoard[6]){
            sx = (int) (width *.15);
            sy = (int) (height * .18);
            ex = (int) (width * .15);
            ey=(int) (height * .89);

            drawLine(sx,sy,ex,sy,imageView10);
            return gameBoard[0];
        }
        //win second colum
        else if (gameBoard[1] != 0 && gameBoard[1] == gameBoard[4] && gameBoard[1] == gameBoard[7]){
            sx = (int) (width * .50);
            sy = (int) (height * .18);

            ex = (int) (width * .50);
            ey = (int) (height * .89);

            drawLine(sx,sy,ex,ey,imageView10);
            return gameBoard[1];
        }
        //win third colum
        else  if (gameBoard[2] != 0 && gameBoard[2] == gameBoard[5] && gameBoard[2] == gameBoard[8]){
            sx = (int) (width * .85);
            sy = (int) (height * .18);

            ex = (int) (width * .85);
            ey = (int) (height * .89);

            drawLine(sx,sy,ex,ey,imageView10);
            return  gameBoard[2];
        }
        else{
            //game not won
            return 0;
        }
    }
    //check to see if the game is a tie
    boolean checkTie(){
        for (int aGameBoard : gameBoard){
            if (aGameBoard == 0)
                return false;
        }
        return true;
    }
    //prints move that was played
    void printMove(int move,String character){
        int x,y;
        //gets the size of the display window
        Point size = new Point();
        Display display = getWindowManager().getDefaultDisplay();

        display.getSize(size);
        int width = size.x;
        int height = size.y;
        //switch statement for each move
        switch (move){
            case 0:
                x = (int) (width * .15);
                y = (int) (height * .30);
                if (character.equals("X"))
                    drawX(x-100,y-100,x+100,y+100,x+100,y-100,x-100,y+100,imageView1);
                else
                    drawO(x,y,imageView1);
                break;
            case 1:
                x = (int) (width * .50);
                y = (int) (height * .30);
                if (character.equals("X"))
                    drawX(x-100,y-100,x+100,y+100,x+100,y-100,x-100,y+100,imageView2);
                else
                    drawO(x,y,imageView2);
                break;
            case 2:
                x = (int) (width * .85);
                y = (int) (height * .30);
                if (character.equals("X"))
                    drawX(x-100,y-100,x+100,y+100,x+100,y-100,x-100,y+100,imageView3);
                else
                    drawO(x,y,imageView3);
                break;
            case 3:
                x = (int) (width * .15);
                y =(int) (height * .54);
                if (character.equals("X"))
                    drawX(x-100,y-100,x+100,y+100,x+100,y-100,x-100,y+100,imageView4);
                else
                    drawO(x,y,imageView4);
                break;
            case 4:
                x = (int) (width *.50);
                y = (int) (height * .54);
                if (character.equals("X"))
                    drawX(x-100,y-100,x+100,y+100,x+100,y-100,x-100,y+100,imageView5);
                else
                    drawO(x,y,imageView5);
                break;
            case 5:
                x = (int) (width * .85);
                y = (int) (height * .54);
                if (character.equals("X"))
                    drawX(x-100,y-100,x+100,y+100,x+100,y-100,x-100,y+100,imageView6);
                else
                    drawO(x,y,imageView6);
                break;
            case 6:
                x = (int) (width * .15);
                y = (int) (height * .77);

                if (character.equals("X"))
                    drawX(x-100,y-100,x+100,y+100,x+100,y-100,x-100,y+100,imageView7);
                else
                    drawO(x,y,imageView7);
                break;
            case 7:
                x = (int) (width * .50);
                y = (int) (height * .77);
                if (character.equals("X"))
                    drawX(x-100,y-100,x+100,y+100,x+100,y-100,x-100,y+100,imageView8);
                else
                    drawO(x,y,imageView8);
                break;
            case 8:
                x =(int) (width * .85);
                y = (int) (height * .77);
                if (character.equals("X"))
                    drawX(x-100,y-100,x+100,y+100,x+100,y-100,x-100,y+100,imageView9);
                else
                    drawO(x,y,imageView9);
                break;
        }
    }
    //draws the gaem board on the screen
    private void drawGameBoard(){
        boardView.setVisibility(View.VISIBLE);
        Bitmap bitmap = Bitmap.createBitmap(getWindowManager().getDefaultDisplay().getWidth(),getWindowManager().getDefaultDisplay().getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        boardView.setImageBitmap(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(20);
        paint.setStyle(Paint.Style.STROKE);

        int sx,sy,ex,ey,x,y;
        Point size = new Point();
        Display display = getWindowManager().getDefaultDisplay();

        display.getSize(size);
        int width = size.x;
        int height = size.y;

        sx = 0;
        sy = (int) (height * .42);
        ex = width;
        ey = (int) (height * .42);

        canvas.drawLine(sx,sy,ex,ey,paint);

        sx = 0;
        sy = (int) (height * .65);
        ex = width;
        ey = (int) (height * .65);

        canvas.drawLine(sx,sy,ex,ey,paint);

        sx = (int) (width * .32);
        sy = (int) (height * .18);
        ex = (int) (width * .32);
        ey = (int) (height * .89);

        canvas.drawLine(sx,sy,ex,ey,paint);

        sx = (int) (width * .68);
        sy = (int) (height * .18);
        ex = (int) (width * .68);
        ey = (int) (height * .89);

        canvas.drawLine(sx,sy,ex,ey,paint);

        x = (int) (width * .07);
        y = (int) (height * .22);

        buttonGame1.setX(x);
        buttonGame1.setY(y);

        x = (int) (width * .38);
        y = (int) (height * .22);

        buttonGame2.setX(x);
        buttonGame2.setY(y);

        x = (int) (width * .69);
        y = (int) (height * .22);

        buttonGame3.setX(x);
        buttonGame3.setY(y);

        x = (int) (width * .07);
        y = (int) (height * .43);

        buttonGame4.setX(x);
        buttonGame4.setY(y);

        x = (int) (width * .38);
        y = (int) (height * .43);

        buttonGame5.setX(x);
        buttonGame5.setY(y);

        x = (int) (width * .69);
        y = (int) (height * .43);

        buttonGame6.setX(x);
        buttonGame6.setY(y);

        x = (int) (width * .07);
        y = (int) (height * .64);

        buttonGame7.setX(x);
        buttonGame7.setY(y);

        x = (int) (width * .38);
        y = (int) (height * .64);

        buttonGame8.setX(x);
        buttonGame8.setY(y);

        x = (int) (width * .69);
        y = (int) (height * .64);

        buttonGame9.setX(x);
        buttonGame9.setY(y);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       //Handle item selectioon
        switch (item.getItemId()){
            case R.id.logout:
                Toast.makeText(this,"Clicked Logoff",Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(GameActivity.this,MainActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
