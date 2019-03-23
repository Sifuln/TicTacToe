package com.example.tictactoe;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class ChoosePlayerActivity extends AppCompatActivity {
    private static final String TAG = ChoosePlayerActivity.class.getSimpleName();

    EditText editInviteEmail;
    Button buttonInvite;
    private ProgressBar progressBar;

    User currentOpponent;
    User loggedInUser;

    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_player);

        editInviteEmail = (EditText) findViewById(R.id.editName);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        buttonInvite = (Button) findViewById(R.id.buttonInvite);

        final ListView listView = (ListView) findViewById(R.id.myListView);

        ArrayList<User> arrayOfUsers = new ArrayList<User>();
        final UserAdapter adapter = new UserAdapter(this,arrayOfUsers);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentOpponent = (User) listView.getAdapter().getItem(position);
                editInviteEmail.setText(currentOpponent.email);
            }
        });

        //firebase instance
        mFirebaseInstance = FirebaseDatabase.getInstance();
        //get refer to user
        mDatabaseRef = mFirebaseInstance.getReference("users");
        Query allUsers = mDatabaseRef.orderByChild("name");
        final FirebaseUser currentUserLoggedIn = FirebaseAuth.getInstance().getCurrentUser();
        allUsers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);

                //get the current user from the database
                if (currentUserLoggedIn.getEmail().equals(user.email)){
                    loggedInUser = user;
                    //go to the game screen if a game is in progress
                    if (user.currentlyPlaying){
                        startActivity(new Intent(ChoosePlayerActivity.this,GameActivity.class));
                        finish();
                    }
                }


                //if the other user is not currently playing and they do not have a
                //current request,then they are a valid opponent to choose
                else if ((!user.currentlyPlaying && user.opponentId.isEmpty())){
                    adapter.add(user);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                String userId = dataSnapshot.getKey();
                //if the user is not currently playing and the do not have
                //current request then they are a valid opponent to choose
                //get the current user from the database
                if (!currentUserLoggedIn.getEmail().equals(user.email)){
                    if (user.currentlyPlaying || !user.opponentId.isEmpty()){
                        adapter.remove(user);
                    }
                }else{
                    //update your object
                    loggedInUser = user;

                    if (user.request = true){
                        showAcceptOrDenyInviteDialog();
                        user.request = false;
                        mDatabaseRef.child(userId).setValue(user);

                    }
                    else if(user.accepted.equals("true")){
                        //set values back is initial state and show button
                        progressBar.setVisibility(View.GONE);
                        buttonInvite.setEnabled(true);

                        mDatabaseRef.child(loggedInUser.myId).child("accepted").setValue("none");

                        //show dialog and go to game screen
                        showAcceptOrDenyStatusDialog(true);
                    }
                    else if (user.accepted.equals(false)){
                        //set values back to intial state and show button
                        progressBar.setVisibility(View.GONE);
                        mDatabaseRef.child(loggedInUser.myId).child("opponentId").setValue("");
                        mDatabaseRef.child(loggedInUser.myId).child("opponentEmail").setValue("");
                        mDatabaseRef.child(loggedInUser.myId).child("accepted").setValue("none");

                        //show dialog
                        showAcceptOrDenyStatusDialog(false);
                        buttonInvite.setEnabled(true);

                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                User user =dataSnapshot.getValue(User.class);
                if (!currentUserLoggedIn.getEmail().equals(user.email)){
                    adapter.remove(user);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showAcceptOrDenyStatusDialog(final boolean status){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        //setting dialog title
        alertDialog.setTitle("Game Invite Status");
        //setting dialog message
        if (status){
            alertDialog.setMessage("Your game with "+loggedInUser.opponentEmail+"has been accepted");
        }else{
            alertDialog.setMessage("Your game with"+loggedInUser.opponentEmail+"has been denied");
        }

        //Setting positive Yes button
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //navigate to game screen
                if (status){
                    startActivity(new Intent(ChoosePlayerActivity.this,GameActivity.class));
                }
            }
        });
        alertDialog.show();

    }
    private void showAcceptOrDenyInviteDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        //setting dialog title
        alertDialog.setTitle("Accept Game Invite..");

        //setting idalog Message
        alertDialog.setMessage("Would you like to play tic tac toe against"+loggedInUser.opponentEmail+"?");

        //set Positive Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //create game and go there
                Game game = new Game(loggedInUser.opponentId);

                mDatabaseRef.child(loggedInUser.opponentId).child("myGame").setValue(game);
                mDatabaseRef.child(loggedInUser.myId).child("myGame").setValue(game);

                //set game status for both player
                mDatabaseRef.child(loggedInUser.opponentId).child("currentlyPlaying").setValue(true);
                mDatabaseRef.child(loggedInUser.myId).child("currentlyPlaying").setValue(true);

                mDatabaseRef.child(loggedInUser.opponentId).child("accepted").setValue("true");

                //navigate to game screen
                startActivity(new Intent(ChoosePlayerActivity.this,GameActivity.class));
            }
        });
        //Setting Negative "NO" button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabaseRef.child(loggedInUser.myId).child("opponentId").child("");
                mDatabaseRef.child(loggedInUser.myId).child("opponentEmail").setValue("");
                mDatabaseRef.child(loggedInUser.opponentId).child("accepted").setValue("false");
                dialog.cancel();
            }
        });
        //showing alert dialog
        alertDialog.show();
    }
    public void onClickInvite(View view){
        if (currentOpponent != null){
            //set opponent id for selected user to invite and let them know they have an invite in database
            mDatabaseRef.child(currentOpponent.myId).child("opponentId").setValue(loggedInUser.myId);
            mDatabaseRef.child(currentOpponent.myId).child("opponentEmail").setValue(loggedInUser.email);
            mDatabaseRef.child(currentOpponent.myId).child("request").setValue(true);

            //set opponent id for current logged in user in database
            mDatabaseRef.child(loggedInUser.myId).child("opponentId").setValue(currentOpponent.myId);
            mDatabaseRef.child(loggedInUser.myId).child("opponentEmail").setValue(currentOpponent.email);
            mDatabaseRef.child(loggedInUser.myId).child("accepted").setValue("pending");

            progressBar.setVisibility(View.VISIBLE);
            buttonInvite.setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
   }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ChoosePlayerActivity.this,MainActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
