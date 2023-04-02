package com.lpi.bloquespam.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MessageBoxUtils
	{
	public static final int BOUTON_OK = 1;
	public static final int BOUTON_CANCEL = 2;
		@SuppressLint("ResourceType")
		public static void messageBox(@NonNull Context context, int titre, int texte, int boutons, final @Nullable Listener listener)
		{
			messageBox(context, context.getString(titre), context.getString(texte), boutons, listener);
		}
		public static void messageBox(@NonNull Context context, @NonNull String titre, @NonNull String texte, int boutons, final @Nullable Listener listener)
		{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(titre);
		builder.setMessage(texte);
		if ((boutons & BOUTON_OK) != 0)
			builder.setPositiveButton(context.getResources().getString(android.R.string.ok), (dialog, id) ->
				{
				if (listener != null)
					listener.onOk();
				});

		if ((boutons & BOUTON_CANCEL) != 0)
			builder.setNegativeButton(context.getResources().getString(android.R.string.cancel), (dialog, id) ->
				{
				if (listener != null)
					listener.onCancel();
				});

		// Create the AlertDialog object and return it
		builder.create().show();
		}

	public interface Listener
		{
		void onOk();
		void onCancel();
		}
	}