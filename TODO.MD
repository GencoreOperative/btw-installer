## Update README
The README currently recommends using an external Game Directory for 
BTW because of a client launch failure. This does not seem to be the 
case any more and instead there is a different issue around save
game files.

As such we should update the documentation to guide the user to use
the default installation folder now as this will work around the 
save game issue.

Documentation: The installer does not refresh the installed versions
view at runtime and so you will need to restart the launcher if it is
already open after installation. Or install before launching.

Update Documentation
- Include remove instructions on separate page

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