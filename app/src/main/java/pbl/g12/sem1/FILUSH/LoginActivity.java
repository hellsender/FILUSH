package pbl.g12.sem1.FILUSH;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    /**
     * Id to identity REQUEST_LOGIN intent result code.
     */
    private static final int REQUEST_LOGIN = 1;
    /**
     * Id to identity REQUEST_SIGNUP intent request code.
     */
    private static final int REQUEST_SIGNUP = 2;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private FirebaseAuth mAuth = null;
    private GoogleSignInClient googleSignInClient = null;

    // UI references.
    private AutoCompleteTextView emailTextView;
    private EditText passwordTextView;
int RC_Login = 2;



    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // Set up the login form.


        emailTextView = findViewById(R.id.login_input_email);
        populateAutoComplete();
        emailTextView.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
            {
                if (id == EditorInfo.IME_ACTION_NEXT || id == EditorInfo.IME_NULL)
                {
                    passwordTextView.requestFocus();
                    return true;
                }
                return false;
            }
        });
        passwordTextView = findViewById(R.id.login_input_password);
        passwordTextView.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
            {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL)
                {
                    attemptEmailLogin();
                    return true;
                }
                return false;
            }
        });

        //[START Init_email_login_button]
        AppCompatButton _emailLoginButton = findViewById(R.id.email_login_button);
        _emailLoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                attemptEmailLogin();
            }
        });
        //[END Init_email_login_button]

        //[START Init_google_login_button]
        SignInButton _googleLoginButton = findViewById(R.id.google_login_button);
        _googleLoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, REQUEST_LOGIN);
            }
        });

        //[START Init_auth]
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.mealtolive_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.signOut();
        mAuth = FirebaseAuth.getInstance();
        //[END Init_auth]
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
            Snackbar.make(emailTextView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
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

    @Override
    public void onBackPressed()
    {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    private void onLoginSuccess()
    {
        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        startActivityForResult(intent, RC_Login);


        //setResult(REQUEST_LOGIN, getIntent());
        //finish();
    }

    private void onLoginFailed()
    {
        Toast.makeText(getBaseContext(), "Authentication failed.", Toast.LENGTH_LONG).show();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    private void attemptEmailLogin()
    {
        // Reset errors.
        emailTextView.setError(null);
        passwordTextView.setError(null);

        // Store values at the time of the login attempt.
        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email))
        {
            emailTextView.setError(getString(R.string.error_field_required));
            focusView = emailTextView;
            cancel = true;
        }
        else if (!isEmailValid(email))
        {
            emailTextView.setError(getString(R.string.error_invalid_email));
            focusView = emailTextView;
            cancel = true;
        }

        // Check for a valid password
        if (TextUtils.isEmpty(password))
        {
            passwordTextView.setError(getString(R.string.error_field_required));
            focusView = passwordTextView;
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
            //[START Init_progress_dialog]
            ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
            //[END Init_progress_dialog]
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            //Sign-in was successful.
                            if (task.isSuccessful())
                            {
                                FirebaseUser user = mAuth.getCurrentUser();
                                //It only logs in if the user is Email Verified
                                if (user.isEmailVerified())
                                {
                                    onLoginSuccess();
                                }
                                //Send Verification Email if the user non-verified
                                else
                                {
                                    Toast.makeText(getBaseContext(), "Account not verified. Please verify your account before logging in.", Toast.LENGTH_LONG).show();
                                    user.sendEmailVerification();
                                    onLoginFailed();
                                    mAuth.signOut();
                                }
                            }
                            //If initial sign-in was unsuccessful
                            else
                            {
                                onLoginFailed();
                            }
                        }
                    });
            progressDialog.dismiss();
        }
    }

    private void attemptGoogleLogin(GoogleSignInAccount acct)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            final FirebaseUser user = mAuth.getCurrentUser();
                            final String userUid = user.getUid();

                            //register user to database if not registered
                            mDatabase.child("Users").equalTo(userUid).addListenerForSingleValueEvent(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot)
                                {
                                    boolean userUidExists = false;
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                                    {
                                        if (snapshot.exists())
                                        {
                                            userUidExists = true;
                                            break;
                                        }
                                    }
                                    if (!userUidExists)
                                    {
                                        mDatabase.child("Users").child("Personal").child(userUid).child("Email").setValue(user.getEmail());
                                        mDatabase.child("Users").child("Personal").child(userUid).child("Birthdate").setValue("not set");
                                        mDatabase.child("Users").child("Personal").child(userUid).child("Gender").setValue("not set");
                                        mDatabase.child("Users").child("Personal").child(userUid).child("ContactNo").setValue("not set");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError)
                                {

                                }
                            });
                            onLoginSuccess();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_LOGIN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try
            {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                attemptGoogleLogin(account);
            }
            catch (ApiException e)
            {
                switch (e.getMessage())
                {
                    //Tapping out of the Google Intent
                    case "12501: ":
                        break;
                    //No internet connection
                    case "7: ":
                        Toast.makeText(getBaseContext(), "Failed to connect. Check your internet connection.", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }

        if (requestCode == REQUEST_SIGNUP)
        {
            Toast.makeText(getBaseContext(), "Please verify your account before logging in.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isEmailValid(String email)
    {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection)
    {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        emailTextView.setAdapter(adapter);
    }


    private interface ProfileQuery
    {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
    }
}


