package com.bawano.semester.repo

import com.bawano.semester.models.Course
import com.bawano.semester.utils.Fp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow

class Repository {
    fun fetchCourses() = callbackFlow<Result<List<Course>>> {
        val listener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.map { ds ->
                    ds.getValue(Course::class.java)
                }
                this@callbackFlow.trySendBlocking(Result.success(items.filterNotNull()))
                Fp.allCoursePath().removeEventListener(this)
            }

            override fun onCancelled(error: DatabaseError) {
                this@callbackFlow.trySendBlocking(Result.failure(error.toException()))
                Fp.allCoursePath().removeEventListener(this)
            }

        }
        Fp.allCoursePath().addValueEventListener(listener)
        awaitClose{ Fp.allCoursePath().removeEventListener(listener)}
    }
}