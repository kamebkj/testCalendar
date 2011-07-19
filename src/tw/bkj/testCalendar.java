package tw.bkj;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class testCalendar extends Activity {

	public String TAG = "bkj";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button button = (Button)findViewById(R.id.button);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				TextView textView = (TextView)findViewById(R.id.textView);
				String[] projection = new String[] { "_id", "displayName" };
				Uri calendars;
				Uri events;
				Uri parsingDuration;

				textView.setText("");
				
				if (android.os.Build.VERSION.SDK_INT <= 7) {
					calendars = Uri.parse("content://calendar/calendars");
					events = Uri.parse("content://calendar/events");
					parsingDuration = Uri.parse("content://calendar/instances/when");
				} else {
					calendars = Uri.parse("content://com.android.calendar/calendars");
					events = Uri.parse("content://com.android.calendar/events");
					parsingDuration = Uri.parse("content://com.android.calendar/instances/when");
				}

				Cursor managedCursor = getContentResolver().query(calendars,
						projection, "selected=1", null, null);

				String[] calName = new String[managedCursor.getCount()];
				int[] calId = new int[managedCursor.getCount()];
				
				if (managedCursor != null && managedCursor.moveToFirst()) {
					
					for (int i = 0; i < calName.length; i++) {
						calId[i] = managedCursor.getInt(0);
						calName[i] = managedCursor.getString(1);
						Log.d("bkj", String.valueOf(calId[i]));
						Log.d("bkj", calName[i]);
						managedCursor.moveToNext();

					}
					managedCursor.close();
				}
				
				for (int i = 0; i < calName.length; i++) {
					Uri.Builder builder = parsingDuration.buildUpon();
					long now = new Date().getTime();
					ContentUris.appendId(builder, now - DateUtils.DAY_IN_MILLIS);
					ContentUris.appendId(builder, now + DateUtils.DAY_IN_MILLIS);

					Cursor eventCursor = getContentResolver().query(builder.build(),
							new String[] { "title", "begin", "end", "allDay"}, "Calendars._id=" + calId[i],
							null, "startDay ASC, startMinute ASC"); 
					
					// Full list of available columns : http://tinyurl.com/yfbg76w

					while (eventCursor.moveToNext()) {
						final String title = eventCursor.getString(0);
						final Date begin = new Date(eventCursor.getLong(1));
						final Date end = new Date(eventCursor.getLong(2));
						final Boolean allDay = !eventCursor.getString(3).equals("0");
						
						if (title!=null) {
							Log.d(TAG, title);
							textView.append(title);
							textView.append("\n");
						}
					}
					eventCursor.close();
				}
				

			}
		});

	}
}