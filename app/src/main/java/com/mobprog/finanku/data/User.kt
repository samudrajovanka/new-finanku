package com.mobprog.finanku.data

data class User(
    val id: Int = 0,
    val name: String? = null,
    val email: String? = null,
    val username: String? = null,
) {
    fun getFirstName(): String? {
        return name?.split(" ")?.get(0)
    }

    fun getPrivateEmail(): String {
        val indexAt = this.email?.indexOf('@')
        val firstChar = this.email?.get(0)
        val lastIndex = this.email?.lastIndex
        val suffix =
            lastIndex?.let { index ->
                (indexAt?.minus(1))?.rangeTo(index)?.let { this.email?.slice(it) }
            }

        return "${firstChar}****${suffix}"
    }
}
