package com.example.fitness.ui.map

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.fitness.R
import com.example.fitness.databinding.FragmentMapBinding
import com.example.fitness.ui.main.MainFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

@Suppress("DEPRECATION")
class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private var routePoints: ArrayList<LatLng>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 전달받은 경로 데이터 가져오기
        routePoints = arguments?.getParcelableArrayList("route_points")

        // 지도 준비
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        view.findViewById<ImageView>(R.id.return_to_main).setOnClickListener {
            val fragmentManager = parentFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()

            // MainFragment로 이동
            fragmentTransaction.replace(R.id.nav_host_fragment, MainFragment())
            fragmentTransaction.addToBackStack(null) // 뒤로가기 시 이전 Fragment로 돌아가기
            fragmentTransaction.commit()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // 경로 그리기
        routePoints?.let { points ->
            if (points.isNotEmpty()) {
                val polylineOptions = PolylineOptions().apply {
                    width(10f)
                    color(Color.BLUE)
                    addAll(points)
                }
                map.addPolyline(polylineOptions)

                // 시작/종료 지점에 마커 추가
                map.addMarker(MarkerOptions().position(points.first()).title("Start"))
                map.addMarker(MarkerOptions().position(points.last()).title("End"))

                // 경로 중앙으로 카메라 이동
                val bounds = LatLngBounds.builder().apply {
                    points.forEach { include(it) }
                }.build()
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            }
        }
    }
}
