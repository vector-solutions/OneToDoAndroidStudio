package com.vector.onetodo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.vector.onetodo.utils.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Accounts extends Fragment {

    private AQuery aq;
    private Boolean message;
    private AQuery aq_changephone;
    private AQuery aq_changeemail;
    private AQuery aq_name;
    private Uri imageUri;
    File photo;
    private static final int TAKE_PICTURE = 1;
    public static final int RESULT_GALLERY = 0;
    CircularImageView imageEvent;
    private Dialog onetodoinfo;
    private Dialog buypro;
    private Dialog phoneinfo;
    private Dialog changephone;
    private Dialog emailinfo;
    private Dialog changeemail;
    private Dialog nameinfo;
    private Dialog select_image;
    private static Boolean boo = false;
    private static String path = null;
    private LruCache<String, Bitmap> mMemoryCache;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account, container, false);
        imageEvent = (CircularImageView) view.findViewById(R.id.image_event);
        aq = new AQuery(getActivity(), view);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_top);
        if (toolbar != null)
            ((ActionBarActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Manage Account");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        setFont();
        setHasOptionsMenu(true);

        //cashing
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };



        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.plain, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStack();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialoglayout6 = inflater.inflate(
                R.layout.account_dialog_attachfrom_gallery_camera, null, false);
        AQuery aq_attach = new AQuery(dialoglayout6);
        AlertDialog.Builder builder6 = new AlertDialog.Builder(getActivity());
        builder6.setView(dialoglayout6);
        select_image = builder6.create();

        View dialoglayout7 = inflater.inflate(
                R.layout.account_dialog_getonetodo, null, false);
        AQuery aq_onetodoinfo = new AQuery(dialoglayout7);
        AlertDialog.Builder builder7 = new AlertDialog.Builder(getActivity());
        builder7.setView(dialoglayout7);
        onetodoinfo = builder7.create();

        View dialoglayout8 = inflater.inflate(R.layout.account_dialog_phoneno,
                null, false);
        AQuery aq_phone = new AQuery(dialoglayout8);
        AlertDialog.Builder builder8 = new AlertDialog.Builder(getActivity());
        builder8.setView(dialoglayout8);
        phoneinfo = builder8.create();

        View dialoglayout9 = inflater.inflate(R.layout.account_dialog_email,
                null, false);
        AQuery aq_email = new AQuery(dialoglayout9);
        AlertDialog.Builder builder9 = new AlertDialog.Builder(getActivity());
        builder9.setView(dialoglayout9);
        emailinfo = builder9.create();

        View dialoglayout1 = inflater.inflate(
                R.layout.account_dialog_changename, null, false);
        aq_name = new AQuery(dialoglayout1);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setView(dialoglayout1);
        nameinfo = builder1.create();

        View dialoglayout2 = inflater.inflate(
                R.layout.account_dialog_getbuypro, null, false);
        AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
        builder2.setView(dialoglayout2);
        buypro = builder2.create();

        View dialoglayout3 = inflater.inflate(
                R.layout.account_dialog_changeemail, null, false);
        aq_changeemail = new AQuery(dialoglayout3);
        AlertDialog.Builder builder3 = new AlertDialog.Builder(getActivity());
        builder3.setView(dialoglayout3);
        changeemail = builder3.create();

        View dialoglayout4 = inflater.inflate(
                R.layout.account_dialog_change_country_phoneno, null, false);
        aq_changephone = new AQuery(dialoglayout4);
        AlertDialog.Builder builder4 = new AlertDialog.Builder(getActivity());
        builder4.setView(dialoglayout4);
        changephone = builder4.create();

        Spinner spinner = aq_changephone.id(R.id.sp_country).getSpinner();
        String[] recourseList = getResources().getStringArray(
                R.array.CountryCodes);
        List<String> countriesList = new ArrayList<String>(
                Arrays.asList(recourseList));
        CountriesListAdapter adapter = new CountriesListAdapter(getActivity(),
                countriesList);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View view,
                                       int position, long Id) {
                String code = ((TextView) view.findViewById(R.id.country_code))
                        .getText().toString();
                aq_changephone.id(R.id.phoneno).text(code);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });
        aq_onetodoinfo.id(R.id.ok_event).clicked(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onetodoinfo.dismiss();
                buypro.show();
            }
        });

        aq_onetodoinfo.id(R.id.cancel_event).clicked(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onetodoinfo.dismiss();
            }
        });
        aq.id(R.id.image_event).clicked(new OnClickListener() {

            @Override
            public void onClick(View v) {
                select_image.show();
            }
        });
        aq.id(R.id.initials).clicked(new OnClickListener() {

            @Override
            public void onClick(View v) {
                select_image.show();
            }
        });
        aq_attach.id(R.id.from_camera).clicked(new OnClickListener() {

            @Override
            public void onClick(View v) {

                select_image.dismiss();
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

                String path = Environment.getExternalStorageDirectory()
                        .toString();
                File makeDirectory = new File(path + File.separator + "OneTodo");
                makeDirectory.mkdir();
                photo = new File(Environment.getExternalStorageDirectory()
                        + File.separator + "OneToDo" + File.separator,
                        "OneToDo_" + System.currentTimeMillis() + ".JPG");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                imageUri = Uri.fromFile(photo);
                startActivityForResult(intent, TAKE_PICTURE);
            }
        });
        aq_attach.id(R.id.from_gallery).clicked(new OnClickListener() {

            @Override
            public void onClick(View v) {

                select_image.dismiss();
                Intent galleryIntent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_GALLERY);
            }
        });
        aq.id(R.id.go_pro).clicked(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onetodoinfo.show();
            }
        });
        aq.id(R.id.verify_number).clicked(new OnClickListener() {

            @Override
            public void onClick(View v) {
                phoneinfo.show();
            }
        });

        aq_phone.id(R.id.ok_event).clicked(new OnClickListener() {

            @Override
            public void onClick(View v) {
                phoneinfo.dismiss();
            }
        });

        aq_phone.id(R.id.cancel_event).clicked(new OnClickListener() {

            @Override
            public void onClick(View v) {
                phoneinfo.dismiss();
                changephone.show();
            }
        });
        aq_changephone.id(R.id.ok_event).clicked(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String number = aq_changephone.id(R.id.phoneno).getText()
                        .toString();
                if (!number.isEmpty() && number.contains("+")
                        && number.length() > 4) {
                    aq.id(R.id.user_number).text(number);
                    App.prefs.setUserNumber(number);
                    changephone.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Invalid number!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        aq_changephone.id(R.id.cancel_event).clicked(new OnClickListener() {

            @Override
            public void onClick(View v) {
                changephone.dismiss();
            }
        });
        aq.id(R.id.email_account).clicked(new OnClickListener() {

            @Override
            public void onClick(View v) {
                emailinfo.show();
            }
        });
        aq_email.id(R.id.ok_event).clicked(new OnClickListener() {

            @Override
            public void onClick(View v) {
                emailinfo.dismiss();

            }
        });
        aq_email.id(R.id.cancel_event).clicked(new OnClickListener() {

            @Override
            public void onClick(View v) {
                emailinfo.dismiss();
                changeemail.show();
            }
        });
        aq_changeemail.id(R.id.ok_event).clicked(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final String email = aq_changeemail.id(R.id.enteremail).getText().toString();
                if(!email.isEmpty() && email.contains("@")){

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id", String.valueOf(App.prefs.getUserId()));
                    params.put("set", "email");
                    params.put("value", email);
                    params.put("table", "user");
                    ProgressDialog dialog = new ProgressDialog(getActivity());
                    dialog.setMessage("Updating...Please wait.");
                    aq.progress(dialog).ajax(
                            "http://api.heuristix.net/one_todo/v1/user/account", params,
                            JSONObject.class, new AjaxCallback<JSONObject>() {
                                @Override
                                public void callback(String url, JSONObject json,
                                                     AjaxStatus status) {
                                    int id = -1;
                                    try {
                                        JSONObject obj1 = new JSONObject(json.toString());
                                        message = obj1.getBoolean("error");
                                        id = obj1.getInt("result");

                                    } catch (Exception e) {
                                    }
                                    if (id != -1) {
                                        Log.v("Response", json.toString());
							            App.prefs.setUserEmail(email);
                                        aq.id(R.id.email1).text(App.prefs.getUserEmail());
                                        changeemail.dismiss();
                                        Toast.makeText(getActivity(), "done",Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });

                }else{
                    Toast.makeText(getActivity(), "Invalid email address", Toast.LENGTH_SHORT).show();
                }

            }
        });
        aq_changeemail.id(R.id.cancel_event).clicked(new OnClickListener() {

            @Override
            public void onClick(View v) {
                changeemail.dismiss();
            }
        });

        aq.id(R.id.name_layout).clicked(new OnClickListener() {

            @Override
            public void onClick(View v) {
                nameinfo.show();
            }
        });
        aq_name.id(R.id.ok_event).clicked(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String userName = aq_name.id(R.id.entername).getText()
                        .toString();
                final String[] name = userName.split(" ");
                if (!userName.isEmpty()) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id", String.valueOf(App.prefs.getUserId()));
                    params.put("set", "first_name,last_name");
                    params.put("value", name[0]+","+ name[1]);
                    params.put("table", "user_profile");
                    ProgressDialog dialog = new ProgressDialog(getActivity());
                    dialog.setMessage("Updating...Please wait.");
                    aq.progress(dialog).ajax(
                            "http://api.heuristix.net/one_todo/v1/user/account", params,
                            JSONObject.class, new AjaxCallback<JSONObject>() {
                                @Override
                                public void callback(String url, JSONObject json,
                                                     AjaxStatus status) {
                                    int id = -1;
                                    try {
                                        JSONObject obj1 = new JSONObject(json.toString());
                                        message = obj1.getBoolean("error");
                                        id = obj1.getInt("result");

                                    } catch (Exception e) {
                                    }
                                    if (id != -1) {
                                        Log.v("Response", json.toString());
                                        App.prefs.setUserName(name[0]+" "+name[1]);
                                        aq.id(R.id.user_name).text(App.prefs.getUserName());
                                        TextView textview = (TextView)getActivity().findViewById(R.id.username);
                                        textview.setText(App.prefs.getUserName());
                                        nameinfo.dismiss();
                                        Toast.makeText(getActivity(), "done",Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });
                } else {
                    Toast.makeText(getActivity(), "Name cannot be empty",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        aq_name.id(R.id.cancel_event).clicked(new OnClickListener() {

            @Override
            public void onClick(View v) {
                nameinfo.dismiss();
            }
        });

        aq_changephone.id(R.id.ok_event).clicked(new  OnClickListener() {

            @Override
            public void onClick(View v) {
                changephone.dismiss();
            }
        });
        aq_changephone.id(R.id.cancel_event).clicked(new  OnClickListener() {

            @Override
            public void onClick(View v) {
                changephone.dismiss();
            }
        });

        if(!App.prefs.getUserProfileUri().isEmpty()){
            Bitmap imgBitmap = aq.getCachedImage(App.prefs.getUserProfileUri());
            if(imgBitmap != null){

                aq.id(R.id.initials).gone();
                aq.id(imageEvent).image(imgBitmap);
                aq.id(R.id.image_event)
                        .background(R.drawable.round_photobutton).visible();
            }else{
                aq.id(R.id.initials).gone();
                aq.id(imageEvent).image(App.prefs.getUserProfileUri(),true,true);
                aq.id(R.id.image_event)
                        .background(R.drawable.round_photobutton).visible();
            }


        }else{
            aq.id(R.id.initials).text(App.prefs.getInitials());
        }


        aq.id(R.id.user_name).text(App.prefs.getUserName());
        aq.id(R.id.user_number).text(App.prefs.getUserNumber());

        aq.id(R.id.email1).text(App.prefs.getUserEmail());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case TAKE_PICTURE:

                if (resultCode == Activity.RESULT_OK) {
                    aq.id(imageEvent).image(imageUri.toString());
                    aq.id(R.id.image_event)
                            .background(R.drawable.round_photobutton).visible();
                    aq.id(R.id.initials).gone();
                }
            case RESULT_GALLERY:
                if (null != data) {

                    Uri selectedImage = data.getData();
                    Bitmap bitmap = null;
                    bitmap = Utils.getBitmap(selectedImage, getActivity(), this.getActivity().getContentResolver());

                    byte[] bitmapArray = Utils.getImageByteArray(bitmap);
                    //fowlloing if conditions works.... upload successful... but u can see/...updateProfileImage()... not working
                    //can we do skype :)
                    if( uploadAttachments(aq, bitmapArray) ){

                    }
//
// try {
//                        bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), selectedImage);
//
//                        Log.e("bitmap",bitmap+"");
//                    } catch (FileNotFoundException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                    Uri selectedImage = data.getData();
//                    Log.e("image",selectedImage+"");
//                    Bundle extras = data.getExtras();

//                    Bitmap imageBitmap = (Bitmap) extras.get("data");
//                  Log.e("image",imageBitmap+"");
//  mImageView.setImageBitmap(imageBitmap);
//                    aq.id(imageEvent).image(imageBitmap);

                }
                break;
            default:
                break;
        }
    }
    //follwoing function is not being called from above onActivityResult
    private void updateProfileImage(final AQuery aq){
        if(path != null){

            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", String.valueOf(App.prefs.getUserId()));
            params.put("set", "profile_image");
            params.put("value", path);
            params.put("table", "user_profile");

            aq.ajax("http://api.heuristix.net/one_todo/v1/user/account", params,
                    JSONObject.class, new AjaxCallback<JSONObject>() {
                        @Override
                        public void callback(String url, JSONObject json,
                                             AjaxStatus status) {
                            try {
                                Log.e("imagd", json.toString());
                                JSONObject obj2 = new JSONObject(json.toString());
                                if (!obj2.getBoolean("error")) {
                                    App.prefs.setUserProfileUri(path);
                                }
                            } catch (Exception e) {

                            }

                        }
                    });
        }
    }
    private Boolean uploadAttachments(final AQuery aq, byte[] byteArray) {

        HttpEntity entity = null;
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("user_image", encoded));

        try {
            entity = new UrlEncodedFormEntity(pairs, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final Map<String, HttpEntity> param = new HashMap<>();

        param.put(AQuery.POST_ENTITY, entity);
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Uploading...Please wait.");
        aq.progress(dialog).ajax("http://api.heuristix.net/one_todo/v1/uploadUserPicture.php", param,
                JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject json,
                                         AjaxStatus status) {

                        try {
                            Log.e("attachment", json.toString());
                            JSONObject obj1 = new JSONObject(json.toString());

                            boolean error = obj1.getBoolean("error");
                            if (error)
                                Toast.makeText(getActivity(), "Error uploading attachment.", Toast.LENGTH_SHORT).show();
                            else {
                                path = obj1.getString("path");
                                App.prefs.setUserProfileUri(path);

                                aq.id(R.id.initials).gone();
                                aq.id(R.id.image_event).gone();

                                aq.id(imageEvent).image(path,true,true);
                                aq.id(R.id.image_event)
                                        .background(R.drawable.round_photobutton).visible();

                                updateProfileImage(aq);





                                boo = true;
                            }
                        } catch (Exception e) {

                        }

                    }
                });
//        if(boo){

//        }


    return boo;
    }

    void setFont() {
        Utils.RobotoRegular(getActivity(), aq.id(R.id.accounts).getTextView());
        Utils.RobotoRegular(getActivity(), aq.id(R.id.email).getTextView());
        Utils.RobotoRegular(getActivity(), aq.id(R.id.mynumber).getTextView());
        Utils.RobotoRegular(getActivity(), aq.id(R.id.personal).getTextView());
        Utils.RobotoRegular(getActivity(), aq.id(R.id.name).getTextView());
        Utils.RobotoRegular(getActivity(), aq.id(R.id.display).getTextView());
        Utils.RobotoMedium(getActivity(), aq.id(R.id.email1).getTextView());
        Utils.RobotoMedium(getActivity(), aq.id(R.id.user_number).getTextView());
        Utils.RobotoMedium(getActivity(), aq.id(R.id.user_name).getTextView());
        Utils.RobotoMedium(getActivity(), aq.id(R.id.display1).getTextView());
    }

}
