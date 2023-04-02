package com.lpi.bloquespam.database;

import static androidx.core.content.ContextCompat.getDrawable;

import android.content.Context;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.lpi.bloquespam.R;

public class ReglesRecyclerViewAdapter extends RecyclerView.Adapter<ReglesRecyclerViewAdapter.ViewHolder>
{
	public interface ItemClicListener
	{
		void onClic(int position);
	}

	private final LayoutInflater _inflater;
	@NonNull private final Cursor _cursor;
	private int _selectedItem = 0;
	@NonNull private final ItemClicListener _itemClicListener;

	// data is passed into the constructor
	public ReglesRecyclerViewAdapter(@NonNull final Context context, @NonNull final Cursor c, @NonNull final ItemClicListener listener)
	{
		_inflater = LayoutInflater.from(context);
		_cursor = c;
		_itemClicListener = listener;
	}

	/***
	 * Creer la fenetre pour contenir une tache
	 */
	@NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		View view = _inflater.inflate(R.layout.element_liste_regles, parent, false);
		return new ViewHolder(view);
	}

	/***
	 * Remplir les controles avec les valeurs de la tache
	 */
	@Override public void onBindViewHolder(@NonNull ViewHolder holder, int position)
	{
		Context context = _inflater.getContext();
		_cursor.moveToPosition(position);
		Regle regle = new Regle(_cursor);

		if (regle._bloquer)
		{
			holder._tvNumero.setText(context.getString(R.string.element_list_bloque, regle._numero));
			holder._image.setImageDrawable(getDrawable(context, R.drawable.phone_cancel));
		}
		else
		{
			holder._tvNumero.setText(context.getString(R.string.element_list_accepte, regle._numero));
			holder._image.setImageDrawable(getDrawable(context, R.drawable.phone_check));
		}
	}

	@Override
	public int getItemCount()
	{
		return _cursor.getCount();
	}

	/***
	 * Retourner la regle a la position donnee, ou null
	 */
	public @Nullable Regle get(int position)
	{
		if (position < 0 || position >= _cursor.getCount())
			return null;

		_cursor.moveToPosition(position);
		return new Regle(_cursor);
	}

	public int getSelectedItem()
	{
		return _selectedItem;
	}


	// stores and recycles views as they are scrolled off screen
	public class ViewHolder extends RecyclerView.ViewHolder
	{
		public final TextView _tvNumero;
		public final ImageView _image;

		ViewHolder(@NonNull View itemView)
		{
			super(itemView);
			_tvNumero = itemView.findViewById(R.id.tvNumero);
			_image = itemView.findViewById(R.id.ivRegle);

			itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener()
			{
				@Override
				public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo)
				{
					if (contextMenu!=null)
					{
						contextMenu.setHeaderTitle(_tvNumero.getText().toString());
						// Dans de rares cas, Android nous donne un menu déjà rempli
						if (contextMenu.findItem(R.id.action_modifier) == null)
							contextMenu.add(0, R.id.action_modifier, 0, R.string.menu_modifier);
						if (contextMenu.findItem(R.id.action_supprimer) == null)
							contextMenu.add(0, R.id.action_supprimer, 0, R.string.menu_supprimer);
					}
				}
			});

			itemView.setOnClickListener(new View.OnClickListener()
			{
				@Override public void onClick(View view)
				{
					if (getAdapterPosition() == RecyclerView.NO_POSITION) return;

					// Updating old as well as new positions
					notifyItemChanged(_selectedItem);
					_selectedItem = getAdapterPosition();
					notifyItemChanged(_selectedItem);
					_itemClicListener.onClic(_selectedItem);

				}
			});

			itemView.setOnLongClickListener(new View.OnLongClickListener()
			{
				@Override public boolean onLongClick(View view)
				{
					notifyItemChanged(_selectedItem);
					_selectedItem = getAdapterPosition();
					notifyItemChanged(_selectedItem);
					return false;
				}
			});
		}

	}
}
