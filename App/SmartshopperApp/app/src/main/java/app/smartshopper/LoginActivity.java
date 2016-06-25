package app.smartshopper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import app.smartshopper.Database.Preferences;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
	private static final String HOST_URL = "http://api.tecfuture.de:3000";

	private UserLoginTask mAuthTask = null; // Keep track of the login task to ensure we can cancel it if requested.

	// UI references.
	private TextInputLayout mPasswordView;
	private TextInputLayout mUserView;
	private View mProgressView;

	private View mLoginFormView;

	private ProgressDialog mProgressDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// Set up the login form.
		mUserView = (TextInputLayout) findViewById(R.id.user_name);
		mPasswordView = (TextInputLayout) findViewById(R.id.password);

		mPasswordView.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_ACTION_DONE)
				{
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		Button signInButton = (Button) findViewById(R.id.email_sign_in_button);
		signInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage(getString(R.string.wait));
	}

	public static boolean isAuthenticated(Context context) {
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
	private void attemptLogin() {
		if (mAuthTask != null)
		{
			return;
		}

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
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			showProgress(true);

			final String hash = Base64.encodeToString((userName + ":" + password).getBytes(), Base64.NO_WRAP);

			Preferences.saveSharedSetting(this, "hash", hash);
			Preferences.saveSharedSetting(this, "userName", userName);

			mAuthTask = new UserLoginTask(hash);
			mAuthTask.execute(HOST_URL + "/lists");
		}
	}

	private boolean isUserNameValid(String user) {
		return user.length() > 3;
	}

	private boolean isPasswordValid(String password) {
		return password.length() > 4;
	}

	/**
	 * Shows the progress UI
	 */
	private void showProgress(final boolean show) {
		if (show)
		{
			mProgressDialog.show();
		}
		else
		{
			mProgressDialog.dismiss();
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<String, Void, Boolean> {
		private final String LOG_TAG = UserLoginTask.class.getName();
		private final String mHash;

		public UserLoginTask(String hash) {
			mHash = hash;
		}

		@Override
		protected Boolean doInBackground(String... params) {
			HttpURLConnection urlConnection = null;
			BufferedReader reader = null;

			try
			{
				URL url = new URL(params[0]);
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestProperty("Authorization", "Basic " + mHash);
				urlConnection.setConnectTimeout(10000);
				urlConnection.setReadTimeout(20000);
				urlConnection.setRequestMethod("GET");
				urlConnection.connect();

				if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
				{
					return true;
				}
			}
			catch (SocketTimeoutException e)
			{
				Log.d(LOG_TAG, "Timeout");
			}
			catch (ConnectException e)
			{
				Log.d(LOG_TAG, "Failed to connect");
			}
			catch (IOException e)
			{
				Log.e(LOG_TAG, "IO Exception", e);
			}
			finally
			{
				if (urlConnection != null)
				{
					urlConnection.disconnect();
				}

				if (reader != null)
				{
					try
					{
						reader.close();
					}
					catch (final IOException e)
					{
						Log.e(LOG_TAG, "Error closing stream", e);
					}
				}
			}

			return false;
		}

		@Override
		protected void onPostExecute(final Boolean result) {
			if (result)
			{
				Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
				startActivity(intent);
				finish();
			}
			else
			{
				Preferences.clearPreferences(LoginActivity.this);
				mPasswordView.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}

			mAuthTask = null;
			showProgress(false);
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}

