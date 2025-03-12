package com.liangguo.tools.lsposed.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import com.liangguo.tools.lsposed.R
import com.liangguo.tools.lsposed.utils.prefs.PrefsUtils

class XpSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return super.dispatchKeyEvent(event)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(bundle: Bundle?, rootKey: String?) {
            preferenceManager.sharedPreferencesName = PrefsUtils.PREFS_NAME
            setPreferencesFromResource(R.xml.prefs_root, rootKey)
            initPreferences()
        }

        @SuppressLint("StringFormatMatches")
        private fun initPreferences() {
            findPreference<SeekBarPreference>("prefs_key_status_bar_show_cpu_threshold")?.apply {
                val format: (Any) -> Unit = {
                    val text = getString(R.string.status_bar_show_cpu_threshold_summary)
                    summary = String.format(text, it)
                }
                format(value)
                setOnPreferenceChangeListener { _, newValue ->
                    format(newValue)
                    true
                }
            }
            findPreference<SeekBarPreference>("prefs_key_status_bar_show_cpu_animation_duration")?.apply {
                val format: (Any) -> Unit = {
                    val text = getString(R.string.status_bar_show_cpu_animation_duration_summary)
                    summary = String.format(text, it)
                }
                format(value)
                setOnPreferenceChangeListener { _, newValue ->
                    format(newValue)
                    true
                }
            }
        }
    }
}