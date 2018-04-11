package com.igalsample.fxpfirebasesample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivity extends AppCompatActivity {

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseText;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ChildEventListener mTextChildEventListener;
    private Query mTextQuery;

    //RecyclerView
    private RecyclerView mTextRecycler;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter mTextRecyclerAdapter;

    private EditText mTextToSend;
    private Button mSendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {

                    // IF YOU USE AUTH CREATE INTENT TO LOGIN ACTIVITY HERE

                    // EXAMPLE:
//                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
//                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(loginIntent);


                }
            }
        };

        mDatabaseText = mFirebaseDatabase.getReference("TextHolder");
        mDatabaseText.keepSynced(true);

        mTextToSend = (EditText) findViewById(R.id.text_input_content_txt);
        mSendBtn = (Button) findViewById(R.id.send_txt_btn);

        // INSERT DATA TO FIREBASE
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadTextToFirebase();
            }
        });


        // RECYCLER VIEW
        mTextRecycler = (RecyclerView)findViewById(R.id.text_recycler_id);
        mTextRecycler.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mTextRecycler.setLayoutManager(mLinearLayoutManager);

        // LISTENER AND RECYCLER ADAPTER
        attachDatabaseListener();
        setupAdapter();



    }


    private void uploadTextToFirebase() {

        String content = mTextToSend.getText().toString().trim();

        DatabaseReference contentRef = mDatabaseText.push().child("content");
        contentRef.setValue(content);

    }


    private void attachDatabaseListener() {

        if (mTextChildEventListener == null){

            final ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    mTextRecyclerAdapter.notifyDataSetChanged();

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };


            mDatabaseText.addChildEventListener(childEventListener);
            mTextChildEventListener = childEventListener;
        }
    }

    private void setupAdapter() {

        mTextQuery = mDatabaseText;

        FirebaseRecyclerOptions<ModelClass> options =
                new FirebaseRecyclerOptions.Builder<ModelClass>()
                .setQuery(mTextQuery, ModelClass.class).build();

        mTextRecyclerAdapter = new FirebaseRecyclerAdapter<ModelClass, ContentViewHolder>(options) {
            @NonNull
            @Override
            public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_item, parent, false);

                return new ContentViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ContentViewHolder holder, int position, @NonNull ModelClass model) {

                holder.setContent(model.getContent());

            }
        };

        mTextRecycler.setAdapter(mTextRecyclerAdapter);

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mTextRecyclerAdapter != null){

            mTextRecyclerAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mTextRecyclerAdapter != null){

            mTextRecyclerAdapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {

            cleanupListener();
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    public void cleanupListener() {
        if (mTextChildEventListener != null) {

            mDatabaseText.removeEventListener(mTextChildEventListener);
            mTextChildEventListener = null;
        }
    }




}
