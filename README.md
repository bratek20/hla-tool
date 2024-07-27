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
