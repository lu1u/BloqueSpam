package com.lpi.bloquespam.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Gestionnaire des preferences de l'application
 */
public class Preferences
{


	@NonNull private static final String PREFERENCES = Preferences.class.getName();
	@NonNull private static final String PREF_BLOCAGE_SIM_ACTIF = "Blocage SIM actif";
	@NonNull private static final String PREF_MEMORISATION_VOLUME = "Memorisation volume";
	@NonNull private static final String PREF_VOLUME_MIS_A_ZERO = "Volume mis a zero";
	@NonNull private static final String PREF_NOTIFICATIONS_ACTIF = "Notifications actif";
	private @Nullable static Preferences _instance;
	private @NonNull final SharedPreferences settings;
	private @NonNull final SharedPreferences.Editor editor;

	/***
	 * Obtenir l'instance (unique) de Preferences
	 * On peut donner un Context null si l'objet a deja ete initialisé
	 */
	public static synchronized Preferences getInstance(@Nullable final Context context)
	{
		if (_instance == null)
			if (context != null)
				_instance = new Preferences(context);

		return _instance;
	}

	/***
	 * Constructeur privé, utilisable uniquement dans getInstance
	 */
	private Preferences(final @NonNull Context context)
	{
		settings = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		editor = settings.edit();

	}
	public void putBlocageActif(boolean val)
	{
		editor.putBoolean(PREF_BLOCAGE_SIM_ACTIF, val);
		editor.apply();
	}

	public boolean getBlocageActif()
	{
		return settings.getBoolean(PREF_BLOCAGE_SIM_ACTIF, true);
	}

	public void putNotificationsActif(boolean val)
	{
		editor.putBoolean(PREF_NOTIFICATIONS_ACTIF, val);
		editor.apply();
	}

	public boolean getNotificationsActif()
	{
		return settings.getBoolean(PREF_NOTIFICATIONS_ACTIF, true);
	}

	public void putMemorisationVolume(int val)
	{
		editor.putInt(PREF_MEMORISATION_VOLUME, val);
		editor.apply();
	}
	public int getMemorisationVolume()
	{
		return settings.getInt(PREF_MEMORISATION_VOLUME, 0);
	}

	public void putVolumeMisAZero(boolean val)
	{
		editor.putBoolean(PREF_VOLUME_MIS_A_ZERO, val);
		editor.apply();
	}

	public boolean getVolumeMisAZero()
	{
		return settings.getBoolean(PREF_VOLUME_MIS_A_ZERO, false);
	}
//	public float getFloat(@NonNull final String nom, float defaut)
//	{
//		return settings.getFloat(nom, defaut);
//	}
//
//
//	public void setFloat(@NonNull final String nom, float val)
//	{
//		editor.putFloat(nom, val);
//		editor.apply();
//	}


// --Commented out by Inspection START (10/03/2023 19:12):
//	public void putBool(@NonNull final String nom, boolean val)
//	{
//		editor.putBoolean(nom, val);
//		editor.apply();
//	}
// --Commented out by Inspection STOP (10/03/2023 19:12)


// --Commented out by Inspection START (10/03/2023 19:12):
//	public boolean getBool(@NonNull final String nom, boolean defaut)
//	{
//		return settings.getBoolean(nom, defaut);
//	}
// --Commented out by Inspection STOP (10/03/2023 19:12)


	/*


		public void setChar(@NonNull final String nom, char val)
		{
			editor.putString(nom, "" + val);
			editor.apply();
		}

		public char getChar(@NonNull final String nom, char defaut)
		{
			return settings.getString(nom, defaut + " ").charAt(0);
		}

	public String getString(@NonNull final String nom, final String defaut)
	{
		return settings.getString(nom, defaut);
	}

	public void setString(@NonNull final String nom, String val)
	{
		editor.putString(nom, val);
		editor.apply();
	}	*/
}
