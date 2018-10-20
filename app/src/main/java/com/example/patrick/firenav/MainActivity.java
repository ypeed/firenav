package com.example.patrick.firenav;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "EmailPassword";

    private TextView mTextMessage;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        Log.i("EmailPassword", "onCreate");

        setContentView(R.layout.activity_main);

        loadFragment(new HomeFragment());

        // mTextMessage = (TextView) findViewById(R.id.message);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.i("EmailPassword", "no user");
            this.signIn("nejc.buric1@student.um.si", "nejc123");
        } else {
            Log.i("EmailPassword", "user ready");
            this.updateUI(currentUser);
        }
    }

    private void signIn(String email, String password) {

        Log.i("EmailPassword", "signIn");
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.i(TAG, "signinoncomplete");
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            Toast.makeText(MainActivity.this, "Authentication successful.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            updateUI(null);
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // mTextMessage.setText(user.getEmail());
            Toast.makeText(MainActivity.this, user.getEmail(),
                    Toast.LENGTH_SHORT).show();
        } else {
            // mTextMessage.setText("no user");
        }

    }

    private boolean loadFragment(Fragment fragment){
        if(fragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.fragment_home:
                fragment = new HomeFragment();
                break;

            case R.id.fragment_pridejo:
                fragment = new PridejoFragment();
                break;

            case R.id.fragment_nepridejo:
                fragment = new NepridejoFragment();
                break;

        }

        return loadFragment(fragment);
    }
}
