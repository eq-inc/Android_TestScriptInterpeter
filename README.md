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
## 優先順位
以下の優先順位にて、使用するConfigファイルが決定される。
1. config_fileパラメータで指定された"デバイス内のファイル"(指定されていても、実際にファイルが存在しない場合は指定なしとみなす)
2. /storage/emulated/0/ui_automator.json
3. file:///android_asset/ui_automator.json

## 構成
Configファイルは以下のような構成となっている。<br/>

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

<table border="1">
<thead>
<tr><th>要素名称</th><th>概要</th><th>指定可能なTestParams</th></tr>
</thead>
<tbody>
<tr>
<td>Click</td><td>指定された部品や画面の特定の位置をクリック</td><td>OffsetX<br/>OffsetY<br/>PositionX<br/>PositionY</td>
</tr>
<tr>
<td>ClickSystemKey</td><td>Back/Homeなどのシステムキーをクリック</td><td>Text<br/></td>
</tr>
<tr>
<td>Drag</td><td>指定された部品や画面の特定の位置をドラッグ</td><td>PositionX<br/>PositionY<br/>SizeX<br/>SizeY<br/></td>
</tr>
<tr>
<td>DumpWindowHierarchy</td><td>表示されている部品をヒエラルキーに沿って出力</td><td>-</td>
</tr>
<tr>
<td>FreezeOrientateScreen</td><td>画面回転を無効にする</td><td>-</td>
</tr>
<tr>
<td>InputText</td><td>特定の部品に対して文字入力を実施</td><td>Text</td>
</tr>
<tr>
<td>LongClick</td><td>指定された部品や画面の特定の位置を長クリック</td><td>OffsetX<br/>OffsetY<br/>PositionX<br/>PositionY</td>
</tr>
<tr>
<td>ScreenShot</td><td>スクリーンショットを取得</td><td>Quality<br/>Scale<br/>Suffix</td>
</tr>
<tr>
<td>ScreenRotateLeft</td><td>画面を左に90度回転させる</td><td>-</td>
</tr>
<tr>
<td>ScreenRotateNatural</td><td>画面を標準の方向に戻す</td><td>-</td>
</tr>
<tr>
<td>ScreenRotateRight</td><td>画面を右に90度回転させる</td><td>-</td>
</tr>
<!--tr>
<td>SelectItem</td><td>ListViewなどにある部品を選択</td><td>Index<br/>Text<br/></td>
</tr-->
<tr>
<td>Sleep</td><td>指定された時間や指定された条件を満たすまでスリープ</td><td>TimeMS<br/>WaitShowPackage<br/>WaitShowItemByResourceId<br/>WaitShowItemByText<br/>WaitTimeoutMS<br/></td>
</tr>
<tr>
<td>StartScreenRecord</td><td>画面録画の開始</td><td>-</td>
</tr>
<tr>
<td>StopScreenRecord</td><td>画面録画の停止</td><td>-</td>
</tr>
<tr>
<td>Swipe</td><td>指定された部品や画面の特定の位置をスワイプ</td><td>PositionX<br/>PositionY<br/>SizeX<br/>SizeY<br/></td>
</tr>
<tr>
<td>Test</td><td>指定された条件を満たしているか確認し、満たしていない場合は試験を中断</td><td>Checkable<br/>Checked<br/>Clickable<br/>Enabled<br/>Focusable<br/>Focused<br/>LongClickable<br/>Scrollable<br/>Selected<br/></td>
</tr>
<tr>
<td>UnFreezeOrientateScreen</td><td>画面回転を有効にする</td><td>-</td>
</tr>
</tbody>
</table>

#### targetItem
typeで指定された内容を実施するアイテムを指定。<br/>
以下の優先順位にて使用される。<br/>
1. itemResourceId
2. itemClass / itemText

type毎の設定要否は以下のようになる。

