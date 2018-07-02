package it.swump.grilborzer.swumpit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    /**
     * GUI components
     */
    private ImageView imgViewFile;
    private ImageView imgViewInfo;
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
        imgViewFile = findViewById(R.id.imgViewFile);
        imgViewInfo = findViewById(R.id.imgViewInfo);
        txtViewPickFile = findViewById(R.id.txtViewPickFile);
        txtViewSendFile = findViewById(R.id.txtViewSendFile);

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
                // TODO: Check if the file is an image
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                imgViewFile.setImageBitmap(BitmapFactory.decodeStream(inputStream));

                // Hide hint how to pick a file & show hint how to send file.
                txtViewPickFile.setVisibility(View.INVISIBLE);
                txtViewSendFile.setVisibility(View.VISIBLE);

                isFilePicked = true;
                inputStream.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "That didn't work out. Sorry! Please try again", Toast.LENGTH_LONG).show();
            }
        }
        // Tooltip to prompt user to choose a file.
        else {
            Toast.makeText(this, "Please choose a file to proceed.", Toast.LENGTH_SHORT).show();
        }
    }


}
