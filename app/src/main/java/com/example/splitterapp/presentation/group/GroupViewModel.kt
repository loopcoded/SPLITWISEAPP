package com.example.splitterapp.presentation.group

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitterapp.data.db.AppDatabase
import com.example.splitterapp.data.model.Group
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GroupViewModel(application: Application) : AndroidViewModel(application) {
    private val groupDao = AppDatabase.getDatabase(application).groupDao()

    // Backing StateFlow
    private val _groups = groupDao.getAllGroups()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // Public read-only state
    val groups: StateFlow<List<Group>> = _groups

    fun addGroup(
        name: String,
        participants: List<String>,
        title: String,
        amountPaid: Double,
        paidBy: String,
        date: String,
        discount: Double,
        gst: Double
    ) {
        viewModelScope.launch {
            groupDao.insertGroup(
                Group(
                    name = name,
                    participants = participants,
                    title = title,
                    amountPaid = amountPaid,
                    paidBy = paidBy,
                    date = date,
                    discount = discount,
                    gst = gst
                )
            )
        }
    }


    fun deleteGroup(group: Group) {
        viewModelScope.launch {
            groupDao.deleteGroup(group)
        }
    }
}