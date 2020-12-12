package com.edu.me.flea.vm;

import android.app.Activity;
import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.edu.me.flea.base.BaseViewModel;
import com.edu.me.flea.config.Config;
import com.edu.me.flea.config.Constants;
import com.edu.me.flea.utils.ToastUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class SignUpViewModel extends BaseViewModel {

    public SignUpViewModel(@NonNull Application application) {
        super(application);
    }

    public void signUp(Activity activity, final String nickName,final String email , final String pwd)
    {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pwd)
            .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Log.d(Config.TAG,"error==>"+task.getException().getMessage());
                    ToastUtils.showShort("Error: " + task.getException().getMessage());
                } else {
                    FirebaseFirestore.getInstance().collection(Constants.Collection.USER).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                final Map<String, Object> user = new HashMap<>();
                                user.put("id", firebaseUser.getUid());
                                user.put("email", email);
                                user.put("name", nickName);
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nickName)
                                    .build();
                                firebaseUser.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("TAG", "User profile updated.");
                                            addUser(firebaseUser.getUid(),user);
                                        }
                                    }
                                });
                            }else{
                                ToastUtils.showShort("Error: " + task.getException().getMessage());
                            }
                        }
                    });
                }
                }
            });
    }

    private void addUser(String id,Map<String, Object> user)
    {
        FirebaseFirestore.getInstance().collection(Constants.Collection.USER)
            .document(id)
            .set(user)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    ToastUtils.showShort("Sign up successful");
                    ARouter.getInstance().build(Config.Page.LOGIN).navigation();
                    closePage();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ToastUtils.showShort("Error: " + e.getMessage());
                }
            });
    }

}
