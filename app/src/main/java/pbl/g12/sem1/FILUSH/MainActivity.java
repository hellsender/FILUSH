package pbl.g12.sem1.FILUSH;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


/**
 Yung may ///////// is yung sa firebase TEST LANG
 **/

/////
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
/////





import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.database.DatabaseReference;



public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener
{
	private static final int REQUEST_LOGIN = 1;
    /////
    private DatabaseReference mDatabase;
    /////



    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;
	private FirebaseAuth mAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		mAuth = FirebaseAuth.getInstance();
		setContentView(R.layout.activity_firstscreen);

	}

	@Override
	public void onStart()
	{
		super.onStart();
		// Check if user is signed in (non-null) and update UI accordingly.
		FirebaseUser currentUser = mAuth.getCurrentUser();

		if (currentUser != null)
			if (currentUser.isEmailVerified());

			else
				onSignOut();
		else
			onSignOut();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == REQUEST_LOGIN)
		{
			if (resultCode == RESULT_OK)
			{
				FirebaseUser user = mAuth.getCurrentUser();

			}
		}
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nav_drawer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item)
	{
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.nav_news)
		{
			// Handle the camera action
		}

		else if (id == R.id.nav_events)
		{

		}
		else if (id == R.id.nav_manage)
		{

		}
		else if (id == R.id.nav_share)
		{

		}
		else if (id == R.id.nav_logout)
		{
			AlertDialog.Builder confirmAlert = new AlertDialog.Builder(MainActivity.this);
			confirmAlert.setMessage("Are you sure you want to logout?");
			confirmAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialogInterface, int i)
				{
					mAuth.signOut();
					onSignOut();
				}
			});

			confirmAlert.setNegativeButton("No", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});

			confirmAlert.show();
		}


		return true;
	}

	private void onSignOut()
	{
		Intent intent = new Intent(this, FirstScreenActivity.class);
		startActivityForResult(intent, REQUEST_LOGIN);
	}



    public boolean isServicesOK()
    {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


}
