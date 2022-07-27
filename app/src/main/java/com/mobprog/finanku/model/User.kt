package com.mobprog.finanku.model

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val limits: ArrayList<Limit>,
) {
    fun getFullName(): String {
        return String.format("%s %s", firstName, lastName)
    }

    fun getTotalLimit(): Int {
        var total = 0
        for (limit: Limit in limits) {
            total += limit.total
        }

        return total
    }

    fun getLimitByType(type: CategoryExpenses): Limit {
        return limits.filter { limit -> limit.type == type }[0]
    }

    fun getPrivateEmail(): String {
        val indexAt = this.email.indexOf('@')
        val firstChar = this.email[0]
        val lastIndex = this.email.lastIndex
        val suffix = this.email.slice((indexAt - 1)..lastIndex)

        return "${firstChar}****${suffix}"
    }
}