package com.lpi.bloquespam.utils;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

/**
 * Helper pour afficher une fenetre de confirmation, fournir un listener pour etre prevenu du
 * resultat
 */
public class ConfirmBox
{
	public static void show(@NonNull final Context context, @StringRes int idRes, @Nullable final ConfirmBoxListener listener)
	{
		show(context, context.getString(idRes), listener);
	}

	/***
	 *  Afficher la fenetre de confirmation
	 */
	public static void show(@NonNull final Context context, @NonNull final String message, @Nullable final ConfirmBoxListener listener)
	{
		//
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				switch (which)
				{
					case DialogInterface.BUTTON_POSITIVE:
						//Yes button clicked
						if( listener!=null)
						listener.onPositive();
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						//No button clicked
						if( listener!=null)
							listener.onNegative();
						break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		AlertDialog dialog = builder.setMessage(message)
				.setPositiveButton(context.getResources().getString(android.R.string.ok), dialogClickListener)
				.setNegativeButton(context.getResources().getString(android.R.string.cancel), dialogClickListener)
				.setOnCancelListener(new DialogInterface.OnCancelListener()
				{
					@Override public void onCancel(final DialogInterface dialogInterface)
					{
						if( listener!=null)
							listener.onNegative();
					}
				})
				.setOnDismissListener(new DialogInterface.OnDismissListener()
				{
					@Override public void onDismiss(final DialogInterface dialogInterface)
					{
						if( listener!=null)
							listener.onNegative();
					}
				})
				.create();
		dialog.show();
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
		dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
	}

	// Le listener a fournir par l'appelant, lui permettra de savoir quelle option a ete choisie
	public interface ConfirmBoxListener
	{
		void onPositive();
		void onNegative();
	}

}
