package com.example.chirp.password;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chirp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

/**
 * The below code allows the user to reset their password if they
 * were to forget it.
 * This activity can be accessed without logging in and facilitates the
 * sending of a link to the users email.
 * Through this link the user can regain access to their account.
 */
public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextView tvMessage;
    private LinearLayout llResetPassword, llMessage;
    private Button btnRetry;
    private View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        etEmail = findViewById(R.id.etEmail);
        tvMessage = findViewById(R.id.tvMessage);
        llMessage = findViewById(R.id.llMessage);
        llResetPassword = findViewById(R.id.llResetPassword);

        btnRetry = findViewById(R.id.btnRetry);
        progressBar = findViewById(R.id.progressBarScreen);
    }

    /**
     * The below code executes when the user clicks the
     * reset password button.
     * @param view
     */
    public  void btnResetPasswordClick(View view){
        final String email = etEmail.getText().toString().trim();

        if(email.equals(""))
        {
            etEmail.setError(getString(R.string.enter_email));
        }
        else
        {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    progressBar.setVisibility(View.GONE);
                    llResetPassword.setVisibility(View.GONE);
                    llMessage.setVisibility(View.VISIBLE);

                    if(task.isSuccessful())
                    {
                        tvMessage.setText(getString(R.string.reset_password_instructions, email));
                        new CountDownTimer(60000, 1000){

                            @Override
                            public void onTick(long l) {
                                btnRetry.setText(getString( R.string.resend_timer,  String.valueOf(l/1000)) );
                                btnRetry.setOnClickListener(null);
                            }

                            @Override
                            public void onFinish() {
                                btnRetry.setText(R.string.retry);

                                btnRetry.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        llResetPassword.setVisibility(View.VISIBLE);
                                        llMessage.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }.start();
                    }
                    else
                    {
                        tvMessage.setText(getString(R.string.email_sent_failed, task.getException()));
                        btnRetry.setText(R.string.retry);

                        btnRetry.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                llResetPassword.setVisibility(View.VISIBLE);
                                llMessage.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * The below code executes when the user clicks the close
     * button.
     * @param view
     */
    public void btnCloseClick(View view)
    {
        finish();
    }
}