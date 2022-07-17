package com.bawano.semester.ui.units

import androidx.lifecycle.ViewModel
import com.bawano.semester.repo.Repository

class CourseUnitsViewModel : ViewModel() {

    private val repository by lazy {
        Repository()
    }

    fun fetchCourseUnits(courseCode: String) = repository.fetchCourseUnits(courseCode)
}