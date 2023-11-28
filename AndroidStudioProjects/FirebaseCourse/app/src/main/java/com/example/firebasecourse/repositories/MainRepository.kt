package com.example.firebasecourse.repositories

import com.example.firebasecourse.db.Run
import com.example.firebasecourse.db.RunDAO
import javax.inject.Inject

class MainRepository @Inject constructor(
    val runDao: RunDAO
) {

    suspend fun insertRun(run: Run)=runDao.insertRun(run)

    suspend fun deleteRun(run: Run)=runDao.deleteRun(run)

    fun getAllRunsSortedByDate()=runDao.getAllRunsSortedByDate()

    fun getAllRunsSortedByDistance()=runDao.getAllRunsSortedByDistance()

    fun getAllRunsSortedByTimeInMillis()=runDao.getAllRunsSortedByTimeInMillis()

    fun getAllRunsSortedByAvgSpeed()=runDao.getAllRunsSortedByAvgSpeed()

    fun getAllRunsSortedByCaloriesBurned()=runDao.getAllRunsSortedByCaloriesBurned()

    //Functions for the statistics fragment

    fun getTotalAvgSpeed()=runDao.getTotalAvgSpeed()

    fun getTotalDistance()=runDao.getTotalDistance()

    fun getTotalCaloriesBurned()=runDao.getTotalCaloriesBurned()

    fun getTotalTimeInMillis()=runDao.getTotalTimeInMillis()

}