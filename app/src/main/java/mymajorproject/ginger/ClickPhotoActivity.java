package mymajorproject.ginger;

import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class ClickPhotoActivity extends AppCompatActivity {
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Button btnSelect,uploadButton;
    private ImageView ivImage;
    private String userChoosenTask;
    public String SERVER = "http://51.137.103.116/uploads/";
    //public String SERVER = "http://192.168.0.7:5486/uploads/simple/";
    public String timestamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_photo);
        btnSelect = (Button) findViewById(R.id.btnSelectPhoto);
        uploadButton= (Button) findViewById(R.id.uploadImage);


        btnSelect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        uploadButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        ivImage = (ImageView) findViewById(R.id.ivImage);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();

                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPhotoActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(ClickPhotoActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }



    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        uploadButton.setVisibility(Button.VISIBLE);

        //get the current timeStamp and store that in the time Variable
        Long tsLong = System.currentTimeMillis() / 1000;
        timestamp = tsLong.toString();

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ivImage.setImageBitmap(thumbnail);
    }

    private void uploadImage() {
//get image in bitmap format
        Bitmap image = ((BitmapDrawable) ivImage.getDrawable()).getBitmap();
        //execute the async task and upload the image to server
        new Upload(image,"IMG_"+timestamp).execute();
    }



    private String hashMapToUrl(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    //extract code from response:
    public static Integer extractCodeFromJson(String response) {
        Log.d("CPA",response);

        try {
            JSONObject baseJsonResponse = new JSONObject(response);
            Integer code = new Integer(baseJsonResponse.getInt("code"));
            return code;

            /**
             JSONArray featureArray = baseJsonResponse.getJSONArray("features");

             // If there are results in the features array
             if (featureArray.length() > 0) {
             // Extract out the first feature (which is an earthquake)
             JSONObject firstFeature = featureArray.getJSONObject(0);
             JSONObject properties = firstFeature.getJSONObject("properties");

             // Extract out the title, time, and tsunami values
             String title = properties.getString("title");
             long time = properties.getLong("time");
             int tsunamiAlert = properties.getInt("tsunami");

             // Create a new {@link Event} object
             return new Event(title, time, tsunamiAlert);
             }
             **/
        } catch (JSONException e) {
            Log.e("CPA", "Problem parsing the earthquake JSON results", e);
        }
        return null;
    }

    //alert dialog
    //async task to upload image
    private class Upload extends AsyncTask<Void,Void,String> {
        private Bitmap image;
        private String name;

        public Upload(Bitmap image,String name){
            this.image = image;
            this.name = name;
        }

        @Override
        protected String doInBackground(Void... params) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();


            //compress the image to jpg format
            image.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            /*
            * encode image to base64 so that it can be picked by saveImage.php file
            * */
            String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT);
            Log.d("IMP",encodeImage);
            //generate hashMap to store encodedImage and the name
            HashMap<String,String> detail = new HashMap<>();
            //detail.put("name", name);
            detail.put("myfile", encodeImage);

            try{
                //convert this HashMap to encodedUrl to send to php file
                String dataToSend = hashMapToUrl(detail);
                //make a Http request and send data to saveImage.php file
                String response = Request.post(SERVER,dataToSend);

                //return the response
                return response;

            }catch (Exception e){
                e.printStackTrace();

                return null;
            }
        }



        @Override
        protected void onPostExecute(String s) {
            //show image uploaded
            Toast.makeText(getApplicationContext(),"Image Uploaded",Toast.LENGTH_SHORT).show();
            Integer code =extractCodeFromJson(s);
            int codevalue = code.intValue();
            if(code ==0 || code ==2){
                String messageString="";
                if(code == 0) messageString="No face Detected. Please Try Again";
                if(code == 2) messageString="Multiple faces Detected. Please Try Again with only one user";

                AlertDialog alertDialog = new AlertDialog.Builder(
                        ClickPhotoActivity.this).create();

                // Setting Dialog Title
                alertDialog.setTitle("Error");

                // Setting Dialog Message
                alertDialog.setMessage(messageString);


                // Setting OK Button
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                    }
                });

                // Showing Alert Message
                alertDialog.show();


            }
            else {
                Intent i = new Intent(ClickPhotoActivity.this,EmotionScreen.class);
                i.putExtra("emotions",s);
                startActivity(i);

            }


        }
    }


}
