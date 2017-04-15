package de.reiss.thepokerguys.settings

import android.os.Bundle
import android.preference.PreferenceFragment
import de.reiss.thepokerguys.R

class SettingsFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)
    }

}