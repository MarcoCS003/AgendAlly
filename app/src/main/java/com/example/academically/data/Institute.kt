package com.example.academically.data

import androidx.compose.ui.graphics.vector.ImageVector

data class Institute(
    val instituteID: Int,
    val name: String,
    val address: String,
    val email: String,
    val phone: String,
    val studentNumber: Int,
    val teacherNumber: Int,
    var logo: ImageVector? = null,
    var webSite: String? = null,
    var facebook: String? = null,
    var instagram: String? = null,
    var twitter: String? = null,
    var youtube: String? = null
)


