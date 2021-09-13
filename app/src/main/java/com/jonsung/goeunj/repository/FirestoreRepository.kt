package com.jonsung.goeunj.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jonsung.goeunj.data.model.WeekGoal
import com.jonsung.goeunj.data.model.WorkoutToBeReviewed
import com.jonsung.goeunj.model.MuscleGroup
import com.jonsung.goeunj.utils.Constants.FirebaseCollectionPath.WORKOUTS_BY_GROUP
import com.jonsung.goeunj.utils.Constants.FirebaseCollectionPath.WORKOUT_PLANS
import com.jonsung.goeunj.utils.Constants.FirebaseCollectionPath.WORKOUT_PLANS_TO_REVIEW
import com.jonsung.goeunj.utils.Constants.FirebaseKey.MUSCLE_GROUP
import com.jonsung.goeunj.utils.Constants.FirebaseKey.PARTICIPANT_ID
import com.jonsung.goeunj.utils.Constants.FirebaseKey.USER_ID
import com.natpryce.Failure
import com.natpryce.Success
import com.natpryce.Result
import kotlinx.coroutines.tasks.await
import java.lang.Exception


class FirestoreRepository {
    private val db = Firebase.firestore

    suspend fun getWorkoutPlansByUserId(userId: String) : Result<QuerySnapshot, Exception> {
        //        db.collection(WORKOUT_PLANS)
        //            .whereEqualTo("participantId", userId)
        //            .get()
        //            .addOnSuccessListener { result ->
        //                for (document in result) {
        //                    Log.d("setData()", "${document.id} => ${document.data}")
        //                    val plan = document.toObject(WorkoutPlan::class.java)
        //                    Success(plan)
        //                }
        //            }
        //            .addOnFailureListener { exception ->
        //                Log.w("setData()", "Error getting documents", exception)
        //                Failure(exception)
        //            }
        return try {
            val result = db.collection(WORKOUT_PLANS)
                .whereEqualTo(PARTICIPANT_ID, userId)
                .get()
                .await()
            Success(result)
        } catch (e: Exception) {
            Failure(e)
        }
    }

    suspend fun getWorkoutsByGroupByUserId(userId: String, muscleGroup: MuscleGroup) : Result<QuerySnapshot, Exception> {
        return try {
            val result = db.collection(WORKOUT_PLANS).document("OzxYDOiLBOugBCMaIm2N").collection(WORKOUTS_BY_GROUP)
                .whereEqualTo(PARTICIPANT_ID, userId)
                .whereEqualTo(MUSCLE_GROUP, muscleGroup.name)
                .get()
                .await()
            Success(result)
        } catch (e: Exception) {
            Failure(e)
        }
    }

    suspend fun getWorkoutGoalsToBeReviewed(userId: String) : Result<QuerySnapshot, Exception> {
        return try {
            val result = db.collection(WORKOUT_PLANS_TO_REVIEW).whereEqualTo(USER_ID, userId).get().await()
            Success(result)
        } catch (e: Exception) {
            Failure(e)
        }
    }

    suspend fun postWorkoutGoalsToBeReviewed(userId: String, goal: WorkoutToBeReviewed) : Result<QuerySnapshot, Exception> {
        return try {
//            db.collection(WORKOUT_PLANS).document("OzxYDOiLBOugBCMaIm2N")
//                .update("goals", FieldValue.arrayUnion(goal)).await()
//            val result = db.collection(WORKOUT_PLANS).whereEqualTo(PARTICIPANT_ID, userId).get().await()

            db.collection(WORKOUT_PLANS_TO_REVIEW).document().set(goal).await()
            val result = db.collection(WORKOUT_PLANS_TO_REVIEW).whereEqualTo(PARTICIPANT_ID, userId).get().await()
            Success(result)
        } catch (e: Exception) {
            Failure(e)
        }
    }
}