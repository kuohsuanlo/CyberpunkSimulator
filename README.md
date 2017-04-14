# Cyberpunk Simulator by LogoCat

![alt tag](http://i.imgur.com/I6kLEVU.png)
[遊戲設計 Project] 
每天擠出時間把之前開出的project盡量有進度。

接續著之前的任務系統 [1]的構想，首先得先把整個社會跟其需求建立起來，暫時做出一個小世界來模擬。

影片中是1000個NPC的AI 尋路與送包裹的狀況
圖片則是150個NPCs[圖一] / 500個NPCs[圖二] / 1000個NPCs[圖三] 

之後會將每天辛苦工作的NPCs，放進之前用程式生成的城市[圖四]裡面，做出一個各司其職的小社會。到時候工作就不只送包裹，而是每個NPC解決其他NPC開出來的需求，從食物生產到工具製造。現在的AI架構是使用GOAP[3]來模擬，每種包裹號碼都代表一種行業的需求。
（現在只有腳程快慢這種人格，跟製造不同包裹來象徵職業 :p ）

目前用09年的Macbook AIR 跑1000個NPCs，FPS還算順，
希望之後加入K-greedy A-star path finding(碩一修AI課的期末報告，特別為了都市遊戲場景優化的A-star，在都市型態充滿馬路跟大樓的地圖中，效能可以增加最多K倍)，希望可以看到更多市民NPCs在一個擁擠的Cyberpunk世界裡面生活。

[1] 用directed acyclic graph 和 topological ordering來實作 procedurally generation on contextual story line. 讓每日任務或environmental quests (地圖上的驚嘆號那種任務) 可以跟main story line產生因果關係，讓人覺得解主線的時候，那些不重要的支線也會產生變化，藉由任務之間的連動，加深開放世界的immersion。
https://www.facebook.com/photo.php?fbid=10154374953497951&set=a.445408877950.238072.557702950&type=3&theater&viewas=100000686899395

[2] Cyberpunk Generator - https://github.com/kuohsuanlo/cyberworld

https://www.facebook.com/media/set/?set=a.10154387306707951.1073741836.557702950&type=3&pnref=story

[3] Goal Oriented Action Planning for a Smarter AI
https://gamedevelopment.tutsplus.com/tutorials/goal-oriented-action-planning-for-a-smarter-ai--cms-20793
