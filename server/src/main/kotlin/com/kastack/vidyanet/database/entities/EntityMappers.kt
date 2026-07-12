package com.kastack.vidyanet.database.entities

import com.kastack.vidyanet.models.user.UserDto
import com.kastack.vidyanet.models.role.*
import kotlinx.datetime.Instant

fun UserEntity.toDto() = UserDto(
    id = id.value,
    phone = phone,
    userType = userType,
    schoolId = schoolId?.value,
    status = status,
    isPhoneVerified = isPhoneVerified,
    roles = roles.map { it.roleName },
    createdAt = createdAt,
    updatedAt = updatedAt,
    lastLoginAt = lastLoginAt,
    deletedAt = deletedAt
)

fun RoleEntity.toDto() = RoleDto(
    id = id.value,
    roleCode = roleCode,
    roleName = roleName,
    description = description,
    isSystemRole = isSystemRole,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun PermissionEntity.toDto() = PermissionAction.valueOf(action)

fun RoleEntity.toPermissionsDto(): RolePermissionsDto {
    val groupedPermissions = permissions.groupBy { it.moduleName }
    val modulePermissions = groupedPermissions.map { (moduleName, permissions) ->
        ModulePermissionDto(
            moduleName = moduleName,
            actions = permissions.map { PermissionAction.valueOf(it.action) }.toSet()
        )
    }
    return RolePermissionsDto(
        roleId = id.value,
        permissions = modulePermissions
    )
}

fun UserRoleAssignmentEntity.toDto() = UserRoleDto(
    id = id.value,
    userId = userId.value,
    roleId = roleId.value,
    roleCode = role.roleCode,
    roleName = role.roleName,
    assignedBy = assignedBy?.value,
    assignedAt = assignedAt
)

fun SchoolEntity.toDto() = com.kastack.vidyanet.models.schoolUser.SchoolDto(
    id = id.value,
    schoolCode = schoolCode,
    schoolName = schoolName,
    schoolType = schoolType,
    phone = phone,
    email = email,
    website = website,
    address = address,
    city = city,
    state = state,
    country = country,
    postalCode = postalCode,
    logoUrl = logoUrl,
    status = status,
    studentCount = studentCount,
    teacherCount = teacherCount
)

//fun MerchantEntity.toDto() = MerchantDto(
//    merchantId = id.value,
//    userId = userId.value,
//    categoryId = categoryId?.value,
//    businessName = businessName,
//    ownerName = ownerName,
//    gstNumber = gstNumber,
//    panNumber = panNumber,
//    storeLogo = storeLogo,
//    storeBanner = storeBanner,
//    address = address,
//    latitude = latitude.toDouble(),
//    longitude = longitude.toDouble(),
//    bankAccountNumber = bankAccountNumber,
//    ifscCode = ifscCode,
//    commissionRate = commissionRate.toDouble(),
//    status = status,
//    isOpen = isOpen
//)
//
//fun PaymentEntity.toDto(customerName: String? = null) = PaymentDto(
//    id = id.value,
//    amount = amount,
//    currency = currency,
//    status = status,
//    method = method,
//    transactionId = transactionId,
//    customerId = customerId.value,
//    customerName = customerName,
//    createdAt = createdAt.toKotlinTime(),
//    updatedAt = updatedAt.toKotlinTime()
//)
//
//fun ProductEntity.toDto() = ProductDto(
//    id = id.value,
//    merchantId = merchantId.value,
//    categoryId = categoryId.value,
//    name = name,
//    brand = brand,
//    description = description,
//    sku = sku,
//    barcode = barcode,
//    mrp = mrp.toDouble(),
//    sellingPrice = sellingPrice.toDouble(),
//    costPrice = costPrice?.toDouble(),
//    stock = stock,
//    unit = unit,
//    unitValue = unitValue.toDouble(),
//    image = image,
//    thumbnail = thumbnail,
//    isVeg = isVeg,
//    taxPercentage = taxPercentage.toDouble(),
//    gst = gst.toDouble(),
//    tags = tags.split(",").filter { it.isNotBlank() },
//    subCategory = subCategory,
//    status = status,
//    isMerchantOpen = merchant.isOpen,
//    images = images.map { it.toDto() },
//    createdAt = createdAt.toKotlinTime(),
//    updatedAt = updatedAt.toKotlinTime()
//)
//
//fun ProductImageEntity.toDto() = ProductImageDto(
//    id = id.value,
//    productId = productId.value,
//    imageUrl = imageUrl,
//    displayOrder = displayOrder,
//    isPrimary = isPrimary
//)
//
//fun CartEntity.toDto(): CartDto {
//    val itemsList = items.toList()
//
//    var totalBaseAmount = BigDecimal.ZERO
//    var totalGstAmount = BigDecimal.ZERO
//
//    val cartItems = itemsList.map { item ->
//        val sellingPrice = item.product.sellingPrice
//        val gstRate = item.product.gst
//        val quantity = BigDecimal.valueOf(item.quantity.toLong())
//
//        val lineTotalPaid = sellingPrice.multiply(quantity)
//
//        val divisor = BigDecimal.ONE.add(gstRate.divide(BigDecimal("100"), 10, RoundingMode.HALF_UP))
//        val baseLineTotal = lineTotalPaid.divide(divisor, 10, RoundingMode.HALF_UP)
//        val lineGst = lineTotalPaid.subtract(baseLineTotal)
//
//        totalBaseAmount = totalBaseAmount.add(baseLineTotal)
//        totalGstAmount = totalGstAmount.add(lineGst)
//
//        CartItemDto(
//            id = item.id.value,
//            product = item.product.toDto(),
//            quantity = item.quantity,
//            lineTotal = lineTotalPaid.toDouble()
//        )
//    }
//
//    val totalItemsPaid = totalBaseAmount.add(totalGstAmount)
//    val deliveryCharge = if (totalItemsPaid.compareTo(BigDecimal.ZERO) > 0 && totalItemsPaid.compareTo(BigDecimal("500")) < 0)
//        BigDecimal("40.00") else BigDecimal.ZERO
//    val platformFee = if (totalItemsPaid.compareTo(BigDecimal.ZERO) > 0)
//        BigDecimal("5.00") else BigDecimal.ZERO
//
//    val finalTotal = totalItemsPaid.add(deliveryCharge).add(platformFee)
//
//    return CartDto(
//        id = id.value,
//        userId = userId.value,
//        items = cartItems,
//        subTotal = totalBaseAmount.setScale(2, RoundingMode.CEILING).toDouble(),
//        gstTotal = totalGstAmount.setScale(2, RoundingMode.CEILING).toDouble(),
//        deliveryCharge = deliveryCharge.toDouble(),
//        platformFee = platformFee.toDouble(),
//        totalAmount = finalTotal.setScale(2, RoundingMode.CEILING).toDouble()
//    )
//}
//
//fun CartItemEntity.toDto() = CartItemDto(
//    id = id.value,
//    product = product.toDto(),
//    quantity = quantity,
//    lineTotal = product.sellingPrice.toDouble() * quantity
//)
//
//fun OrderEntity.toDto(backupName: String? = null, backupPhone: String? = null) = OrderDto(
//    id = id.value,
//    orderNumber = orderNumber,
//    userId = userId.value,
//    merchantId = merchantId.value,
//    riderId = riderId?.value,
//    items = items.map { it.toDto() },
//    subTotal = subTotal.toDouble(),
//    gstTotal = gstTotal.toDouble(),
//    deliveryCharge = deliveryCharge.toDouble(),
//    platformFee = platformFee.toDouble(),
//    totalAmount = totalAmount.toDouble(),
//    status = status,
//    deliveryAddress = deliveryAddress,
//    customerPhone = customerPhone ?: backupPhone ?: "",
//    customerName = customerName ?: backupName,
//    paymentId = paymentId?.value,
//    gatewayOrderId = gatewayOrderId,
//    paymentMethod = paymentMethod,
//    createdAt = createdAt.toKotlinTime(),
//    updatedAt = updatedAt.toKotlinTime()
//)
//
//fun OrderItemEntity.toDto() = OrderItemDto(
//    id = id.value,
//    productId = productId.value,
//    productName = productName,
//    productThumbnail = productThumbnail,
//    quantity = quantity,
//    unitPrice = unitPrice.toDouble(),
//    lineTotal = lineTotal.toDouble()
//)
//
//fun CustomerProfileEntity.toDto() = CustomerProfileDto(
//    customerId = id.value,
//    userId = userId.value,
//    fullName = fullName,
//    gender = gender,
//    dob = dob,
//    walletBalance = walletBalance.toDouble(),
//    referralCode = referralCode,
//    referredBy = referredBy,
//    createdAt = createdAt.toKotlinTime()
//)
//
//fun BannerEntity.toDto() = Banner(
//    id = id.value,
//    imageUrl = imageUrl,
//    title = title,
//    description = description,
//    type = type,
//    targetId = targetId,
//    isActive = isActive,
//    priority = priority,
//    createdAt = createdAt.toKotlinTime().toString(),
//    updatedAt = updatedAt.toKotlinTime().toString()
//)
