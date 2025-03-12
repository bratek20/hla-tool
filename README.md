# HLA Tool (High Level Architecture Tool)

Project focused on increasing code production velocity by generating code for repeated patterns

It assumes bratek20-architecture is used by the project using the tool

To build project it is needed to configure gradle.properties file in the root of your user with the following content:
```
githubActor=<your_github_username>
githubToken=<your_github_token>
```
As an alternative you can create environment variables
```
GITHUB_ACTOR (your_github_username) 
GITHUB_TOKEN (your_github_token)
```

## Tool Publishing
To publish HLA tool used by other projects to generate code

1. tag branch and it will be pushed automatically
2. update tool version in project submodule HLA

## Lib Publishing
To publish new version of HLA lib to be used by other projectsù

1. update version manually in `lib/build.gradle.kts`
2. Open console in `bash` folder
3. execute `libPublish.sh`
4. update HLA version in other projects to use new version of lib

