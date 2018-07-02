package it.swump.grilborzer.swumpit;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    /**
     * GUI components
     */
    private ImageView imgViewTop;
    private ImageView imgViewFile;
    private ImageView imgViewInfo;
    private TextView txtViewFileName;
    private TextView txtViewPickFile;
    private TextView txtViewSendFile;

    private static final int PICK_FILE = 1;
    private boolean isFilePicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Initialize all GUI components
         */
        imgViewTop = findViewById(R.id.imgViewTop);
        imgViewFile = findViewById(R.id.imgViewFile);
        imgViewInfo = findViewById(R.id.imgViewInfo);
        txtViewFileName = findViewById(R.id.txtViewFileName);
        txtViewPickFile = findViewById(R.id.txtViewPickFile);
        txtViewSendFile = findViewById(R.id.txtViewSendFile);

        /**
         * Sets up a custom touch listener and detects swipes in 1 of 4 directions.
         */
        imgViewTop.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {
                Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {
                Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeBottom() {
                Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * Opens a dialog for the user to choose a file selector to pick a file to send.
         */
        imgViewFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select file"), PICK_FILE);
            }
        });

        /**
         * Hides the "how to send" bubble on click.
         */
        txtViewPickFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtViewPickFile.setVisibility(View.INVISIBLE);
            }
        });

        /**
         * Hides the "how to pick" bubble on click.
         */
        txtViewSendFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtViewSendFile.setVisibility(View.INVISIBLE);
            }
        });

        /**
         * Shows all hint bubble on click.
         */
        imgViewInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtViewPickFile.setVisibility(View.VISIBLE);
                if(isFilePicked) txtViewSendFile.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Make sure the file was picked successfully
        if (requestCode == PICK_FILE && resultCode == Activity.RESULT_OK && data != null) {
            try {
                Uri uri = data.getData();

                // Checks if the file is an image, converts it to a Bitmap and displays it
                if (isImageFile(uri)) {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    txtViewFileName.setText("");
                    imgViewFile.setImageBitmap(bitmap);
                    inputStream.close();
                }
                // Displays the file name if it's no image. Doesn't work with some file pick apps
                // TODO: Display file name of images as well. (Leads to NullPointerException as of right now)
                else {
                    imgViewFile.setImageResource(R.drawable.file);

                    // Calls getFilePath to retrieve storage path and file name from Intent Uri
                    txtViewFileName.setText(new File(getFilePath(this, uri)).getName());
                }
                // Hide hint how to pick a file, but show hint how to send file.
                txtViewPickFile.setVisibility(View.INVISIBLE);
                txtViewSendFile.setVisibility(View.VISIBLE);

                isFilePicked = true;
            } catch (java.io.IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "That didn't work out. Sorry! Please try again", Toast.LENGTH_LONG).show();
            }
        }
        // Tooltip to prompt user to choose a file.
        else {
            Toast.makeText(this, "Please choose a file to proceed.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Turns Intent Uri to actual file path
     * @param cntx
     * @param uri
     * @return
     */
    String getFilePath(Context cntx, Uri uri) {
        Cursor cursor = null;
        try {
            String[] arr = { MediaStore.Images.Media.DATA };
            cursor = cntx.getContentResolver().query(uri,  arr, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Checks if file is image via mime type.
     * @param uri
     * @return
     */
    boolean isImageFile(Uri uri) {
        ContentResolver cR = this.getContentResolver();
        String type = cR.getType(uri);
        return type != null && type.startsWith("image");
    }

}
