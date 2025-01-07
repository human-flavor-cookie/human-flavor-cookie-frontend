package com.example.fitness.ui.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

//경로 데이터 공유 위한 class
class RouteViewModel : ViewModel() {
    val routePoints = MutableLiveData<MutableList<LatLng>>(mutableListOf())
    val isTracking = MutableLiveData<Boolean>(false)

    fun addRoutePoint(point: LatLng) {
        routePoints.value?.add(point)
        routePoints.postValue(routePoints.value) // LiveData 업데이트
    }

    fun clearRoutePoints() {
        routePoints.value = mutableListOf()
    }
}
