package com.app.invite.model

import com.google.gson.annotations.SerializedName

data class MemberModel(
    @SerializedName("administrators")
    val administrators: Int,
    @SerializedName("editors")
    val editors: Int,
    @SerializedName("managers")
    val managers: Int,
    @SerializedName("members")
    val members: Int,
    @SerializedName("supporters")
    val supporters: Int,
    @SerializedName("total")
    val total: Int
)