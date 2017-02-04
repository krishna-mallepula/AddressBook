//package com.addressBook;
//import android.content.Context;
//import android.location.Criteria;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.view.KeyEvent;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.MapActivity;
//import com.google.android.maps.MapController;
//import com.google.android.maps.MapView;
//
//
//public class MyGPSActivity extends MapActivity implements LocationListener {
//	private MapView mapView;
//	private TextView latituteField;
//	private TextView longitudeField;
//	private LocationManager locationManager;
//	private String provider;
//	private GeoPoint geoPoint;
//	double	latitude, longitude ;
//	private MapController myMC;
//	/** Called when the activity is first created. */
//	
//	@Override
//	
//	public void onCreate(Bundle savedInstanceState) {
//
//	super.onCreate(savedInstanceState);
//	setContentView(R.layout.test);
//
//	mapView=(MapView)findViewById(R.id.mapview1);
//	geoPoint = new GeoPoint((int) (latitude * 1000000), (int) (longitude * 1000000));
//	mapView.setSatellite(false);
//	mapView.setBuiltInZoomControls(true);
//	
//	 myMC = mapView.getController();
//     myMC.setCenter(geoPoint);
//     myMC.setZoom(5);
//
//
//	// Get the location manager
//	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//	// Define the criteria how to select the locatioin provider -> use
//	// default
//	Criteria criteria = new Criteria();
//	provider = locationManager.getBestProvider(criteria, false);
//	Location location = locationManager.getLastKnownLocation(provider);
//
//
//	// Initialize the location fields
//	if (location != null) {
//		System.out.println("Provider " + provider + " has been selected.");
//		int lat = (int) (location.getLatitude());
//		int lng = (int) (location.getLongitude());
//		latituteField.setText(String.valueOf(lat));
//		longitudeField.setText(String.valueOf(lng));
//	} else {
////		latituteField.setText("Provider not available");
////		longitudeField.setText("Provider not available");
//	}
//	
//	}
//	
//
//	 @Override
//	 protected boolean isRouteDisplayed()
//	 {
//	 return false;
//	 }
//	 public boolean onKeyDown(int keyCode, KeyEvent event) {
//			if (keyCode == KeyEvent.KEYCODE_I) {
//				mapView.getController().setZoom(mapView.getZoomLevel() + 1);
//				return true;
//			} else if (keyCode == KeyEvent.KEYCODE_O) {
//				mapView.getController().setZoom(mapView.getZoomLevel() - 1);
//				return true;
//			} else if (keyCode == KeyEvent.KEYCODE_S) {
//				mapView.setSatellite(true);
//				return true;
//			} else if (keyCode == KeyEvent.KEYCODE_M) {
//				mapView.setSatellite(false);
//				return true;
//			}
//			return false;
//		}
//
//	
//
///* Request updates at startup */
//@Override
//protected void onResume() {
//	super.onResume();
//	locationManager.requestLocationUpdates(provider, 400, 1, this);
//}
//
///* Remove the locationlistener updates when Activity is paused */
//@Override
//protected void onPause() {
//	super.onPause();
//	locationManager.removeUpdates(this);
//}
//
//@Override
//public void onLocationChanged(Location location) {
//	int lat = (int) (location.getLatitude());
//	int lng = (int) (location.getLongitude());
//	latituteField.setText(String.valueOf(lat));
//	longitudeField.setText(String.valueOf(lng));
//}
//
//@Override
//public void onStatusChanged(String provider, int status, Bundle extras) {
//
//}
//
//@Override
//public void onProviderEnabled(String provider) {
//	Toast.makeText(this, "Enabled new provider " + provider,
//			Toast.LENGTH_SHORT).show();
//
//}
//
//@Override
//public void onProviderDisabled(String provider) {
//	Toast.makeText(this, "Disabled provider " + provider,
//			Toast.LENGTH_SHORT).show();
//}
//
//
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
