package com.lpi.bloquespam;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.bloquespam.database.Regle;
import com.lpi.bloquespam.utils.MessageBoxUtils;

public class EditRegle
{
	public interface Listener
	{
		void onOK(@NonNull final Regle regle);
	}

	public static void start(@NonNull final Activity activity, @Nullable final Regle regle, @NonNull final Listener listener)
	{
		final AlertDialog dialogBuilder = new AlertDialog.Builder(activity).create();
		final LayoutInflater inflater = activity.getLayoutInflater();
		final View dialogView = inflater.inflate(R.layout.dialog_edit_regle, null);
		final EditText etNumero = dialogView.findViewById(R.id.etNumero);
		final RadioGroup rgRegle = dialogView.findViewById(R.id.rgRegle);
		final RadioButton rbBloquer = dialogView.findViewById(R.id.rbBloquer);

		if ( regle !=null)
		{
			etNumero.setText(regle._numero);
			rgRegle.check(regle._bloquer? R.id.rbBloquer : R.id.rbAutoriser);
		}
		else
		{
			rgRegle.check(R.id.rbBloquer);
		}


		// Ok
		final Button bOk = dialogView.findViewById(R.id.bOk);
		bOk.setOnClickListener(new View.OnClickListener()
		{
			@Override public void onClick(View view)
			{
				final String numero = RulesManager.nettoyerNumero(etNumero.getText().toString());
				etNumero.setText(numero);
				if (incorrect(numero))
				{
					MessageBoxUtils.messageBox(activity, activity.getString(R.string.numero_vide_titre), activity.getString(R.string.numero_vide_message), MessageBoxUtils.BOUTON_OK, null);
					return;
				}

				final Regle regleEditee = new Regle();
				regleEditee._numero = numero;
				regleEditee._bloquer = rbBloquer.isChecked();
				if ( regle!=null)
					regleEditee._id = regle._id;

				listener.onOK(regleEditee);
				dialogBuilder.dismiss();
			}

			private boolean incorrect(@Nullable final String numero)
			{
				if ( numero == null)
					return true;
				return numero.isEmpty();
			}
		});

		// Annuler
		final Button bCancel = dialogView.findViewById(R.id.bCancel);
		bCancel.setOnClickListener(new View.OnClickListener()
		{
			@Override public void onClick(View view)
			{
				dialogBuilder.dismiss();
			}
		});


		dialogBuilder.setView(dialogView);
		dialogBuilder.show();

		Window window = dialogBuilder.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}
}
