package com.lpi.bloquespam;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.lpi.bloquespam.database.Evenement;
import com.lpi.bloquespam.database.HistoriqueDatabase;
import com.lpi.bloquespam.report.Report;
import com.lpi.bloquespam.ui.history.HistoryFragment;
import com.lpi.bloquespam.utils.Preferences;

import java.lang.reflect.Method;
import java.util.Random;

@SuppressWarnings("CommentedOutCode")
public class PhoneListener extends BroadcastReceiver
{
	@NonNull private static final String TAG = "PhoneListener";
	@NonNull private static final String CHANNEL_ID = "Bloque inconnu";
	private static final long DELAI_RESTAURER_SON = 30 * 1000; // Restaurer le volume au bout de 30 secondes si on n'a pas detecté le raccrochage
	private static final int NOTIFICATION_ID = 2569;
	private static int lastState = TelephonyManager.CALL_STATE_IDLE;

	@NonNull static final Random rand = new Random();

	/***
	 * Reception de l'evenement de changement d'etat du telephone
	 */
	@Override
	public void onReceive(@NonNull Context context, Intent intent)
	{
		Report.getInstance(context);
		try
		{
			if (!TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(intent.getAction()))
				// Ne nous concerne pas
				return;

			Bundle b = intent.getExtras();
			if (b != null)
			{
				String stateStr = b.getString(TelephonyManager.EXTRA_STATE);
				if (stateStr == null)
					return;

				String number = b.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
				if (number == null)
					return; // Important car on est appelé deux fois (!?) avec state = RINGING, la premiere fois avec number = null

//				int subId = getSubscription(intent);
//				r.log(Report.HISTORIQUE, "Appel entrant, sub id " + subId + ", numero " + number);

				int state = 0;
				if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE))
				{
					state = TelephonyManager.CALL_STATE_IDLE;
				}
				else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
				{
					state = TelephonyManager.CALL_STATE_OFFHOOK;
				}
				else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING))
				{
					state = TelephonyManager.CALL_STATE_RINGING;
				}

				if (lastState == state)
					//No change, debounce extras
					return;
				lastState = state;

				switch (state)
				{
					case TelephonyManager.CALL_STATE_RINGING:
						onAppelEntrant(context, number);
						break;
					case TelephonyManager.CALL_STATE_IDLE:
						onRaccroche(context);
						break;
				}

			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void onRaccroche(@NonNull final Context context)
	{
		Report.getInstance(context).log(Report.HISTORIQUE, "Appel raccroché, restaurer le volume de la sonnerie");
		restaurerSon(context);
	}

	/***
	 * Gestion d'un appel entrant, eventuellement le bloquer
	 */
	protected void onAppelEntrant(@NonNull final Context context, @NonNull String number)
	{
		Report r = Report.getInstance(context);
		try
		{
			if (RulesManager.numeroBloque(context, number))
			{
				r.log(Report.HISTORIQUE, "Numéro bloqué");
				essaieDeRejeterLAppel(context);
				ajouteEvenement(context, number, true);
				ajouteNotification(context, number);
			}
			else
			{
				r.log(Report.HISTORIQUE, "Numero non bloqué");
				ajouteEvenement(context, number, false);
			}
		} catch (Exception e)
		{
			r.log(Report.ERROR, "Erreur dans IncomingCallReceiver.onAppelEntrant");
			r.log(Report.ERROR, e);
		}
	}

	/***
	 * Ajoute une notification pour signifier qu'on a bloqué un numéro
	 */
	private void ajouteNotification(@NonNull final Context context, @NonNull final String number)
	{
		final Preferences preferences = Preferences.getInstance(context);
		if (!preferences.getNotificationsActif())
			return;

		try
		{
			NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
			notificationManager.cancel(NOTIFICATION_ID);

			createNotificationChannel(context);

			// Pour afficher l'activity de l'application quand on clique sur la notification
			Intent resultIntent = new Intent(context, MainActivity.class);// Create an Intent for the activity you want to start
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);// Create the TaskStackBuilder and add the intent, which inflates the back stack
			stackBuilder.addNextIntentWithParentStack(resultIntent);        // Get the PendingIntent containing the entire back stack
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);

			// Creer la notification
			@SuppressLint("ResourceType") NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
					.setContentIntent(resultPendingIntent)
					.setSmallIcon(R.drawable.icone_notification)
					.setContentTitle(context.getString(R.string.notification_numero_bloque))
					.setContentText(number)
					//.addAction(R.drawable.snooze, _context.getString(R.string.snooze_5_minutes),getSnoozeIntent(tache, 5))
					//.addAction(R.drawable.snooze, _context.getString(R.string.snooze_1_hour),getSnoozeIntent(tache, 60))
					.setPriority(NotificationCompat.PRIORITY_DEFAULT);
			notificationManager.notify(getNotificationId(), builder.build());
		} catch (Exception e)
		{
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
	}

	/***
	 * Ajouter un numero dans la table des evenements
	 */
	private void ajouteEvenement(@NonNull final Context context, @NonNull final String number, boolean numeroBloque)
	{
		HistoriqueDatabase db = HistoriqueDatabase.getInstance(context);
		Evenement evt = new Evenement();
		evt._numero = number;
		evt._bloquer = numeroBloque;
		db.ajoute(evt);
		HistoryFragment.UpdateListesHistorique(context);
	}

	/***
	 * Refuser un appel
	 */
	private void essaieDeRejeterLAppel(@NonNull final Context context)
	{
		if (rejeterAppel(context))
			return;

		baisserSonnerie(context);
	}

	/***
	 * Baisser la sonnerie pour le cas ou on ne pourrait pas refuser l'appel
	 */
	private void baisserSonnerie(@NonNull final Context context)
	{
		Report report = Report.getInstance(context);
		report.log(Report.DEBUG, "baisser la sonnerie");
		try
		{
			AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			final int ringerMode = audioManager.getRingerMode();
			report.log(Report.DEBUG, "Ringer mode " + ringerMode);

			if (ringerMode != AudioManager.RINGER_MODE_NORMAL)
				return;

			// Memoriser l'ancien volume sonore
			final int volume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
			report.log(Report.DEBUG, "Volume initial " + volume);
			if (volume > 0)
			{
				Preferences preferences = Preferences.getInstance(context);
				preferences.putMemorisationVolume(volume);
				preferences.putVolumeMisAZero(true);

				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_ALLOW_RINGER_MODES | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

				// Remettre le volume a sa valeur initiale apres un certain delai si on n'a pas detecté la fin de l'appel
				final Handler handler = new Handler(Looper.getMainLooper());
				handler.postDelayed(() ->
				{
					report.log(Report.DEBUG, "Remettre le volume apres un delai");
					restaurerSon(context);
				}, DELAI_RESTAURER_SON);
			}
		} catch (Exception e)
		{
			report.log(Report.ERROR, "Erreur dans PhoneListener.baisserVolume");
			report.log(Report.ERROR, e);
		}

	}

	/***
	 * Restaurer le volume a sa valeur initiale apres l'avoir baissé temporairement
	 */
	private synchronized void restaurerSon(@NonNull final Context context)
	{
		Report report = Report.getInstance(context);
		report.log(Report.DEBUG, "restaurer le volume");
		try
		{
			Preferences preferences = Preferences.getInstance(context);

			if (!preferences.getVolumeMisAZero())
				return;

			final int volume = preferences.getMemorisationVolume();
			AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_ALLOW_RINGER_MODES | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			preferences.putVolumeMisAZero(false);
		} catch (Exception e)
		{
			report.log(Report.ERROR, "Erreur dans PhoneListener.restaurerSon");
			report.log(Report.ERROR, e);
		}
	}


	private static int getNotificationId()
	{
		return rand.nextInt();
	}

	public static void createNotificationChannel(@NonNull final Context context)
	{
		// Create the NotificationChannel, but only on API 26+ because
		// the NotificationChannel class is new and not in the support library
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		{
			CharSequence name = context.getString(R.string.channel_name);
			String description = context.getString(R.string.channel_description);
			int importance = NotificationManager.IMPORTANCE_DEFAULT;
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
			channel.setDescription(description);
			// Register the channel with the system; you can't change the importance
			// or other notification behaviors after this
			NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(channel);
		}
	}

	/***
	 * Rejeter l'appel en raccrochant, pour cause de securité, cette méthode a peu de chance de
	 * fonctionner
	 * @return true si on a reussi a bloquer l'appel
	 */
	private boolean rejeterAppel(@NonNull final Context context)
	{
		if (rejetMethode1(context))
			return true;

		return rejetMethode2(context);
	}

	/***
	 * Normalement, ca ne marche pas!
	 */
	private static boolean rejetMethode1(@NonNull final Context context)
	{
		Report report = Report.getInstance(context);
		try
		{
			// https://stackoverflow.com/questions/47916927/blocking-incoming-calls
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			@SuppressLint("SoonBlockedPrivateApi") Method m = tm.getClass().getDeclaredMethod("getITelephony");

			m.setAccessible(true);
			com.android.internal.telephony.ITelephony telephonyService = (com.android.internal.telephony.ITelephony) m.invoke(tm);
			telephonyService.endCall();
			report.log(Report.HISTORIQUE, "Appel bloqué avec la méthode 1");
		} catch (Exception e)
		{
			// Impossible de rejeter l'appel par cette methode
			report.log(Report.HISTORIQUE, "Méthode 1 échouée");
			return false;
		}
		return true;
	}

	private static boolean rejetMethode2(@NonNull final Context context)
	{
		Report report = Report.getInstance(context);
		try
		{
			//https://stackoverflow.com/questions/15012082/rejecting-incoming-call-in-android
			String serviceManagerName = "android.os.ServiceManager";
			String serviceManagerNativeName = "android.os.ServiceManagerNative";
			String telephonyName = "com.android.internal.telephony.ITelephony";
			Class<?> telephonyClass;
			Class<?> telephonyStubClass;
			Class<?> serviceManagerClass;
			Class<?> serviceManagerNativeClass;
			Method telephonyEndCall;
			Object telephonyObject;
			Object serviceManagerObject;
			telephonyClass = Class.forName(telephonyName);
			telephonyStubClass = telephonyClass.getClasses()[0];
			serviceManagerClass = Class.forName(serviceManagerName);
			serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
			Method getService = // getDefaults[29];
					serviceManagerClass.getMethod("getService", String.class);
			Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
			Binder tmpBinder = new Binder();
			tmpBinder.attachInterface(null, "fake");
			serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
			IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
			Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
			telephonyObject = serviceMethod.invoke(null, retbinder);
			telephonyEndCall = telephonyClass.getMethod("endCall");
			telephonyEndCall.invoke(telephonyObject);
			report.log(Report.HISTORIQUE, "Appel bloqué avec la méthode 2");
		}
		catch (Exception e)
		{
			report.log(Report.HISTORIQUE, "Méthode 2 échouée");
			return false;
		}
		return true;
	}
}