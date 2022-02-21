rootProject.name = "GsonBooster-Plugin"
include("plugin-main")

include(":base:check")
project(":base:check").projectDir = File("../../AndroidStudioProjects/GsonBooster/base/check")

include(":base:gen")
project(":base:gen").projectDir = File("../../AndroidStudioProjects/GsonBooster/base/gen")

include(":base:processor-base")
project(":base:processor-base").projectDir = File("../../AndroidStudioProjects/GsonBooster/base/processor-base")
