package com.kastack.vidyanet.plugins

class UnauthorizedException(message: String = "Unauthorized") : Exception(message)
class NotFoundException(message: String = "Not Found") : Exception(message)
class BadRequestException(message: String = "Bad Request") : Exception(message)
