package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class Department extends Activity {


    private RecyclerView rv;
    private ProgressDialog loadingbar;
    private FirebaseFirestore fbstore;
    private FirebaseDatabase fbdata;
    private FirebaseAuth fbauth;
    private DatabaseReference dataref;
    private ImageButton signout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department);



        fbstore=FirebaseFirestore.getInstance();
        fbdata = FirebaseDatabase.getInstance();
        fbauth = FirebaseAuth.getInstance();
        dataref = fbdata.getReference().child("Department");
        signout = findViewById(R.id.signout);
        rv = findViewById(R.id.rc1_view);
        dataref.keepSynced(true);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));



        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbauth.signOut();
                Intent intent = new Intent(Department.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK );
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<list_data_dept,Department.departmentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<list_data_dept, departmentViewHolder>
                (list_data_dept.class,R.layout.list_data_dept,Department.departmentViewHolder.class,dataref) {
            @Override
            protected void populateViewHolder(Department.departmentViewHolder departmentViewHolder, list_data_dept list_data_dept,final int i) {
                departmentViewHolder.setName(list_data_dept.getName());
                departmentViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String key_value = getRef(i).getKey();
                        DatabaseReference venue_clicked = getRef(i);
                        Query dataquery = dataref.child(key_value);

                        venue_clicked.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String clicked = (String) snapshot.child("name").getValue();

                                Intent intent = new Intent(Department.this, Period.class);
                                intent.putExtra("dept_name",clicked);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
            }
        };
        rv.setAdapter(firebaseRecyclerAdapter);

    }
    public static class departmentViewHolder extends RecyclerView.ViewHolder{

        public  View eview;
        public departmentViewHolder(@NonNull View itemView) {
            super(itemView);
            eview=itemView;
        }
        public void setName(String name){
            TextView textView = eview.findViewById(R.id.deptName);
            textView.setText(name);
        }
    }
}


