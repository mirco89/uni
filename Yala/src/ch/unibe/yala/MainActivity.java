package ch.unibe.yala;

import java.io.IOException;
import java.util.List;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class MainActivity extends MapActivity implements LocationListener {

	LocationManager locationManager;
	Geocoder geocoder;
	TextView locationText;
	MapView map;
	MapController mapController;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		locationText = (TextView) findViewById(R.id.myText);
		map = (MapView) findViewById(R.id.map);
		map.setBuiltInZoomControls(true);

		mapController = map.getController();
		mapController.setZoom(8);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		geocoder = new Geocoder(this);

		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location != null)
			onLocationChanged(location);
	}

	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		String text = String.format("Lat:\t %f\nLong:\t %f\nAlt:\t %f\nBearing:\t %f",
				location.getLatitude(), location.getLongitude(), location.getAltitude(),
				location.getBearing());
		locationText.setText(text);

		try {
			List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
					location.getLongitude(), 10);
			for (Address address : addresses)
				locationText.append("\n" + address.getAddressLine(0));

			int latitude = (int) (location.getLatitude() * 1000000);
			int longitude = (int) (location.getLongitude() * 1000000);

			GeoPoint point = new GeoPoint(latitude, longitude);
			mapController.animateTo(point);

		} catch (IOException e) {
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
