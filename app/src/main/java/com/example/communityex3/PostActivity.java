package com.example.communityex3;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mTitle, mContents;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mTitle = findViewById(R.id.post_title_edit);
        mContents = findViewById(R.id.post_contents_edit);

        findViewById(R.id.post_save_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mAuth.getCurrentUser() != null) {
            String postId = mStore.collection(FirebaseId.post).document().getId();
            Map<String, Object> data = new HashMap<>();
            data.put(FirebaseId.documentId, mAuth.getCurrentUser().getUid());
            data.put(FirebaseId.title, mTitle.getText().toString());
            data.put(FirebaseId.contents, mContents.getText().toString());
            data.put(FirebaseId.timestamp, FieldValue.serverTimestamp());
            mStore.collection(FirebaseId.post).document(postId).set(data, SetOptions.merge());
            finish();
        }
    }
}