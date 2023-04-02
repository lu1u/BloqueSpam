package com.lpi.bloquespam.database;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import com.lpi.bloquespam.utils.DateUtilitaires;

import java.util.Calendar;
import java.util.Objects;

public class Evenement
{
	public int _id;
	public String _numero;
	public boolean _bloquer; // Numéro bloqué ou accepté
	@NonNull public Calendar _date = Calendar.getInstance();
	public Evenement()
	{
	}

	public Evenement(Cursor cursor)
	{
		_id = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLONNE_HISTORIQUE_ID));
		_numero = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLONNE_HISTORIQUE_NUMERO));
		_bloquer = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLONNE_HISTORIQUE_BLOQUER)) != 0;
		_date = Objects.requireNonNull(DateUtilitaires.sqlStringToCalendar(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLONNE_HISTORIQUE_DATE))));
	}



	public void toContentValues(@NonNull ContentValues content, boolean putId)
	{
		if (putId)
			content.put(DbHelper.COLONNE_HISTORIQUE_ID, _id);

		content.put(DbHelper.COLONNE_HISTORIQUE_NUMERO, _numero);
		content.put(DbHelper.COLONNE_HISTORIQUE_BLOQUER, _bloquer ? 1:0);
		content.put(DbHelper.COLONNE_HISTORIQUE_DATE, DateUtilitaires.calendarToSqlString(_date));
	}
}
