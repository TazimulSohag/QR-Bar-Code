package co.banglabs.qr_barcode_scanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Generator extends AppCompatActivity {
    EditText input;
    Button btnOutput, download;
    ImageView ivOutput;


    //InterstitialAd initialize
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);

        input = findViewById(R.id.etInput);
        btnOutput = findViewById(R.id.gen_btn_id);
        ivOutput = findViewById(R.id.iv_output);
        download = findViewById(R.id.download_btn_id);


        //Interstitial add
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                createPersonalizeAdd();
            }
        });


        btnOutput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("click", "ok");


                if (mInterstitialAd != null) {
                    mInterstitialAd.show(Generator.this);
                } else {
                    Log.d("---AdMob", "The interstitial ad wasn't ready yet.");
                    String txt = input.getText().toString().trim();
                    MultiFormatWriter writer = new MultiFormatWriter();
                    try {
                        BitMatrix matrix = writer.encode(txt, BarcodeFormat.QR_CODE,
                                3050, 3050);
                        BarcodeEncoder encoder = new BarcodeEncoder();
                        Bitmap bitmap = encoder.createBitmap(matrix);
                        ivOutput.setImageBitmap(bitmap);
                        InputMethodManager manager = (InputMethodManager) getSystemService(
                                Context.INPUT_METHOD_SERVICE
                        );
                        manager.hideSoftInputFromWindow(input.getApplicationWindowToken(),
                                0);

                    } catch (Exception ignored) {
                        Toast.makeText(Generator.this, "input text to generate qr", Toast.LENGTH_SHORT).show();
                    }


                }


            }
        });

        ActivityCompat.requestPermissions(Generator.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(Generator.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveToGallery();

               /* try {

                    BitmapDrawable drawable = (BitmapDrawable) ivOutput.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();

                    File filepath = Environment.getExternalStorageDirectory();
                    File dir = new File(filepath.getAbsolutePath() + "/QRBarCodeScanner");
                    dir.mkdir();

                    String file = String.format("%d.png", System.currentTimeMillis());
                    File outFiles = new File(dir, file);
                    // File file = new File(dir, System.currentTimeMillis() + ".jpg");
                    try {
                        outputStream = new FileOutputStream(outFiles);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    Toast.makeText(Generator.this, "Image save to QR Bar Code",
                            Toast.LENGTH_SHORT).show();
                    try {
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    Toast.makeText(Generator.this, "nothing to download", Toast.LENGTH_SHORT).show();

                }*/



                /*BitmapDrawable draw = (BitmapDrawable) ivOutput.getDrawable();
                Bitmap bitmap = draw.getBitmap();

                FileOutputStream outStream = null;
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/QR Bar Code Scanner");
                dir.mkdirs();
                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);
                try {
                    outStream = new FileOutputStream(outFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                try {
                    outStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/


            }
        });


        // banner add section
        AdView mAdView = findViewById(R.id.adView);
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        //ca-app-pub-1716189511430378/4738080301
        adView.setAdUnitId("ca-app-pub-7419624357060010/1928514365");

        //InterstitialAd initialize
        MobileAds.initialize(this, initializationStatus -> {
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    private void saveToGallery( ) {

       /*
        mRequest.allowScanningByMediaScanner();
        mRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        mRequest.setDestinationInExternalPublicDir("/QR Bar Code Scanner", System.currentTimeMillis()+".jpeg");
        DownloadManager mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        mDownloadManager.enqueue(mRequest);
        Toast.makeText(Generator.this,"Image Downloaded Successfully...",Toast.LENGTH_LONG).show();
*/



        try {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) ivOutput.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();


            FileOutputStream outputStream = null;
            File file = Environment.getExternalStorageDirectory();
            File dir = new File(file.getAbsolutePath() + "/QR Bar Code Scanner/");
            dir.mkdirs();

            String filename = String.format("%d.jpeg", System.currentTimeMillis());
            File outFile = new File(dir, filename);
            try {
                outputStream = new FileOutputStream(outFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            try {
                outputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "Download Successful", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "404 Error", Toast.LENGTH_SHORT).show();
        }
    }


    private void createPersonalizeAdd() {

        AdRequest adRequest = new AdRequest.Builder().build();
        createInterstitialAdd(adRequest);
    }

    private void createInterstitialAdd(AdRequest adRequest) {
        //ca-app-pub-1716189511430378/5125572042
        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.d("---AdMob", "onAdLoaded");


                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        Log.d("---AdMob", "The ad was dismissed.");
                        String txt = input.getText().toString().trim();
                        MultiFormatWriter writer = new MultiFormatWriter();
                        try {
                            BitMatrix matrix = writer.encode(txt, BarcodeFormat.QR_CODE,
                                    3050, 3050);
                            BarcodeEncoder encoder = new BarcodeEncoder();
                            Bitmap bitmap = encoder.createBitmap(matrix);
                            ivOutput.setImageBitmap(bitmap);
                            InputMethodManager manager = (InputMethodManager) getSystemService(
                                    Context.INPUT_METHOD_SERVICE
                            );
                            manager.hideSoftInputFromWindow(input.getApplicationWindowToken(),
                                    0);

                        } catch (Exception ignored) {
                            Toast.makeText(Generator.this, "input text to generate qr", Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.d("---AdMob", "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitialAd = null;
                        Log.d("---AdMob", "The ad was shown.");
                    }
                });


            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.d("---AdMob", loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });

    }
    //InterstitialAd initialize


}