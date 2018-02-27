package info.sayederfanarefin.location_sharing.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.reflect.Field;

import info.sayederfanarefin.location_sharing.R;
import info.sayederfanarefin.location_sharing.adapter.InviteFriendsContactAdapter;
import info.sayederfanarefin.location_sharing.model.invite_contacts;

import static com.google.android.gms.internal.zzagz.runOnUiThread;

public class InviteFriendsContactsFragment extends Fragment {

    private ProgressDialog mProgress;

    private InviteFriendsContactAdapter inviteFriendsContactAdapter;
    private ListView contact_list;
    private EditText search_box;

    public InviteFriendsContactsFragment() {

    }

    private Button send_invitation_button;
    @Override
    public void onDetach() {
        super.onDetach();

        if(getAllContactsTask != null){
            getAllContactsTask.cancel(true);
            Log.v("-----check", "cacled");
        }

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            Log.v("=x=", "actvty dstryd");
            // throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            // throw new RuntimeException(e);
            Log.v("=x=", "actvty dstryd");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friend_invite_contact, container, false);
        findViews(view);
        InviteFriendsContactsFragment inviteFriendsContactsFragment = new InviteFriendsContactsFragment();
        fragmentTransaction(inviteFriendsContactsFragment);

        mProgress = new ProgressDialog(getActivity());


        inviteFriendsContactAdapter = new InviteFriendsContactAdapter(getActivity(), R.layout.row_invite_friend_contact);

        return view;
    }
    private ProgressBar spinner;
    private LinearLayout spinner_l;

    private AsyncTask<Void, Void, Void> getAllContactsTask;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showPermissionContacts();

    }
    private void fragmentTransaction(Fragment groupFragment) {
//        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.invite_friend_frame, groupFragment);
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();
    }
    private void findViews(View view) {
        contact_list = view.findViewById(R.id.contact_invite);
        spinner_l = view.findViewById(R.id.linlaHeaderProgress);
        spinner = (ProgressBar) view.findViewById(R.id.progressBar1);
        search_box = (EditText) view.findViewById(R.id.id_search_friend_invite_contact);
        send_invitation_button = view.findViewById(R.id.button_send_invitation_contacts);
        send_invitation_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendInvitation();
            }
        });
    }

    private void getContacts(){


        getAllContactsTask = new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected void onPreExecute()
            {
                try {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            contact_list.setVisibility(View.GONE);
                            spinner_l.setVisibility(View.VISIBLE);
                        }
                    });
                    //Thread.sleep(300);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }// End of onPreExecute method

            @Override
            protected Void doInBackground(Void... params)
            {
                ContentResolver cr = getActivity().getContentResolver();
                Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                        null, null, null, null);

                if (cur.getCount() > 0) {
                    while (cur.moveToNext()) {
                        String id = cur.getString(
                                cur.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cur.getString(cur.getColumnIndex(
                                ContactsContract.Contacts.DISPLAY_NAME));
                        if (cur.getInt(cur.getColumnIndex(
                                ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                            Cursor pCur = cr.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                    new String[]{id}, null);
                            while (pCur.moveToNext()) {
                                String phoneNo = pCur.getString(pCur.getColumnIndex(
                                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                                invite_contacts contact = new invite_contacts();
                                contact.setName(name);
                                contact.setContact(phoneNo);
                                inviteFriendsContactAdapter.add(contact);
                            }
                            pCur.close();
                        }
                    }
                }

                return null;
            }// End of doInBackground method

            @Override
            protected void onPostExecute(Void result) {
                try {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            contact_list.setVisibility(View.VISIBLE);
                            spinner_l.setVisibility(View.GONE);
                            contact_list.setAdapter(inviteFriendsContactAdapter);
                        }
                    });
                    //Thread.sleep(300);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(search_box.getText().toString().length() > 0){
                    inviteFriendsContactAdapter.getFilter().filter(search_box.getText().toString());
                }
                search_box.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        inviteFriendsContactAdapter.getFilter().filter(charSequence);
                    }
                    @Override
                    public void afterTextChanged(Editable editable) {}
                });
            }//End of onPostExecute method
        };
        getAllContactsTask.execute((Void[]) null);
    }

    private final int REQUEST_PERMISSION_CONTACTS=2;

    private void showPermissionContacts() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                getActivity(), Manifest.permission.READ_CONTACTS);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

//            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                    Manifest.permission.READ_CONTACTS)) {
//
//                showExplanation("Permission Needed", "Permission to read your contacts is required to run this feature", Manifest.permission.READ_CONTACTS, REQUEST_PERMISSION_CONTACTS);
//            } else {
//                requestPermission(Manifest.permission.READ_CONTACTS, REQUEST_PERMISSION_CONTACTS);
//                //requestPermission(perms, REQUEST_PERMISSION_PHONE_STATE);
//
//            }


            requestPermission(Manifest.permission.READ_CONTACTS, REQUEST_PERMISSION_CONTACTS);
        } else {
            getContacts();

            //  Toast.makeText(ProfileActivity.this, "Permission (already) Granted! extstr", Toast.LENGTH_SHORT).show();
        }
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });

        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{permissionName}, permissionRequestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted

                getContacts();
            } else {
                Toast.makeText(getActivity(), "Until you grant the permission, we canot display the contacts", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(getAllContactsTask != null){
            getAllContactsTask.cancel(true);
           // Log.v("-----check", "cacled");
        }
    }

    private void sendInvitation(){
        for(int i =0; i < contact_list.getAdapter().getCount() ; i++){
            invite_contacts inv = (invite_contacts) contact_list.getAdapter().getItem(i);
            if(inv.getChecked()){
                sendSMS(inv.getContact(),  getResources().getString(R.string.invitation_sms_body));
            }
        }
        Toast.makeText(getActivity(), "Invitation Sent!",
                Toast.LENGTH_LONG).show();
    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
        } catch (Exception ex) {
            //Log.v("---x---" , "sms error " + ex.getMessage());
        }
    }
}