<table border="1">
<thead>
<tr><th>名称</th><th>targetItemの設定要否</th><th>備考</th></tr>
</thead>
<tbody>
<tr><td>Click</td><td>任意</td><td>設定しない場合はtestParamにてPositionX/PositionYの設定が必要</td></tr>
<tr><td>ClickSystemKey</td><td>不要</td><td>-</td></tr>
<tr><td>Drag</td><td>任意</td><td>設定しない場合はtestParamにてPositionX/PositionY/SizeX/SizeYの設定が必要</td></tr>
<tr><td>DumpWindowHierarchy</td><td>不要</td><td>-</td></tr>
<tr><td>FreezeOrientateScreen</td><td>不要</td><td>-</td></tr>
<tr><td>InputText</td><td><b>必要</b></td><td>設定しない場合は処理が行われない</td></tr>
<tr><td>LongClick</td><td>任意</td><td>設定しない場合はtestParamにてPositionX/PositionYの設定が必要</td></tr>
<tr><td>ScreenShot</td><td>不要</td><td>-</td></tr>
<tr><td>ScreenRotateLeft</td><td>不要</td><td>-</td></tr>
<tr><td>ScreenRotateNatural</td><td>不要</td><td>-</td></tr>
<tr><td>ScreenRotateRight</td><td>不要</td><td>-</td></tr>
<!--tr><td>SelectItem</td><td>任意</td><td>設定しない場合はtestParamにてIndex/Textの設定が必要</td></tr-->
<tr><td>Sleep</td><td>不要</td><td>-</td></tr>
<tr><td>StartScreenRecord</td><td>不要</td><td>-</td></tr>
<tr><td>StopScreenRecord</td><td>不要</td><td>-</td></tr>
<tr><td>Swipe</td><td>任意</td><td>設定しない場合はtestParamにてPositionX/PositionY/SizeX/SizeYの設定が必要</td></tr>
<tr><td>Test</td><td><b>必要</b></td><td>設定しない場合は処理が行われない</td></tr>
<tr><td>UnFreezeOrientateScreen</td><td>不要</td><td>-</td></tr>
</tbody>
</table>


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

