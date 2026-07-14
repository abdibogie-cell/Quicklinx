package com.example.bulksms

import android.content.Context
import android.provider.ContactsContract

data class Contact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    var isSelected: Boolean = false
)

object ContactsHelper {
    fun getContacts(context: Context): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val seen = mutableSetOf<String>()

        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null, null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            val idIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val id = it.getString(idIdx) ?: continue
                val name = it.getString(nameIdx) ?: continue
                val number = it.getString(numberIdx)?.replace("\\s|-".toRegex(), "") ?: continue

                // Deduplicate by contact ID (use first number per contact)
                if (id !in seen) {
                    seen.add(id)
                    contacts.add(Contact(id, name, number))
                }
            }
        }

        return contacts
    }
}
