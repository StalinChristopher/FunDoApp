package com.bl.todo.ui.labels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bl.todo.data.services.DatabaseService
import com.bl.todo.ui.wrapper.LabelDetails
import com.bl.todo.ui.wrapper.UserDetails
import com.bl.todo.common.SharedPref
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class LabelViewModel : ViewModel() {

    private val _addLabelStatus = MutableLiveData<LabelDetails>()
    val addLabelStatus = _addLabelStatus as LiveData<LabelDetails>

    private val _profileData = MutableLiveData<UserDetails>()
    val profileData = _profileData as LiveData<UserDetails>

    private val _getAllLabelsStatus = MutableLiveData<ArrayList<LabelDetails>>()
    val getAllLabelStatus = _getAllLabelsStatus as LiveData<ArrayList<LabelDetails>>

    private val _deleteLabelStatus = MutableLiveData<LabelDetails>()
    val deleteLabelStatus = _deleteLabelStatus as LiveData<LabelDetails>

    private val _editLabelStatus = MutableLiveData<LabelDetails>()
    val editLabelStatus = _editLabelStatus as LiveData<LabelDetails>

    fun addNewLabel(context : Context, label : LabelDetails){
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            var dateTime = cal.time
            label.dateModified = dateTime
            var uid = SharedPref.getUserId()
            var userDetails = DatabaseService.getInstance(context).getUserData(uid)
            val label =
                userDetails?.let { DatabaseService.getInstance(context).addNewLabel(label, it) }
            if(label != null){
                _addLabelStatus.postValue(label)
            }
        }
    }

    fun getUserData(context: Context) {
        val uid = SharedPref.getUserId()
        viewModelScope.launch {
            var userDetails = DatabaseService.getInstance(context).getUserData(uid)
            if (userDetails != null) {
                _profileData.postValue(userDetails)
            }
        }
    }

    fun getAllLabels(context: Context) {
        viewModelScope.launch {
            var uid = SharedPref.getUserId()
            var userDetails = DatabaseService.getInstance(context).getUserData(uid)
            var labelList = userDetails?.let { DatabaseService.getInstance(context).getAllLabels(it) }
            if(labelList != null) {
                _getAllLabelsStatus.postValue(labelList)
            }
        }
    }

    fun deleteLabel(context: Context, label: LabelDetails) {
        viewModelScope.launch {
            var uid = SharedPref.getUserId()
            var userDetails = DatabaseService.getInstance(context).getUserData(uid)
            var deletedLabel =
                userDetails?.let { DatabaseService.getInstance(context).deleteLabel(label, it) }
            if(deletedLabel != null) {
                _deleteLabelStatus.postValue(deletedLabel)
            }
        }
    }

    fun editLabel(context: Context, label: LabelDetails) {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            var dateTime = cal.time
            label.dateModified = dateTime
            var uid = SharedPref.getUserId()
            var userDetails = DatabaseService.getInstance(context).getUserData(uid)
            val editedLabel =
                userDetails?.let { DatabaseService.getInstance(context).editLabel(label, it) }
            if(editedLabel != null) {
                _editLabelStatus.postValue(editedLabel)
            }
        }
    }
}