|name名称|対応しているtype|概要|設定値|
|:--|:--|:--|:--|
|Checkable|Test<br/>|対象とする部品のCheckable属性がvalueで指定された値か否かを判定|true / false|
|Checked|Test<br/>|対象とする部品のChecked属性がvalueで指定された値か否かを判定|true / false|
|Clickable|Test<br/>|対象とする部品のClickable属性がvalueで指定された値か否かを判定|true / false|
|Enabled|Test<br/>|対象とする部品のEnabled属性がvalueで指定された値か否かを判定|true / false|
|Focusable|Test<br/>|対象とする部品のFocusable属性がvalueで指定された値か否かを判定|true / false|
|Focused|Test<br/>|対象とする部品のFocused属性がvalueで指定された値か否かを判定|true / false|
|LongClickable|Test<br/>|対象とする部品のLongClickable属性がvalueで指定された値か否かを判定|true / false|
|OffsetX|Click|起点とする部品の中心からのX軸方向のオフセット量。単位なし時はpx、対応している単位は%/dp/sp<br/>%は画面横方向のサイズに対する割合|単位付整数(dp / sp / %)|
|OffsetY|Click|起点とする部品の中心からのY軸方向のオフセット量。単位なし時はpx、対応している単位は%/dp/sp<br/>%は画面縦方向のサイズに対する割合|単位付整数(dp / sp / %)|
|PositionX|Click<br/>Drag<br/>LongClick<br/>Swipe<br/>|操作する際のX座標。単位なし時はpx、対応している単位は%/dp/sp<br/>%は画面横方向のサイズに対する割合|単位付整数(dp / sp / %)|
|PositionY|Click<br/>Drag<br/>LongClick<br/>Swipe<br/>|操作する際のY座標。単位なし時はpx、対応している単位は%/dp/sp<br/>%は画面縦方向のサイズに対する割合|単位付整数(dp / sp / %)|
|Quality|ScreenShot<br/>|スクリーンショット取得時のQuality(0 ~ 100)。<br/>デフォルト100|0 ~ 100の整数|
|Scale|ScreenShot<br/>|スクリーンショット取得時のScale(0.0 ~ 1.0)。<br/>デフォルト1.0|0.0 ~ 1.0までの数値|
|Scrollable|Test<br/>|対象とする部品のScrollable属性がvalueで指定された値か否かを判定|true / false|
|Selected|Test<br/>|対象とする部品のSelected属性がvalueで指定された値か否かを判定|true / false|
|SizeX|Drag<br/>Swipe<br/>|操作時の画面横方向の移動距離などのサイズを設定。単位なし時はpx、対応している単位は%/dp/sp<br/>%は画面横方向のサイズに対する割合|単位付整数(dp / sp / %)|
|SizeY|Drag<br/>Swipe<br/>|操作時の画面縦方向の移動距離などのサイズを設定。単位なし時はpx、対応している単位は%/dp/sp<br/>%は画面縦方向のサイズに対する割合|単位付整数(dp / sp / %)|
|Steps|Drag<br/>Swipe<br/>|操作時のstep数を設定|整数|
|Suffix|DumpWindowHierarchy<br/>ScreenShot<br/>StartScreenRecord<br/>|保存するファイル名称に付与するsuffixを設定。無指定時はsuffixなし|文字列|
|Text|ClickSystemKey<br/>InputText<br/>Swipe<br/>Test<br/>|表示したい文字列や表示している文字列に対して検索したい場合などに設定|文字列|
|TimeMS|Sleep<br/>|一定期間Sleepさせたいときに設定|整数|
|WaitShowPackage|Sleep<br/>|対象とするパッケージが表示されるまでSleepさせたいときに設定|パッケージ名称|
|WaitShowItemByResourceId|Sleep<br/>|対象とする部品(リソースID名称にて対象部品を特定)が表示されるまでSleepさせたいときに設定|リソース名称|
|WaitShowItemByText|Sleep<br/>|対象とする部品(表示される文字列にて対象部品を特定)が表示されるまでSleepさせたいときに設定|文字列|
|WaitTimeoutMS|Sleep<br/>|条件付Sleepを行う場合に条件が満たされなくても自動的にSleepを解除する時間(単位: ミリ秒)。<br/>デフォルト5000ミリ秒|整数|


##### value
パラメータ値。JSONとしては全て文字列として設定すること。各パラメータ毎に設定できる値は上表の「設定値」列を参照。


# Configファイル例

## 設定アプリを起動して、Drawerメニューから「日付と時刻」を選択(Nexus 6P, Android 7.1.2)
```
{
	"test": [
		{
			"testApplicationId": "com.android.settings",
			"testProcedures": [
				{
					"type": "swipe",
					"testParams": [
						{
							"name": "PositionX",
							"value": "2%"
						},
						{
							"name": "PositionY",
							"value": "50%"
						},
						{
							"name": "SizeX",
							"value": "100%"
						},
						{
							"name": "SizeY",
							"value": "0"
						}
					]
				},
				{
					"type": "sleep",
					"testParams": [
						{
							"name": "TimeMS",
							"value": "1000"
						}
					]
				},
				{
					"type": "swipe",
					"targetItem": {
						"itemResourceId": "com.android.settings:id/left_drawer"
					},
					"testParams": [
						{
							"name": "text",
							"value": "日付と時刻"
						}
					]
				},
				{
					"type": "click",
					"targetItem": {
						"itemText": "日付と時刻"
					}
				},
				{
					"type": "sleep",
					"testParams": [
						{
							"name": "TimeMS",
							"value": "100"
						}
					]
				},
				{
					"type": "ClickSystemKey",
					"targetItem": {
						"itemText": "Home"
					}
				}
			]
		}
	]
}
```
