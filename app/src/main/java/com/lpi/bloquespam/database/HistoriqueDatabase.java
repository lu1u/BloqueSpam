package com.lpi.bloquespam.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.bloquespam.report.Report;


@SuppressWarnings("ALL")
public class HistoriqueDatabase
{
	// --Commented out by Inspection (01/04/2023 23:26):private static final String TAG = "TachesDatabase";
	@Nullable
	protected static HistoriqueDatabase INSTANCE = null;
	protected SQLiteDatabase _database;
	protected DbHelper _dbHelper;

	/**
	 * Point d'accès pour l'instance unique du singleton
	 */
	@NonNull
	public static synchronized HistoriqueDatabase getInstance(@NonNull Context context)
	{
		if (INSTANCE == null)
			INSTANCE = new HistoriqueDatabase(context);
		return INSTANCE;
	}

	private HistoriqueDatabase(@NonNull Context context)
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
	public void ajoute(@NonNull Evenement evenement)
	{
		try
		{
			ContentValues initialValues = new ContentValues();
			evenement.toContentValues(initialValues, false);
			_database.insert(DbHelper.TABLE_HISTORIQUE, null, initialValues);
		} catch (Exception e)
		{
			Report.getInstance(null).log(Report.ERROR, "Erreur dans ajoute");
			Report.getInstance(null).log(Report.ERROR, e);
		}
	}


//	public void modifie(@NonNull Tache tache)
//	{
//		try
//		{
//			ContentValues valeurs = new ContentValues();
//			tache.toContentValues(valeurs, true);
//			_database.update(DbHelper.TABLE_REGLES, valeurs, DbHelper.COLONNE_TACHE_ID + " = " + tache._id, null);
//		} catch (Exception e)
//		{
//			MainActivity.SignaleErreur(null,"modification e la tache", e);
//		}
//	}
//
//	/***
//	 * Supprime une tache
//	 * @return true si operation ok, false si erreur
//	 */
//	public boolean supprime(int Id)
//	{
//		try
//		{
//			// Operation effectuee dans une transaction sqlite pour garantir la coherence de la base
//			_database.beginTransaction();
//			// supprimer la tache
//			_database.delete(DbHelper.TABLE_REGLES, DbHelper.COLONNE_TACHE_ID + " = " + Id, null);
//			_database.setTransactionSuccessful();
//		} catch (Exception e)
//		{
//			_database.endTransaction();
//			return false;
//		}
//
//		_database.endTransaction();
//		return true;
//	}


	public @Nullable
	Cursor getCursor()
	{
		try
		{
			return _database.query(DbHelper.TABLE_HISTORIQUE, null,null, null, null, null, DbHelper.COLONNE_HISTORIQUE_DATE + " DESC");
		} catch (Exception e)
		{
			Report.getInstance(null).log(Report.ERROR, "Erreur dans getCursor");
			Report.getInstance(null).log(Report.ERROR, e);
		}

		return null;
	}

// --Commented out by Inspection START (01/04/2023 23:26):
//	/***
//	 * Retourne le nombre de regles dans la base
//	 * @return
//	 */
//	public int nbEvenement()
//	{
//		Cursor cursor = _database.rawQuery("SELECT COUNT (*) FROM " + DbHelper.TABLE_HISTORIQUE, null);
//		int count = 0;
//		if (null != cursor)
//		{
//			if (cursor.getCount() > 0)
//			{
//				cursor.moveToFirst();
//				count = cursor.getInt(0);
//			}
//			cursor.close();
//		}
//		return count;
//	}
// --Commented out by Inspection STOP (01/04/2023 23:26)

	public void vide()
	{
		try
		{
			// Operation effectuee dans une transaction sqlite pour garantir la coherence de la base
			_database.beginTransaction();
			// supprimer la tache
			_database.delete(DbHelper.TABLE_HISTORIQUE, null, null);
			_database.setTransactionSuccessful();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		_database.endTransaction();
	}


//	/***
//	 * Return TRUE si une tache existe avec ce nom
//	 * @param nom
//	 * @return
//	 */
//	private boolean existe(@NonNull final String nom)
//	{
//		boolean existe = false;
//		try
//		{
//			Cursor cursor = _database.rawQuery("SELECT COUNT(*) FROM " + DbHelper.TABLE_REGLES + " WHERE " + DbHelper.COLONNE_TACHE_NOM + "=?", new String[]{nom});
//			if (null != cursor)
//			{
//				if (cursor.getCount() > 0)
//				{
//					cursor.moveToFirst();
//					existe = cursor.getInt(0) > 0;
//				}
//				cursor.close();
//			}
//		} catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//		return existe;
//	}
//
//	/***
//	 * Charge une tache a partir de son Id dans la base
//	 * @param id
//	 * @return
//	 */
//	public @Nullable  Tache getTache(int id)
//	{
//		Tache result = null;
//		String selection = DbHelper.COLONNE_TACHE_ID + " =?";
//		String[] selectionArg = new String[]{"" + id};
//		try
//		{
//			Cursor cursor = _database.query(DbHelper.TABLE_REGLES, DbHelper.TABLE_TACHES_COLONNES, selection, selectionArg, null, null, null);
//			if (null != cursor)
//			{
//				if (cursor.getCount() > 0)
//				{
//					cursor.moveToFirst();
//					result  = new Tache(cursor);
//					Log.d(TAG, "Tache trouvée " + result);
//				}
//				cursor.close();
//			}
//		} catch (Exception e)
//		{
//			MainActivity.SignaleErreur(null,"Erreur dans getCursor", e);
//		}
//		return result;
//	}

//	/***
//	 * Retourne un cursor qui ne contient que la tache dont on donne l'id
//	 * @param id
//	 * @return
//	 */
//	public @Nullable Cursor getCursorFromId(int id)
//	{
//		String selection = DbHelper.COLONNE_TACHE_ID + " =?";
//		String[] selectionArg = new String[]{"" + id};
//		try
//		{
//			return _database.query(DbHelper.TABLE_REGLES, DbHelper.TABLE_TACHES_COLONNES, selection, selectionArg, null, null, null);
//		} catch (Exception e)
//		{
//			MainActivity.SignaleErreur(null,"Erreur dans getCursor", e);
//			return null;
//		}
//	}
//
//	/***
//	 * Retourne la tache dont l'alarme est la plus proche dans l'avenir
//	 * @param dateMaintenant
//	 * @return
//	 */
//	public Tache getProchaineTache(String dateMaintenant)
//	{
//		Tache result = null;
//		try
//		{
//			final String query = "SELECT * FROM " + DbHelper.TABLE_REGLES + " WHERE " + DbHelper.COLONNE_TACHE_ALARME + " >= ?"
//					+ " ORDER BY " + DbHelper.COLONNE_TACHE_ALARME + " ASC, " + DbHelper.COLONNE_TACHE_PRIORITE + " DESC"
//					+ " LIMIT 1";
//
//			Log.d(TAG, "getProchaineTache: " + dateMaintenant);
//			Log.d(TAG, query);
//
//			Cursor cursor = _database.rawQuery(query, new String[]{dateMaintenant});
//			if (null != cursor)
//			{
//				if (cursor.getCount() > 0)
//				{
//					cursor.moveToFirst();
//					result  = new Tache(cursor);
//					Log.d(TAG, "Tache trouvée " + result);
//				}
//				cursor.close();
//			}
//		} catch (Exception e)
//		{
//			MainActivity.SignaleErreur(null,"Erreur dans getCursor", e);
//		}
//
//		return result;
//	}


}