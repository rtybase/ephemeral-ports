package com.rtybase.ephemeralportvalidator;

import com.rtybase.ephemeralportvalidator.httpclient.HttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class EphemeralPortValidatorActivity extends Activity implements View.OnClickListener {

	private EditText hostAndPortEditView;
	private EditText resultEditView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ephemeral_port_validator);

		findViewById(R.id.processButton).setOnClickListener(this);

		hostAndPortEditView = (EditText) findViewById(R.id.hostAndPortEdit);
		resultEditView = (EditText) findViewById(R.id.resultViewEdit);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ephemeral_port_validator, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.processButton) {
			String hostAndPort = getHostAndPort();
			if (hostAndPort != null && hostAndPort.length() > 0) {
				new RequestServerAsyncTask(hostAndPort).execute();
			} else {
				showMessage("Please provide a host and/or port!", null);
			}
		}
	}

	private void showMessage(String message, DialogInterface.OnClickListener listener) {
		new AlertDialog.Builder(this).setTitle("Note").setMessage(message).setNeutralButton("Close", listener).show();
	}

	private String getHostAndPort() {
		Editable host = hostAndPortEditView.getText();
		if (host != null) {
			return host.toString().trim();
		}
		return "";
	}

	private class ExecResult {
		private final Exception exception;
		private final String result;

		private ExecResult(String result, Exception exception) {
			this.result = result;
			this.exception = exception;
		}

		Exception getException() {
			return exception;
		}

		String getResult() {
			return result;
		}
	}

	private class RequestServerAsyncTask extends AsyncTask<Void, Void, ExecResult> {
		private String url;

		private RequestServerAsyncTask(String hostAndPort) {
			url = "http://" + hostAndPort + "/myport/";
		}

		@Override
		protected void onPreExecute() {
			findViewById(R.id.processButton).setEnabled(false);
		}

		@Override
		protected ExecResult doInBackground(Void... params) {
			try {
				HttpClient client = new HttpClient();
				String result = client.doGet(url);
				return new ExecResult(result, null);
			} catch (Exception e) {
				return new ExecResult(null, e);
			}
		}

		@Override
		protected void onPostExecute(ExecResult result) {
			findViewById(R.id.processButton).setEnabled(true);

			if (result.getException() != null) {
				Exception ex = result.getException();
				showMessage("Call failed! " + ex.getMessage(), null);
			} else {
				resultEditView.setText(result.getResult());
			}
		}
	}
}
