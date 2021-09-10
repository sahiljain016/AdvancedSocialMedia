package com.gic.memorableplaces.utils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class QueryDatabase {
    public static DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    public static DataSnapshot MethodSnapshot;
    //FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static void TwoLineQuerySingleValueEvent(final Runnable runnable, String Field1, String Field2) {


        Query query = myRef.child(Field1)
                .child(Field2);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MethodSnapshot = snapshot;
                runnable.run();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void SetFieldsInDatabase5Lines(int NumberOfLines, String Field1, String Field2,
                                                 String Field3, String Field4, String Field5,
                                                 String Field6, String Field7) {
        if (NumberOfLines == 2) {
            myRef.child(Field1)
                    .setValue(Field2);
        } else if (NumberOfLines == 3) {
            myRef.child(Field1)
                    .child(Field2)
                    .setValue(Field3);
        } else if (NumberOfLines == 4) {
            myRef.child(Field1)
                    .child(Field2)
                    .child(Field3)
                    .setValue(Field4);
        } else if (NumberOfLines == 5) {
            myRef.child(Field1)
                    .child(Field2)
                    .child(Field3)
                    .child(Field4)
                    .setValue(Field5);
        } else if (NumberOfLines == 6) {
            myRef.child(Field1)
                    .child(Field2)
                    .child(Field3)
                    .child(Field4)
                    .child(Field5)
                    .setValue(Field6);
        } else if (NumberOfLines == 76) {
            myRef.child(Field1)
                    .child(Field2)
                    .child(Field3)
                    .child(Field4)
                    .child(Field5)
                    .child(Field6)
                    .setValue(Field7);
        }
    }

}
