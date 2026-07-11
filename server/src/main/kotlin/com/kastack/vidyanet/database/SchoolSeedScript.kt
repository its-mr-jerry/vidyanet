package com.kastack.vidyanet.database

import com.kastack.vidyanet.database.entities.SchoolEntity
import com.kastack.vidyanet.database.toKotlinx
import com.kastack.vidyanet.models.schoolUser.SchoolStatus
import com.kastack.vidyanet.models.schoolUser.SchoolType
import com.kastack.vidyanet.constants.IndiaConstants
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.random.Random
import kotlin.time.Clock

object SchoolSeedScript {
    fun seedSchools(count: Int = 100) = transaction {
        val existingCount = SchoolEntity.count()
        if (existingCount >= count.toLong()) {
            println("Schools already seeded ($existingCount). Skipping...")
            return@transaction
        }

        val schoolNames = listOf(
            "St. Xavier's", "Delhi Public School", "Heritage School", "Mount Carmel",
            "Green Valley", "Holy Cross", "Little Flower", "Ryan International",
            "The Doon School", "Mayo College", "Amity International", "G.D. Goenka",
            "Loreto House", "Don Bosco", "Bishop Cotton", "La Martiniere"
        )

        val cities = listOf("Mumbai", "Delhi", "Bangalore", "Hyderabad", "Ahmedabad", "Chennai", "Kolkata", "Surat", "Pune", "Jaipur", "Ranchi", "Patna")

        for (i in 1..count) {
            val name = "${schoolNames.random()} ${if (Random.nextBoolean()) "High School" else "International School"} #$i"
            val city = cities.random()
            val state = IndiaConstants.states.random()
            
            SchoolEntity.new {
                schoolCode = "vid${(existingCount + i).toString().padStart(3, '0')}"
                schoolName = name
                schoolType = SchoolType.entries.random()
                phone = "987654${Random.nextInt(1000, 9999)}"
                email = "contact@${name.lowercase().replace(" ", "").replace("#", "")}.edu"
                website = "www.${name.lowercase().replace(" ", "").replace("#", "")}.edu"
                address = "${Random.nextInt(1, 999)}, MG Road, $city"
                this.city = city
                this.state = state
                country = "India"
                postalCode = "${Random.nextInt(100000, 999999)}"
                logoUrl = null
                status = SchoolStatus.entries.random()
                studentCount = Random.nextInt(100, 5000)
                teacherCount = Random.nextInt(10, 200)
                createdAt = Clock.System.now().toKotlinx()
                updatedAt = Clock.System.now().toKotlinx()
            }
        }
        println("Successfully seeded $count schools!")
    }
}
