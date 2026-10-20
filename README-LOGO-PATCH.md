# FixTrack logo patch

This patch adds the FixTrack logo to the app's top bar and empty-products screen, and configures it as the Android launcher icon.

## Apply on GitHub from an Android phone
1. Open your FixTrack repository and open `app/src/main/java/com/fixtrack/app/MainActivity.kt`.
2. Replace its contents with the `MainActivity.kt` included in this patch (preserve the exact path).
3. Open `app/src/main/AndroidManifest.xml` and replace its contents with the manifest included here.
4. Upload the `app/src/main/res/` folders and files from this patch, preserving their folder structure. The logo image is `drawable-nodpi/fixtrack_logo.png`; launcher icon PNGs are in each `mipmap-*` folder.
5. Commit the changes to `main` and open **Actions**. Wait for **Build FixTrack APK** to finish successfully, then download the `FixTrack-debug-apk` artifact ZIP.

## Important
The uploaded source files did not include the complete project structure or the original standalone logo PNG. The included logo was cropped from the FixTrack logo preview in the setup-guide image available in the conversation, so it may be less crisp than the original 1024×1024 logo file. If you still have the original `fixtrack_logo.png`, use it to replace `app/src/main/res/drawable-nodpi/fixtrack_logo.png` and the launcher PNGs in the mipmap folders before building.

This is a patch containing the modified files/assets, not a complete Android project or a built APK.
