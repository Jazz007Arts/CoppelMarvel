package com.jazz.coppelmarvel

import android.accounts.AuthenticatorDescription
import java.util.*

data class Character(var id:Int, var name:String, var description:String, var modified:Date, var resourceURI:String, var thumbnail:Image)