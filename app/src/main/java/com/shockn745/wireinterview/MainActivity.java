package com.shockn745.wireinterview;

import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;


/**
 * Main activity of the application
 *
 * @author Florian Kempenich
 */
public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int ANIM_DURATION = 6000;
    private static final int ROTATION_SPAN = 270;

    private AnimatedMinion mMinion1;
    private AnimatedMinion mMinion2;
    private SeekBar mRotationSeekBar;
    private SeekBar mSaturationSeekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find views by id
        ImageView mMinion1ImageView = (ImageView) findViewById(R.id.minion_1_image_view);
        ImageView mMinion2ImageView = (ImageView) findViewById(R.id.minion_2_image_view);
        mRotationSeekBar = (SeekBar) findViewById(R.id.rotation_seek_bar);
        mSaturationSeekBar = (SeekBar) findViewById(R.id.saturation_seek_bar);

        // Init AnimatedMinions
        mMinion1 = new AnimatedMinion();
        mMinion2 = new AnimatedMinion();
        initAnimatedMinion(mMinion1, mMinion1ImageView, false);
        initAnimatedMinion(mMinion2, mMinion2ImageView, true);

        // Set up the rotationSeekBar
        // Max value (in degree * 100)
        // Degree * 100 : For smoother scrolling
        mRotationSeekBar.setMax(ROTATION_SPAN*100);
        mRotationSeekBar.setProgress(ROTATION_SPAN*100/2);
        mRotationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Set rotation (in degree)
                float rotation = -(ROTATION_SPAN/2) + ((float) progress) / 100;
                mMinion1.getImageView().setRotation(rotation);
                mMinion2.getImageView().setRotation(rotation);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        
        // Set up the saturation Seekbar
        final ColorMatrix colorMatrix = new ColorMatrix();
        final int maxProgress = 1000;
        mSaturationSeekBar.setMax(maxProgress);
        mSaturationSeekBar.setProgress(maxProgress);
        mSaturationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                colorMatrix.setSaturation(((float) progress) / 1000);
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);

                mMinion1.getImageView().setColorFilter(filter);
                mMinion2.getImageView().setColorFilter(filter);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


    }

    /**
     * Create and init CustomRotationAnimation
     * @param animatedMinion Must contains the ImageView. Will be loaded with the Animation
     */
    private void createCustomRotationAnimation(AnimatedMinion animatedMinion) {
        if (animatedMinion.getImageView() != null) {
            CustomRotationAnimation animation = new CustomRotationAnimation(
                    animatedMinion.getImageView(),
                    animatedMinion.startsHidden()
            );
            animation.setDuration(ANIM_DURATION);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE);

            animatedMinion.setAnimation(animation);
        }
    }

    /**
     * Start animations if both AnimatedMinions have been initialized
     */
    private void startAnimationsIfReady() {
        if (mMinion1.isInitialized() && mMinion2.isInitialized()) {
            mMinion1.startAnimation();
            mMinion2.startAnimation();
        }
    }


    /**
     * Init the animatedMinion with the imageView, the animation and the bitmap drawable
     * Also start the animations
     * @param minion animatedMinion to be initialized
     * @param imageView imageView to add to the AnimatedMinion
     * @param startHidden true if starts animation hidden
     */
    private void initAnimatedMinion(
            final AnimatedMinion minion,
            ImageView imageView,
            boolean startHidden){

        // Initialization - Fist part
        minion.setImageView(imageView);
        minion.setStartsHidden(startHidden);

        // Initialization - Second part : After layout
        minion.getImageView().getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // gets called after layout has been done but before display
                        // so getTop/Bottom/etc != 0

                        // Remove listener after first layout
                        minion.getImageView()
                                .getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        // Create the animation
                        createCustomRotationAnimation(minion);

                        startAnimationsIfReady();
                    }
                });
    }

}
