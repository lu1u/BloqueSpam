/**
 * Enregistre les traces du programme dans une base de donnees, consultable avec ReportActivity
 * Les traces ne sont enregistrees que si le fichier build.gradle contient la definition suivante:
 * buildConfigField "boolean", "REPORT", "true"
 * <p>
 * exemple:
 * defaultConfig {
 * applicationId "com.lpi.compagnonderoute"
 * minSdkVersion 27
 * targetSdkVersion 28
 * versionCode 1
 * versionName "1.0"
 * buildConfigField "boolean", "REPORT", "true"
 * testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
 * }
 */
package com.lpi.bloquespam.report;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.bloquespam.BuildConfig;


/**
 * @author lucien
 */
@SuppressWarnings("nls")
public class Report
{
	public static final int HISTORIQUE = 3;
	// Niveaux de trace
	public static final int DEBUG = 0;
	public static final int WARNING = 1;
	public static final int ERROR = 2;
	@NonNull final private static String TAG = "Report";
	public static final boolean GENERER_TRACES = BuildConfig.REPORT;

	private static final int MAX_BACKTRACE = 10;
	@Nullable
	private static Report INSTANCE = null;
	@NonNull final ReportDatabase _reportDatabase;

	private Report(Context context)
	{
		if (GENERER_TRACES)
			_reportDatabase = ReportDatabase.getInstance(context);
		else
			_reportDatabase = null;
	}

	/**
	 * Point d'accès pour l'instance unique du singleton
	 * @param context: le context habituel d'Android, peut être null si l'objet a deja ete utilise
	 */
	@NonNull
	public static synchronized Report getInstance(@Nullable Context context)
	{
		if (INSTANCE == null)
		{
			INSTANCE = new Report(context);
		}
		return INSTANCE;
	}

	public void log(int niv, @NonNull Exception e)
	{
		log(niv, e.getLocalizedMessage());
		for (int i = 0; i < e.getStackTrace().length && i < MAX_BACKTRACE; i++)
			log(niv, e.getStackTrace()[i].getClassName() + '/' + e.getStackTrace()[i].getMethodName() + ':' + e.getStackTrace()[i].getLineNumber());
	}

	public void log(int niv, @NonNull String message)
	{
		Log.d(TAG, message);
		_reportDatabase.Ajoute(ReportDatabaseHelper.CalendarToSQLiteDate(null), niv, message);
	}
}
