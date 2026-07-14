package com.example.bulksms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactsAdapter(
    private val onSelectionChanged: () -> Unit
) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    private val contacts = mutableListOf<Contact>()

    fun submitList(list: List<Contact>) {
        contacts.clear()
        contacts.addAll(list)
        notifyDataSetChanged()
    }

    fun selectAll(selected: Boolean) {
        contacts.forEach { it.isSelected = selected }
        notifyDataSetChanged()
    }

    fun getSelectedContacts(): List<Contact> = contacts.filter { it.isSelected }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contacts[position])
    }

    override fun getItemCount() = contacts.size

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.contactCheckBox)
        private val nameText: TextView = itemView.findViewById(R.id.contactName)
        private val phoneText: TextView = itemView.findViewById(R.id.contactPhone)

        fun bind(contact: Contact) {
            nameText.text = contact.name
            phoneText.text = contact.phoneNumber
            checkBox.isChecked = contact.isSelected

            val toggle = {
                contact.isSelected = !contact.isSelected
                checkBox.isChecked = contact.isSelected
                onSelectionChanged()
            }

            checkBox.setOnClickListener { toggle() }
            itemView.setOnClickListener { toggle() }
        }
    }
}
