package pbl.g12.sem1.FILUSH;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A signup screen where users can signup for a personal account.
 */
public class SignUpActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>
{
	/**
	 * Id to identity RC_LOGIN intent request code.
	 */
	private static final int RC_LOGIN = 1;
	/**
	 * Id to identity READ_CONTACTS permission request.
	 */
	private static final int REQUEST_READ_CONTACTS = 0;
	private FirebaseAuth mAuth;
	private DatabaseReference mDatabase;

	// UI references.
	private AutoCompleteTextView mNameView;
	private AutoCompleteTextView mEmailView;
	private EditText mPasswordView;
	private Button _signupButton;
	private EditText mBirthdayView;
	//ADDED CHANGES


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_signup_personal);

		// Set up the signup form.
		mNameView = findViewById(R.id.p_signup_input_name);
		mNameView.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
			{
				if (id == EditorInfo.IME_ACTION_NEXT || id == EditorInfo.IME_NULL)
				{
					mEmailView.requestFocus();
					return true;
				}
				return false;
			}
		});
		mEmailView = findViewById(R.id.p_signup_input_email);
		mEmailView.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
			{
				if (id == EditorInfo.IME_ACTION_NEXT || id == EditorInfo.IME_NULL)
				{
					mPasswordView.requestFocus();
					return true;
				}
				return false;
			}
		});
		populateAutoComplete();

		mPasswordView = findViewById(R.id.p_signup_input_password);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
			{
				if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL)
				{
					attemptSignup();
					return true;
				}
				return false;
			}
		});


		mBirthdayView = findViewById(R.id.p_signup_birthdate);



		_signupButton = findViewById(R.id.p_signup_button);
		_signupButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				attemptSignup();
			}
		});

		TextView _loginLink = findViewById(R.id.link_login);
		_loginLink.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Finish the registration screen and return to the Login activity
				setResult(RC_LOGIN, null);
				finish();
			}
		});

		mDatabase = FirebaseDatabase.getInstance().getReference();
		mAuth = FirebaseAuth.getInstance();
	}

	private void populateAutoComplete()
	{
		if (!mayRequestContacts())
		{
			return;
		}

		getLoaderManager().initLoader(0, null, this);
	}

	private boolean mayRequestContacts()
	{
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
		{
			return true;
		}
		if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
		{
			return true;
		}
		if (shouldShowRequestPermissionRationale(READ_CONTACTS))
		{
			Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
					.setAction(android.R.string.ok, new View.OnClickListener()
					{
						@Override
						@TargetApi(Build.VERSION_CODES.M)
						public void onClick(View v)
						{
							requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
						}
					});
		}
		else
		{
			requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
		}
		return false;
	}

	/**
	 * Callback received when a permissions request has been completed.
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
	                                       @NonNull int[] grantResults)
	{
		if (requestCode == REQUEST_READ_CONTACTS)
		{
			if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
			{
				populateAutoComplete();
			}
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)


	/**
	 * Attempts to sign in or register the personal account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	private void attemptSignup()
	{
		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		_signupButton.setEnabled(false);

		// Store values at the time of the login attempt.
		final String displayName = mNameView.getText().toString();
		String email = mEmailView.getText().toString();
		String password = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password, if the user entered one.
		if (TextUtils.isEmpty(password))
		{
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}
		else if (!isPasswordValid(password))
		{
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(email))
		{
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		}
		else if (!isEmailFormatValid(email))
		{
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		//Check for a valid display name
		if (displayName.isEmpty())
		{
			mNameView.setError(getString(R.string.error_field_required));
			focusView = mNameView;
			cancel = true;
		}

		if (cancel)
		{
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		}
		else
		{
			//[START init_progress_dialog]
			final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this,
					R.style.AppTheme_Dark_Dialog);
			progressDialog.setIndeterminate(true);
			progressDialog.setMessage("Creating Account...");
			progressDialog.show();
			//[END init_progress_dialog]
			mAuth.createUserWithEmailAndPassword(email, password)
					.addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
					{
						@Override
						public void onComplete(@NonNull Task<AuthResult> task)
						{
							if (task.isSuccessful())
							{
								onSignUpSuccess(displayName);
							}
							else
							{
								onSignUpFailed();
							}

							// [START_EXCLUDE]
							progressDialog.dismiss();
							// [END_EXCLUDE]
						}
					});
		}
	}

	private void onSignUpFailed()
	{
		// If sign in fails, display a message to the user.
		Toast.makeText(SignUpActivity.this, "Signup failed.",
				Toast.LENGTH_SHORT).show();
		_signupButton.setEnabled(true);
	}

	private void onSignUpSuccess(String displayName)
	{
		_signupButton.setEnabled(true);
		// Sign in success, update database with the signed-in user's information
		final FirebaseUser user = mAuth.getCurrentUser();
		if (user != null)
		{
			final String userUid = user.getUid();

			//Fill in database values
			UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
					.setDisplayName(displayName)
					.build();

			mDatabase.child("Users").child("Personal").child(userUid).child("Email").setValue(user.getEmail());
			mDatabase.child("Users").child("Personal").child(userUid).child("Birthdate").setValue("not set");
			mDatabase.child("Users").child("Personal").child(userUid).child("Gender").setValue("not set");
			mDatabase.child("Users").child("Personal").child(userUid).child("ContactNo").setValue("not set");

			user.updateProfile(profileUpdates);
			user.sendEmailVerification();
		}
		setResult(RESULT_OK, null);
		finish();
	}

	private boolean isEmailFormatValid(String email)
	{
		return Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	private boolean isPasswordValid(String password)
	{
		return password.length() >= 6;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
	{
		return new CursorLoader(this,
				// Retrieve data rows for the device user's 'profile' contact.
				Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
						ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

				// Select only email addresses.
				ContactsContract.Contacts.Data.MIMETYPE +
						" = ?", new String[]{ContactsContract.CommonDataKinds.Email
				.CONTENT_ITEM_TYPE},

				// Show primary email addresses first. Note that there won't be
				// a primary email address if the user hasn't specified one.
				ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
	{
		List<String> emails = new ArrayList<>();
		List<String> names = new ArrayList<>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			emails.add(cursor.getString(ProfileQuery.ADDRESS));
			names.add(cursor.getString(ProfileQuery.DISPLAY_NAME_PRIMARY));
			cursor.moveToNext();
		}

		addEmailsToAutoComplete(emails);
		addNamesToAutoComplete(names);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader)
	{
	}

	private void addEmailsToAutoComplete(List<String> emailAddressCollection)
	{
		//Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
		ArrayAdapter<String> adapter =
				new ArrayAdapter<>(SignUpActivity.this,
						android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

		mEmailView.setAdapter(adapter);
	}

	private void addNamesToAutoComplete(List<String> nameCollection)
	{
		//Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
		ArrayAdapter<String> adapter =
				new ArrayAdapter<>(SignUpActivity.this,
						android.R.layout.simple_dropdown_item_1line, nameCollection);

		mNameView.setAdapter(adapter);
	}

	private interface ProfileQuery
	{
		String[] PROJECTION = {
				ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
				ContactsContract.CommonDataKinds.Email.ADDRESS,
				ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
		};

		int DISPLAY_NAME_PRIMARY = 0;
		int ADDRESS = 1;
	}
}

