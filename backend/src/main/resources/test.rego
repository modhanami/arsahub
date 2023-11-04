# Rego policy for ArsaHub (Kotlin Spring Boot + ReactJS) application
# Domain: events
package arsahub.authz.events
# import or

# Deny by default
default allow = false

is_organizer {
    input.user.role = "organizer"
}

is_admin {
    input.user.role = "admin"
}

is_organizer_or_admin {
    is_organizer
}

is_organizer_or_admin {
    is_admin
}

allow {
    is_organizer_or_admin
}


