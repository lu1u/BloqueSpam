package com.lpi.bloquespam.database;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

public class Regle
{
	public int _id;
	public String _numero;
	public boolean _bloquer; // Si oui: toujours bloquer, sinon toujours autoriser

	public Regle()
	{
	}

	public Regle( @NonNull Cursor cursor)
	{
		_id = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLONNE_REGLE_ID));
		_numero = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLONNE_REGLE_NUMERO));
		_bloquer = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLONNE_REGLE_BLOQUER)) != 0;
	}


	public void toContentValues(@NonNull ContentValues content, boolean putId)
	{
		if (putId)
			content.put(DbHelper.COLONNE_REGLE_ID, _id);

		content.put(DbHelper.COLONNE_REGLE_NUMERO, _numero);
		content.put(DbHelper.COLONNE_REGLE_BLOQUER, _bloquer ? 1:0);
	}

	/***
	 * Determine si le numero est conforme a la regle
	 */
	public boolean conforme(@NonNull final String numero)
	{
		return numero.startsWith(_numero);
	}
}
