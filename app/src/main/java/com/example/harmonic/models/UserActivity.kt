package com.example.harmonic.models

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val email: String, val profile_img: String): Parcelable {
    constructor() : this("", "", "")
}