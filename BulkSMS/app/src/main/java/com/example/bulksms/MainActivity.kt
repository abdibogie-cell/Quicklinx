package com.example.bulksms

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var contactsAdapter: ContactsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var selectAllCheckBox: CheckBox
    private lateinit var selectedCountText: TextView
    private lateinit var statusText: TextView

    private val PERMISSIONS_REQUEST_CODE = 100
    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.SEND_SMS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)
        selectAllCheckBox = findViewById(R.id.selectAllCheckBox)
        selectedCountText = findViewById(R.id.selectedCountText)
        statusText = findViewById(R.id.statusText)
        recyclerView = findViewById(R.id.contactsRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        contactsAdapter = ContactsAdapter { updateSelectedCount() }
        recyclerView.adapter = contactsAdapter

        sendButton.setOnClickListener { confirmAndSend() }

        selectAllCheckBox.setOnCheckedChangeListener { _, isChecked ->
            contactsAdapter.selectAll(isChecked)
            updateSelectedCount()
        }

        checkPermissionsAndLoad()
    }

    private fun checkPermissionsAndLoad() {
        val missing = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isEmpty()) {
            loadContacts()
        } else {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            loadContacts()
        } else {
            statusText.text = "⚠️ Contacts and SMS permissions are required."
        }
    }

    private fun loadContacts() {
        val contacts = ContactsHelper.getContacts(this)
        if (contacts.isEmpty()) {
            statusText.text = "No contacts with phone numbers found."
        } else {
            statusText.text = "${contacts.size} contacts loaded"
            contactsAdapter.submitList(contacts)
        }
    }

    private fun updateSelectedCount() {
        val count = contactsAdapter.getSelectedContacts().size
        selectedCountText.text = "$count selected"
        sendButton.isEnabled = count > 0
    }

    private fun confirmAndSend() {
        val message = messageEditText.text.toString().trim()
        val selected = contactsAdapter.getSelectedContacts()

        if (message.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            return
        }
        if (selected.isEmpty()) {
            Toast.makeText(this, "Please select at least one contact", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Send Messages")
            .setMessage("Send \"$message\"\n\nTo ${selected.size} contact(s)?")
            .setPositiveButton("Send") { _, _ -> sendMessages(selected, message) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun sendMessages(contacts: List<Contact>, message: String) {
        val smsManager = SmsManager.getDefault()
        var successCount = 0
        var failCount = 0

        for (contact in contacts) {
            try {
                val parts = smsManager.divideMessage(message)
                smsManager.sendMultipartTextMessage(contact.phoneNumber, null, parts, null, null)
                successCount++
            } catch (e: Exception) {
                failCount++
            }
        }

        val result = buildString {
            if (successCount > 0) append("✅ Sent to $successCount contact(s)")
            if (failCount > 0) append("\n❌ Failed for $failCount contact(s)")
        }
        statusText.text = result
        Toast.makeText(this, result, Toast.LENGTH_LONG).show()
    }
}
