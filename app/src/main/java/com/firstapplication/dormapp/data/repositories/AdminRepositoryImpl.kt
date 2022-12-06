package com.firstapplication.dormapp.data.repositories

import android.util.Log
import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.data.models.NewsEntity
import com.firstapplication.dormapp.data.models.SingleEvent
import com.firstapplication.dormapp.data.remote.PACKAGE_NEWS
import com.firstapplication.dormapp.sealed.CorrectResult
import com.firstapplication.dormapp.sealed.InsertResult
import com.firstapplication.dormapp.sealed.ProgressResult
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : AdminRepository {

    private var insertedValueId = ""

    private val _newsData = MutableStateFlow(listOf(NewsEntity()))
    val newsData: StateFlow<List<NewsEntity>> get() = _newsData.asStateFlow()

    private val _insertNewsResult = MutableStateFlow<SingleEvent<InsertResult>>(SingleEvent(ProgressResult))
    val insertNewsResult: StateFlow<SingleEvent<InsertResult>> get() = _insertNewsResult.asStateFlow()

    private val insertNewsListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                _insertNewsResult.value = SingleEvent(CorrectResult)
                database.reference
                    .child(PACKAGE_NEWS)
                    .child(insertedValueId)
                    .removeEventListener(this)

                insertedValueId = ""
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.i(this@AdminRepositoryImpl::class.java.simpleName, "$insertedValueId not exists")
        }
    }

    override suspend fun readNewsFromDB() {
        val newsReference = database.reference.child(PACKAGE_NEWS)

        newsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(allSnapshots: DataSnapshot) {
                val newsList = mutableListOf<NewsEntity>()
                for (snapshot in allSnapshots.children) {
                    val entity = snapshot.getValue(NewsEntity::class.java)
                    if (entity != null) newsList.add(entity)
                }
                _newsData.value = newsList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(StudentRepositoryImpl::class.java.simpleName, error.message)
            }
        })
    }

    override suspend fun addNews(news: NewsEntity) {
        val reference = database.reference.child(PACKAGE_NEWS)
        val key = reference.push().key
        reference.child(key.toString()).apply {
            setValue(news)
            database.reference.child(PACKAGE_NEWS)
            addValueEventListener(insertNewsListener)
        }
    }
}