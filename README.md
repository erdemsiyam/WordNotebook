<p align="center">
  <img height="198" width="198"  src="images/icon512.png">
</p>

# Summary
Developed with Android Studio using Java for Android platform.
Memorize your own strange language words in a hierarchical way.

# Features
1. List Page.
    - [x] Create, edit, delete your lists.
    - [x] Sorting or searching by choice.
    - [x] Select lists and words to put exam.
    - [x] Periodically send notification strange words and descriptions of selected lists.
      - [x] Click to mark word as learned.
    - [x] Set alarm to exam of selected list.
2. Word Page.
    - [x] Create, edit, delete your words.
    - [x] Sorting or searching by choice.
    - [x] Switch word groups visibility (All, Only Marked, Only Learned, Only Not Learned).
    - [x] Switch words visibility (All, Only Strange, Only Explain).
    - [x] Mark your words as like star or learned.
    - [x] Add words by Import 'Excel File'.
    - [x] Export words as 'Excel File'.
    - [x] Look at the history of the selected word. 
      - [x] How many right or wrong choices did you make at exam.
      - [x] Which you confused with the words.
      - [x] How fast did you answer, the average of answer time.
3. Exam Page.
    - [x] Stopwatch for average response time calculation.
    - [x] Auto pass after quetion answered.
4. Setting Page.
    - [x] Word notifications time interval.
    - [x] Word notifications loop time interval.
    - [x] General notifications settings (Heads-Up, Sound, Vibration).
    - [x] Font size.
    
# Screenshots
<p align="center">
<img height="385" width="275" src="images/en1.png">
<img height="385" width="275" src="images/en2.png">
<img height="385" width="275" src="images/en3.png">
</p>
<p align="center">
<img height="385" width="275" src="images/en4.png">
<img height="385" width="275" src="images/en5.png">
<img height="385" width="275" src="images/en6.png">
</p>
<p align="center">
<img height="385" width="275" src="images/en7.png">
</p>

# Download
<p align="center">
  <a href="https://play.google.com/store/apps/details?id=com.erdemsiyam.memorizeyourwords" target="_blank">
    <img src="images/googlePlayDownload.png">
  </a>
</p>

# Dependencies
- AndroidX
- RoomAPI / SQLite
- SwipeToAction : https://github.com/vcalvello/SwipeToAction
- Android5xlsx : https://github.com/andruhon/android5xlsx
- Google Ads

# Versions
### Version 1.0.1
<p>-New Feature : Notification added for words and lists.</p>
<p>-New Feature : Excel export added.</p>
-New Feature : Settings added for notification and font.
-Bug Fixed : A lot of bug fixed.

### Version 1.0.2
<p>
-Bug Fixed : Phone available wake up when notification received.
-Bug Fixed : Excel exporting file listing works now.
-New Feature : Added option to Excel importing : "Do not import same words if exists already in the list".
-New Feature : Added "I learned this word" button to word notification.
-Redesign : Word page's buttons resized.
-Redesign : Word group list sorting changed for nice look.
-Redesign : Ads moved to end of exam instead of start.
</p>

### Version 1.0.3
<p>
-Bug Fixed : Handled of crashing app at start.
-New Feature : You can change now of word groups visibility.
-Redesign : Word page resize according ad.
-Redesign : Font setting effect enlarged.
</p>
