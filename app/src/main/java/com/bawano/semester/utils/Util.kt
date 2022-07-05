package com.bawano.semester.utils

import com.bawano.semester.models.Course
import com.bawano.semester.models.CourseUnit

interface Utils {
    interface OnAccount {
        fun signIn()
        fun signOut()
        fun signUp()
        fun signUpWith(email: String?, password: String?)
        fun openProfile()
        fun signInSuccess()
    }

    interface OnPdf {
        fun click(name: String?)
    }

    interface OnCourse {
        fun click(course: Course?)
    }

    interface OnCourseUnit {
        fun click(courseUnit: CourseUnit?)
    }

    interface ActionBarVisibility {
        fun hide()
        fun show()
    }
}
