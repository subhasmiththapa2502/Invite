package com.app.invite.model

import com.google.gson.annotations.SerializedName

data class TeamModel(
    @SerializedName("id")
    val id: String,
    @SerializedName("members")
    val members: MemberModel,
    @SerializedName("plan")
    val plan: PlanModel
)