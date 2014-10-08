RichFaces3 IE9
=============

A fork of [richfaces.3.3.4-Final](http://anonsvn.jboss.org/repos/richfaces/branches/community/release-3.3.4/) with patches intended to fix IE9 (and any other browser) compatibility issues.

Since the RichFaces team does not support the version 3 anymore, this project aims to support applications with richfaces 3 running on legacy browsers by applying fixes pointed out by the community. Only the "core" projects (`richfaces-impl` and `richfaces-api`) are planned to be changed, after all, it is where the client scripts supposed to be located.

Don't worry, the namespaces and class hierarchy did not change, so you could simply move on from the previous versions.

Any contributions will be appreciated.

Usage
=====

[Download the `jars`](https://github.com/ricardozanini/richfaces-ie9/releases) and replace the old `richfaces-api-3.3.x.jar` and `richfaces-impl-3.3.x.jar` in your `classpath` by  `richfaces-ie9-api-X.X.X.Final.jar` and `richfaces-ie9-impl-X.X.X.Final.jar`. 

Credits
=======

* [The RichFaces Team](http://richfaces.jboss.org/community).
* [Marko Wallin](http://ruleoftech.com) for pointed out a way to fix the `SCRIPT16386` Bug.

Links
=====

* [RichFaces Docs](http://docs.jboss.org/richfaces/latest_3_3_X/en/devguide/html/)
* [How To Configure Maven For RichFaces](https://developer.jboss.org/wiki/HowToConfigureMavenForRichFaces)
* [How to add RichFaces 3.3.x to maven based project](https://developer.jboss.org/wiki/HowToAddRichFaces33xToMavenBasedProject)
* [RichFaces 3.3.X SVN Structure Overview](https://developer.jboss.org/wiki/RichFaces33XSVNStructureOverview)