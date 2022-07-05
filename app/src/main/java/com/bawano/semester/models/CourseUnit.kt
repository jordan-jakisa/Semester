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
    var code: String?,
    var description: String?,
    var role: Role?,
    var image: String? = null,
    var grade: Grade? = null,
    private var notes: List<String>?
) : Parcelable {
    val isEmpty: Boolean
        get() = code == null || courseCode == null || creditUnits == 0 || role == null
}

object CourseUnitCallback : DiffUtil.ItemCallback<CourseUnit>() {
    override fun areItemsTheSame(oldItem: CourseUnit, newItem: CourseUnit) = oldItem == newItem
    override fun areContentsTheSame(oldItem: CourseUnit, newItem: CourseUnit) = oldItem.code == newItem.code
}

enum class Role {
    CORE, ELECTIVE
}

enum class Grade {
    Year1, Year2, Year3, Year4
}