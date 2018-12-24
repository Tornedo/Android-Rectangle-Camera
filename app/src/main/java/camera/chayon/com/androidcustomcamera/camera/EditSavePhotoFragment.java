package camera.chayon.com.androidcustomcamera.camera;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;



import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import camera.chayon.com.androidcustomcamera.R;


/**
 *
 */
public class EditSavePhotoFragment extends Fragment {

    public static final String TAG = EditSavePhotoFragment.class.getSimpleName();
    public static final String BITMAP_KEY = "bitmap_byte_array";
    public static final String ROTATION_KEY = "rotation";
    public static final String IMAGE_INFO = "image_info";
    public static final String ORIENTATION_DEGREE = "orientation_degree";
    public Bitmap finalBitmap;
    public String imgPath;
    private static final int REQUEST_STORAGE = 1;

    public static Fragment newInstance(byte[] bitmapByteArray, int rotation,
                                       @NonNull ImageParameters parameters, int orientationDegree) {
        Fragment fragment = new EditSavePhotoFragment();

        Bundle args = new Bundle();
        args.putByteArray(BITMAP_KEY, bitmapByteArray);
        args.putInt(ROTATION_KEY, rotation);
        args.putParcelable(IMAGE_INFO, parameters);
        args.putInt(ORIENTATION_DEGREE, orientationDegree);
        fragment.setArguments(args);
        return fragment;
    }

    public EditSavePhotoFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.squarecamera__fragment_edit_save_photo, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int rotation = getArguments().getInt(ROTATION_KEY);
        byte[] data = getArguments().getByteArray(BITMAP_KEY);
        ImageParameters imageParameters = getArguments().getParcelable(IMAGE_INFO);
        int mCurrentNormalizedOrientation = getArguments().getInt(ORIENTATION_DEGREE);
        if (imageParameters == null) {
            return;
        }
        final ImageView photoImageView = (ImageView) view.findViewById(R.id.photo);
        Rect drar = new Rect();


        drar.set((int) TouchView.getmLeftTopPosX(), (int) TouchView.getmLeftTopPosY(),
                (int) TouchView.getmRightBottomPosX(), (int) TouchView.getmRightBottomPosY());
        Bitmap bmp;
        bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        Bitmap mutableBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);

//        mutableBitmap = Bitmap.createBitmap(mutableBitmap, (int) TouchView.getmLeftTopPosX(), (int) TouchView.getmLeftTopPosY(),
//                300, 600);
//        Bitmap croppedBmp = Bitmap.createBitmap(bmp, (int) TouchView.getmLeftTopPosX(), (int) TouchView.getmLeftTopPosY(),
//                (int)TouchView.getmRightBottomPosX(), (int)TouchView.getmRightBottomPosY());

        Log.e("left", String.valueOf((int) TouchView.getmLeftTopPosX()));
        Log.e("left", String.valueOf((int) TouchView.getmLeftTopPosY()));
        Log.e("wid", String.valueOf(drar.width()));
        Log.e("hei", String.valueOf(drar.height()));
        //photoImageView.setImageBitmap(croppedBmp);

        imageParameters.mIsPortrait =
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;// always true

        final View topView = view.findViewById(R.id.topView);
        if (imageParameters.mIsPortrait) {
            topView.getLayoutParams().height = imageParameters.mCoverHeight;
        } else {
            topView.getLayoutParams().width = imageParameters.mCoverWidth;
        }
        finalBitmap = ImageUtility.decodeSampledBitmapFromByte(getActivity(), data);
        rotatePicture(rotation, data, photoImageView, mCurrentNormalizedOrientation);
        savePicture();
       /* view.findViewById(R.id.save_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePicture();
            }
        });*/
    }

    private void rotatePicture(int rotation, byte[] data, ImageView photoImageView, int degreeForRotate) {
        Bitmap bitmap = ImageUtility.decodeSampledBitmapFromByte(getActivity(), data);
//        Log.d(TAG, "original bitmap width " + bitmap.getWidth() + " height " + bitmap.getHeight());
        Log.e("dads2", String.valueOf(bitmap.getWidth()));
        Log.e("dads2", String.valueOf(bitmap.getHeight()));
        Rect drar = new Rect();


        drar.set((int) TouchView.getmLeftTopPosX(), (int) TouchView.getmLeftTopPosY(),
                (int) TouchView.getmRightBottomPosX(), (int) TouchView.getmRightBottomPosY());

        Bitmap oldBitmap = bitmap;
//        if(degreeForRotate == 90){
//            oldBitmap = rotateImage(bitmap, 90);
//        }
//        else if(degreeForRotate == 270){
//            oldBitmap = rotateImage(bitmap, 270);
//        }
//        else if(degreeForRotate == 0){
//            oldBitmap = rotateImage(bitmap, -90);
//        }
//        else
//        {
//            oldBitmap = bitmap;
//        }



        Matrix matrix = new Matrix();
        matrix.postRotate(0);

        bitmap = Bitmap.createBitmap(
                oldBitmap, (int) TouchView.getmLeftTopPosY(), (int) TouchView.getmLeftTopPosX(),
                oldBitmap.getWidth() - (int) TouchView.getmLeftTopPosY(),
                oldBitmap.getHeight() - (int) TouchView.getmLeftTopPosX(), matrix, false
        );

        oldBitmap.recycle();
        // }
        Log.e("dads11", String.valueOf(bitmap.getWidth()));
        Log.e("dads11", String.valueOf(bitmap.getHeight()));
        bitmap= rotateImage(bitmap, 90);
        finalBitmap = rotateImage(bitmap, 180);
        photoImageView.setImageBitmap(bitmap);
    }

    public Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void savePicture() {
        Log.e("save", "picture");
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + String.valueOf(n) + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            ((CameraActivity) getActivity()).returnPhotoUri(Uri.fromFile(file));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestForPermission() {
        RuntimePermissionActivity.startActivity(EditSavePhotoFragment.this,
                REQUEST_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK != resultCode) return;

        if (REQUEST_STORAGE == requestCode && data != null) {
            final boolean isGranted = data.getBooleanExtra(RuntimePermissionActivity.REQUESTED_PERMISSION, false);
            final View view = getView();
            if (isGranted && view != null) {
                ImageView photoImageView = (ImageView) view.findViewById(R.id.photo);

                Bitmap bitmap = ((BitmapDrawable) photoImageView.getDrawable()).getBitmap();
                //  Uri photoUri = ImageUtility.savePicture(getActivity(), bitmap);

                // ((CameraActivity) getActivity()).returnPhotoUri(photoUri);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//
//            case R.id.closeBtn:
//                Log.e("close","img");
//                break;
//            case R.id.retakeImage:
//                Log.e("close", "retake");
//                break;
//            case R.id.confirmImage:
//                Log.e("close", "confirm");
//                break;
//        }
//    }


}
