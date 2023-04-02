package com.lpi.bloquespam.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.bloquespam.report.Report;


public class ReglesDatabase
{
	@NonNull private static final String TAG = "TachesDatabase";
	@Nullable protected static ReglesDatabase INSTANCE = null;
	protected SQLiteDatabase _database;
	protected DbHelper _dbHelper;

	/**
	 * Point d'accès pour l'instance unique du singleton
	 */
	@NonNull
	public static synchronized ReglesDatabase getInstance(@NonNull Context context)
	{
		if (INSTANCE == null)
			INSTANCE = new ReglesDatabase(context);
		return INSTANCE;
	}

	private ReglesDatabase(@NonNull Context context)
	{
		try
		{
			_dbHelper = new DbHelper(context);
			_database = _dbHelper.getWritableDatabase();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	@Override
	protected void finalize()
	{
		_dbHelper.close();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/***
	 * ajoute une regle
	 */
	////////////////////////////////////////////////////////////////////////////////////////////////
	public void ajoute(@NonNull Regle regle)
	{
		try
		{
			ContentValues initialValues = new ContentValues();
			regle.toContentValues(initialValues, false);
			_database.insert(DbHelper.TABLE_REGLES, null, initialValues);
		} catch (Exception e)
		{
			Report.getInstance(null).log(Report.ERROR,"Erreur dans ajoute");
			Report.getInstance(null).log(Report.ERROR, e);
		}
	}


	public void modifie(@NonNull Regle regle)
	{
		try
		{
			ContentValues valeurs = new ContentValues();
			regle.toContentValues(valeurs, true);
			_database.update(DbHelper.TABLE_REGLES, valeurs, DbHelper.COLONNE_REGLE_ID + " = " + regle._id, null);
		} catch (Exception e)
		{
			Report report = Report.getInstance(null);
			report.log(Report.ERROR,"modification de la regle");
			report.log(Report.ERROR,e);
		}
	}

	/***
	 * Supprime une tache
	 * @return true si operation ok, false si erreur
	 */
	public boolean supprime(int Id)
	{
		try
		{
			// Operation effectuee dans une transaction sqlite pour garantir la coherence de la base
			_database.beginTransaction();
			// supprimer la tache
			_database.delete(DbHelper.TABLE_REGLES, DbHelper.COLONNE_REGLE_ID + " = " + Id, null);
			_database.setTransactionSuccessful();
		} catch (Exception e)
		{
			_database.endTransaction();
			return false;
		}

		_database.endTransaction();
		return true;
	}


	public @Nullable
	Cursor getCursor()
	{
		try
		{
			return _database.query(DbHelper.TABLE_REGLES, null,null, null, null, null, DbHelper.COLONNE_REGLE_BLOQUER + " ASC");
		} catch (Exception e)
		{
			Report.getInstance(null).log(Report.ERROR,"Erreur dans getCursor");
			Report.getInstance(null).log(Report.ERROR, e);
		}

		return null;
	}


	/***
	 * Retourne le nombre de regles dans la base
	 */
	public int nbRegles()
	{
		Cursor cursor = _database.rawQuery("SELECT COUNT (*) FROM " + DbHelper.TABLE_REGLES, null);
		int count = 0;
		if (null != cursor)
		{
			if (cursor.getCount() > 0)
			{
				cursor.moveToFirst();
				count = cursor.getInt(0);
			}
			cursor.close();
		}
		return count;
	}


	public Cursor getNumerosAcceptes()
	{
		String selection;
		String[] selectionArg;
				selection = DbHelper.COLONNE_REGLE_BLOQUER + " =?";
				selectionArg = new String[]{"0"};

		return _database.query(DbHelper.TABLE_REGLES, DbHelper.TABLE_REGLES_COLONNES, selection, selectionArg, null, null, null);
	}


	public Cursor getNumerosBloques()
	{
		String selection;
		String[] selectionArg;
		selection = DbHelper.COLONNE_REGLE_BLOQUER + " <>?";
		selectionArg = new String[]{"0"};

		return _database.query(DbHelper.TABLE_REGLES, DbHelper.TABLE_REGLES_COLONNES, selection, selectionArg, null, null, null);
	}





}