package co.banglabs.qr_barcode_scanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import eu.dkaratzas.android.inapp.update.Constants;
import eu.dkaratzas.android.inapp.update.InAppUpdateManager;
import eu.dkaratzas.android.inapp.update.InAppUpdateStatus;

// Author

public class MainActivity extends AppCompatActivity implements InAppUpdateManager.InAppUpdateHandler {

    Button button, gen, share, rate;
    InAppUpdateManager inAppUpdateManager;
    //InterstitialAd initialize
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.btn_scan);
        gen = findViewById(R.id.btn_gen);

        share = findViewById(R.id.share_id);
        rate = findViewById(R.id.rate_id);


        //Interstitial add
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                createPersonalizeAdd();
            }
        });

        button.setOnClickListener(view -> {
            IntentIntegrator intentIntegrator = new IntentIntegrator(
                    MainActivity.this
            );
            intentIntegrator.setPrompt("For Flash use volume up key");
            intentIntegrator.setBeepEnabled(true);

            intentIntegrator.setOrientationLocked(true);

            intentIntegrator.setCaptureActivity(Capture.class);

            intentIntegrator.initiateScan();

        });


        gen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                } else {
                    Intent intent = new Intent(MainActivity.this, Generator.class);
                    startActivity(intent);
                }
            }
        });


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String shareMessage = "if you like,share this app";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=co.banglabs.qr_barcode_scanner";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));

                } catch (Exception e) {
                    //e.toString();
                }
            }
        });


        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=co.banglabs.qr_barcode_scanner")));


            }
        });

        // add section
        AdView mAdView = findViewById(R.id.adView);
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        //ca-app-pub-1716189511430378/9507191253
        adView.setAdUnitId("ca-app-pub-7419624357060010/1928514365");
        MobileAds.initialize(this, initializationStatus -> {
        });

        AdRequest adRequest = new AdRequest.Builder().build();

        //InterstitialAd initialize
        MobileAds.initialize(this, initializationStatus -> {
        });

        mAdView.loadAd(adRequest);


        //update method section
        inAppUpdateManager = InAppUpdateManager.Builder(this, 101)
                .resumeUpdates(true)
                .mode(Constants.UpdateMode.IMMEDIATE)
                .snackBarAction("An update has been downloaded.")
                .snackBarAction("RESTART")
                .handler(this);


        inAppUpdateManager.checkForAppUpdate();


    }


    //Interstitial add
    private void createPersonalizeAdd() {

        AdRequest adRequest = new AdRequest.Builder().build();
        createInterstitialAdd(adRequest);


    }

    private void createInterstitialAdd(AdRequest adRequest) {
        //ca-app-pub-1716189511430378/4738080301
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
                        try {
                            Intent intent = new Intent(MainActivity.this, Generator.class);
                            startActivity(intent);

                        } catch (Exception ignored) {
                            Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        IntentResult intentResult = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, data
        );

        if (intentResult.getContents() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    MainActivity.this
            );

            builder.setTitle("Result");

            builder.setMessage(intentResult.getContents());
            builder.setPositiveButton("Ok", (dialogInterface, i) -> dialogInterface.dismiss());


            builder.show();

        } else {

            Toast.makeText(this, "Oops.....You did not scan anything", Toast.LENGTH_SHORT).show();
        }


    }


    //update section

    @Override
    public void onInAppUpdateError(int code, Throwable error) {
        Toast.makeText(this, "404 Error", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onInAppUpdateStatus(InAppUpdateStatus status) {

        if (status.isDownloaded()) {
            View view = getWindow().getDecorView().findViewById(android.R.id.content);

            Snackbar snackbar = Snackbar.make(view,
                    "An update just been downloaded.",
                    Snackbar.LENGTH_INDEFINITE);

            snackbar.setAction("", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    inAppUpdateManager.completeUpdate();

                }
            });

            snackbar.show();
        }


    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    // on back press
    public void onBackPressed() {
        new android.app.AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}