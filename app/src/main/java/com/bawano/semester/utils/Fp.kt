package com.bawano.semester.utils

import com.bawano.semester.models.CourseUnit
import com.bawano.semester.utils.Constants.COURSES_NODE
import com.bawano.semester.utils.Constants.COURSE_UNITS_NODE
import com.bawano.semester.utils.Constants.ENGINEERING
import com.bawano.semester.utils.Constants.KYAMBOGO
import com.bawano.semester.utils.Constants.STUDENTS_NODE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


object Fp {
    //
    fun database(): DatabaseReference = FirebaseDatabase.getInstance().reference

    fun storage(): StorageReference = FirebaseStorage.getInstance().reference

    fun user(): FirebaseUser? = authInstance().currentUser

    fun authInstance(): FirebaseAuth = FirebaseAuth.getInstance()

    fun engineeringNotesPath(pdfName: String) =
        storage().child(KYAMBOGO).child(ENGINEERING).child("$pdfName.pdf")

    fun courseUnitPath(courseUnit: CourseUnit) =
        database().child(KYAMBOGO).child(COURSE_UNITS_NODE).child(courseUnit.courseCode!!)
            .child(courseUnit.unitCode!!)


    fun courseUnitPath(courseCode: String, unitCode: String) =
        allCourseUnitPath(courseCode).child(unitCode)


    fun allCourseUnitPath(courseCode: String) =
        database().child(KYAMBOGO).child(COURSE_UNITS_NODE).child(courseCode)


    fun coursePath(courseCode: String?) = allCoursePath().child(courseCode!!)

    fun allCoursePath(): DatabaseReference = database().child(KYAMBOGO).child(COURSES_NODE)


    fun userPath() = database().child(STUDENTS_NODE).child(user()!!.uid)

    fun getErrorString(errorCode: String) = when (errorCode) {
        "ERROR_INVALID_CUSTOM_TOKEN" ->
            "The custom token format is incorrect. Please check the documentation."

        "ERROR_CUSTOM_TOKEN_MISMATCH" ->
            "The custom token corresponds to a different audience."

        "ERROR_INVALID_CREDENTIAL" -> "The supplied auth credential is malformed or has expired."

        "ERROR_INVALID_EMAIL" ->
            "The email address is badly formatted."

        "ERROR_WRONG_PASSWORD" ->
            "The password is invalid or the user does not have a password."

        "ERROR_USER_MISMATCH" ->
            "The supplied credentials do not correspond to the previously signed in user."

        "ERROR_REQUIRES_RECENT_LOGIN" ->
            "This operation is sensitive and requires recent authentication. Log in again before retrying this request."

        "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" ->
            "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address."

        "ERROR_EMAIL_ALREADY_IN_USE" ->
            "The email address is already in use by another account."

        "ERROR_CREDENTIAL_ALREADY_IN_USE" ->
            "This credential is already associated with a different user account."

        "ERROR_USER_DISABLED" ->
            "The user account has been disabled by an administrator."

        "ERROR_USER_TOKEN_EXPIRED",
        "ERROR_INVALID_USER_TOKEN" ->
            "The user\\'s credential is no longer valid. The user must sign in again."

        "ERROR_USER_NOT_FOUND" ->
            "There is no user record corresponding to this identifier. The user may have been deleted."

        "ERROR_OPERATION_NOT_ALLOWED" ->
            "This operation is not allowed. This service is Disabled."

        "ERROR_WEAK_PASSWORD" ->
            "The password is invalid it must 6 characters at least"

        else -> "An unknown Error Occurred"
    }

}
