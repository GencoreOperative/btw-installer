## How the Installer Works

The installer requires the 1.5.2 client to patch. It will start by looking for 
the client binary locally in the user versions. If it is not present then the 
application will use the publicly accessible binary provided by Mojang:

* Client URL: https://launcher.mojang.com/mc/game/1.5.2/client/465378c9dc2f779ae1d6e8046ebc46fb53a57968/client.jar
* File Size: 5564661
* MD5 Hash: 6897c3287fb971c9f362eb3ab20f5ddd

This behaviour allows us to skip repeated downloads of the client binary each time
we want to patch the installation.