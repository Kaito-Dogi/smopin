package app.kaito_dogi.smopin

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform