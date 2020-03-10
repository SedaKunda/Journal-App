package com.zuhlke.upskilling.journal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.zuhlke.upskilling.journal.journalEntry.list.JournalEntriesFragmentDirections

class MainActivity : AppCompatActivity() {

    lateinit var config: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navController = findNavController(R.id.nav_host_fragment)

        config = AppBarConfiguration
            .Builder()
            .setFallbackOnNavigateUpListener {
                navController.navigate(JournalEntriesFragmentDirections.actionJournalEntriesFragmentToFoldersListFragment())
                true
            }
            .build()

        NavigationUI.setupActionBarWithNavController(this, navController, config)
//        setupActionBarWithNavController(navController) //normal action bar
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, config)
    }
}
