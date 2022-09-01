package com.app.invite.model

import com.google.gson.annotations.SerializedName

data class PlanModel(
    @SerializedName("memberLimit")
    val memberLimit: Int,
    @SerializedName("supporterLimit")
    val supporterLimit: Int
)