package com.lpi.bloquespam.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ContactUtils
{
	@NonNull public static final String[] COLONNES_NUMERO = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

	/**
	 * Essaie de retrouver le nom d'un contact a partir de son numero de telephone
	 * @param numero : numero appelant
	 * @return le nom du contact ou null
	 */
	public static @Nullable	String getContactFromNumber(@NonNull final Context context, @Nullable final String numero)
	{
		if (numero == null)
			return null;

		String res = null;
		try
		{
			Cursor c = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(numero)), COLONNES_NUMERO, null, null, null);
			if (c != null)
			{
				c.moveToFirst();
				res = c.getString(c.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
				c.close();
			}
		} catch (Exception e)
		{
			if (numero.startsWith("+33")) //$NON-NLS-1$
				return getContactFromNumber(context, "0" + numero.substring(3));
			else
				res = null;
		}

		return res;
	}
}
