package legendarius.finalproject.vsh;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;
import com.clarifai.api.exception.ClarifaiException;

public class RandomChallenge extends Activity implements Button.OnClickListener {
	
	private String challenge = "Find a(n) ";
	
	private String[] objects = {"pigeon", "fast food restaurant", "shopping cart", 
								"playground", "fire hydrant", "sign", 
								"gas station", "cemetery", "bug", 
								"cat", "bridge", "newspaper",
								"recycling bin", "food stand", "policeman", "bank"};
	
	private String obj = "";

    private ArrayList<String> resultTags = new ArrayList<String>();


	private TextView scav, log;
	private Button refresh, testTokenButton;
	private ImageButton camButton;
    private ImageView preview;

	private final ClarifaiClient client = new ClarifaiClient(Credentials.CLIENT_ID,
			Credentials.CLIENT_SECRET);

    private Bitmap taken;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_random_challenge);
		
		randomize();
		
		scav = (TextView)findViewById(R.id.textChallenge);
		scav.setText(challenge + obj);
		
		refresh = (Button)findViewById(R.id.btnRefresh);
		refresh.setOnClickListener(this);
		
		camButton = (ImageButton)findViewById(R.id.imageButton);
		camButton.setOnClickListener(this);
		
		testTokenButton = (Button)findViewById(R.id.btnTestTag);
		testTokenButton.setOnClickListener(this);

        log = (TextView)findViewById(R.id.tv_visibleLog);

        preview = (ImageView)findViewById(R.id.iv_preview);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.random_challenge, menu);
		return true;
	}
	
	private void randomize() {
		Random r = new Random();
		int chosen = r.nextInt(objects.length);
		
		obj = objects[chosen];
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
		int viewId = arg0.getId();
		
		switch (viewId) {
			case R.id.btnRefresh: // will change scavenger objective to something random from the list
				randomize();
				scav.setText(challenge + obj);
				break;
			case R.id.imageButton:
                // Change: Instead of trying to save each and every picture, we'll just save them to temp.jpg
				// Another picture taken, and the previous one will be gone. Oh well.
				
                File newfile = new File(CamController.SCAVENGER_FILE);
                
                try {
                    newfile.createNewFile();
                }
                catch (IOException e) {}

                Uri outputFileUri = Uri.fromFile(newfile);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                
                startActivityForResult(cameraIntent, CamController.PICTURE_TAKING_CODE);
                
				break;
			case R.id.btnTestTag:
		        try {
		        	new PostTaskTag().execute();
		        } catch (Exception e) { e.printStackTrace(); }
				
				break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case CamController.PICTURE_TAKING_CODE:
                    Log.i("Directory check", CamController.SCAVENGER_FILE);
                    Log.i("CameraResult", "Pic saved");

                    //Bitmap taken = loadBitmapFromUri(data.getData(), preview); // might just load bitmap from scavDir
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                    taken = BitmapFactory.decodeFile(CamController.SCAVENGER_FILE, options);

                    preview.setImageBitmap(taken);

                    if (taken != null) {
                        // imageview currently not displaying image
                        preview.setImageBitmap(Bitmap.createScaledBitmap(taken, 120, 120, false));
                        log.setText("Scanning image...");
                        camButton.setEnabled(false);

                        new AsyncTask<Bitmap, Void, RecognitionResult>() {
                            @Override
                            protected RecognitionResult doInBackground(Bitmap... bitmaps) {
                                return recognizeBitmap(bitmaps[0]);
                            }

                            @Override
                            protected void onPostExecute(RecognitionResult result) {
                                // not going to update UI like in starter project
                                // Instead, add tags to an arraylist of strings

                                camButton.setEnabled(true);

                                for (Tag tag : result.getTags()) {
                                    if (tag.getName().length() > 0)
                                        resultTags.add(tag.getName());
                                }

                                // Checks if one of the tags matches objective
                                for (String r : resultTags) {
                                    if (r.equalsIgnoreCase(obj)) {
                                        log.setText("Good job! You've found a(n) " + obj + ".");
                                        Log.i("ScavCheck", "Scav Challenge completed");
                                        break;
                                    }

                                    log.setText("Sorry! The object in your photo does not complete the objective.");
                                }
                            }
                        }.execute(taken);

                    } else {
                        log.setText("Error: Image invalid");
                    }

                    taken = null;
					break;
			}
		}
	}

    /** Sends the given bitmap to Clarifai for recognition and returns the result. */
    private RecognitionResult recognizeBitmap(Bitmap bitmap) {
        try {
            // Scale down the image. This step is optional. However, sending large images over the
            // network is slow and  does not significantly improve recognition performance.
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 320,
                    320 * bitmap.getHeight() / bitmap.getWidth(), true);

            // Compress the image as a JPEG.
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            scaled.compress(Bitmap.CompressFormat.JPEG, 90, out);
            byte[] jpeg = out.toByteArray();

            // Send the JPEG to Clarifai and return the result.
            return client.recognize(new RecognitionRequest(jpeg)).get(0);
        } catch (ClarifaiException e) {
            Log.e("RecogResult", "Clarifai error", e);
            return null;
        }
    }
}