# Youtube downloader

Description:
 - A video to mp3 converter targeting google's youtube platform made for educational purposes. My main motivation for building this app was to better understand automatic deployment pipelines.

Usage:
  - User can download music from a youtube video by providing the video url.
  
Development:
  - Built using Java server pages on the backend and bootstrap/vanilla js for the frontend.
  
Deployment:

The deployment of the app is handled by github actions. On each commit to the master branch:
  - A new docker image is built using the dockerfile on the project's root where: 
    - Maven downloads every dependency specified in the pom file, compiles and packages the app in a .war file.
    - Tomcat is initialized with the .war file on its root.
  - The new image is published to amazon web services using my github-secrets credentials.

Hosting:
  - The app is temporarily hosted in amazon web services at this url: https://gkwb3ff9ii.us-east-1.awsapprunner.com/
