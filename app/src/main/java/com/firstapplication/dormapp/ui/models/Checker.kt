package com.firstapplication.dormapp.ui.models

import com.firstapplication.dormapp.data.models.NewsEntity
import com.firstapplication.dormapp.data.models.StudentEntity
import com.firstapplication.dormapp.sealed.CorrectSelect
import com.firstapplication.dormapp.sealed.Empty
import com.firstapplication.dormapp.sealed.ErrorSelect
import com.firstapplication.dormapp.sealed.SelectResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Checker(private val list: List<*>, private val checkType: String) {

    @Suppress("UNCHECKED_CAST")
    suspend fun check(): SelectResult = withContext(Dispatchers.Default) {
        val isSelectedList = checkListType(list)
        return@withContext when {
            list.isEmpty() -> Empty
            isSelectedList -> {
                if (checkType == NEWS_CHECK) {
                    CorrectSelect((list as List<NewsEntity>).map {
                        it.migrateToNewsModel()
                    })
                } else {
                    CorrectSelect((list as List<StudentEntity>).map {
                        it.migrateToStudentModel()
                    })
                }
            }
            else -> ErrorSelect
        }
    }


    private fun checkListType(result: List<*>): Boolean {
        if (checkType == NEWS_CHECK) {
            result.forEach {
                if (it !is NewsEntity) return false
            }
        } else if (checkType == STUDENT_CHECK) {
            result.forEach {
                if (it !is StudentEntity) return false
            }
        }
        return true
    }

    companion object {
        const val NEWS_CHECK = "NEWS"
        const val STUDENT_CHECK = "STUDENT"
    }
}