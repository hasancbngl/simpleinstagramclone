package com.cobanogluhasan.instagramclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText usernameEditText;
    EditText passwordEditText;
    Button signUpButton;
    TextView switchButton;
    boolean signUpModeActive;
    RelativeLayout myLayout;
    ImageView imageView;
    private static final String TAG = "MainActivity";


    private void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1) {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();

            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri imageUri=data.getData();

        if(requestCode == 1 && resultCode==RESULT_OK && data!=null) {

            try {
                Log.i(TAG, "onActivityResult: " + "picture added a-hole");
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);

                byte[] byteArray = stream.toByteArray();

                ParseFile file = new ParseFile("image.png",byteArray);
                ParseObject object = new ParseObject("Image");
                object.put("image", file);

                object.put("username", ParseUser.getCurrentUser().getUsername());
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null) {
                            Toast.makeText(MainActivity.this, "Image is shared", Toast.LENGTH_SHORT).show();
                        } else {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "There has been issue while uploading!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onClick(View v) {
        if(v.getId() ==R.id.switchButton) {
            if(signUpModeActive) {
                signUpModeActive=false;
                switchButton.setText("or,Sign Up");
                signUpButton.setText("Log In");

            }
            else {

                signUpModeActive=true;
                switchButton.setText("or,Log In");
                signUpButton.setText("Sign Up");

            }
        }

        else if(v.getId() ==R.id.imageView || v.getId()==R.id.myLayout ) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.share_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.share) {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
            else getPhoto();

        }

        else if(item.getItemId()==R.id.logout) {
            ParseUser.logOut();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpView();

        signUpModeActive = true;
      //  ParseUser.logOut();


        switchButton.setOnClickListener(this);
        myLayout.setOnClickListener(this);
        imageView.setOnClickListener(this);

        if (ParseUser.getCurrentUser()!=null) {
            userList();
            Log.i(TAG, "onCreate: "+ "signed in: user=>" + ParseUser.getCurrentUser().getUsername());
        }
        else {

            Log.i(TAG, "onCreate: " + "you're not signed in "); }



        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if(signUpModeActive) {
                    if (username.equals("") || password.equals("")) {
                        Toast.makeText(MainActivity.this, "please enter username and password", Toast.LENGTH_SHORT).show();
                    } else {signUpParse(username, password);}
                }  else {
                    logInParse(username,password);
                }
                }
        });

        passwordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    signUpButton.callOnClick();
                }

                return false;
            }
        });


        ParseAnalytics.trackAppOpenedInBackground(getIntent());

    }

    private void userList() {
        Intent intent = new Intent(getApplicationContext(), UsersListActivity.class);
        startActivity(intent);
    }

    private void setUpView() {
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        switchButton = findViewById(R.id.switchButton);
        myLayout = findViewById(R.id.myLayout);
        imageView = findViewById(R.id.imageView);

    }

    private void signUpParse(String username,String password) {
        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(username);
        parseUser.setPassword(password);
        parseUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null) {
                    Log.i(TAG, "done: " + "you signed up");
                    userList();
                }
                else{
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            }
        });

    }


    private void logInParse(String username,String password) {
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null && user != null) {
                        userList();
                        Log.i(TAG, "done: " + "login successfull");
                    }

                    else {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }



}