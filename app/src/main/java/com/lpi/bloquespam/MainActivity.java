package com.lpi.bloquespam;

import android.Manifest;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lpi.bloquespam.database.ReglesDatabase;
import com.lpi.bloquespam.databinding.ActivityMainBinding;
import com.lpi.bloquespam.report.Report;
import com.lpi.bloquespam.utils.ConfirmBox;
import com.lpi.bloquespam.utils.MessageBoxUtils;
import com.lpi.bloquespam.utils.Permissions;

public class MainActivity extends AppCompatActivity
{

	// Les permissions necessaires au fonctionnement de cette application
	@NonNull static final String[] PERMISSIONS = {android.Manifest.permission.READ_PHONE_STATE,
			android.Manifest.permission.READ_CONTACTS,
			android.Manifest.permission.READ_PHONE_NUMBERS,
			Manifest.permission.READ_CALL_LOG,
			Manifest.permission.POST_NOTIFICATIONS,
			Manifest.permission.CALL_PHONE};

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		com.lpi.bloquespam.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		getSupportActionBar().hide();
		BottomNavigationView navView = findViewById(R.id.nav_view);
		// Passing each menu ID as a set of Ids because each
		// menu should be considered as top level destinations.
		AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
				R.id.navigation_settings, R.id.navigation_rules, R.id.navigation_history)
				.build();
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
		NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
		NavigationUI.setupWithNavController(binding.navView, navController);
		Permissions.demandePermissionsSiBesoin(this, PERMISSIONS);
		verifRegles();

		Report.getInstance(this);
	}

	/***
	 * Verifie que l'application a des regles de fonctionnement
	 */
	private void verifRegles()
	{
		ReglesDatabase db = ReglesDatabase.getInstance(this);
		if (db.nbRegles() == 0)
		{
			MessageBoxUtils.messageBox(this, R.string.titre_creer_regles, R.string.creer_des_regles, MessageBoxUtils.BOUTON_OK, new MessageBoxUtils.Listener()
			{
				@Override public void onOk()
				{
					// On attend la validation du message pour basculer vers l'onglet Regles
					NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_activity_main);
					navController.navigate(R.id.navigation_rules);
				}

				@Override public void onCancel()
				{

				}
			});
		}
	}

}