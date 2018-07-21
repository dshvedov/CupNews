package ru.d9d.cupnews;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    private static final String LOG_TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class CupNewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, DatePickerDialog.OnDateSetListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference startDate = findPreference(getString(R.string.settings_date_key));
            startDate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showDateDialog();
                    return false;
                }
            });

            // Bind preference summary text to display string value
            bindPreferenceSummaryToValue(startDate);

            Preference queryText = findPreference(getString(R.string.settings_query_key));
            bindPreferenceSummaryToValue(queryText);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

        }

        /**
         * Update preference summary and string value on datepicker select
         *
         * @param datePicker - DatePicker object
         * @param year       - Year, yyyy format
         * @param month      - month, m format
         * @param day        - day, d format
         */
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Preference startDate = findPreference(getString(R.string.settings_date_key));
            // Format date parts into string, yyyy-mm-dd format
            String dateString = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", day);
            startDate.setSummary(dateString);
            updatePreferenceString(getString(R.string.settings_date_key), dateString);
        }

        /**
         * Show date picker dialog
         */
        private void showDateDialog() {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            new DatePickerDialog(getActivity(), this, year, month, day).show();
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            // The code in this method takes care of updating the displayed preference summary after it has been changed
            String stringValue = newValue.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                if (stringValue.trim().equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.empty_query),
                            Toast.LENGTH_LONG).show();
                    return false;
                }
                preference.setSummary(stringValue);
            }
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }

        /**
         * Update preference value with string
         *
         * @param preferenceKey - preference key
         * @param string        - new value
         */
        private void updatePreferenceString(String preferenceKey, String string) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(preferenceKey, string);
            editor.apply();
        }

    }
}