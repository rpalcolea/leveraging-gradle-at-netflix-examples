# Dependency Locking Example

Let's start with a quick example on how to use dependency locking via jackson-bom usage

Make sure that jackson-bom `2.9.8` is in use and `2.12.1` is commented out:

```
api platform('com.fasterxml.jackson:jackson-bom:2.9.8')
//api platform('com.fasterxml.jackson:jackson-bom:2.12.1')
```


Execute `dependencies` task with `--write-locks` argument:

```
./gradlew dependencies --write-locks
```

This command should have created a set of `.lockfile` files inside `gradle/dependency-locks`, for example `compileClasspath.lockfile`:

```
# This is a Gradle generated file for dependency locking.
# Manual edits can break the build and are not advised.
# This file is expected to be part of source control.
com.fasterxml.jackson.core:jackson-annotations:2.9.0
com.fasterxml.jackson.core:jackson-core:2.9.8
com.fasterxml.jackson.core:jackson-databind:2.9.8
com.fasterxml.jackson.datatype:jackson-datatype-guava:2.9.8
com.fasterxml.jackson.datatype:jackson-datatype-hibernate5:2.9.8
com.fasterxml.jackson:jackson-bom:2.9.8
com.google.guava:guava:18.0
javax.transaction:jta:1.1
```

Run `dependencyInsight` task for `com.fasterxml.jackson.core:jackson-databind`:

`./gradlew dependencyInsight --dependency com.fasterxml.jackson.core:jackson-databind`

You should see the following:

```
> Task :dependencyInsight
com.fasterxml.jackson.core:jackson-databind:2.9.8
   variant "compile" [
      org.gradle.status              = release (not requested)
      org.gradle.usage               = java-api
      org.gradle.libraryelements     = jar (compatible with: classes)
      org.gradle.category            = library

      Requested attributes not found in the selected variant:
         org.gradle.dependency.bundling = external
         org.gradle.jvm.version         = 8
   ]
   Selection reasons:
      - By constraint : dependency was locked to version '2.9.8'
      - By ancestor

com.fasterxml.jackson.core:jackson-databind:{strictly 2.9.8} -> 2.9.8
\--- compileClasspath

com.fasterxml.jackson.core:jackson-databind:2.9.8
+--- com.fasterxml.jackson:jackson-bom:2.9.8
|    \--- compileClasspath
+--- com.fasterxml.jackson.datatype:jackson-datatype-guava:2.9.8
|    +--- compileClasspath (requested com.fasterxml.jackson.datatype:jackson-datatype-guava)
|    \--- com.fasterxml.jackson:jackson-bom:2.9.8 (*)
\--- com.fasterxml.jackson.datatype:jackson-datatype-hibernate5:2.9.8
     +--- compileClasspath (requested com.fasterxml.jackson.datatype:jackson-datatype-hibernate5)
     \--- com.fasterxml.jackson:jackson-bom:2.9.8 (*)

(*) - dependencies omitted (listed previously)

```

Notice `By constraint : dependency was locked to version '2.9.8'` and  `{strictly 2.9.8}`

Now, let's switch versions:

```
//api platform('com.fasterxml.jackson:jackson-bom:2.9.8')
api platform('com.fasterxml.jackson:jackson-bom:2.12.1')
```

Execute `build` task. 

This time it should fail with the following:

```
> Task :compileJava FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':compileJava'.
> Could not resolve all files for configuration ':compileClasspath'.
   > Could not resolve com.fasterxml.jackson:jackson-bom:2.12.1.
     Required by:
         project :
      > Cannot find a version of 'com.fasterxml.jackson:jackson-bom' that satisfies the version constraints:
           Dependency path ':dependency-locking-example:unspecified' --> 'com.fasterxml.jackson:jackson-bom:2.12.1'
           Constraint path ':dependency-locking-example:unspecified' --> 'com.fasterxml.jackson:jackson-bom:{strictly 2.9.8}' because of the following reason: dependency was locked to version '2.9.8'

   > Could not resolve com.fasterxml.jackson:jackson-bom:{strictly 2.9.8}.
     Required by:
         project :
      > Cannot find a version of 'com.fasterxml.jackson:jackson-bom' that satisfies the version constraints:
           Dependency path ':dependency-locking-example:unspecified' --> 'com.fasterxml.jackson:jackson-bom:2.12.1'
           Constraint path ':dependency-locking-example:unspecified' --> 'com.fasterxml.jackson:jackson-bom:{strictly 2.9.8}' because of the following reason: dependency was locked to version '2.9.8'

```

The reason for this is that we have a version in the lock file which defers from the one asked in the build file.

Let's generate the lock files again via `./gradlew dependencies --write-locks` and run dependency insight `./gradlew dependencyInsight --dependency com.fasterxml.jackson.core:jackson-databind`:

```
> Task :dependencyInsight
com.fasterxml.jackson.core:jackson-databind:2.12.1
   variant "apiElements" [
      org.gradle.category            = library
      org.gradle.dependency.bundling = external
      org.gradle.libraryelements     = jar (compatible with: classes)
      org.gradle.usage               = java-api
      org.gradle.status              = release (not requested)

      Requested attributes not found in the selected variant:
         org.gradle.jvm.version         = 8
   ]
   Selection reasons:
      - By constraint : dependency was locked to version '2.12.1'
      - By ancestor

com.fasterxml.jackson.core:jackson-databind:{strictly 2.12.1} -> 2.12.1
\--- compileClasspath

```

Now it is upgraded 

