package legendarius.finalproject.vsh;

import android.os.Environment;
import android.view.View;

/** http://stackoverflow.com/questions/14421694/taking-pictures-with-camera-on-android-programmatically */
/** Above link will help with file directories and image storage */

public class CamController {
	public static final int PICTURE_TAKING_CODE = 47;

	public static final String BASE_PHOTO_DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Scavenger Hunt";
	public static final String SCAVENGER_FILE = BASE_PHOTO_DIRECTORY + "/scavTemp.jpg";

	// might be deprecated soon
	public static int count = 0;
	
	public static void incrementScavengerImageIndex() {
		count++;
	}
}