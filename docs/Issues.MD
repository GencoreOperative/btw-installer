## Minecraft 1.5.2 Crash when using the Latest Launcher

[MCL-5837](https://bugs.mojang.com/browse/MCL-5837)
Minecraft 1.5.2 crashes on start up when launched in a folder that has been
previously used for a more recent version of the game. The cause of this
crash is due to the `lang` property of the `options.txt` file.

The crash report for this contains the following stack trace.
```
java.lang.NullPointerException
	at java.io.Reader.<init>(Reader.java:78)
	at java.io.InputStreamReader.<init>(InputStreamReader.java:97)
	at bp.a(SourceFile:64)
	at bp.a(SourceFile:100)
	at net.minecraft.client.Minecraft.a(SourceFile:252)
	at avv.a(SourceFile:56)
	at net.minecraft.client.Minecraft.run(SourceFile:507)
	at java.lang.Thread.run(Thread.java:745)
```
To resolve this issue we need to look at the `lang` property and convert
country code to uppercase.
* Failure: `lang:en_us`
* Success: `lang:en_US`

[BTW Official Instructions](http://www.sargunster.com/btwforum/viewtopic.php?f=9&t=9272#p153045)
This detail has been recognised by the latest installation instructions for Better 
Than Wolves which use the 1.5.2 installation and startup as a means of getting the 
1.5.2 client jar.

This is not an issue for this installer because the 1.5.2 client jar is downloaded
or located automatically as part of patching.

## BTW and Save Games

When using the latest Minecraft launcher with BTW in a non-default Game Directory 
we are no longer able to access saved games. It appears that BTW is looking for 
the save game in the default Minecraft installation folder rather than the 
external Game Directory.

The observed issue appears to be present in all versions of the patch which
makes me wonder whether this is an issue with the latest launcher. This is notable
because in the current README for this simple installer, we recommend using an
external Game Directory. We will need to update the README steps to account for the
change.

## BTWMod4-A2TimingRodentb Launch Failure

The oldest version I have a copy of appears to fail with the latest Minecraft launcher.
The cause of the failure is different from the `lang` country code failure above.

```
java.lang.NullPointerException
	at java.io.Reader.<init>(Reader.java:78)
	at java.io.InputStreamReader.<init>(InputStreamReader.java:97)
	at bp.a(StringTranslate.java:85)
	at bp.a(StringTranslate.java:125)
	at net.minecraft.client.Minecraft.a(Minecraft.java:412)
	at avv.a(SourceFile:56)
	at net.minecraft.client.Minecraft.run(Minecraft.java:729)
	at java.lang.Thread.run(Thread.java:745)
```
No action is to be recommended at the moment as this is an older version.