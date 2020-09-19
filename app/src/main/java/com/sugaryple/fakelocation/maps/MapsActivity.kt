package com.sugaryple.fakelocation.maps

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.livedatapermission.PermissionManager
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.Marker
import com.sugaryple.fakelocation.R
import com.sugaryple.fakelocation.data.SimpleLatLng
import com.sugaryple.fakelocation.databinding.ActivityMapsBinding
import com.sugaryple.fakelocation.feature.fakeGps.FakeGpsWorkManager
import com.sugaryple.fakelocation.feature.fakeGps.FakeGpsWorkSate
import com.sugaryple.fakelocation.helper.MyLocationHelper
import com.sugaryple.fakelocation.model.*
import com.sugaryple.fakelocation.showOnlyOne
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapsActivity : AppCompatActivity(), PermissionManager.PermissionObserver {

    companion object {
        const val PERMISSION_REQUEST_ID = 100
        const val TAG_REQUIRED_MOCK_LOCATION_DIALOG = "TAG_REQUIRED_MOCK_LOCATION_DIALOG"
    }

    private val mapModel: MapModel by lazy {
        GoogleMapModel(
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        )
    }
    private val viewModel: MapsViewModel by viewModel()
    private var targetMarker: Marker? = null
    private val fakeGpsManager: FakeGpsWorkManager by inject()
    private val gpsProviderModel: GpsProviderModel by inject()
    private val myLocationHelper: MyLocationHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMapsBinding>(
            this,
            R.layout.activity_maps
        ).apply {
            lifecycleOwner = this@MapsActivity
            viewModel = this@MapsActivity.viewModel
        }
        viewModelObserve()
        mapObserve()
        gpsProviderObserve()
        fakeGpsManagerObserve()
        myLocationObserve()
    }

    private fun myLocationObserve() {
        myLocationHelper.myLocation.observe(this) { myLocation ->
            mapModel.moveCamera(myLocation, 15f)
        }
    }

    private fun fakeGpsManagerObserve() {
        fakeGpsManager.state.observe(this) { viewModel.onChangedState(it) }
    }

    private fun clearTargetMarker() {
        targetMarker?.remove()
    }

    private fun setTargetMarker(latLng: SimpleLatLng) {
        clearTargetMarker()
        targetMarker = mapModel.addMarker(latLng)
    }

    override fun onResume() {
        super.onResume()
        gpsProviderModelInit()
    }

    private fun gpsProviderModelInit() {
        if (gpsProviderModel.isGPSProviderEnabled()) {
            gpsProviderModel.initMockLocationProvider()
        }
    }

    private fun viewModelObserve() {
        viewModel.clickEventPlay.observe(this) {
            it.getContentIfNotHandled()?.let {
                val centerLocation = mapModel.getCenterLocation()
                when (fakeGpsManager.state.value) {
                    is FakeGpsWorkSate.On -> fakeGpsManager.stop()
                    is FakeGpsWorkSate.Off,
                    is FakeGpsWorkSate.Failed,
                    is FakeGpsWorkSate.Uninitialized -> fakeGpsManager.start(centerLocation!!)
                }
            }
        }
    }

    private fun mapObserve() {
        mapModel.mapReadyEvent.observe(this) {
            enableMyLocation()
            moveMyLocation()
            mapModel.setCompassEnabled(true)
            mapModel.setZoomControlsEnabled(true)
            // TODO 기능 구현 필요
            // button_play.isEnabled = true

            // fakeGps가 살아 있다면 그 상태로 초기화 한다.
            fakeGpsManager.state.value?.let { viewModel.onChangedState(it) }
        }
    }

    private fun gpsProviderObserve() {
        gpsProviderModel.eventMockLocationRequest.observe(this) {
            it.getContentIfNotHandled()?.let {
                startRequiredMockLocationDialog()
            }
        }
    }

    private fun moveMyLocation() {
        if (checkSelfPositionPermission()) {
            myLocationHelper.init()
        }
    }

    private fun requestPermission() {
        PermissionManager.requestPermissions(
            this,
            PERMISSION_REQUEST_ID,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private fun enableMyLocation() {
        if (checkSelfPositionPermission()) {
            mapModel.setIsMyLocationEnabled(true)
        } else {
            requestPermission()
        }
    }

    // 위치를
    private fun checkSelfPositionPermission(): Boolean {
        return !(ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)
    }


    override fun setupObserver(permissionResultLiveData: LiveData<PermissionResult>) {
        permissionResultLiveData.observe(this, Observer<PermissionResult> {
            when (it) {
                is PermissionResult.PermissionGranted ->

                {
                    if (it.requestCode == PERMISSION_REQUEST_ID) {
                        // 사용자가 권한을 부여한 후 여기에 논리를 추가하십시오.
                        enableMyLocation()
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

    fun startRequiredMockLocationDialog() {
        RequiredMockLocationDialog.newInstance()
            .showOnlyOne(supportFragmentManager, TAG_REQUIRED_MOCK_LOCATION_DIALOG)
    }

}