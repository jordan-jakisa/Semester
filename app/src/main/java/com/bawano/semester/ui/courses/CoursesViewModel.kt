package com.bawano.semester.ui.courses

import androidx.lifecycle.ViewModel
import com.bawano.semester.repo.Repository

class CoursesViewModel : ViewModel() {
    private val repository by lazy {
        Repository()
    }

    fun fetchCourses() = repository.fetchCourses()
}