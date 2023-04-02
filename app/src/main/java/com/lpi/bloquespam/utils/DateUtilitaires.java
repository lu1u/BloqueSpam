package com.lpi.bloquespam.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


/***
 * Classe de fonctions utilitaires pour gerer les date sous Android (java.util.Calendar)
 */
public class DateUtilitaires
{
//	/***
//	 * Calcule une representation texte localisee de la date
//	 * @param cal
//	 * @return
//	 */
//	public static @NonNull String getDateShort(@NonNull Calendar cal)
//	{
//		try
//		{
//			Date date = cal.getTime();
//			DateFormat f = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
//			return f.format(date);
//		} catch (Exception e)
//		{
//			return Objects.requireNonNull(e.getLocalizedMessage());
//		}
//	}

// --Commented out by Inspection START (01/04/2023 23:26):
//	/***
//	 * Calcule la reprensentation texte d'une date
//	 * @param Y
//	 * @param M
//	 * @param D
//	 * @param format Une des constantes DateFormat.SHORT, DateFormat.LONG, DateFormat.MEDIUM
//	 * @return
//	 */
//	public static @NonNull String getDateShort(int Y, int M, int D, int format)
//	{
//		Calendar cal = Calendar.getInstance();
//		cal.set(Calendar.YEAR, Y);
//		cal.set(Calendar.MONTH, M);
//		cal.set(Calendar.DAY_OF_MONTH, D);
//		try
//		{
//			Date date = cal.getTime();
//			DateFormat f = DateFormat.getDateInstance(format, Locale.getDefault());
//			return f.format(date);
//		} catch (Exception e)
//		{
//			return Objects.requireNonNull(e.getLocalizedMessage());
//		}
//	}
// --Commented out by Inspection STOP (01/04/2023 23:26)


// --Commented out by Inspection START (01/04/2023 23:26):
//	/***
//	 * Calcule la reprensentation texte d'une heure
//	 * @param H
//	 * @param M
//	 * @param format Une des constantes DateFormat.SHORT, DateFormat.LONG, DateFormat.MEDIUM
//	 * @return
//	 */
//	public static @NonNull String getTimeShort(int H, int M, int format)
//	{
//		Calendar cal = Calendar.getInstance();
//		cal.set(Calendar.HOUR_OF_DAY, H);
//		cal.set(Calendar.MINUTE, M);
//		try
//		{
//			Date date = cal.getTime();
//			DateFormat f = DateFormat.getTimeInstance(format, Locale.getDefault());
//			return f.format(date);
//		} catch (Exception e)
//		{
//			return Objects.requireNonNull(e.getLocalizedMessage());
//		}
//	}
// --Commented out by Inspection STOP (01/04/2023 23:26)

	public static @NonNull String getDateAndTime(Calendar cal, int formatDate, int formatTime)
	{
		try
		{
			Date date = cal.getTime();
			DateFormat f = DateFormat.getDateTimeInstance(formatDate, formatTime, Locale.getDefault());
			return f.format(date);
		} catch (Exception e)
		{
			return Objects.requireNonNull(e.getLocalizedMessage());
		}
	}

	/***
	 * Converti l'objet Calendar en chaine de caractere stockable dans une table SQLITE
	 * format: AAAA MM JJ HH MM SS -> permet de faire des tris
	 */
	public static @Nullable String calendarToSqlString(@Nullable Calendar cal)
	{
		if ( cal == null)
			return null;

		final int annee = cal.get(Calendar.YEAR);
		final int mois = cal.get(Calendar.MONTH)+1;
		final int jour = cal.get(Calendar.DAY_OF_MONTH);
		final int heure = cal.get(Calendar.HOUR_OF_DAY);

		final int minute = cal.get(Calendar.MINUTE);
		final int seconde = cal.get(Calendar.SECOND);

		return String.format("%04d %02d %02d %02d %02d %02d", annee, mois, jour, heure, minute, seconde);
	}
	public static @NonNull String longToSqlString(long date)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date);
		return calendarToSqlString(cal);
	}

	/***
	 * Converti une chaine de caractere stockable dans une table SQLITE en un objet Calendar
	 * format: AAAA MM JJ HH MM SS -> permet de faire des tris
	 */
	public static @Nullable Calendar sqlStringToCalendar(@Nullable String val)
	{
		if (val==null)
			return Calendar.getInstance();

		try
		{
			String[] morceaux = val.split(" ");
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, Integer.parseInt(morceaux[0]));
			cal.set(Calendar.MONTH, Integer.parseInt(morceaux[1])-1);
// --Commented out by Inspection START (01/04/2023 23:26):
//			cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(morceaux[2]));
//			cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(morceaux[3]));
//			cal.set(Calendar.MINUTE, Integer.parseInt(morceaux[4]));
// --Commented out by Inspection STOP (01/04/2023 23:26)
			cal.set(Calendar.SECOND, Integer.parseInt(morceaux[5]));
			return cal;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}


	public static long sqlStringToLong(String val)
	{
		Calendar cal = sqlStringToCalendar(val);
		if ( cal !=null)
			return cal.getTimeInMillis();

		return 0;
	}
}
