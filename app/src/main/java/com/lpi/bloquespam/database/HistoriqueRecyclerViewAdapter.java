package com.lpi.bloquespam.database;

import static androidx.core.content.ContextCompat.getDrawable;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lpi.bloquespam.R;
import com.lpi.bloquespam.utils.DateUtilitaires;

import java.text.DateFormat;

public class HistoriqueRecyclerViewAdapter extends RecyclerView.Adapter<HistoriqueRecyclerViewAdapter.ViewHolder>
{


	private final LayoutInflater _inflater;
	@NonNull private final Cursor _cursor;

	// data is passed into the constructor
	public HistoriqueRecyclerViewAdapter(@NonNull final Context context, @NonNull final Cursor c)
	{
		_inflater = LayoutInflater.from(context);
		_cursor = c;
	}

	/***
	 * Creer la fenetre pour contenir une tache
	 */
	@NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		View view = _inflater.inflate(R.layout.element_liste_historique, parent, false);
		return new ViewHolder(view);
	}

	/***
	 * Remplir les controles avec les valeurs de la tache
	 */
	@Override public void onBindViewHolder(@NonNull ViewHolder holder, int position)
	{
		Context context = _inflater.getContext();
		_cursor.moveToPosition(position);
		Evenement evenement = new Evenement(_cursor);
		holder._tvNumero.setText(evenement._numero);
		String date;
		if (evenement._bloquer)
		{
			holder._image.setImageDrawable(getDrawable(context, R.drawable.phone_cancel));
			date = context.getString(R.string.appel_bloque,DateUtilitaires.getDateAndTime(evenement._date, DateFormat.MEDIUM, DateFormat.MEDIUM) );
		}
		else
		{
			holder._image.setImageDrawable(getDrawable(context, R.drawable.phone_check));
			date = context.getString(R.string.appel_autorise,DateUtilitaires.getDateAndTime(evenement._date, DateFormat.MEDIUM, DateFormat.MEDIUM) );
		}

		holder._tvDate.setText(date);
	}

	@Override public int getItemCount()
	{
		return _cursor.getCount();
	}

	// stores and recycles views as they are scrolled off screen
	public static class ViewHolder extends RecyclerView.ViewHolder
	{
		public final TextView _tvNumero;
		public final TextView _tvDate;
		public final ImageView _image;

		ViewHolder(@NonNull final View itemView)
		{
			super(itemView);
			_tvNumero = itemView.findViewById(R.id.tvNumero);
			_tvDate = itemView.findViewById(R.id.tvDate);
			_image = itemView.findViewById(R.id.ivRegle);
		}
	}
}
