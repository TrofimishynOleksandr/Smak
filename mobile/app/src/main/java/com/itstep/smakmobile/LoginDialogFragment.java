package com.itstep.smakmobile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itstep.smakmobile.auth.AuthManager;
import com.itstep.smakmobile.auth.TokenManager;
import com.itstep.smakmobile.dto.LoginRequest;
import com.itstep.smakmobile.dto.TokenResponse;
import com.itstep.smakmobile.dto.UserInfoDto;
import com.itstep.smakmobile.service.ApiClient;
import com.itstep.smakmobile.service.AuthApiService;
import com.itstep.smakmobile.service.UserApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginDialogFragment extends DialogFragment {
    private EditText emailInput, passwordInput;
    private Button loginBtn;
    private TextView registerLink;
    private boolean passwordVisible = false;
    private ImageView togglePasswordVisibility;

    private AuthApiService authApiService;
    private UserApiService userApiService;
    private TokenManager tokenManager;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_login_dialog, null);

        emailInput = view.findViewById(R.id.input_email);
        passwordInput = view.findViewById(R.id.input_password);
        loginBtn = view.findViewById(R.id.btn_login);
        registerLink = view.findViewById(R.id.link_to_register);
        togglePasswordVisibility = view.findViewById(R.id.toggle_password_visibility);

        togglePasswordVisibility.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                passwordInput.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility);
            } else {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off);
            }
            passwordInput.setSelection(passwordInput.length());
        });

        tokenManager = new TokenManager(requireContext());
        authApiService = ApiClient.getClient(requireContext()).create(AuthApiService.class);
        userApiService = ApiClient.getClient(requireContext()).create(UserApiService.class);

        loginBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            login(email, password);
        });

        registerLink.setOnClickListener(v -> {
            dismiss();
            new RegisterDialogFragment().show(requireActivity().getSupportFragmentManager(), "register");
        });

        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .setCancelable(true)
                .create();
    }

    private void login(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        authApiService.login(request).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TokenResponse tokenResponse = response.body();
                    tokenManager.saveTokens(tokenResponse.accessToken, tokenResponse.refreshToken);

                    userApiService.getMe().enqueue(new Callback<UserInfoDto>() {
                        @Override
                        public void onResponse(Call<UserInfoDto> call, Response<UserInfoDto> userResponse) {
                            if (userResponse.isSuccessful() && userResponse.body() != null) {
                                UserInfoDto user = userResponse.body();
                                new AuthManager(requireContext()).saveUserInfo(user.getId(), user.getRole(), user.getName());

                                Toast.makeText(getContext(), "Успішний вхід!", Toast.LENGTH_SHORT).show();
                                dismiss();

                                requireActivity().recreate();
                            } else {
                                Toast.makeText(getContext(), "Помилка при отриманні користувача", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserInfoDto> call, Throwable t) {
                            Toast.makeText(getContext(), "Помилка з'єднання: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Невірні дані", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Помилка: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}