package com.app.invite.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.invite.model.Role
import com.app.invite.network.DataState
import com.app.invite.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val teamsRepository: MainRepository) :
    ViewModel() {

    val dataState: MutableLiveData<DataState<Response<ResponseBody>>> =
        MutableLiveData<DataState<Response<ResponseBody>>>()

    val roleState: MutableLiveData<DataState<Response<ResponseBody>>> =
        MutableLiveData<DataState<Response<ResponseBody>>>()

    fun getTeams() {
        viewModelScope.launch {
            teamsRepository.showTeams().onEach {
                dataState.value = it
            }.launchIn(viewModelScope)
        }
    }

    fun updateRole(teamId: String, payload: Role) {
        viewModelScope.launch {
            teamsRepository.updateWithTeamId(teamId, payload).onEach {
                roleState.value = it
            }.launchIn(viewModelScope)
        }
    }

}