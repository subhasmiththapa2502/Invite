package com.app.invite.network

import co.infinum.retromock.meta.Mock
import co.infinum.retromock.meta.MockResponse
import com.app.invite.model.Role
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface InviteServiceAPI {

    @Mock
    @MockResponse(body = "mockdata/teams/t1.json")
    @MockResponse(body = "mockdata/teams/t2.json")
    @MockResponse(body = "mockdata/teams/t3.json")
    @MockResponse(body = "mockdata/teams/t4.json")
    @GET("/teams")
    fun getTeams(): Call<ResponseBody>

    @Mock
    @MockResponse(body = "mockdata/qr/1.json")
    @MockResponse(body = "mockdata/qr/2.json")
    @MockResponse(body = "mockdata/qr/3.json")
    @MockResponse(body = "mockdata/qr/4.json")
    @POST("/teams/{team_id}/invites")
    fun updateDataWithTeamId(@Path("team_id") teamId: String, @Body payload: Role): Call<ResponseBody>

}