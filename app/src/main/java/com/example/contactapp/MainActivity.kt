package com.example.contactapp

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.content.DialogInterface
import android.app.PendingIntent.getActivity
import android.graphics.Color.convert
import android.support.v7.app.AlertDialog
import android.telephony.PhoneNumberUtils
import kotlinx.android.synthetic.main.activity_main.*
import android.R.string
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.net.Uri.withAppendedPath
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import android.text.method.TextKeyListener.clear
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    lateinit var contact_list: ListView
    var uniqueMobilePhones = ArrayList<String>()
    var uniqueNames = ArrayList<String>()
    var array_sort =ArrayList<String>()
    var contact_id = ArrayList<String>()
    lateinit var adapter: ArrayAdapter<String>
    lateinit var seachbox :EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getPermission()
        contact_list = listview
        seachbox = searchview
        adapter = CustomAdapter(this, R.layout.layout_listview, uniqueNames)
        contact_list.adapter = adapter


        contact_list.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val seltext = parent.getItemAtPosition(position).toString().trim()
            val intent = Intent(this, contact_detail::class.java)
            intent.putExtra("Phone", seltext)
            startActivity(intent)
        }

        seachbox.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    val textlength = seachbox.text.toString().length
                    array_sort.clear()
                if(textlength == 0)
                {
                    adapter = CustomAdapter(this@MainActivity,R.layout.layout_listview, uniqueNames)
                    contact_list.setAdapter(adapter)
                }
                for (i in 0..uniqueNames.size-1)
                {
                    if(textlength<uniqueNames.get(i).length)
                    {
                        if (uniqueNames.get(i).toLowerCase().trim().contains(seachbox.getText().toString().toLowerCase().trim()))
                        {
                            array_sort.add(uniqueNames.get(i))
                        }
                    }
                }
                adapter = CustomAdapter(this@MainActivity,R.layout.layout_listview, array_sort)
                contact_list.setAdapter(adapter)
            }

        })
    }

    fun getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), 100)
        } else {
            getContacts()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContacts()
            }
        } else {
            Toast.makeText(this, "Please provide permission for contacts!!", Toast.LENGTH_LONG).show()
        }
    }

    fun getContacts() {
        val PROJECTION = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        val contentResolver: ContentResolver = getContentResolver()
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            PROJECTION,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                if (!uniqueMobilePhones.contains(phone) && !uniqueNames.contains(name)) {
                    contact_id.add(id)
                    uniqueMobilePhones.add(phone)
                    uniqueNames.add(name)
                    array_sort.add(name)
                }
            }
        }
    }

    inner class CustomAdapter(context: Context, resource: Int, objects: MutableList<String>) :
        ArrayAdapter<String>(context, resource, objects) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater: LayoutInflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.layout_listview, parent, false)
            val nametext = view.findViewById<TextView>(R.id.name)
            nametext.setText(uniqueNames[position])
            return view
        }
    }
}
