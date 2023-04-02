package com.lpi.bloquespam;

import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;

import com.lpi.bloquespam.database.Regle;
import com.lpi.bloquespam.database.ReglesDatabase;
import com.lpi.bloquespam.report.Report;
import com.lpi.bloquespam.utils.ContactUtils;
import com.lpi.bloquespam.utils.Preferences;

/***************************************************************************************************
 * Gestion des regles de blocage pour decider si on bloque un appel entrant
 */
public class RulesManager
{

	/***
	 * Retourne TRUE si on doit bloquer le numero
	 */
	public static boolean numeroBloque(@NonNull final Context context, @NonNull final String numero)
	{
		final Report report = Report.getInstance(context);
		final Preferences preferences = Preferences.getInstance(context);

		report.log(Report.DEBUG, "Faut il bloquer " + numero + "?");

		// Le blocage de numero est-il actif?
		if ( ! preferences.getBlocageActif())
		{
			report.log(Report.DEBUG, "Non, blocage inactif");
			return false;
		}

		// Est ce un contact connu?
		final String contact = ContactUtils.getContactFromNumber(context, numero);
		if ( contact!=null)
		{
			report.log(Report.DEBUG, "Non, numéro connu dans les contacts " + contact);
			return false;
		}

		// Est ce qu'il fait partie des numeros a toujours accepter?
		if ( toujoursAccepter(context, numero))
		{
			report.log(Report.DEBUG, "Non, fait partie des numéros toujours acceptés");
			return false;
		}

		// Est ce qu'il fait partie des numeros a toujours bloquer?
		if (toujoursBloquer(context, numero))
		{
			report.log(Report.DEBUG, "Oui, fait partie des numéros toujours refusés");
			return true;
		}

		// Aucune regle pour ce numero
		report.log(Report.DEBUG, "Non, aucune règle pour ce numéro");
		return false ;
	}


	/***
	 * Determine si le numero fait partie de ceux qu'on accepte toujours
	 */
	private static boolean toujoursAccepter(@NonNull final Context context, @NonNull final String numero)
	{
		Cursor cursor = ReglesDatabase.getInstance(context).getNumerosAcceptes();
		if ( cursor == null)
			return false;

		return numeroDansListe(numero, cursor);
	}

	/***
	 * Determine si le numero fait partie de ceux qu'on bloque toujours
	 */
	private static boolean toujoursBloquer(@NonNull final Context context, @NonNull final String numero)
	{
		Cursor cursor = ReglesDatabase.getInstance(context).getNumerosBloques();
		if ( cursor == null)
			return false;

		return numeroDansListe(numero, cursor);
	}

	private static boolean numeroDansListe(@NonNull final String numero, @NonNull final Cursor cursor)
	{
		String n = nettoyerNumero(numero);
		while (cursor.moveToNext())
		{
			Regle regle = new Regle(cursor);
			if ( regle.conforme(n))
				return true;
		}
		return false;
	}

	/***
	 * Nettoyer le numero des caracteres indésirables
	 */
	public static @NonNull String nettoyerNumero(@NonNull final String numero)
	{
		StringBuilder sb = new StringBuilder();
		for ( int i = 0; i < numero.length();i++)
			switch( numero.charAt(i))
			{
				case ' ':
					break;
				default:
					sb.append(numero.charAt(i));
			}
		return sb.toString();
	}


}
