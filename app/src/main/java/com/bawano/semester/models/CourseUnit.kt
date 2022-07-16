package com.bawano.semester.models

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import kotlinx.parcelize.Parcelize

@Parcelize
class CourseUnit(
    var id: Int = 0,
    var title: String?,
    var courseCode: String?,
    var creditUnits: Int,
    var unitCode: String?,
    var description: String?,
    var tier: Tier?,
    var image: String? = null,
) : Parcelable {
    val isEmpty: Boolean
        get() = unitCode == null || courseCode == null || creditUnits == 0 || tier == null
}

object CourseUnitCallback : DiffUtil.ItemCallback<CourseUnit>() {
    override fun areItemsTheSame(oldItem: CourseUnit, newItem: CourseUnit) = oldItem == newItem
    override fun areContentsTheSame(oldItem: CourseUnit, newItem: CourseUnit) = oldItem.unitCode == newItem.unitCode
}

enum class Tier {
    CORE, ELECTIVE
}

enum class Grade {
    Year1, Year2, Year3, Year4
}