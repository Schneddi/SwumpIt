package it.swump.grilborzer.swumpit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    /**
     * GUI components
     */
    private Button btnPickFile;
    private ImageView imgViewPickedFile;

    public static final int PICK_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Initialize all GUI components
         */
        btnPickFile = findViewById(R.id.btnPickFile);
        imgViewPickedFile = findViewById(R.id.imgViewPickedFile);

        /**
         * Opens a dialog for the user to choose a file selector to pick a file to send.
         */
        btnPickFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select file"), PICK_FILE);
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
                imgViewPickedFile.setImageBitmap(BitmapFactory.decodeStream(inputStream));

                inputStream.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
        // Or display an error if data ends up being null
        else {

        }
    }


}
