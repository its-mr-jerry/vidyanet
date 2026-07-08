package com.kastack.vidyanet.validators

import com.kastack.vidyanet.models.system.UpdateSystemConfigRequest

object SystemValidator {
    fun validateUpdate(request: UpdateSystemConfigRequest) {
        request.supportPhone?.let {
            if (it.isBlank()) throw ValidationException("Support phone cannot be blank")
        }
        request.appVersion?.let {
            if (it.isBlank()) throw ValidationException("App version cannot be blank")
        }
    }
}
