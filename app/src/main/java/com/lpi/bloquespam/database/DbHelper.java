package com.lpi.bloquespam.database;

/*
 * Utilitaire de gestion de la base de donnees
 */

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;

public class DbHelper extends SQLiteOpenHelper
{
	public static final int DATABASE_VERSION = 5;
	public static final @NonNull String DATABASE_NAME = "regles.db";
	////////////////////////////////////////////////////////////////////////////////////////////////////
	// Table des regles
	public static final @NonNull String TABLE_REGLES = "REGLES";
	public static final @NonNull String COLONNE_REGLE_ID = "_id";
	public static final @NonNull String COLONNE_REGLE_NUMERO = "NUMERO";
	public static final @NonNull String COLONNE_REGLE_BLOQUER = "BLOQUER";	// Si 0: toujours autoriser, sinon bloquer
	public static final @NonNull String[] TABLE_REGLES_COLONNES = {COLONNE_REGLE_ID, COLONNE_REGLE_NUMERO, COLONNE_REGLE_BLOQUER};

	////////////////////////////////////////////////////////////////////////////////////////////////////
	// Table des historiques
	public static final @NonNull String TABLE_HISTORIQUE = "HISTORIQUE";
	public static final @NonNull String COLONNE_HISTORIQUE_ID = "_id";
	public static final @NonNull String COLONNE_HISTORIQUE_NUMERO = "NUMERO";
	public static final @NonNull String COLONNE_HISTORIQUE_BLOQUER = "BLOQUER";	// Si 0: toujours autoriser, sinon bloquer
	public static final @NonNull String COLONNE_HISTORIQUE_DATE = "DATE_CREATION";	// Si 0: toujours autoriser, sinon bloquer
	public static final @NonNull String[] TABLE_HISTORIQUE_COLONNES= {COLONNE_HISTORIQUE_ID, COLONNE_HISTORIQUE_NUMERO, COLONNE_HISTORIQUE_BLOQUER,COLONNE_HISTORIQUE_DATE};

	public static final int INVALID_ID = -1;

	@NonNull private static final String DATABASE_REGLES_CREATE = "create table "
			+ TABLE_REGLES + " ("
			+ COLONNE_REGLE_ID + " integer primary key autoincrement, "
			+ COLONNE_REGLE_NUMERO + " text not null,"
			+ COLONNE_REGLE_BLOQUER + " integer not null"
			+ ");";

	@NonNull private static final String DATABASE_HISTORIQUE_CREATE = "create table "
			+ TABLE_HISTORIQUE + " ("
			+ COLONNE_HISTORIQUE_ID + " integer primary key autoincrement, "
			+ COLONNE_HISTORIQUE_NUMERO + " text not null,"
			+ COLONNE_HISTORIQUE_BLOQUER + " integer not null,"
			+ COLONNE_HISTORIQUE_DATE + " text not null"
			+ ");";



	public DbHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(@NonNull SQLiteDatabase database)
	{
		try
		{
			database.execSQL(DATABASE_REGLES_CREATE);
			database.execSQL(DATABASE_HISTORIQUE_CREATE);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(@NonNull SQLiteDatabase database, int oldVersion, int newVersion)
	{
		try
		{
			Log.w(DbHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
			database.execSQL("DROP TABLE IF EXISTS " + TABLE_REGLES);
			database.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORIQUE);

			database.execSQL(DATABASE_HISTORIQUE_CREATE);
			onCreate(database);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}


}
