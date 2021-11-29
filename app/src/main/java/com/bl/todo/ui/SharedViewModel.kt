package com.bl.todo.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl.todo.auth.service.FirebaseAuthentication
import com.bl.todo.ui.wrapper.NoteInfo
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {

    private val _gotoHomePageStatus = MutableLiveData<Boolean>()
    val gotoHomePageStatus = _gotoHomePageStatus as LiveData<Boolean>

    private val _gotoLoginPageStatus = MutableLiveData<Boolean>()
    val gotoLoginPageStatus = _gotoLoginPageStatus as LiveData<Boolean>

    private val _gotoSplashScreenStatus = MutableLiveData<Boolean>()
    val gotoSplashScreenStatus = _gotoSplashScreenStatus as LiveData<Boolean>

    private val _gotoSignupPageStatus = MutableLiveData<Boolean>()
    val gotoSignupPageStatus = _gotoSignupPageStatus as LiveData<Boolean>

    private val _gotoForgotPasswordStatus = MutableLiveData<Boolean>()
    val gotoForgotPasswordStatus = _gotoForgotPasswordStatus as LiveData<Boolean>

    private val _gotoNoteFragmentStatus = MutableLiveData<Boolean>()
    val gotoNoteFragmentStatus = _gotoNoteFragmentStatus as LiveData<Boolean>

    private val _gotoExistingNoteFragmentStatus = MutableLiveData<NoteInfo>()
    val gotoExistingNoteFragmentStatus = _gotoExistingNoteFragmentStatus as LiveData<NoteInfo>

    private val _gotoLabelCreationFragmentStatus = MutableLiveData<Boolean>()
    val gotoLabelCreationFragmentStatus = _gotoLabelCreationFragmentStatus as LiveData<Boolean>

    private val _gotoArchivedFragmentStatus = MutableLiveData<Boolean>()
    val gotoArchivedFragmentStatus = _gotoArchivedFragmentStatus as LiveData<Boolean>

    private val _gotoReminderFragmentStatus = MutableLiveData<Boolean>()
    val gotoReminderFragmentStatus = _gotoReminderFragmentStatus as LiveData<Boolean>

    fun setGotoHomePageStatus(status: Boolean) {
        _gotoHomePageStatus.value = status
    }

    fun setLoginPageStatus(status: Boolean) {
        _gotoLoginPageStatus.value = status
    }

    fun setSplashScreenStatus(status: Boolean) {
        _gotoSplashScreenStatus.value = status
    }

    fun setSignupPageStatus(status: Boolean) {
        _gotoSignupPageStatus.value = status
    }

    fun setForgotPasswordPageStatus(status: Boolean) {
        _gotoForgotPasswordStatus.value = status
    }

    fun setNoteFragmentPageStatus(status: Boolean) {
        _gotoNoteFragmentStatus.value = status
    }

    fun setExistingNoteFragmentStatus(noteInfo: NoteInfo) {
        _gotoExistingNoteFragmentStatus.value = noteInfo
    }

    fun setArchivedFragmentStatus(status : Boolean) {
        _gotoArchivedFragmentStatus.value = status
    }

    fun setReminderFragmentStatus(status: Boolean) {
        _gotoReminderFragmentStatus.value = status
    }

    fun logOutFromApp(context: Context) {
        viewModelScope.launch {
            FirebaseAuthentication.logOut(context)
        }
    }

    fun setLabelCreationFragmentStatus(status : Boolean) {
        _gotoLabelCreationFragmentStatus.value = status
    }

    fun checkUser() : Boolean{
        return FirebaseAuthentication.getCurrentUser() != null
    }
}