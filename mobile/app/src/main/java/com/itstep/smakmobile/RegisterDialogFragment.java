package com.itstep.smakmobile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.itstep.smakmobile.dto.RegisterRequest;
import com.itstep.smakmobile.service.ApiClient;
import com.itstep.smakmobile.service.AuthApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterDialogFragment extends DialogFragment {
    private EditText nameInput, emailInput, passwordInput;
    private Button registerBtn;
    private ImageView togglePasswordVisibility;
    private TextView nameError, emailError, passwordError, linkToLogin;

    private boolean passwordVisible = false;
    private AuthApiService authApiService;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_register_dialog, null);

        nameInput = view.findViewById(R.id.input_name);
        emailInput = view.findViewById(R.id.input_email);
        passwordInput = view.findViewById(R.id.input_password);
        togglePasswordVisibility = view.findViewById(R.id.toggle_password_visibility);

        nameError = view.findViewById(R.id.name_error);
        emailError = view.findViewById(R.id.email_error);
        passwordError = view.findViewById(R.id.password_error);

        registerBtn = view.findViewById(R.id.btn_register);
        linkToLogin = view.findViewById(R.id.link_to_login);

        authApiService = ApiClient.getClient(requireContext()).create(AuthApiService.class);

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

        registerBtn.setOnClickListener(v -> {
            if (validateInputs()) {
                register();
            }
        });

        linkToLogin.setOnClickListener(v -> {
            dismiss();
            new LoginDialogFragment().show(requireActivity().getSupportFragmentManager(), "login");
        });

        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .setCancelable(true)
                .create();
    }

    private boolean validateInputs() {
        boolean valid = true;

        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        nameError.setVisibility(View.GONE);
        emailError.setVisibility(View.GONE);
        passwordError.setVisibility(View.GONE);

        if (name.isEmpty()) {
            nameError.setVisibility(View.VISIBLE);
            valid = false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError.setVisibility(View.VISIBLE);
            valid = false;
        }

        if (password.length() < 8 || !password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
            passwordError.setVisibility(View.VISIBLE);
            valid = false;
        }

        return valid;
    }

    private void register() {
        RegisterRequest request = new RegisterRequest();
        request.name = nameInput.getText().toString().trim();
        request.email = emailInput.getText().toString().trim();
        request.password = passwordInput.getText().toString();

        authApiService.register(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Успішна реєстрація", Toast.LENGTH_SHORT).show();
                    dismiss();
                    new LoginDialogFragment().show(requireActivity().getSupportFragmentManager(), "login");
                } else {
                    Toast.makeText(getContext(), "Помилка реєстрації", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Помилка: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
