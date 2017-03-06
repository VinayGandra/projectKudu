# projectKudu

<b> SBT Installation </b>

1. cmd -> home -> .sbt -> 0.13 -> mkdir plugins
2. <b> In plugins directory, </b> create plugins.sbt and add line :
`addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.12.0")`

<b>Note:</b> To remove META-INF folders, use: 
`zip -d <jar name> "META-INF/.RSA" "META-INF/.SF" "META-INF/*SF"`
