package com.example.marinepunk.ui.game;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marinepunk.LoginActivity;
import com.example.marinepunk.R;
import com.example.marinepunk.cell.GameState;
import com.example.marinepunk.cell.GameStateStat;
import com.example.marinepunk.help.AccountInfo;
import com.example.marinepunk.viewmodel.ApplicationViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AccountFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 23;
    private ImageView img;
    private EditText nickname;
    private ListView listView;
    private ProgressBar progressBar;
    private Button choose, upload, save;
    private NavController navController;

    private Uri imageUri;

    private FirebaseAuth auth;
    private StorageTask uploadTask;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;
    private DatabaseReference databaseRefStat;
    private ApplicationViewModel applicationViewModel;
    private Button logoutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        applicationViewModel =
                new ViewModelProvider(requireActivity()).get(ApplicationViewModel.class);

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        img =           (ImageView) view.findViewById(R.id.user_image);
        choose =           (Button) view.findViewById(R.id.choose_file);
        upload =           (Button) view.findViewById(R.id.upload);
        save =             (Button) view.findViewById(R.id.save);
        nickname =       (EditText) view.findViewById(R.id.name);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_upload);
        listView =       (ListView) view.findViewById(R.id.list_stat);
        logoutButton =     (Button) view.findViewById(R.id.logoutButton);

        storageRef = FirebaseStorage.getInstance().getReference("uploads");
        databaseRef = FirebaseDatabase.getInstance().getReference("profiles");
        databaseRefStat = FirebaseDatabase.getInstance().getReference("processes");
        auth = FirebaseAuth.getInstance();

        choose.setOnClickListener(view13 -> openFilePicker());

        upload.setOnClickListener(view1 -> {
            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(requireActivity(),
                        "Загрузка в процессе", Toast.LENGTH_SHORT).show();
            } else {
                uploadFile();
            }
        });

        save.setOnClickListener(view12 -> {
            String name = nickname.getText().toString().trim();
            if (name.equals("")){
                Toast.makeText(requireActivity(), "Введите имя", Toast.LENGTH_SHORT).show();
            }
            else{
                databaseRef.child(auth.getUid()).child("nickname").setValue(name);
                Toast.makeText(requireActivity(), "Данные изменены", Toast.LENGTH_SHORT).show();
            }
        });

        databaseRefStat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StatAdapter adapter = new StatAdapter(applicationViewModel);
                String cuid = "";
                if (auth.getCurrentUser() != null) {
                    cuid = auth.getCurrentUser().getUid();
                }
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String user = ds.child("userId").getValue(String.class);
                    String host = ds.child("hostId").getValue(String.class);
                    // if game ended and players are not null
                    if (ds.child("state").getValue(GameState.State.class).equals(GameState.State.Ended) &&
                        user != null && host != null
                    ) {
                        GameStateStat gss = new GameStateStat();
                        gss.gs = ApplicationViewModel.parseGSSnapshot(ds);
                        if (user.contains(cuid)) {
                            gss.victory = user.charAt(0) == '_';
                            gss.code = ds.getKey();
                            adapter.addItem(gss);
                        } else if (host.contains(cuid)) {
                            gss.victory = host.charAt(0) == '_';
                            gss.code = ds.getKey();
                            adapter.addItem(gss);
                        }
                    }
                }
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseRef.child(auth.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                AccountInfo ai = applicationViewModel.getAccountInfo().getValue();
                if (ai != null && snapshot.getValue() != null && auth.getCurrentUser() != null) {
                    if (snapshot.getKey().equals("nickname")){
                        String name = snapshot.getValue().toString();
                        nickname.setText(name);
                        ai.accountName = name;
                    }
                    ai.accountEmail = auth.getCurrentUser().getEmail();
                    if (snapshot.getKey().equals("ImagePath")){
                        String imageUrl = snapshot.getValue().toString();
                        Picasso.get().load(imageUrl).into(img);

                        ai.accountImage = imageUrl;
                        applicationViewModel.setAccountInfo(ai);
                    }
                    applicationViewModel.setAccountInfo(ai);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(requireActivity(), LoginActivity.class);
            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            getActivity().startActivity(i);
            getActivity().finish();
        });

        return view;
    }

    private void openFilePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openFilePicker();
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            Picasso.get().load(data.getData()).into(img);
        }
    }

    private String fileExt(Uri uri) {
        ContentResolver cR = requireActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(){
        if (imageUri != null) {
            StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + fileExt(imageUri));

            uploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Handler handler = new Handler();
                        handler.postDelayed(() -> progressBar.setProgress(0), 500);

                        Toast.makeText(requireActivity(), "Uploaded", Toast.LENGTH_LONG).show();

                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(task -> {
                            String upload = task.getResult().toString();
                            databaseRef.child(auth.getUid()).child("ImagePath").setValue(upload);
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show())
                    .addOnProgressListener(snapshot -> {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressBar.setProgress((int) progress);
                    });
        } else {
            Toast.makeText(requireActivity(), "File wasn't chosen", Toast.LENGTH_SHORT).show();
        }
    }

    private class StatAdapter extends BaseAdapter {

        private final ArrayList<GameStateStat> mData = new ArrayList<GameStateStat>();
        private final LayoutInflater mInflater;
        private final ApplicationViewModel applicationViewModel;

        public StatAdapter(ApplicationViewModel applicationViewModel) {
            this.applicationViewModel = applicationViewModel;
            mInflater = (LayoutInflater)requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final GameStateStat item) {
            mData.add(item);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public GameStateStat getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.list_stat_item, null);
                holder.textView = (TextView)convertView.findViewById(R.id.statLine);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.textView.setText(mData.get(position).code);

            @SuppressLint("UseCompatLoadingForDrawables")
            Drawable background = mData.get(position).victory ?
                    getResources().getDrawable(R.drawable.stat_win) :
                    getResources().getDrawable(R.drawable.stat_end);

            convertView.setBackground(background);

            convertView.setOnClickListener(v -> {
                applicationViewModel.setCurrentGameStateInfo(mData.get(position));
                navController.navigate(R.id.action_nav_account_to_gameStatFragment);
            });
            return convertView;
        }

    }

    public static class ViewHolder {
        public TextView textView;
    }
}