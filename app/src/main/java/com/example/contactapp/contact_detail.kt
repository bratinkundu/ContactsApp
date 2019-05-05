package com.example.contactapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.R.attr.phoneNumber
import android.graphics.BitmapFactory
import android.net.Uri
import android.net.Uri.withAppendedPath
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_contact_detail.*
import java.io.ByteArrayInputStream
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import java.security.AccessController.getContext
import android.content.ContentUris
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.R.id








class contact_detail : AppCompatActivity() {

    lateinit var phone:String
    lateinit var nametext:TextView
    lateinit var emailtext:TextView
    lateinit var imageViews: ImageView
    lateinit var phonetext:TextView
    lateinit var addressline:TextView
    lateinit var workline:TextView
    var phonearray = arrayListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail)

        phone = intent.getStringExtra("Phone")
        phonetext = textView
        emailtext = textView1
        imageViews = imageView
        nametext =textView2
        addressline = tvaddress
        workline = tvwork

        getdetails()
    }

    fun getdetails()
    {
        var photo = BitmapFactory.decodeResource(this.resources,R.drawable.profile_shape)
        var contactName = ""
        var phoneno=""
        var email=""
        var id=""
        var phonestr=""
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection =
            arrayOf(ContactsContract.CommonDataKinds.Phone.CONTACT_ID,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER)
        val cursor = contentResolver.query(uri, projection, null, null, null)


        if (cursor != null) {

                while (cursor.moveToNext())
                {
                    contactName = cursor!!.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    phoneno = cursor!!.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    id = cursor!!.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                    if (contactName.equals(phone))
                    {
                        val dtext = nametext.text.toString() + " " + contactName
                        nametext.setText(dtext)
                        break
                    }
                }
            }
            cursor!!.close()



        val cursor2 = this.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
            arrayOf(id), null)
        while (cursor2.moveToNext())
        {
            email = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
        }
        cursor2.close()



            val cursor3 = this.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"=?",
            arrayOf(id),null)
        while (cursor3.moveToNext())
        {
            var phone2 = cursor3.getString(cursor3.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            phone2 = phone2.replace(" ","")
            if (!phonearray.contains(phone2))
            {
                phonearray.add(phone2)
            }
        }
        cursor3.close()
        phonearray.forEach { item->
            phonestr+="\n" +item
        }
        val pext = phonetext.text.toString()+" "+phonestr
        phonetext.setText(pext)
        val etext = emailtext.text.toString() +" "+email
        emailtext.setText(etext)


        
        val cursor4 = contentResolver.query(ContactsContract.Data.CONTENT_URI,
            null, ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?", arrayOf(id, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE), null);
        var street =""
        var city =""
        var state=""
        var postalCode=""
        var country=""
        while(cursor4!!.moveToNext()) {
            street = cursor4.getString(cursor4.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
            city = cursor4.getString(cursor4.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
            state = cursor4.getString(cursor4.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
            postalCode = cursor4.getString(cursor4.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
            country = cursor4.getString(cursor4.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
        }
        if(street!="" || city!="" || state!="" || postalCode!="" || country!="")
        {
            val address = street+","+city+","+state+","+country+","+postalCode
            addressline.setText(addressline.text.toString()+" "+address)
        }
        cursor4.close()



        var orgName=""
        var title=""
        val cursor5 = contentResolver.query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?", arrayOf(id, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE), null)
        if (cursor5.moveToFirst())
        {
            orgName = cursor5.getString(cursor5.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA))
            title = cursor5.getString(cursor5.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE))
        }
        if(orgName!="" || title!="")
        {
            val work = title+","+orgName
            workline.setText(workline.text.toString()+" "+work)
        }
        cursor5.close()

        val inputStream = ContactsContract.Contacts.openContactPhotoInputStream(
            this.getContentResolver(),
            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,id.toLong()))
        if (inputStream != null) {
            photo = BitmapFactory.decodeStream(inputStream);
        }
        imageViews.setImageBitmap(photo)


    }
}
