package org.pursuit.usolo.view;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.group.view.AddLocationFragment;
import com.android.group.view.StartGroupFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.mapboxsdk.geometry.LatLng;


import org.pursuit.usolo.R;
import org.pursuit.usolo.map.MapFragment;
import org.pursuit.usolo.map.model.Zone;

public final class HostActivity extends AppCompatActivity implements OnFragmentInteractionListener,
        StartGroupFragment.OnFragmentInteractionListener {
    private static final String TAG = "HostActivity";
    public static boolean granted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        loginToFirebase();
        requestUserLocationPermission();
        final String path = getString(R.string.firebase_path) + "2/";
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
        ref.setValue(new Zone("Pursuit",
          new LatLng(40.7430877d, -73.9419287d),
          0));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            granted = true;
            inflateFragment(MapFragment.newInstance());
        } else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void inflateFragment(Fragment fragment) {
        getSupportFragmentManager()
          .beginTransaction()
          .replace(R.id.main_container, fragment)
          .commit();
    }

    @Override
    public void inflateFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_container, StartGroupFragment.getInstance())
                .addToBackStack(null)
                .commit();
    }

    public void requestUserLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
          ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        } else {
            granted = true;
            inflateFragment(MapFragment.newInstance());
        }
    }

    private void loginToFirebase() {
        // Authenticate with Firebase, and request location updates
        String email = getString(R.string.firebase_email);
        String password = getString(R.string.firebase_password);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
          email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "firebase auth success");
            } else {
                Log.d(TAG, "firebase auth failed");
            }
        });
    }

    @Override
    public void inflateAddLocationFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_container,AddLocationFragment.getInstance())
                .addToBackStack(null)
                .commit();
    }
}
