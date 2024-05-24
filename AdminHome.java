package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helloworld.Gmail.SendMailTask;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;


import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

public class AdminHome extends AppCompatActivity {

    private FirebaseAuth fbauth;
    private FirebaseFirestore fbstore;
    private FirebaseDatabase fbdb;
    private RecyclerView rv;
    private Button btn_accepted;
    private ImageButton signout;
    private DatabaseReference dataref;
    private String uid,dept_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        fbauth = FirebaseAuth.getInstance();
        fbstore = FirebaseFirestore.getInstance();
        fbdb = FirebaseDatabase.getInstance();
        rv = findViewById(R.id.rv_request);
        btn_accepted=findViewById(R.id.accepted_btn);


        uid = fbauth.getUid();
        signout= findViewById(R.id.signout1);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbauth.signOut();
                Intent intent = new Intent(AdminHome.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });




        btn_accepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this,acceptedDetails.class);
                intent.putExtra("dept",dept_name);
                startActivity(intent);
            }
        });
    }

    public static class requestFetchViewHolder extends RecyclerView.ViewHolder{

        public  View eview;
        public requestFetchViewHolder(@NonNull View itemView) {
            super(itemView);
            eview=itemView;
        }
        public void setName(String name){
            TextView textView = eview.findViewById(R.id.req_name);
            textView.setText(name);
        }
        public void setDept(String dept){
            TextView textView= eview.findViewById(R.id.req_dept);
            textView.setText(dept);

        }
        public void setDate(String date)
        {
            TextView textView = eview.findViewById(R.id.req_date);
            textView.setText(date);
        }
        public void setVenue(String venue){
            TextView textView = eview.findViewById(R.id.req_venue);
            textView.setText(venue);
        }
        public void setTime(String time){
            TextView textView = eview.findViewById(R.id.req_time);
            textView.setText(time);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        fbstore.collection("admin").document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        dept_name = documentSnapshot.getString("dept");
                        String path = dept_name+"-REQUEST";
                        dataref = fbdb.getReference().child(path);

                        dataref.keepSynced(true);
                        rv.setHasFixedSize(true);
                        rv.setLayoutManager(new LinearLayoutManager(AdminHome.this));



                        FirebaseRecyclerAdapter<request_fetch,AdminHome.requestFetchViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<request_fetch, requestFetchViewHolder>
                                (request_fetch.class,R.layout.request_card,AdminHome.requestFetchViewHolder.class,dataref) {
                            @Override
                            protected void populateViewHolder(AdminHome.requestFetchViewHolder requestFetchViewHolder, request_fetch request_fetch,final int i) {
                                requestFetchViewHolder.setName(request_fetch.getName());
                                requestFetchViewHolder.setDept(request_fetch.getDept());
                                requestFetchViewHolder.setDate(request_fetch.getDate());
                                requestFetchViewHolder.setTime(request_fetch.getTime());
                                requestFetchViewHolder.setVenue(request_fetch.getVenue());

                                check_avail(request_fetch.getDept(),request_fetch.getDate(),request_fetch.getTime(),request_fetch.getVenue(),request_fetch.getName(),request_fetch.getMail());

                                requestFetchViewHolder.itemView.findViewById(R.id.req_accept).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Map<String,Object> accept = new HashMap<>();
                                        accept.put("name",request_fetch.getName());
                                        accept.put("dept",request_fetch.getDept());
                                        accept.put("date",request_fetch.getDate());
                                        accept.put("email",request_fetch.getMail());
                                        accept.put("venue",request_fetch.getVenue());
                                        accept.put("time",request_fetch.getTime());
                                        String time_venue = request_fetch.getTime().charAt(0)+request_fetch.getVenue();

                                        fbstore.collection(dept_name+"Accept").document(request_fetch.getDate()+request_fetch.getTime()+request_fetch.getVenue())
                                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if(!documentSnapshot.exists()){
                                                    fbstore.collection(dept_name+"Accept").document(request_fetch.getDate()+request_fetch.getTime()+request_fetch.getVenue())
                                                            .set(accept).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(AdminHome.this,"Request Accepted and Hall has been Allocated",Toast.LENGTH_SHORT).show();
                                                            fbdb.getReference(request_fetch.getDept()+"-REQUEST").child(request_fetch.getDate()+" "+request_fetch.getTime()+" "+request_fetch.getVenue()+" "+request_fetch.getName()).removeValue();
                                                            new SendMailTask(AdminHome.this).execute("nechallbooking@gmail.com","nechallbook",request_fetch.getMail(),"The Hall has been successfully allocated","Hall Detais :\n"+request_fetch.getName()+" "+request_fetch.getDate()+" "+request_fetch.getDept()+" "+request_fetch.getVenue()+" "+request_fetch.getTime());
                                                            AdminHome.super.onStart();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(AdminHome.this,"Failed Accepting",Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                                else{
                                                    Toast.makeText(AdminHome.this, "Slot Already Booked", Toast.LENGTH_SHORT).show();
                                                    fbdb.getReference(request_fetch.getDept()+"-REQUEST").child(request_fetch.getDate()+" "+request_fetch.getTime()+" "+request_fetch.getVenue()+" "+request_fetch.getName()).removeValue();
                                                    new SendMailTask(AdminHome.this).execute("nechallbooking@gmail.com","nechallbook",request_fetch.getMail(),"Your Request for booking Hall is Not Succesfull","Hall Detais :\n"+request_fetch.getName()+" "+request_fetch.getDate()+" "+request_fetch.getDept()+" "+request_fetch.getVenue()+" "+request_fetch.getTime());
                                                    AdminHome.super.onStart();
                                                }
                                            }
                                        });


                                    }
                                });
                                requestFetchViewHolder.itemView.findViewById(R.id.req_reject).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(AdminHome.this, "Request Rejected", Toast.LENGTH_SHORT).show();
                                        fbdb.getReference(request_fetch.getDept()+"-REQUEST").child(request_fetch.getDate()+" "+request_fetch.getTime()+" "+request_fetch.getVenue()+" "+request_fetch.getName()).removeValue();
                                        new SendMailTask(AdminHome.this).execute("nechallbooking@gmail.com","nechallbook",request_fetch.getMail(),"Your Request for booking Hall is Not Succesfull","Hall Detais :\n"+request_fetch.getName()+" "+request_fetch.getDate()+" "+request_fetch.getDept()+" "+request_fetch.getVenue()+" "+request_fetch.getTime());
                                    }
                                });

                                requestFetchViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String key_value = getRef(i).getKey();
                                        DatabaseReference req_clicked = getRef(i);
                                        Query dataquery = dataref.child(key_value);

                                        req_clicked.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String clicked = (String) snapshot.child("name").getValue();
                                                Toast.makeText(AdminHome.this, "name : " + clicked, Toast.LENGTH_SHORT).show();
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
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();

    }
    public void check_avail(String dept,String date,String time,String venue,String name,String mail){
        fbstore = FirebaseFirestore.getInstance();
        fbdb = FirebaseDatabase.getInstance();
        fbstore.collection(dept+"Accept").document(date+time+venue)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    fbdb.getReference(dept+"-REQUEST").child(date+" "+time+" "+venue+" "+name).removeValue();
                    new SendMailTask(AdminHome.this).execute("nechallbooking@gmail.com","nechallbook",mail,"Your Request for booking Hall is Not Succesfull","Hall Detais :\n"+name+" "+date+" "+dept+" "+venue+" "+time);
                    Toast.makeText(AdminHome.this,"Request Removed for Hall Unavailable ",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}