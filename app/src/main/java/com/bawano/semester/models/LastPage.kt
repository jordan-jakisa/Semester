package com.bawano.semester.models

import android.os.Bundle
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LastPage(
    var name:String = "",
    var pageVariables: HashMap<String, String> = HashMap(),
    var navState:Bundle? = null
): Parcelable
