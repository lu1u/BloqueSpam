package com.lpi.bloquespam.ui.regles;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.lpi.bloquespam.EditRegle;
import com.lpi.bloquespam.R;
import com.lpi.bloquespam.database.Regle;
import com.lpi.bloquespam.database.ReglesDatabase;
import com.lpi.bloquespam.database.ReglesRecyclerViewAdapter;
import com.lpi.bloquespam.report.Report;
import com.lpi.bloquespam.utils.ConfirmBox;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReglesFragment extends Fragment
{
	private ReglesRecyclerViewAdapter _adapter;
	private int _currentItemSelected = -1;
	private RecyclerView _rvRegles;
	private Report _report;
	private TextView _tvEmpty;

	public ReglesFragment()
	{
		// Required empty public constructor
	}


	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_regles, container, false);
		_rvRegles = v.findViewById(R.id.rvRegles);
		_tvEmpty = v.findViewById(R.id.tvEmpty);
		_rvRegles.setLayoutManager(new LinearLayoutManager(getContext()));

		_report = Report.getInstance(getContext());

		FloatingActionButton fab = v.findViewById(R.id.fabAjouter);
		fab.setOnClickListener(view ->
		{
			EditRegle.start(ReglesFragment.this.getActivity(), null, regle ->
			{
				ReglesDatabase.getInstance(getContext()).ajoute(regle);
				setAdapter();
			});
		});

		setAdapter();
		return v;
	}

	@Override public boolean onContextItemSelected(@NonNull MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_modifier:
				_currentItemSelected = _adapter.getSelectedItem();
				onEdit(_currentItemSelected);
				return true;
			case R.id.action_supprimer:
				onSupprime();
				return true;

			default:
				return super.onContextItemSelected(item);
		}
	}

	/***
	 * Supprimer la regle selectionnee
	 */
	private void onSupprime()
	{
		_currentItemSelected = _adapter.getSelectedItem();
		if (_currentItemSelected == -1)
			return;

		final Regle objetASupprimer = _adapter.get(_currentItemSelected);
		if (objetASupprimer != null)
		{
			ConfirmBox.show(getContext(), getResources().getString(R.string.supprimer_regle, objetASupprimer._numero), new ConfirmBox.ConfirmBoxListener()
			{
				@Override public void onPositive()
				{
					// Supprimer
					final ReglesDatabase database = ReglesDatabase.getInstance(getContext());

					if (database.supprime(objetASupprimer._id))
						messageNotification(_rvRegles, getString(R.string.message_regle_supprimee, objetASupprimer._numero));
					_currentItemSelected = -1;
					setAdapter();
				}

				@Override public void onNegative()
				{

				}
			});
		}
	}

	public static void messageNotification(@NonNull View v, @NonNull String message)
	{
		Snackbar.make(v, message, Snackbar.LENGTH_LONG).show();
	}

	/***
	 * Configurer l'adapteur de taches de la RecyclerView (a faire après chaque modification dans la table des Taches),
	 * avertir les Widgets du changement
	 */
	private void setAdapter()
	{
		Cursor cursor = ReglesDatabase.getInstance(getContext()).getCursor();
		if (cursor != null)
		{
			if (cursor.getCount() == 0)
			{
				_tvEmpty.setVisibility(View.VISIBLE);
				_rvRegles.setVisibility(View.GONE);
			}
			else
			{
				_tvEmpty.setVisibility(View.GONE);
				_rvRegles.setVisibility(View.VISIBLE);
			}

			_adapter = new ReglesRecyclerViewAdapter(getContext(), cursor, position -> onEdit(position));
			_rvRegles.setAdapter(_adapter);
		}
	}

	/***
	 * Appeler la fenetre d'édition d'une tache
	 * @param position de la tache dans la liste
	 */
	private void onEdit(int position)
	{
		Regle regle = _adapter.get(position);
		if (regle == null)
			_report.log(Report.ERROR, "Impossible de retrouver la tache a modifier");
		else
		{
			_report.log(Report.HISTORIQUE, "Modification de règle " + regle._numero + "(" + regle._id + ")");
			EditRegle.start(getActivity(), regle, new EditRegle.Listener()
			{
				@Override public void onOK(@NonNull Regle regle)
				{
					ReglesDatabase db = ReglesDatabase.getInstance(getContext());
					db.modifie(regle);
					setAdapter();
				}
			});
		}
	}
}