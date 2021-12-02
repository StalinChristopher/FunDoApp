package com.bl.todo.ui.labels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Database
import com.bl.todo.data.services.DatabaseService
import com.bl.todo.ui.wrapper.LabelDetails
import com.bl.todo.ui.wrapper.UserDetails
import com.bl.todo.common.SharedPref
import com.bl.todo.ui.wrapper.NoteInfo
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

    private val _labelNoteLinkStatus = MutableLiveData<ArrayList<LabelDetails>>()
    val labelNoteLinkStatus = _labelNoteLinkStatus as LiveData<ArrayList<LabelDetails>>

    private val _labelsFromNoteStatus = MutableLiveData<ArrayList<LabelDetails>>()
    val labelFromNoteStatus = _labelsFromNoteStatus as LiveData<ArrayList<LabelDetails>>

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

    fun linkLabelAndNote(context: Context, labelList: ArrayList<LabelDetails>, noteInfo: NoteInfo) {
        viewModelScope.launch {
            val uid = SharedPref.getUserId()
            val userDetails = DatabaseService.getInstance(context).getUserData(uid)
//            removeList.forEach {
//                val linkID = "${noteInfo.fnid}_${it.labelFid}"
//                if (userDetails != null) {
//                    DatabaseService.getInstance(context).removeLabelAndNoteLink(linkID, userDetails)
//                }
//            }
            val labelList = userDetails?.let {
                DatabaseService.getInstance(context).linkLabelAndNote(labelList, noteInfo, it) }
            if(labelList != null)
                _labelNoteLinkStatus.postValue(labelList)
        }
    }

    fun resetLinkLabelAndStatus() {
        _labelNoteLinkStatus.value = null
    }

    fun getLabelFromNote(context: Context, noteInfo: NoteInfo) {
        viewModelScope.launch {
            val uid = SharedPref.getUserId()
            val userDetails = DatabaseService.getInstance(context).getUserData(uid)
            val labelList = userDetails?.let {
                DatabaseService.getInstance(context).getLabelsForNote(noteInfo, userDetails)
            }
            if(labelList != null) {
                _labelsFromNoteStatus.postValue(labelList)
            }
        }
    }

    fun removeLabelAndNoteLink(context: Context, linkId: String) {
        viewModelScope.launch {
            val uid = SharedPref.getUserId()
            val userDetails = DatabaseService.getInstance(context).getUserData(uid)
            if (userDetails != null) {
                DatabaseService.getInstance(context).removeLabelAndNoteLink(linkId, userDetails)
            }
        }
    }


}