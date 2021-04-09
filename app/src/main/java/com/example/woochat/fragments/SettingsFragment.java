package com.example.woochat.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.woochat.Landing;
import com.example.woochat.R;
import com.example.woochat.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kennyc.bottomsheet.BottomSheetListener;
import com.kennyc.bottomsheet.BottomSheetMenuDialogFragment;
import com.stfalcon.imageviewer.StfalconImageViewer;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    public static final int GALLERY_IMAGE_OK = 3030;
    public static final int CAMERA_IMAGE_OK = 4040;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    Button logoutButton;
    Button changeProfileButton;
    String userName;
    String userId;
    String userEmail;
    String imageUrl;

    TextView userNameText;
    TextView userIdText;
    TextView userEmailText;
    ImageView userImage;

    public SettingsFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment SettingsFragment.
     */
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        Bundle bundle = this.getArguments();
        assert bundle != null;
        userId = bundle.getString("id");

        databaseReference = FirebaseDatabase.getInstance().getReference("user");
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                assert currentUser != null;
                userName = currentUser.name;
                userNameText.setText(userName);

                userEmail = currentUser.email;
                userEmailText.setText(userEmail);

                imageUrl = currentUser.imageUrl;
                userImage = getView().findViewById(R.id.profile_image);

                Glide
                        .with(getContext())
                        .load(imageUrl)
                        .thumbnail(Glide.with(getContext()).load(R.drawable.loading))
                        .into(userImage);

                userImage.setOnClickListener(v -> new StfalconImageViewer.Builder<>(getContext(),
                        new String[]{imageUrl}, (imageView, image) ->
                        Glide
                                .with(getContext())
                                .load(image)
                                .thumbnail(Glide.with(getContext()).load(R.drawable.loading))
                                .into(imageView))
                        .withTransitionFrom(userImage)
                        .show());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        userNameText = view.findViewById(R.id.userName_textView);
        userIdText = view.findViewById(R.id.userId_textView);
        userEmailText = view.findViewById(R.id.userEmail_textView);

        userIdText.setText(userId);

        logoutButton = view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> logout());

        changeProfileButton = view.findViewById(R.id.profileChange_button);
        changeProfileButton.setOnClickListener(this::showBottomDialog);
        return view;
    }


    public void logout() {
        firebaseAuth.signOut();
        Intent intent = new Intent(getContext(), Landing.class);
        Toast.makeText(getContext(), "Successfully Log Out!", Toast.LENGTH_LONG).show();
        startActivity(intent);
    }

    public void showBottomDialog(View view) {
        new BottomSheetMenuDialogFragment.Builder(getContext())
                .setSheet(R.menu.bottom_sheet_menu)
                .setTitle(R.string.select_an_image)
                .setListener(new BottomSheetListener() {
            @Override
            public void onSheetShown(@NonNull BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment, Object o) {

            }

            @Override
            public void onSheetItemSelected(@NonNull BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment, @NonNull MenuItem menuItem, Object o) {
                if (menuItem.getItemId() == R.id.camera) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    startActivityForResult(intent, CAMERA_IMAGE_OK);

                } else {
                    startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), GALLERY_IMAGE_OK);
                }
            }

            @Override
            public void onSheetDismissed(@NonNull BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment, Object o, int i) {

            }
        }).show(getFragmentManager());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_IMAGE_OK || requestCode == GALLERY_IMAGE_OK) {
            System.out.println("OK!");
            System.out.println(data);
            if (data != null) {
                uploadImageToFireBaseStorage(data, requestCode == CAMERA_IMAGE_OK);
            }
        }
    }

    private void uploadImageToFireBaseStorage(Intent intent, boolean isCamera) {
        Uri imageUri;

        // When upload gallery image.
        if (!isCamera) {
            imageUri = intent.getData();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_image/" + System.currentTimeMillis() + ".png");
            storageReference.putFile(imageUri).continueWithTask(task1 -> {
                if (!task1.isSuccessful()) {
                    throw task1.getException();
                }
                return storageReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri uri = task.getResult();
                    imageUrl = uri.toString();
                    databaseReference.child(userId).child("imageUrl").setValue(uri.toString());
                    Toast.makeText(getContext(), "Successfully Upload Image!", Toast.LENGTH_SHORT).show();

                    Glide
                            .with(getContext())
                            .load(imageUrl)
                            .thumbnail(Glide.with(getContext()).load(R.drawable.loading))
                            .into(userImage);
                } else {
                    Toast.makeText(getContext(), "Failed to Upload Image!", Toast.LENGTH_SHORT).show();
                }
            });

            // When upload camera image
        } else {
            Bitmap photo = (Bitmap) intent.getExtras().get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            byte[] bytes = stream.toByteArray();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_image/" + System.currentTimeMillis() + ".png");
            storageReference.putBytes(bytes).continueWithTask(task1 -> {
                if (!task1.isSuccessful()) {
                    throw task1.getException();
                }
                return storageReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri uri = task.getResult();
                    imageUrl = uri.toString();
                    databaseReference.child(userId).child("imageUrl").setValue(uri.toString());
                    Toast.makeText(getContext(), "Successfully Upload Image!", Toast.LENGTH_SHORT).show();

                    Glide
                            .with(getContext())
                            .load(imageUrl)
                            .thumbnail(Glide.with(getContext()).load(R.drawable.loading))
                            .into(userImage);
                } else {
                    Toast.makeText(getContext(), "Failed to Upload Image!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}