package com.lpi.bloquespam.ui.history;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lpi.bloquespam.R;
import com.lpi.bloquespam.database.HistoriqueDatabase;
import com.lpi.bloquespam.database.HistoriqueRecyclerViewAdapter;
import com.lpi.bloquespam.utils.ConfirmBox;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment
{
	private static final String ACTION = HistoryFragment.class.getName() + "UPDATE";
	private RecyclerView _rvEvenements;
	private TextView _tvEmpty;


	/***
	 * Receptionne l'intent qui declenche la mise a jour des fragments
	 */
	@NonNull final BroadcastReceiver _receiver = new BroadcastReceiver()
	{
		@Override public void onReceive(@NonNull Context context, @NonNull Intent intent)
		{
			if (ACTION.equals(intent.getAction()))
				setAdapter();
		}
	};

	@NonNull final IntentFilter _filter = new IntentFilter(ACTION);

	@Override public void onPause()
	{
		super.onPause();
		getContext().unregisterReceiver(_receiver);
	}

	@Override public void onResume()
	{
		super.onResume();
		getContext().registerReceiver(_receiver, _filter);
		setAdapter();
	}

	public HistoryFragment()
	{
		// Required empty public constructor
	}


	/***
	 * Envoyer un message qui sera recu par les HistoryFragment pour se mettre a jour
	 */
	public static void UpdateListesHistorique(@NonNull final Context context)
	{
		Intent intent = new Intent(ACTION);
		context.sendBroadcast(intent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_history, container, false);
		_rvEvenements = v.findViewById(R.id.rvHistorique);
		_tvEmpty = v.findViewById(R.id.tvEmpty);

		_rvEvenements.setLayoutManager(new LinearLayoutManager(getContext()));

		FloatingActionButton fab = v.findViewById(R.id.fabViderHistorique);
		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override public void onClick(View view)
			{
				ConfirmBox.show(getContext(), R.string.supprimer_evenements, new ConfirmBox.ConfirmBoxListener()
				{
					@Override public void onPositive()
					{
						HistoriqueDatabase.getInstance(getContext()).vide();
						setAdapter();
					}

					@Override public void onNegative()
					{

					}
				});
				setAdapter();
			}
		});

		setAdapter();
		return v;
	}

	/***
	 * Configurer l'adapteur de taches de la RecyclerView (a faire après chaque modification dans la table des Taches),
	 * avertir les Widgets du changement
	 */
	private void setAdapter()
	{
		Cursor cursor = HistoriqueDatabase.getInstance(getContext()).getCursor();
		if (cursor != null)
		{
			if (cursor.getCount() == 0)
			{
				_tvEmpty.setVisibility(View.VISIBLE);
				_rvEvenements.setVisibility(View.GONE);
			}
			else
			{
				_tvEmpty.setVisibility(View.GONE);
				_rvEvenements.setVisibility(View.VISIBLE);
			}
			HistoriqueRecyclerViewAdapter _adapter = new HistoriqueRecyclerViewAdapter(getContext(), cursor);
			_rvEvenements.setAdapter(_adapter);
		}
	}
}

