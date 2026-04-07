package com.example.demo_muzicapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.demo_muzicapp.data.repository.MusicRepository

class AuthViewModel(private val repository: MusicRepository) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    fun login(username: String, password: String) {
        if (username.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Nhập đầy đủ thông tin")
            return
        }
        val role = repository.login(username, password)
        if (role != null) {
            _authState.value = AuthState.Success(username, role)
        } else {
            _authState.value = AuthState.Error("Sai tài khoản hoặc mật khẩu X")
        }
    }

    fun register(username: String, password: String) {
        if (username.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Nhập đầy đủ thông tin")
            return
        }
        if (password.length < 4) {
            _authState.value = AuthState.Error("Mật khẩu tối thiểu 4 ký tự")
            return
        }
        val success = repository.register(username, password)
        if (success) {
            _authState.value = AuthState.RegisterSuccess
        } else {
            _authState.value = AuthState.Error("User đã tồn tại ❌")
        }
    }

    sealed class AuthState {
        data class Success(val username: String, val role: String) : AuthState()
        data class Error(val message: String) : AuthState()
        object RegisterSuccess : AuthState()
    }
}