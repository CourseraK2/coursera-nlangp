lazy val assignment1 = project.in(file("assignment1"))

lazy val assignment2 = project.in(file("assignment2")).dependsOn(assignment1)
