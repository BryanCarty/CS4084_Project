package com.example.chirp.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.chirp.Common.Util;
import com.example.chirp.MainActivity;
import com.example.chirp.MessageActivity;
import com.example.chirp.R;
import com.example.chirp.password.ResetPasswordActivity;
import com.example.chirp.signup.SignupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private String email, password;
    private View progressBar;
    private TextInputLayout passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        progressBar = findViewById(R.id.progressBarLayout);

        passwordInput = findViewById(R.id.passwordInput);

    }

    public void tvSignupClick(View v)
    {
        startActivity(new Intent(this, SignupActivity.class));
    }
    public void btnLoginClick(View v)
    {

        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();

        if (email.equals("") && password.equals("")){
            passwordInput.setEndIconVisible(false);
            etEmail.setError(getString(R.string.missing_email));
            etPassword.setError(getString(R.string.missing_password));
        }
        else if(email.equals(""))
        {
            etEmail.setError(getString(R.string.missing_email));
        }
        else if (password.equals(""))
        {
            passwordInput.setEndIconVisible(false);
            etPassword.setError(getString(R.string.missing_password));
        }
        else
        {
            if(Util.connectionAvailable(this)) {
                progressBar.setVisibility(View.VISIBLE);
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failed : " +
                                    task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else
            {
                startActivity(new Intent(LoginActivity.this, MessageActivity.class));
            }

        }


    }

    public  void tvResetPasswordClick(View view){
        startActivity(new Intent(LoginActivity.this , ResetPasswordActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser!=null)
        {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
}