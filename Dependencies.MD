This installer depends on the following for its operation:

**Mojang public downloads of 1.5.2 client jar**
In order to remove the need for the user to pre-setup 1.5.2, we can download 
this directly from Mojang. The URL can be identified by parsing 
https://mcversions.net/ instead but of course this would add a different
less authoritative dependency.

- *url* https://launcher.mojang.com/mc/game/1.5.2/client/465378c9dc2f779ae1d6e8046ebc46fb53a57968/client.jar
- *md5* 6897c3287fb971c9f362eb3ab20f5ddd
- *size* 5564661

**Format of the BTW Patch Zip**

The internal structure of the BTW Zip is used by this installer to locate
the client patch class files for patching.

**Readme of the BTW Patch**

The Readme contains the version number and name of the patch which is used
to present to the user to provide visual feedback that they are installing
the version they intend.