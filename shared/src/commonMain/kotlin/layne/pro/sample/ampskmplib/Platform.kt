package layne.pro.sample.ampskmplib

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform