# Bulk Texter — Android App

Send individual SMS messages to all (or selected) contacts at once.

## Features
- Loads all phone contacts with phone numbers
- Check/uncheck individual contacts or use "Select All"
- Shows count of selected contacts
- Confirmation dialog before sending
- Success/fail count reported after sending
- Long messages are automatically split into parts (multipart SMS)

## Project Structure

```
app/src/main/
├── java/com/example/bulksms/
│   ├── MainActivity.kt       ← Main screen logic
│   ├── ContactsAdapter.kt    ← RecyclerView list adapter
│   └── ContactsHelper.kt     ← Reads contacts from phone
├── res/
│   ├── layout/
│   │   ├── activity_main.xml ← Main screen layout
│   │   └── item_contact.xml  ← Per-contact row layout
│   └── drawable/
│       └── edittext_border.xml
└── AndroidManifest.xml       ← Permissions declared here
```

## Setup in Android Studio

1. Open Android Studio → **File > New > Import Project**
2. Select the `BulkSMS` folder
3. Let Gradle sync complete
4. Connect a physical Android device (SMS won't work on emulator)
5. Run the app — it will ask for **Contacts** and **Send SMS** permissions

## Required Permissions

Declared in `AndroidManifest.xml` and requested at runtime:
- `READ_CONTACTS` — to load your contact list
- `SEND_SMS` — to send text messages

## Important Notes

- **Test on a real device** — SMS cannot be sent from the Android emulator
- Carriers may flag bulk SMS as spam if you send to hundreds of numbers rapidly
- Long messages over 160 characters are automatically split into multiple SMS parts
- Each contact receives an individual message (not a group MMS)
- The app deduplicates contacts so each person only appears once even if they have multiple numbers
