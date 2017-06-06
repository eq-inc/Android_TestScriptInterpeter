# Test Script Interpreter for Android UI Automator
JSONで記載された試験スクリプトファイルを読み込み、UI Automatorにて自動試験を試みるプロジェクトになります。

# 起動方法
本プロジェクトをクローンし、そのフォルダにて以下のコマンドを実行<br/>

adb shell am instrument -w -r -e config_file [Configファイルのパス] -e debug false -e class jp.eq_inc.testuiautomator.TestDynamicUIAutomator jp.eq_inc.testuiautomator.test/android.support.test.runner.AndroidJUnitRunner

Configファイルのパス:<br/>
試験を実施するスマートフォンなどのデバイス上にJSONで記載された試験スクリプトファイルを設置し、そこへのパスを指定。assetに含めたい場合は、assetに試験スクリプトファイルを追加した上で、以下のように指定<br/>
file:///android_asset/[assetsフォルダ内のパス]<br/>
例 assetsフォルダのcategory/test.jsonを指定する場合<br/>
file:///android_asset/category/test.json<br/>

# Configファイル
Configファイルは以下のような構成となっています。<br/>

<table border="1">
<thead>
<tr><th colspan="4">要素名称</th><th>要素種別</th></tr>
</thead>
<tbody>
<tr><td colspan="4">test</td><td>配列</td></tr>
<tr><td rowspan="11"></td><td colspan="3">testApplicationId</td><td>-</td></tr>
<tr><td colspan="3">testActivity</td><td>-</td></tr>
<tr><td colspan="3">testProcedures</td><td>配列</td></tr>
<tr><td rowspan="8"></td><td colspan="2">type</td><td>-</td></tr>
<tr><td colspan="2">targetItem</td><td>-</td></tr>
<tr><td rowspan="3"></td><td>itemClass</td><td>-</td></tr>
<tr><td>itemText</td><td>-</td></tr>
<tr><td>itemResourceId</td><td>-</td></tr>
<tr><td colspan="2">testParams</td><td>配列</td></tr>
<tr><td rowspan="2"></td><td>name</td><td>-</td></tr>
<tr><td>value</td><td>-</td></tr>
</tbody>
</table>

## test
試験スクリプトのルート要素。

### testApplicationId
試験スクリプトを実行させたいアプリのアプリID<br/>
例: 設定アプリ = com.android.settings

### testActivity
試験スクリプトを実行させたいアプリの最初に起動するアクティビティ名称<br/>
指定されていない場合は、ランチャー用のActivityを起動


### testProcedures
試験手順を記載する要素。記載されている順に実行される。


#### type
試験時に実行する内容。以下のものが設定可能(大文字小文字区別なし)。<br/>

|要素名称|概要|指定可能なTestParams|
|:--|:--|:--|
|Click|指定された部品や画面の特定の位置をクリック|PositionX<br/>PositionY|
|ClickSystemKey|Back/Homeなどのシステムキーをクリック|Text<br/>|
|Drag|指定された部品や画面の特定の位置をドラッグ|PositionX<br/>PositionY<br/>SizeX<br/>SizeY<br/>|
|DumpWindowHierarchy|表示されている部品をヒエラルキーに沿って出力|-|
|FreezeOrientateScreen|画面回転を無効にする|-|
|InputText|特定の部品に対して文字入力を実施|Text|
|LongClick|指定された部品や画面の特定の位置を長クリック|PositionX<br/>PositionY|
|ScreenShot|スクリーンショットを取得|Quality<br/>Scale<br/>Suffix|
|ScreenRotateLeft|画面を左に90度回転させる|-|
|ScreenRotateNatural|画面を標準の方向に戻す|-|
|ScreenRotateRight|画面を右に90度回転させる|-|
|SelectItem|ListViewなどにある部品を選択|Index<br/>Text<br/>|
|Sleep|指定された時間や指定された条件を満たすまでスリープ|TimeMS<br/>WaitShowPackage<br/>WaitShowItemByResourceId<br/>WaitShowItemByText<br/>WaitTimeoutMS<br/>|
|StartScreenRecord|画面録画の開始|
|StopScreenRecord|画面録画の停止|
|Swipe|指定された部品や画面の特定の位置をスワイプ|PositionX<br/>PositionY<br/>SizeX<br/>SizeY<br/>|
|Test|指定された条件を満たしているか確認し、満たしていない場合は試験を中断|Checkable<br/>Checked<br/>Clickable<br/>Enabled<br/>Focusable<br/>Focused<br/>LongClickable<br/>Scrollable<br/>Selected<br/>|
|UnFreezeOrientateScreen|画面回転を有効にする|-|


#### targetItem
typeで指定された内容を実施するアイテムを指定。<br/>
以下の優先順位にて使用される。<br/>
1. itemResourceId
2. itemClass / itemText

type毎の設定要否は以下のようになる。

