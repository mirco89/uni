package ch.unibe.yala;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class FinishActivity extends MapActivity {

	ProgressDialog pd;
	static GeoPoint[] pts;
	static Long[] tm;
	static Double[] alt;
	static GeoPoint[] paPts;
	static Long[] paTm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finish);

		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.show();
		pd.setProgress(0);
		pd.setMessage("Loading...");

		List<Double> myDistances = new ArrayList<Double>();
		List<Double> myTimes = new ArrayList<Double>();
		List<Double> myHeight = new ArrayList<Double>();

		Double[] distances;
		Double[] times;
		Double[] speeds;
		Double[] height;

		GraphViewData[] gvd;
		GraphViewData[] gvdA;

		String[] vLabelSpeed = new String[5];
		String[] vLabelAlti = new String[5];

		float maxSpeed;
		float maxAlti;

		TextView stats = (TextView) findViewById(R.id.stats);
		int ptsLen = pts.length;
		if (ptsLen > 1) {
			pd.setProgress(5);
			for (int i = 0; i < ptsLen - 1; i++) {
				float[] result = new float[3];
				Location.distanceBetween(pts[i].getLatitudeE6() / 1E6,
						pts[i].getLongitudeE6() / 1E6, pts[i + 1].getLatitudeE6() / 1E6,
						pts[i + 1].getLongitudeE6() / 1E6, result);
				myDistances.add(Double.valueOf(result[0]));
				double timeDiff = tm[i + 1] - tm[i];
				myTimes.add(timeDiff / 1000);
				myHeight.add(alt[i]);
			}
			myHeight.add(alt[ptsLen - 1]);

			times = new Double[myTimes.size()];
			times = myTimes.toArray(times);
			distances = new Double[myDistances.size()];
			distances = myDistances.toArray(distances);
			height = new Double[myHeight.size()];
			height = myHeight.toArray(height);

			gvd = new GraphViewData[distances.length];
			gvdA = new GraphViewData[height.length];
			speeds = new Double[distances.length];
			double thatTime = 0;
			for (int i = 0; i < distances.length; i++) {
				speeds[i] = distances[i] / times[i];
				gvd[i] = new GraphViewData(thatTime, speeds[i]);
				thatTime += times[i];
			}
			thatTime = 0;
			for (int i = 0; i < height.length; i++) {
				if (i > 0)
					thatTime += times[i - 1];
				gvdA[i] = new GraphViewData(thatTime, height[i]);
			}
			vLabelSpeed[0] = "0 km/h";
			vLabelSpeed[1] = Math.round(maxValue(speeds) / 4 * 3.6) + " km/h";
			vLabelSpeed[2] = Math.round(maxValue(speeds) / 2 * 3.6) + " km/h";
			vLabelSpeed[3] = Math.round(maxValue(speeds) * 3 / 4 * 3.6) + " km/h";
			vLabelSpeed[4] = Math.round(maxValue(speeds) * 3.6) + " km/h";
			maxSpeed = Math.round(maxValue(speeds));
			vLabelAlti[0] = "0 m";
			vLabelAlti[1] = Math.round(maxValue(height) / 4) + " m";
			vLabelAlti[2] = Math.round(maxValue(height) / 2) + " m";
			vLabelAlti[3] = Math.round(maxValue(height) * 3 / 4) + " m";
			vLabelAlti[4] = Math.round(maxValue(height)) + " m";
			maxAlti = Math.round(maxValue(height));

			long sec = 0;
			sec = (tm[tm.length - 1] - tm[0]) / 1000;
			double elev = Math.round(maxValue(height)) - Math.round(minValue(height));
			double dist = 0;
			for (Double distance : distances)
				dist += distance;
			stats.setText("Duration: " + secToTimeString(sec) + "\n");
			stats.append("Elevation: " + elev + " m\n");
			stats.append("Avg Speed: " + Math.round(dist / sec * 3.6) + " km/h ("
					+ Math.round(dist / sec) + " m/s)\n");
			if (dist > 1000)
				stats.append("Distance: " + Math.round(dist / 10) / 100f + " km");
			else
				stats.append("Distance: " + (int) dist + " m");
		} else {
			gvd = new GraphViewData[] { new GraphViewData(1, 2.0d), new GraphViewData(2, 3.0d) };
			gvdA = new GraphViewData[] { new GraphViewData(1, 3.0d), new GraphViewData(2, 2.0d) };
			vLabelSpeed = new String[] { "slow", "", "average", "", "fast" };
			maxSpeed = 5;
			vLabelAlti = new String[] { "low", "", "average", "", "high" };
			maxAlti = 5;
			stats.setText("Duration: 0:0:0 (h:m:s)\n");
			stats.append("Elevation: 0 m\n");
			stats.append("Average Speed: 0 km/h (0 m/s)\n");
			stats.append("Distance: 0 m");
		}

		pd.setProgress(50);

		String[] hLabel = new String[] { "Start", "Middle", "End" };

		GraphView graphSpeed = new GraphView(this, gvd, "Speed/Time", hLabel, vLabelSpeed, maxSpeed);
		LinearLayout layoutSpeed = (LinearLayout) findViewById(R.id.graphSpeed);
		layoutSpeed.addView(graphSpeed);

		pd.setProgress(60);

		GraphView graphAlti = new GraphView(this, gvdA, "Altitude/Time", hLabel, vLabelAlti,
				maxAlti);
		LinearLayout layoutAlti = (LinearLayout) findViewById(R.id.graphAlti);
		layoutAlti.addView(graphAlti);

		pd.setProgress(70);

		MapView routeMap = (MapView) findViewById(R.id.route);
		MapController mMapController = routeMap.getController();
		mMapController.setZoom(19);
		if (ptsLen > 0) {
			mMapController.setCenter(pts[0]);
			MyMapOverlay mapOvlay = new MyMapOverlay(pts);
			routeMap.getOverlays().add(mapOvlay);
		}
		if (ptsLen > 1) {
			Drawable startPin = getResources().getDrawable(R.drawable.startpin);
			Drawable endPin = getResources().getDrawable(R.drawable.endpin);
			MyItemOverlay startOverlay = new MyItemOverlay(startPin, this);
			MyItemOverlay endOverlay = new MyItemOverlay(endPin, this);
			OverlayItem startItem = new OverlayItem(pts[0], "Start", "");
			OverlayItem endItem = new OverlayItem(pts[ptsLen - 1], "End", "");
			startOverlay.addOverlay(startItem);
			endOverlay.addOverlay(endItem);
			routeMap.getOverlays().add(startOverlay);
			routeMap.getOverlays().add(endOverlay);
		}

		pd.setProgress(80);

		Drawable pausePin = getResources().getDrawable(R.drawable.pausepin);
		MyItemOverlay pauseOverlay = new MyItemOverlay(pausePin, this);
		for (int i = 0; i < paTm.length; i++) {
			long sec = paTm[i] / 1000;
			String locTitle = "Paused: " + secToTimeString(sec) + "\n";
			String locSnippet = "";
			GeoPoint point = paPts[i];
			if (isConnected())
				try {
					List<Address> addresses = new Geocoder(this).getFromLocation(
							point.getLatitudeE6() / 1E6, point.getLongitudeE6() / 1E6, 10);
					for (Address address : addresses) {
						locSnippet += address.getAddressLine(0);
						if (addresses.get(addresses.size() - 1) != address)
							locSnippet += "\n";
					}
					locSnippet.substring(20);
				} catch (IOException e) {
				}
			OverlayItem pauseItem = new OverlayItem(point, locTitle, locSnippet);
			pauseOverlay.addOverlay(pauseItem);
			routeMap.getOverlays().add(pauseOverlay);
		}

		DataLayer d = new DataLayer(getBaseContext());
		d.addRun(new Date().getTime() + "", convertGeoPointToString(pts), convertLongToString(tm),
				convertDoubleToString(alt), convertLongToString(paTm),
				convertGeoPointToString(paPts));

		pd.dismiss();

	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private double minValue(Double[] myArray) {
		List<Double> b = Arrays.asList(myArray);
		return Collections.min(b);
	}

	private double maxValue(Double[] myArray) {
		List<Double> b = Arrays.asList(myArray);
		return Collections.max(b);
	}

	private String secToTimeString(long s) {
		long sec = s;
		long min = 0;
		long hour = 0;
		if (sec / 60 >= 1) {
			min = (long) Math.floor(sec / 60);
			sec = sec - min * 60;
			if (min / 60 >= 1) {
				hour = (long) Math.floor(min / 60);
				min = min - hour * 60;
			}
		}
		return hour + ":" + min + ":" + sec + " (h:m:s)";
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	public boolean isConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isAvailable() && netInfo.isConnected())
			return true;
		else
			return false;
	}

	public static void setPoints(GeoPoint[] p) {
		pts = p;
	}
	public static void setTimes(Long[] t) {
		tm = t;
	}

	public static void setAltitude(Double[] a) {
		alt = a;
	}

	public static void setPausePoints(GeoPoint[] pp) {
		paPts = pp;
	}

	public static void setPauseTimes(Long[] pt) {
		paTm = pt;
	}

	public static String convertLongToString(Long[] array) {
		String str = "";
		for (int i = 0; i < array.length; i++) {
			str = str + array[i];
			if (i < array.length - 1)
				str = str + ",";
		}
		return str;
	}

	public static String convertDoubleToString(Double[] array) {
		String str = "";
		for (int i = 0; i < array.length; i++) {
			str = str + array[i];
			if (i < array.length - 1)
				str = str + ",";
		}
		return str;
	}

	public static String convertGeoPointToString(GeoPoint[] array) {
		String str = "";
		for (int i = 0; i < array.length; i++) {
			str = str + array[i].getLatitudeE6();
			str = str + ";";
			str = str + array[i].getLongitudeE6();
			if (i < array.length - 1)
				str = str + ",";
		}
		return str;
	}

}
