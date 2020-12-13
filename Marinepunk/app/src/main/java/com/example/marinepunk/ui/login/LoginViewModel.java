package com.example.marinepunk.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Log;
import android.util.Patterns;

import com.example.marinepunk.R;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private MutableLiveData<FirebaseUser> user;
    private FirebaseAuth auth;

    public LoginViewModel() {
        auth = FirebaseAuth.getInstance();
        user = new MutableLiveData<>(auth.getCurrentUser());
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public LiveData<FirebaseUser> getCurrentUser() {
        return user;
    }

    public void login(String email, String password) {
        // can be launched in a separate asynchronous job
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    this.user.setValue(auth.getCurrentUser());
                    Log.d("login", "signIn:success");
                    loginResult.setValue(new LoginResult(new LoggedInUserView(this.user.getValue().getEmail())));
                } else {
                    Log.d("login", "signIn:fail");
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()) {
                                this.user.setValue(auth.getCurrentUser());
                                loginResult.setValue(new LoginResult(new LoggedInUserView(this.user.getValue().getEmail())));
                                Log.d("register", "signUn:success");
                            } else {
                                loginResult.setValue(new LoginResult(R.string.login_failed));
                                Log.d("register", "signUn:fail");
                            }
                        });
                }
            });
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}