|名称|targetItemの設定要否|備考|
|:--|:--|:--|
|Click|任意|設定しない場合はtestParamにてPositionX/PositionYの設定が必要|
|ClickSystemKey|不要|-|
|Drag|任意|設定しない場合はtestParamにてPositionX/PositionY/SizeX/SizeYの設定が必要|
|DumpWindowHierarchy|不要|-|
|FreezeOrientateScreen|不要|-|
|InputText|** 必要 **|設定しない場合は処理が行われない|
|LongClick|任意|設定しない場合はtestParamにてPositionX/PositionYの設定が必要|
|ScreenShot|不要|-|
|ScreenRotateLeft|不要|-|
|ScreenRotateNatural|不要|-|
|ScreenRotateRight|不要|-|
|SelectItem|任意|設定しない場合はtestParamにてIndex/Textの設定が必要|
|Sleep|不要|-|
|StartScreenRecord|不要|-|
|StopScreenRecord|不要|-|
|Swipe|任意|設定しない場合はtestParamにてPositionX/PositionY/SizeX/SizeYの設定が必要|
|Test|** 必要 **|設定しない場合は処理が行われない|
|UnFreezeOrientateScreen|不要|-|

##### itemClass
対象とするView部品のクラス名称を指定。<br/>
重複した場合は先に見つかった部品が使用される。<br/>
itemTextとの重複設定可能。<br/>

##### itemText
対象とするView部品に設定されている文字列を指定。<br/>
重複した場合は先に見つかった部品が使用される。<br/>
itemClassとの重複設定可能。<br/>

##### itemResourceId
対象とするView部品に設定されているリソースID名称を設定。<br/>
値は"[アプリID]:id/[ID名称]"となる。
例 "itemResourceId": "com.android.settings:id/dashboard_container"
<br/>
3rdパーティが作成したアプリのためにリソースIDが不明な場合は、"** uiautomatorviewer **"または"** Android Device Monitor **" - "** Dump View Hierarchy for UI Automator **"から調査可能。

#### testParams
試験手順毎に指定したいパラメータを設定。<br/>

##### name
パラメータ名称<br/>
それぞれ以下のtypeに対応<br/>

|name名称|対応しているtype|概要|
|:--|:--|:--|
|Checkable|Test<br/>|対象とする部品のCheckable属性がvalueで指定された値か否かを判定|
|Checked|Test<br/>|対象とする部品のChecked属性がvalueで指定された値か否かを判定|
|Clickable|Test<br/>|対象とする部品のClickable属性がvalueで指定された値か否かを判定|
|Enabled|Test<br/>|対象とする部品のEnabled属性がvalueで指定された値か否かを判定|
|Focusable|Test<br/>|対象とする部品のFocusable属性がvalueで指定された値か否かを判定|
|Focused|Test<br/>|対象とする部品のFocused属性がvalueで指定された値か否かを判定|
|Index|SelectItem<br/>||
|LongClickable|Test<br/>|対象とする部品のLongClickable属性がvalueで指定された値か否かを判定|
|PositionX|Click<br/>Drag<br/>Swipe<br/>|操作する際のX座標。単位なし時はpx、対応している単位は%/dp/sp<br/>%は画面横方向のサイズに対する割合|
|PositionY|Click<br/>Drag<br/>Swipe<br/>|操作する際のY座標。単位なし時はpx、対応している単位は%/dp/sp<br/>%は画面縦方向のサイズに対する割合|
|Quality|ScreenShot<br/>||
|Scale|ScreenShot<br/>||
|Scrollable|Test<br/>|対象とする部品のScrollable属性がvalueで指定された値か否かを判定|
|Selected|Test<br/>|対象とする部品のSelected属性がvalueで指定された値か否かを判定|
|SizeX|Drag<br/>Swipe<br/>|操作時の画面横方向の移動距離などのサイズを設定。単位なし時はpx、対応している単位は%/dp/sp<br/>%は画面横方向のサイズに対する割合|
|SizeY|Drag<br/>Swipe<br/>|操作時の画面縦方向の移動距離などのサイズを設定。単位なし時はpx、対応している単位は%/dp/sp<br/>%は画面縦方向のサイズに対する割合|
|Steps|Drag<br/>Swipe<br/>|操作時のstep数を設定|
|Suffix|DumpWindowHierarchy<br/>ScreenShot<br/>StartScreenRecord<br/>|保存するファイル名称に付与するsuffixを設定。無指定時はsuffixなし|
|Text|ClickSystemKey<br/>SelectItem<br/>Test<br/>||
|TimeMS|Sleep<br/>||
|WaitShowPackage|Sleep<br/>||
|WaitShowItemByResourceId|Sleep<br/>||
|WaitShowItemByText|Sleep<br/>||
|WaitTimeoutMS|Sleep<br/>||


##### value
パラメータ値



Run test script written by JSON by Android UI Automator

1.


* How to launch
