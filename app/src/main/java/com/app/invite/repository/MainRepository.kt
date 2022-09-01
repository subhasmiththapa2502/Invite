package com.app.invite.repository

import com.app.invite.model.Role
import com.app.invite.network.DataState
import com.app.invite.network.InviteServiceAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import retrofit2.Response

class MainRepository constructor(private val inviteServiceAPI: InviteServiceAPI) {
    /***
     *  show teams from mock data
     */
    suspend fun showTeams(): Flow<DataState<Response<ResponseBody>>> = flow {
        emit(DataState.Loading)
        try {
            val entry = inviteServiceAPI.getTeams()
            emit(DataState.Success(entry.execute()))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    /***
     * Update data with team id
     *
     */
    suspend fun updateWithTeamId(teamId: String, payload: Role): Flow<DataState<Response<ResponseBody>>> = flow {
        emit(DataState.Loading)
        try {
            val entry = inviteServiceAPI.updateDataWithTeamId(teamId, payload)
            emit(DataState.Success(entry.execute()))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }
}