# Fix bug with 2023 launcher
There is an error with the 2023 installer. It seems that it does not
know how to download the assets needed for 1.5.2 any more.
https://bugs.mojang.com/browse/MCL-23639
The solution in this ticket appears to solve the issue,
therefore, we should install this json file automatically
as part of installation, and not that we need to remove it
if the user removes BTW.

```
Download this legacy.json file: legacy.json

(VirusTotal scan results)

Navigate to %AppData%\Roaming\.minecraft\assets\indexes

Drop the file in that folder. Then try to start the Minecraft launcher.
```
https://bugs.mojang.com/secure/attachment/546166/546166_legacy.json
static/legacy.json

# Add version information for installed AddOns
This installer supports upgrading existing installations. Now that we 
support including AddOns in the installation, it would be useful to 
warn the user that they are about to patch an installation that 
previously included addons. This would only be a warning dialog and
the user can still proceed with the patching.

# Add an About dialog
Include license and credits.

# Include an AddOn Icon
The patch progress dialog could do with an additional icon.

# Release 0.11.0
This is the point when we can release the new feature.

## Investigate signing of the Jar so that MacOS Gatekeeper allows it to run
https://www.baeldung.com/java14-jpackage
https://blog.dgunia.de/2020/02/12/signed-macos-programs-with-java-14/
It is possible to create a self-contained application which installs
our application. This would also be signed as well. This could
be interesting, but might add a layer of complexity we don't need.

## Improve feedback when no mod is installed
RFE: If there is no version installed, we should indicate this to the
user in the 'installed version' label. Something like: BTW not installed

## Replace the system information with logging
The system information computer icon bottom left is not useful in the 
scheme of things. Better would be to move this information into the
log file which is generated. This way it would likely be included in a 
cut and paste that a user submits.

## Patching Service Class
Introduce a Patching Service class which provides the highest level of
function below the user interface. This can be used to automated patch
testing which can be performed per release.

Automated Patch Testing
Each patch in the /static folder should be tested automatically with
the installer on each platform to simplify the process of testing and
verifying behaviour.
Robot tests?
What are we trying to test? Patching behaviour is critical and should
be tested on each build. User Interface behaviour is harder to test.

## UI Drop Target
Update the Patch chooser to support Drag and Drop for File targets

Release Checklist
VirusTotal scan
Jotti Malware scan
Update 'latest' label
Upload installer.jar to GitHub Latest Release
Test on Windows, MacOS and Linux


Automate Jotti Malware scan
POST
https://virusscan.jotti.org/en-GB/submit-file?isAjax=true
Body: Base64 encoded?

Automate VirusTotal scan
http://www.mashthatkey.com/2014/09/using-curl-to-retrieve-virustotal.html
https://www.virustotal.com/en/documentation/private-api/

## Detail about sound issues - resolved
REF: If the user has not copied over their sound files, they need to do
that for BTW to work correctly. Likely not something the installer should
do, but certainly we can check and warn the user.
http://www.sargunster.com/btwforum/viewtopic.php?f=9&t=9378
has the details.

---
"the problem seems to be with the missing files from resorces they are placed in .minecraft\assets\virtual\legacy
it seems that this is a work around so the new launcher works with the older versions but it is done poorly or there is a bug and the music files will not be read from there, the logs say that they are missing from .minecraft\resources
so a work around seems to be that you have to move everything from .minecraft\assets\virtual\legacy\sounds into .minecraft\resources\sound3
but i'm not sure that this is the same for everyone you should look in the console to see from where the launcher is trying to read the music files from and place them there"
---
