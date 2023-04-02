package com.lpi.bloquespam.ui.settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;

import com.lpi.bloquespam.DialogAPropos;
import com.lpi.bloquespam.R;
import com.lpi.bloquespam.ui.CustomOnOffSwitch;
import com.lpi.bloquespam.utils.Preferences;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment
{


	public SettingsFragment()
	{
		// Required empty public constructor
	}



	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_settings, container, false);
		final Preferences preferences = Preferences.getInstance(getContext());

		// Activation du blocage
		CustomOnOffSwitch cOnOff = v.findViewById(R.id.cOnOff);
		cOnOff.setChecked(preferences.getBlocageActif());
		cOnOff.setOnCheckedChangeListener(val ->
		{
			preferences.putBlocageActif(val);
		});

		// Afficher une option
		Switch swNotifier = v.findViewById(R.id.swNotify);
		swNotifier.setChecked(preferences.getNotificationsActif());
		swNotifier.setOnCheckedChangeListener((compoundButton, b) -> preferences.putNotificationsActif(b));

		// Bouton A Propos
		ImageButton ibAPropos = v.findViewById(R.id.ibInfos);
		ibAPropos.setOnClickListener(new View.OnClickListener()
		{
			@Override public void onClick(View view)
			{
				DialogAPropos.start(getActivity());
			}
		});

		return v;
	}
}