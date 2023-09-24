
#!/bin/bash

# Change to the root directory of your Gradle project
cd ../../

# Run the 'runApiTests' task with '--info' and '-Dthreads=1'
./gradlew clean runApiTests --info -Dthreads=1

# Run the 'runUiTests' task with '--info' and '-Dthreads=1'
./gradlew clean runUiTests --info -Dthreads=1
