package com.vector.onetodo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.vector.model.AssignedTaskData;
import com.vector.model.TaskData;
import com.vector.onetodo.db.gen.ToDo;
import com.vector.onetodo.db.gen.ToDoDao;
import com.vector.onetodo.utils.Utils;

import net.appkraft.parallax.ParallaxScrollView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import it.feio.android.checklistview.ChecklistManager;
import it.feio.android.checklistview.exceptions.ViewNotSupportedException;

public class TaskView extends BaseActivity {

    AQuery aq, aqd, aq_menu,aq_imgAlert;
    AlertDialog alert;
    ParallaxScrollView parallax;
    ImageView image;
    private boolean isAssignedTask = false;
    private PopupWindow popupWindowTask;
    long todoId;
    long todoTypeID;
    private Boolean rsvp = false;
    private Boolean commentFrg = false;
    private static String taskAddress = "";
    private ToDo obj;

//    GoogleMap map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewtask);



        parallax = (ParallaxScrollView) findViewById(R.id.scrollView123);
        image = (ImageView) findViewById(R.id.imageView1123);
        aq = new AQuery(this);
        init();
    }

    private void init() {
        isAssignedTask = getIntent().getBooleanExtra("is_assigned_task", false);
        todoId = getIntent().getLongExtra("todo_id", -1);
        rsvp = getIntent().getBooleanExtra("rsvp", false);
        commentFrg = getIntent().getBooleanExtra("commentFrg", false);
        ToDoDao toDoDao = App.daoSession.getToDoDao();
        obj = toDoDao.load(todoId);
        ShowTaskViewData();

        final View view1 = getLayoutInflater().inflate(
                R.layout.landing_menu, null, false);
        aq_menu = new AQuery(this, view1);

        if(todoTypeID == 1){
            aq_menu.id(R.id.menu_item1).gone();
            aq_menu.id(R.id.menu_item2).text("Comments").clicked(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindowTask.dismiss();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, AddTaskComment.newInstance(isAssignedTask, todoId))
                            .addToBackStack(null)
                            .commit();
                }
            });
        }else if(todoTypeID == 2){
            aq_menu.id(R.id.menu_item1).text("RSVP");

            aq_menu.id(R.id.menu_item2).text("Comments").clicked(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindowTask.dismiss();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, AddTaskComment.newInstance(isAssignedTask, todoId))
                            .addToBackStack(null)
                            .commit();
                }
            });
        }else{
            aq_menu.id(R.id.menu_item1).gone();
            aq_menu.id(R.id.menu_item2).text("Comments").clicked(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindowTask.dismiss();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, AddTaskComment.newInstance(isAssignedTask, todoId))
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
        aq_menu.id(R.id.menu_item3).visibility(View.GONE);




        popupWindowTask = new PopupWindow(view1, Utils.getDpValue(150,
                this), WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindowTask.setBackgroundDrawable(getResources().getDrawable(
                android.R.drawable.dialog_holo_light_frame));
        popupWindowTask.setOutsideTouchable(true);

        View dialog = getLayoutInflater().inflate(R.layout.rsvp,
                null, false);
        aqd = new AQuery(dialog);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialog);
        alert = builder.create();

        aqd.id(R.id.cancel).clicked(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                alert.dismiss();
            }
        });
        aqd.id(R.id.ok).clicked(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                alert.dismiss();
            }
        });

        aq_menu.id(R.id.menu_item1).clicked(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                popupWindowTask.dismiss();
                alert.show();
            }
        });

        aq.id(R.id.backview).clicked(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                TaskView.this.finish();
            }
        });
        aq.id(R.id.imageView4).clicked(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (popupWindowTask.isShowing())
                    popupWindowTask.dismiss();
                else {
                    popupWindowTask.showAsDropDown(aq.id(R.id.imageView4)
                            .getView(), 0, 0);
                }
            }
        });
        parallax.setImageViewToParallax(image);

        if(isAssignedTask)
            aq.id(R.id.btn_edit).getView().setVisibility(View.INVISIBLE);
        aq.id(R.id.btn_edit).clicked(new OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (obj.getTodo_type_id()){
                    case 1:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.container, AddTaskFragment.newInstance(0, true, todoId))
                                .commit();
                        break;
                    case 2:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.container, AddEventFragment.newInstance(0, true, todoId))
                                .commit();
                        break;
                }
            }
        });

        if(rsvp){
//            Log.d("received intent", "yes");
            alert.show();
        }
        if(commentFrg){
            Log.d("CommentFRG", "Got into Comment frg");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, AddTaskComment.newInstance(isAssignedTask, todoId))
                    .addToBackStack(null)
                    .commit();
        }


        aq.id(R.id.backview).clicked(new OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskView.this.finish();
            }
        });

    }
    private void ShowTaskViewData(){
//        ArrayList<ItemDetails> items = GetSearchResults();
//        aq.id(R.id.invitee_list).adapter(new InviteeAdapter(this, items));
        int serverTaskPosition = -1;
        todoTypeID = obj.getTodo_type_id();
        if(obj.getTodo_type_id() == 2){
            aq.id(R.id.imageView1123).image(R.drawable.view_event);
        }
        aq.id(R.id.title).text(obj.getTitle());
        if(obj.getIs_assigned_task() != null && obj.getIs_assigned_task()){
            aq.id(R.id.sender_name).text("Assigned by "+ obj.getUser_name());
            aq.id(R.id.btn_edit).invisible();
        }else{
            aq.id(R.id.sender_name).invisible();
        }
        try{
            if(!obj.getRepeat().getRepeat_interval().isEmpty()){
                aq.id(R.id.repeat).text(obj.getRepeat().getRepeat_interval()).visible();
                aq.id(R.id.repeat_layout).visible();
            }
        }catch (NullPointerException npe){
            //no need to do anything
        }
        String strDate = String.valueOf(obj.getStart_date());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(strDate));
        strDate = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT,
                Locale.US)
                + " "
                + calendar.get(Calendar.DAY_OF_MONTH)
                + " "
                + calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT,
                Locale.US) + " " + calendar.get(Calendar.YEAR);
        aq.id(R.id.time).text(strDate);
        try{
            if(!obj.getLocation().isEmpty()) {
                taskAddress = obj.getLocation();
                aq.id(R.id.location).text(obj.getLocation());
                aq.id(R.id.gmap_anchortext).text("map");
                aq.id(R.id.location_layout).visible();
            }
        }catch (NullPointerException npe){}


        try{
            if(obj.getReminder().getTime() == 0) {
                aq.id(R.id.reminder).text("Remind on time");
            }else{
                aq.id(R.id.reminder).text("Remind before " + Integer.parseInt(obj.getReminder().getTime().toString()) / 60000 + " mins");
            }
            aq.id(R.id.reminder_layout).visible();
        }catch (NullPointerException npe){}
        try{
            if(!obj.getRepeat().getRepeat_interval().isEmpty() ) {
                aq.id(R.id.repeat_view).text(obj.getRepeat().getRepeat_interval());
                aq.id(R.id.repeat_layout).visible();
            }
        }catch (NullPointerException npe){}
        try {
            if (!obj.getCheckList().getTitle().isEmpty()){
                aq.id(R.id.check_list).text(obj.getCheckList().getTitle());
                aq.id(R.id.check_list_layout).visible();
                toggleCheckList(aq.id(R.id.check_list).getView());
                aq.id(R.id.sub_task_header).clicked(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showHideCheckListView();
                    }
                });
                aq.id(R.id.check_list).getView().setEnabled(false);
                aq.id(R.id.arrow).clicked(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showHideCheckListView();
                    }
                });
            }
        }catch (NullPointerException npe){}
        try{
            if(!obj.getNotes().isEmpty()) {
                aq.id(R.id.notes).text(obj.getNotes());
                aq.id(R.id.notes_layout).visible();
            }
        }catch (NullPointerException npe){}

        if(!isAssignedTask) {
            Log.e("todoSize",TaskData.getInstance().result.todos.size()+"");

            for (int i = 0; i < TaskData.getInstance().result.todos.size(); i++) {
                try {
                    if (obj.getTodo_server_id() == Integer.parseInt(TaskData.getInstance().result.todos.get(i).id)) {
                        serverTaskPosition = i;
                        break;
                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            try {
                for (int i = 0; i < TaskData.getInstance().result.todos.get(serverTaskPosition).todo_attachment.size(); i++)
                    showAttachments(TaskData.getInstance().result.todos.get(serverTaskPosition).todo_attachment.get(i));
            } catch (Exception npe) {
                npe.printStackTrace();
            }
        }else{

            try {
                for (int i = 0; i < AssignedTaskData.getInstance().task.get(1).attachments.size(); i++)
                    showAttachments(AssignedTaskData.getInstance().task.get(1).attachments.get(i));
            } catch (Exception npe) {
                npe.printStackTrace();
            }
        }

        if(!isAssignedTask && serverTaskPosition != -1) {
            if (TaskData.getInstance().result.todos.get(serverTaskPosition).invitee_list != null) {
                aq.id(R.id.invitee_layout).visible();
                if (TaskData.getInstance().result.todos.get(serverTaskPosition).invitee_list.accepted != null) {
                    int size = TaskData.getInstance().result.todos.get(serverTaskPosition).invitee_list.accepted.size();
                    String[] forCommaSeparated = new String[size];
                    for (int i = 0; i < size; i++)
                        forCommaSeparated[i] = TaskData.getInstance().result.todos.get(serverTaskPosition).invitee_list.accepted.get(i).name;

                    String acceptedNames = Joiner.on(", ").join(forCommaSeparated);
                    aq.id(R.id.accepted).visible().text(acceptedNames);
                }
                if (TaskData.getInstance().result.todos.get(serverTaskPosition).invitee_list.pending != null) {

                    int size = TaskData.getInstance().result.todos.get(serverTaskPosition).invitee_list.pending.size();
                    String[] forCommaSeparated = new String[size];
                    for (int i = 0; i < size; i++)
                        forCommaSeparated[i] = TaskData.getInstance().result.todos.get(serverTaskPosition).invitee_list.pending.get(i).name;

                    String acceptedNames = Joiner.on(", ").join(forCommaSeparated);
                    aq.id(R.id.pending).visible().text(acceptedNames);
                }
                if (TaskData.getInstance().result.todos.get(serverTaskPosition).invitee_list.rejected != null) {
                    int size = TaskData.getInstance().result.todos.get(serverTaskPosition).invitee_list.accepted.size();
                    String[] forCommaSeparated = new String[size];
                    for (int i = 0; i < size; i++)
                        forCommaSeparated[i] = TaskData.getInstance().result.todos.get(serverTaskPosition).invitee_list.rejected.get(i).name;

                    String acceptedNames = Joiner.on(", ").join(forCommaSeparated);
                    aq.id(R.id.rejected).visible().text(acceptedNames);
                }
            }
        }
    }
//    @Override
//    public void onMapReady(GoogleMap map) {
//
//        if(map != null){
//            Log.e("mapObj", map+"");
//            map.addMarker(new MarkerOptions()
//                    .position(new LatLng(37.7750, 122.4183))
//                    .title("San Francisco").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//        }
//
//
//    }
    public void onMapClick(View v){
        Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4192")
                .buildUpon()
                .appendQueryParameter("q", taskAddress
                )
                .build();
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }
    private void showHideCheckListView() {

        if(aq.id(R.id.check_list).getView().getVisibility() == View.VISIBLE){
            aq.id(R.id.check_list).gone();
            aq.id(R.id.arrow).image(R.drawable.arrow_down);
        }
        else{
            aq.id(R.id.check_list).visible();
            aq.id(R.id.arrow).image(R.drawable.arrow);
        }
    }

    //    private void populateAssignedTaskData(){
//        AssignedTaskData obj = AssignedTaskData.getInstance();
//        aq.id(R.id.title).text(obj.task.get(0).title);
//        try {
//            aq.id(R.id.repeat).text(obj.task.get(0).repeatUntil);
//            aq.id(R.id.repeat_layout).visible();
//        }catch (NullPointerException npe){}
//
//        String strDate = String.valueOf(obj.task.get(0).startDate);
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(Utils.milliFromServerDate(strDate));
//        strDate = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT,
//                Locale.US)
//                + " "
//                + calendar.get(Calendar.DAY_OF_MONTH)
//                + " "
//                + calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT,
//                Locale.US) + " " + calendar.get(Calendar.YEAR);
//        aq.id(R.id.time).text(strDate);
//        try{
//            aq.id(R.id.location).text(obj.task.get(0).location);
//            aq.id(R.id.location_layout).visible();
//        }catch (NullPointerException npe){}
//        try {
////            aq.id(R.id.reminder).text("Remind before " + obj.task.get(0). / 60000 + " mins");
//        }catch (NullPointerException npe){}
//        try {
//            aq.id(R.id.repeat_view).text(obj.task.get(0).repeatUntil);
//            aq.id(R.id.repeat_layout).visible();
//        }catch (NullPointerException npe){}
//        try {
//            aq.id(R.id.check_list).text(obj.task.get(0).checkListData);
//            aq.id(R.id.check_list_layout).visible();
//            toggleCheckList(aq.id(R.id.check_list).getView());
//        }catch (NullPointerException npe){}
//        try{
//            aq.id(R.id.notes).text(obj.task.get(0).notes);
//            aq.id(R.id.notes_layout).visible();
//        }catch (NullPointerException npe){}
//    }
    private void toggleCheckList(View switchView) {
        View newView;
        try {
            ChecklistManager mChecklistManager = ChecklistManager
                    .getInstance(this);
            mChecklistManager.setNewEntryHint("Add a sub task");
            mChecklistManager.setMoveCheckedOnBottom(1);
            mChecklistManager.setKeepChecked(true);
            mChecklistManager.setShowChecks(true);
            newView = mChecklistManager.convert(switchView);
            mChecklistManager.replaceViews(switchView, newView);

        } catch (ViewNotSupportedException e) {
            e.printStackTrace();
        }
    }


    private void showAttachments(String imageUrl){
        aq.id(R.id.attachment_layout).visible();
        final String image_url = imageUrl;
        try {
            final LinearLayout item = (LinearLayout) aq
                    .id(R.id.added_image_outer).visible().getView();
            item.setClickable(true);
            item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonClicked(image_url);
                }
            });
            final View child = getLayoutInflater().inflate(
                    R.layout.image_added_layout, null);

            ImageView image = (ImageView) child
                    .findViewById(R.id.image_added);


            AQuery aq = new AQuery(child);
            Bitmap preset = aq.getCachedImage(imageUrl);

            if(preset != null){
                aq.id(image).image(preset);
            }
            else {
                aq.id(image).image(imageUrl, true, true, 0, 0, new BitmapAjaxCallback() {

                    @Override
                    public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                        iv.setImageBitmap(Utils.getRoundedCornerBitmap(bm, 8));
                        //do something to the bitmap
                        bm.getByteCount();
                        TextView size = (TextView) child
                                .findViewById(R.id.image_added_size);
                        size.setText("( " + bm.getByteCount() / 1024 + " KB)");

                    }

                });
            }

            child.findViewById(R.id.image_menu).setVisibility(View.GONE);
            TextView by = (TextView) child
                    .findViewById(R.id.image_added_by);
            Calendar cal = Calendar.getInstance();

            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
            if(obj.getIs_assigned_task() != null && obj.getIs_assigned_task()){
                by.setText("By " + obj.getUser_name()+" on " + sdf.format(cal.getTime()));
            }else{
                by.setText("By " + App.prefs.getUserName()+" on " + sdf.format(cal.getTime()));
            }
            item.addView(child);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buttonClicked(final String imgURL){
        Bitmap imgBitmap = aq.getCachedImage(imgURL);
        Log.e("imgpp", imgBitmap+"");
        final Dialog settingsDialog = new Dialog(this);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.image_layout
                , null));

        ImageView imgView = (ImageView) settingsDialog.findViewById(R.id.mainImage);
        imgView.setImageBitmap(imgBitmap);

        Button button = (Button) settingsDialog.findViewById(R.id.alertImageButton);
        button.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                settingsDialog.dismiss();
            }
        });
//        aq_imgAlert = new AQuery(this,settingsDialog);
        settingsDialog.show();

    }


}
