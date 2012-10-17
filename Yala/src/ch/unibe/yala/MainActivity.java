package ch.unibe.yala;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MainActivity extends MapActivity implements LocationListener {

	LocationManager locationManager;
	Geocoder geocoder;
	TextView locationText;
	MapView map;
	MapController mapController;
	Drawable mapPin;
	List<Overlay> mapOverlays;
	MyItemOverlay itemizedoverlay;
	Boolean started;
	List<GeoPoint> myPoints;
	List<Long> myTimes;
	List<Double> myAlti;
	AlertDialog.Builder builder;
	static GeoPoint[] points;
	static Long[] times;
	static Double[] alti;
	Boolean first;
	GeoPoint lastPoint;
	Double lastAlti;
	Button startButton;
	Button resetButton;
	Button finishButton;
	NotificationManager mNotificationManager;
	Boolean calledFinish;
	Long pauseTime;
	Long pauseBegin;
	int startState;
	Boolean paused;
	MyMapOverlay seg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		locationText = (TextView) findViewById(R.id.myText);
		map = (MapView) findViewById(R.id.map);
		map.setSatellite(false);

		startButton = (Button) findViewById(R.id.buttonStart);
		resetButton = (Button) findViewById(R.id.buttonReset);
		finishButton = (Button) findViewById(R.id.buttonFinish);
		resetButton.setEnabled(false);
		finishButton.setEnabled(false);

		lastPoint = new GeoPoint(46700000, 7500000);
		lastAlti = 0d;
		startState = 0;
		pauseTime = 0l;

		mapController = map.getController();
		mapController.setZoom(10);
		mapController.animateTo(lastPoint);
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
		geocoder = new Geocoder(this);
		mapPin = getResources().getDrawable(R.drawable.startpin);
		itemizedoverlay = new MyItemOverlay(mapPin, this);
		mapOverlays = map.getOverlays();

		started = false;
		paused = false;
		first = true;
		calledFinish = false;
		myPoints = new ArrayList<GeoPoint>();
		myPoints.clear();
		myTimes = new ArrayList<Long>();
		myTimes.clear();
		myAlti = new ArrayList<Double>();
		myAlti.clear();

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					doReset();
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					break;
				}
			}
		};
		builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
		.setNegativeButton("No", dialogClickListener);
	}

	@Override
	public void onLocationChanged(Location location) {

		if (paused)
			return;

		String text = String.format("%f / %f / %d", location.getLatitude(),
				location.getLongitude(), (int) location.getAltitude());
		locationText.setText(text);

		int latitude = (int) (location.getLatitude() * 1000000);
		int longitude = (int) (location.getLongitude() * 1000000);

		lastPoint = new GeoPoint(latitude, longitude);
		GeoPoint point = new GeoPoint(latitude, longitude);
		mapController.animateTo(point);

		if (!started)
			return;

		myPoints.add(new GeoPoint(latitude, longitude));
		myTimes.add(new Date().getTime() - pauseTime);
		if (location.getAltitude() > 1) {
			myAlti.add(location.getAltitude());
			lastAlti = location.getAltitude();
		} else
			myAlti.add(lastAlti);

		if (first) {
			OverlayItem overlayitem = new OverlayItem(point, "Start", "");
			itemizedoverlay.addOverlay(overlayitem);
			mapOverlays.add(itemizedoverlay);
			first = false;
			mapController.setZoom(18);
		}

		if (myPoints.size() > 2)
			finishButton.setEnabled(true);

		if (myPoints.size() > 1) {
			GeoPoint[] segments = new GeoPoint[myPoints.size()];
			segments = myPoints.toArray(segments);
			seg = new MyMapOverlay(segments);
			int i = map.getOverlays().indexOf(seg);
			if (i >= 0)
				map.getOverlays().remove(i);
			map.getOverlays().add(seg);
		}

	}

	public void doReset() {
		map.getOverlays().clear();
		map.removeAllViews();
		started = false;
		paused = false;
		first = true;
		resetButton.setEnabled(false);
		finishButton.setEnabled(false);
		startButton.setText("Start");
		startState = 0;
		myPoints.clear();
		myTimes.clear();
		myAlti.clear();
	}

	public void start(View view) {
		switch (startState) {
		case 0:
			startButton.setText("Pause");
			startState = 1;
			locationText.setText("Waiting for GPS signal");
			break;
		case 1:
			startButton.setText("Resume");
			pauseBegin = new Date().getTime();
			paused = true;
			startState = 2;
			locationText.setText("Paused");
			break;
		case 2:
			startButton.setText("Pause");
			pauseTime = new Date().getTime() - pauseBegin;
			paused = false;
			startState = 1;
			locationText.setText("Resumed");
			break;
		default:
			break;
		}
		calledFinish = false;
		started = true;
		resetButton.setEnabled(true);
	}

	public void reset(View view) {
		if (!started || itemizedoverlay == null)
			return;
		builder.show();
	}

	public void finish(View view) {
		calledFinish = true;
		started = false;
		paused = false;
		points = new GeoPoint[myPoints.size()];
		points = myPoints.toArray(points);
		times = new Long[myTimes.size()];
		times = myTimes.toArray(times);
		alti = new Double[myAlti.size()];
		alti = myAlti.toArray(alti);
		if (points.length > 0)
			lastPoint = points[points.length - 1];
		doReset();
		locationManager.removeUpdates(this);
		removeNotification();
		Intent intent = new Intent(this, FinishActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		calledFinish = false;
		mapController.animateTo(lastPoint);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
		removeNotification();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (isFinishing()) {
			locationManager.removeUpdates(this);
			removeNotification();
		} else
			showRunningNotification();
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (isFinishing()) {
			locationManager.removeUpdates(this);
			removeNotification();
		} else
			showRunningNotification();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		locationManager.removeUpdates(this);
		removeNotification();
		finish();
	}

	private void showRunningNotification() {
		if (calledFinish)
			return;
		Notification noti = new Notification(R.drawable.ic_launcher, "Yala is still running",
				System.currentTimeMillis());
		Intent notifyIntent = new Intent(this, MainActivity.class);
		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notifyIntent,
				Notification.FLAG_ONGOING_EVENT);
		noti.flags = Notification.FLAG_ONGOING_EVENT + Notification.FLAG_AUTO_CANCEL;
		noti.setLatestEventInfo(this, "Yala", "Use back key to end Yala", contentIntent);
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNotificationManager.notify(1, noti);
	}

	private void removeNotification() {
		if (mNotificationManager != null)
			mNotificationManager.cancel(1);
	}
}
