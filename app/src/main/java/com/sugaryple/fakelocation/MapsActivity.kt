package com.sugaryple.fakelocation

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.livedatapermission.PermissionManager

import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, PermissionManager.PermissionObserver {

    companion object {
        const val PERMISSION_REQUEST_ID = 100
    }

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        PermissionManager.requestPermissions(
            this,
            PERMISSION_REQUEST_ID,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun setupObserver(permissionResultLiveData: LiveData<PermissionResult>) {
        permissionResultLiveData.observe(this, Observer<PermissionResult> {
            when (it) {
                is PermissionResult.PermissionGranted -> {
                    if (it.requestCode == PERMISSION_REQUEST_ID) {
                        // 사용자가 권한을 부여한 후 여기에 논리를 추가하십시오.
                        Toast.makeText(this@MapsActivity, "PermissionGranted", Toast.LENGTH_SHORT).show()
                    }
                }
                is PermissionResult.PermissionDenied -> {
                    if (it.requestCode == PERMISSION_REQUEST_ID) {
                        // 권한 거부 처리를위한 논리 추가
                        Toast.makeText(this@MapsActivity, "PermissionDenied", Toast.LENGTH_SHORT).show()
                    }
                }
                is PermissionResult.PermissionDeniedPermanently -> {
                    if (it.requestCode == PERMISSION_REQUEST_ID) {
                        // 사용자가 권한을 영구적으로 거부 한 경우 여기에 논리를 추가하십시오.
                        // 이상적으로 사용자에게 설정으로 이동하여 권한을 활성화하도록 요청해야합니다.
                        Toast.makeText(this@MapsActivity, "PermissionDeniedPermanently", Toast.LENGTH_SHORT).show()
                    }
                }
                is PermissionResult.ShowRational -> {
                    if (it.requestCode == PERMISSION_REQUEST_ID) {
                        // 사용자가 권한을 자주 거부하면이 권한을 요청한 이유가 확실하지 않습니다.
                        // 권한이 필요한 이유를 설명 할 수있는 기회입니다.
                        Toast.makeText(this@MapsActivity, "ShowRational", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }


}