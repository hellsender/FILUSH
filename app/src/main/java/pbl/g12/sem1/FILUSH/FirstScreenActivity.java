package pbl.g12.sem1.FILUSH;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;

import android.widget.Button;

/**
 * A login screen that offers login via email/password.
 */

public class FirstScreenActivity extends AppCompatActivity {

	private static final int RC_LOGIN = 1;
	private int REQUEST_SIGNUP = 2;
	Button button;
	Button buttonsignup;

	@Override
	public void onBackPressed() {
		// disable going back to the MainActivity
		moveTaskToBack(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_firstscreen);


		// Capture button clicks
		button = findViewById(R.id.email_login_button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
				startActivityForResult(intent, REQUEST_SIGNUP);
			}


		});

		// Capture singup botton clicks
		buttonsignup = findViewById(R.id.email_signup_button);
		buttonsignup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
				startActivityForResult(intent, RC_LOGIN);


			}
		});


	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_SIGNUP) {
			if (resultCode == RESULT_OK || resultCode == RC_LOGIN) {
				finish();
			}
		}
	}
}





