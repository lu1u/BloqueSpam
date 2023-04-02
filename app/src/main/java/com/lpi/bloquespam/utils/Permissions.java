package com.lpi.bloquespam.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import com.lpi.bloquespam.R;


/***
 * Gestion des permissions de l'application
 */
public class Permissions
{


	/*******************************************************************************************************************
	 * Verifie que toutes les permissions demandées par l'application ont bien ete accordées et fait une demande au
	 * systeme si besoin
	 *******************************************************************************************************************/
	public static boolean demandePermissionsSiBesoin(@NonNull final Activity activity, @NonNull final String[] permissions)
	{
		if (verifiePermissions(activity, permissions))
			return true;

		AlertDialog.Builder dlgAlert = new AlertDialog.Builder(activity);
		dlgAlert.setMessage(R.string.message_ask_for_permissions);
		dlgAlert.setTitle(activity.getResources().getString(R.string.message_missing_permissions));
		dlgAlert.setCancelable(false);
		dlgAlert.setPositiveButton(activity.getResources().getString(R.string.message_grant_permissions),
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						//dismiss the dialog
						activity.requestPermissions(permissions, 0);
					}
				});

		dlgAlert.create().show();
		return false;
	}

	/*******************************************************************************************************************
	 * Verifie que toutes les permissions demandées par l'application ont bien ete accordées
	 * @return false si au moins une permission n'est pas accordee
	 *******************************************************************************************************************/
	public static boolean verifiePermissions(@NonNull Context context, @NonNull final String[] permissions)
	{
		for (String p : permissions)
			if (context.checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED)
				return false;

		return true;
	}
}
