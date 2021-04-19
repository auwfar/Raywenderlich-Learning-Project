package com.raywenderlich.android.memories.model.response

import kotlinx.serialization.Serializable

@Serializable
class UploadResponse(val message: String = "", val url: String = "")