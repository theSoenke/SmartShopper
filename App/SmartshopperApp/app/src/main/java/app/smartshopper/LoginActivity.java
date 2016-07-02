package app.smartshopper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.gson.JsonElement;

import java.util.ArrayList;

import app.smartshopper.Database.Preferences;
import app.smartshopper.Database.Sync.APIFactory;
import app.smartshopper.Database.Sync.ApiService;
import app.smartshopper.Database.Entries.ShoppingList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity
{
	private static final String TAG = LoginActivity.class.getSimpleName();

	// UI references.
	private TextInputLayout mPasswordView;
	private TextInputLayout mUserView;

	private ProgressDialog mProgressDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// Set up the login form.
		mUserView = (TextInputLayout) findViewById(R.id.user_name);
		mPasswordView = (TextInputLayout) findViewById(R.id.password);

		Button signInButton = (Button) findViewById(R.id.sign_in_button);
		signInButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				authenticateUser(true);
			}
		});

		Button registerButton = (Button) findViewById(R.id.register_button);
		registerButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				authenticateUser(false);
			}
		});

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage(getString(R.string.wait));
	}

	public static boolean isAuthenticated(Context context)
	{
		if (Preferences.preferencesExist(context))
		{
			return true;
		}
		return false;
	}


	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid username, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	private void authenticateUser(boolean login)
	{
		// Reset errors.
		mUserView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		String userName = mUserView.getEditText().getText().toString();
		String password = mPasswordView.getEditText().getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password, if the user entered one.
		if (TextUtils.isEmpty(password))
		{
			mPasswordView.setError(getString(R.string.error_incorrect_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid username
		if (TextUtils.isEmpty(userName))
		{
			mUserView.setError(getString(R.string.error_field_required));
			focusView = mUserView;
			cancel = true;
		}
		else if (!isUserNameValid(userName))
		{
			mUserView.setError(getString(R.string.error_invalid_email));
			focusView = mUserView;
			cancel = true;
		}

		if (cancel)
		{
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			if (focusView != null)
			{
				focusView.requestFocus();
			}
		}
		else
		{
			showProgress(true);

			final String hash = Base64.encodeToString((userName + ":" + password).getBytes(), Base64.NO_WRAP);

			Preferences.setBasicAuthHash(hash);
			Preferences.setUserName(userName);

			if (login)
			{
				startAuthentication();
			}
			else
			{
				startRegistration();
			}
		}
	}

	private boolean isUserNameValid(String user)
	{
		return user.length() > 3;
	}

	/**
	 * Shows the progress UI
	 */
	private void showProgress(final boolean show)
	{
		if (show)
		{
			mProgressDialog.show();
		}
		else
		{
			mProgressDialog.dismiss();
		}
	}

	public void startAuthentication()
	{
		ApiService restClient = new APIFactory().getInstance();
		Call<ArrayList<ShoppingList>> call = restClient.listsLimit(1);

		call.enqueue(new Callback<ArrayList<ShoppingList>>() {
			@Override
			public void onResponse(Call<ArrayList<ShoppingList>> call, Response<ArrayList<ShoppingList>> response) {
				if (response.isSuccessful())
				{
					Log.e(TAG, "Login successful");

					mProgressDialog.dismiss(); // Prevent WindowLeaked
					startActivity(new Intent(LoginActivity.this, HomeActivity.class));
					finish();
				}
				else
				{
					Preferences.clearPreferences(LoginActivity.this);
					mPasswordView.setError(getString(R.string.error_incorrect_password));
					mPasswordView.requestFocus();

					showProgress(false);
				}

			}

			@Override
			public void onFailure(Call<ArrayList<ShoppingList>> call, Throwable t) {
				Log.d(TAG, "login failure");
				Log.d(TAG, t.getMessage());
			}
		});
	}

	public void startRegistration()
	{
		ApiService restClient = new APIFactory().getInstance();
		Call<JsonElement> call = restClient.register();

		call.enqueue(new Callback<JsonElement>()
		{
			@Override
			public void onResponse(Call<JsonElement> call, Response<JsonElement> response)
			{

				if (response.isSuccessful())
				{
					Log.e(TAG, "Registration successful");

					mProgressDialog.dismiss(); // Prevent WindowLeaked
					startActivity(new Intent(LoginActivity.this, HomeActivity.class));
					finish();
				}
				else
				{
					Preferences.clearPreferences(LoginActivity.this);
					mUserView.setError(getString(R.string.error_duplicate_username));
					mUserView.requestFocus();

					showProgress(false);
				}
			}

			@Override
			public void onFailure(Call<JsonElement> call, Throwable t)
			{
				Log.d(TAG, "register failure");
				Log.d(TAG, t.getMessage());
			}
		});
	}
}