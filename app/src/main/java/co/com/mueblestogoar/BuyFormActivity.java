package co.com.mueblestogoar;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

import java.io.IOException;

public class BuyFormActivity extends AppCompatActivity  {

    private Button sendButton;
    private static final String SENDGRID_USERNAME = "";
    private static final String SENDGRID_PASSWORD = "";
    private static final int ADD_ATTACHMENT = 0;
    private EditText emailText;
    private EditText commentText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_buy_form);
        sendButton = (Button) findViewById(R.id.sendBt);
        emailText =  (EditText) findViewById(R.id.txtEmail);
        commentText =  (EditText) findViewById(R.id.txtComentario);
        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start send email ASyncTask
                SendEmailASyncTask task = new SendEmailASyncTask(BuyFormActivity.this,
                        "muebletogo@gmail.com",
                        "muelestogo@sendgrid.net",
                        "Intención de compra a través de la app",
                        "Email: "+emailText.getText().toString()+"<br>Comentario:"+commentText.getText().toString(),
                        null,
                        null);
                task.execute();
            }
        });

    }

    /**
     * ASyncTask that composes and sends email
     */
    private static class SendEmailASyncTask extends AsyncTask<Void, Void, Void> {

        private Context mAppContext;
        private String mMsgResponse;

        private String mTo;
        private String mFrom;
        private String mSubject;
        private String mText;
        private Uri mUri;
        private String mAttachmentName;

        public SendEmailASyncTask(Context context, String mTo, String mFrom, String mSubject,
                                  String mText, Uri mUri, String mAttachmentName) {
            this.mAppContext = context.getApplicationContext();
            this.mTo = mTo;
            this.mFrom = mFrom;
            this.mSubject = mSubject;
            this.mText = mText;
            this.mUri = mUri;
            this.mAttachmentName = mAttachmentName;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                SendGrid sendgrid = new SendGrid(SENDGRID_USERNAME, SENDGRID_PASSWORD);

                SendGrid.Email email = new SendGrid.Email();

                // Get values from edit text to compose email
                // TODO: Validate edit texts
                email.addTo(mTo);
                email.setFrom(mFrom);
                email.setSubject(mSubject);
                email.setText(mText);

                // Attach image
                if (mUri != null) {
                    email.addAttachment(mAttachmentName, mAppContext.getContentResolver().openInputStream(mUri));
                }

                // Send email, execute http request
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                SendGrid.Response response = sendgrid.send(email);
                mMsgResponse = response.getMessage();

                Log.d("SendAppExample", mMsgResponse);

            } catch (SendGridException | IOException e) {
                Log.e("SendAppExample", e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Toast.makeText(mAppContext, mMsgResponse, Toast.LENGTH_SHORT).show();
        }
    }


}
