package com.example.travelon.ui.about

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast

import com.example.travelon.R
import com.example.travelon.ui.login.LoginActivity

class AboutFragment : Fragment() {

    companion object {
        fun newInstance() = AboutFragment()
    }

    private lateinit var viewModel: AboutViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.about_fragment, container, false)
        val signOutButton = root.findViewById<Button>(R.id.signOutButton)

        signOutButton .setOnClickListener {
            displaySignOutAlert()
        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AboutViewModel::class.java)
    }

    private fun displaySignOutAlert() {
        val alertDialog = AlertDialog.Builder(
            activity
        )

        // Setting Dialog Title
        alertDialog.setTitle("Confirm Sign out")

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want to Sign out?")

        alertDialog.setPositiveButton("YES") { dialog, which ->
            // Write your code here to execute after dialog
            viewModel.signOut()
            val i = Intent(
                activity,
                LoginActivity::class.java
            )
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }

        alertDialog.setNegativeButton("NO") { dialog, _ ->
            dialog.cancel()
        }

        alertDialog.show()
    }

}
