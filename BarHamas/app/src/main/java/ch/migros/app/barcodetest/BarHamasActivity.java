package ch.migros.app.barcodetest;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.oned.ITFWriter;

import java.io.File;
import java.io.FileOutputStream;

public class BarHamasActivity extends ActionBarActivity implements ActionBar.OnNavigationListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    private ImageView mBarcodeView;

    private TextView mBarcodeValueDisplay;

    private BarcodeFormat mBarcodeFormat = BarcodeFormat.DATA_MATRIX;
    private EditText mInputValue;

    private int mWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_hamas);

        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = 1F;
        getWindow().setAttributes(layout);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int i =metrics.heightPixels;
        mWidth = metrics.widthPixels;

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        mBarcodeView = (ImageView)findViewById(R.id.barhamasImageView);
        mBarcodeValueDisplay = (TextView)findViewById(R.id.barcodeValueDisplay);

        generateBarcode("Hello! :-)");

        mInputValue = (EditText)findViewById(R.id.inputField);

        Button generateBarcodeButton = (Button)findViewById(R.id.confirmInputButton);
        generateBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateBarcode(mInputValue.getText().toString());
            }
        });

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<String>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new String[] {
                                getString(R.string.title_section1),
                                getString(R.string.title_section2),
                                getString(R.string.title_section3),
                        }),
                this);
    }

    private void generateBarcode(String message) {
        BitMatrix bitMatrix = null;
        try {
            if (mBarcodeFormat.equals(BarcodeFormat.DATA_MATRIX)){
                bitMatrix = new DataMatrixWriter().encode(message, mBarcodeFormat, mWidth, 500, null);
            } else if (mBarcodeFormat.equals(BarcodeFormat.EAN_13)){
                bitMatrix = new EAN13Writer().encode(message, mBarcodeFormat, mWidth, 300, null);
            } else if (mBarcodeFormat.equals(BarcodeFormat.ITF)){
                bitMatrix = new ITFWriter().encode(message, mBarcodeFormat, mWidth, 300, null);
            } else {
                throw new RuntimeException("Unsupported Barcode type: " + mBarcodeFormat.toString());
            }

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int[] pixels = new int[width * height];
            // All are 0, or black, by default
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? BLACK : WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            mBarcodeView.setImageBitmap(bitmap);
            mBarcodeValueDisplay.setText(message);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            Toast.makeText(this, "Unsupported value for barcode generation", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getSupportActionBar().getSelectedNavigationIndex());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bar_hamas, menu);
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.
//        ge;
        switch (position){
            case 0:
                mBarcodeFormat = BarcodeFormat.DATA_MATRIX;
                generateBarcode("success");
                break;
            case 1:
                mBarcodeFormat = BarcodeFormat.EAN_13;
                generateBarcode("7612345678900");
                break;
            case 2:
                mBarcodeFormat= BarcodeFormat.ITF;
                generateBarcode("2401234567");
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.example_number:
                mInputValue.setText(R.string.example_number);
                generateBarcode(getString(R.string.example_number));
            return true;
            default:
            return super.onOptionsItemSelected(item);
        }
    }
}
