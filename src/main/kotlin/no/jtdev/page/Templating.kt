package no.jtdev.page

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.css.*
import kotlinx.css.properties.Timing
import kotlinx.css.properties.ms
import kotlinx.css.properties.transition
import kotlinx.html.*
import kotlin.random.Random


fun Application.configureTemplating() {
    routing {
        get("") {
            call.respondHtml {
                head {
                    link {
                        rel = "stylesheet"
                        href = "/styles.css"
                        type = "text/css"
                    }
                    script { src = "https://unpkg.com/htmx.org@1.9.5" }
                    script { src = "https://unpkg.com/hyperscript.org@0.9.11" }
                }
                body {
                    h1("page-title") {
                        +"HTMX Demo"
                    }
                    button {
                        id = "myButton"
                        attributes["hx-get"] = "/data"
                        attributes["hx-swap"] = "outerHTML"
                        attributes["hx-target"] = "#data"
                        +"Fetch data"
                    }
                    div("flex-column justify-center pl-8 pt-1 pb-2 pr-0") {
                        div {
                            +"This is a static element"
                        }
                        div {
                            id = "data"
                            +"Our data will be swapped with this element"
                        }
                    }
                    div("w-40 h-4 p-2 position-absolute right-4 top-4 hidden") {
                        attributes["_"] = """
                            on htmx:afterRequest from #myButton 
                            if (event.detail.xhr.status == 422) add .bg-critical-500 
                                then put event.detail.xhr.responseText into my.textContent
                                then transition opacity to 1 over 200ms 
                                then wait 1500ms
                                then transition opacity to 0 over 200ms
                                then remove .bg-critical-500
                            else if (event.detail.xhr.status >= 400) add .bg-critical-500 
                                then put 'Failed to fetch data' into my.textContent
                                then transition opacity to 1 over 200ms 
                                then wait 1500ms
                                then transition opacity to 0 over 200ms
                                then remove .bg-critical-500
                            else add .bg-success-500 
                                then put 'Data fetch successful' into my.textContent
                                then transition opacity to 1 over 200ms 
                                then wait 1500ms
                                then transition opacity to 0 over 200ms 
                                then remove .bg-success-500
                        """.trimIndent()
                        +"I'm a notification"
                    }
                }
            }
        }
        get("/data") {
            val random = Random.nextInt()
            if (random % 3 == 0) {
                throw Error("Internal server error")
            }
            if (random % 3 == 1) {
                return@get call.respond(HttpStatusCode.UnprocessableEntity, "Could not fetch our data")
            }
            call.respondHtml {
                body {
                    div {
                        id = "data"
                        +"Bitch please!"
                    }
                }
            }
        }

        get("/error") {
            throw Error("Blah")
        }

        get("/styles.css") {
            val step = 0.5
            call.respondCss {
                body {
                    backgroundColor = Color("#D5D5D5")
                    margin(0.px)
                }
                // Colors
                rule(".critical-500") { color = Color("#EA5768") }

                // Background colors
                rule(".bg-success-500") { backgroundColor = Color("#4BB543") }
                rule(".bg-critical-500") { backgroundColor = Color("#FC100D") }

                // Visibility
                rule(".hidden") { opacity = 0 }

                // Layout
                rule(".flex") { display = Display.flex }
                rule(".flex-column") { display = Display.flex; flexDirection = FlexDirection.column }
                rule(".justify-center") { justifyContent = JustifyContent.center }
                rule(".justify-space-between") { justifyContent = JustifyContent.spaceBetween }
                rule(".justify-space-evenly") { justifyContent = JustifyContent.spaceEvenly }
                rule(".position-absolute") { position = Position.absolute }
                rule(".position-relative") { position = Position.relative }

                // Top
                (0..50).map { rule(".top-$it") { top = (step * it).rem } }

                // Left
                (0..50).map { rule(".left-$it") { left = (step * it).rem } }

                // Right
                (0..50).map { rule(".right-$it") { right = (step * it).rem } }

                // Margins
                (0..8).map { rule(".m-$it") { margin = "${step * it}rem" } }
                (0..8).map { rule(".mr-$it") { marginRight = (step * it).rem } }
                (0..8).map { rule(".ml-$it") { marginLeft = (step * it).rem } }
                (0..8).map { rule(".mt-$it") { marginTop = (step * it).rem } }
                (0..8).map { rule(".mb-$it") { marginBottom = (step * it).rem } }

                // Paddings
                (0..8).map { rule(".p-$it") { padding = "${step * it}rem" } }
                (0..8).map { rule(".pr-$it") { paddingRight = (step * it).rem } }
                (0..8).map { rule(".pl-$it") { paddingLeft = (step * it).rem } }
                (0..8).map { rule(".pt-$it") { paddingTop = (step * it).rem } }
                (0..8).map { rule(".pb-$it") { paddingBottom = (step * it).rem } }

                // Widths
                (0..50).map { rule(".w-$it") { width = (step * it).rem } }

                // Heights
                (0..50).map { rule(".h-$it") { height = (step * it).rem } }

                // Transitions
                rule(".ease-in") { transition("all", duration = 500.ms, timing = Timing.easeIn) }
                rule(".ease-out") { transition("all", duration = 500.ms, timing = Timing.easeOut) }
            }
        }
    }
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}

