# Reverse engineering an ieGeek DD201 security camera

This is a record of my attempts to hack a security camera I purshased on [Amazon](https://www.amazon.fr/ieGeek-Surveillance-Exterieure-Batteries-Bidirectionnel/dp/B09D2T373L).
It is branded as an ieGeek, model DD201 on their [official website](https://www.iegeek.com/collections/battery-powered-camera/products/outdoor-camera-dd201).

The camera is used via a mobile application, [CloudEdge](https://play.google.com/store/apps/details?id=com.cloudedge.smarteye&hl=fr&gl=US).
The application itself is not too bad but I wanted to see if it was possible to enable/disable the camera remotely.
Ideally I would like to receive alerts only when I'm not home which is known by the fact I enable or disable my home alarm (another system).

TLDR: I **finally** managed to do it 🤓!

## Gathering intel about the camera

I did not find much on the internet about this specific model but by searching about the mobile application I found:
- https://github.com/sucotronic/meari-camera-cli
- https://github.com/Mearitek/MeariSdk/blob/master/Android/docs/Meari%20Android%20SDK%20Guide.md
(this repository also contains some of the .so native library loaded by the application)

The camera is also branded as a Cooau product: https://www.cooau.com/products/battery-camera-dd201

But the best source of information came from: https://github.com/guino/BazzDoorbell. 
This looks like a different device but the same application is used and the software stack seems similar.

Via the DNS queries it makes I can see the camera talking to:
- https://apis-eu-frankfurt.cloudedge360.com/

The camera is running this firmware: `ppstrong-b8-neutral_std-2.3.1.20211028`.

## Trying to access the camera locally

By default this camera does not expose any port to access from the local network (the description on the product clearly states that the camera
can only be used with the mobile application).

But thanks to the [ppsFactoryTool.txt](https://github.com/guino/Merkury720) hack, you start the camera in a sort of factory mode.
It opens the port 8090 and for this model the credentials that worked for me are: `PpStRoNg:#%&wL1@*tU123zv`.

I managed to access the following URLs:
- http://CAMERA_IP:8090/devices/settings (GET to read, POST to change settings)
- http://CAMERA_IP:8090/log/open to start writing logs
- http://CAMERA_IP:8090/log/upload to access them

There are a bunch of URLs listed here: https://github.com/guino/BazzDoorbell/issues/49, but only a few worked.

The main issue is that once the camera is started in this factory mode, accessing the camera via the mobile application does not work anymore.
However a workaround is to start the camera without the SD card and then insert it with the file.
I've observed that recording on the SD card was not working anymore though. But at least you can try to play with the local open port and the cloud APIs at the same time.

## Trying to access the camera via its APIs

On https://github.com/guino/BazzDoorbell they go as far as playing with the firware of the camera. As I'm not too familar with embedded software I went
the applicative way and tried to find if the APIs used by the mobile application could be a way to control it remotely.

### Android 
Recording API calls via a [proxy](https://mitmproxy.org/) becomes more and more difficult with certificate pinning and recent Android versions.
I first looked at the logs of the application attaching Android Studio to a device, there were a few things like the domain called, sometimes a few logs.

By decompiling the CloudEdge app with [apktool](https://ibotpeaches.github.io/Apktool/) you can get a log of information:

```
apktool d CloudEdge_v4.0.4_apkpure.com.apk
```

The code can then be read with [jadx](https://github.com/skylot/jadx).

The interesting parts are in the com.meari.sdk package.
By reading the code you can understand the different APIs used to control the camera.

There is even a `AppDebugActivity` but I could not figure out how it is started, perhaps it is only enabled at compile time.
Looking at its code it is probably exposing the API keys, ids and maybe even the native JNI logs.

The main interesting one for my use case is:  https://apis-eu-frankfurt.cloudedge360.com/ppstrongs/pushCtrl.action
That enables or disables the push notifications (I would actually like to keep the webcam recording all the time).
Another interesting API is  https://apis-eu-frankfurt.cloudedge360.com/ppstrongs/getDevice.action too.

The requests are signed and it turned out a bit difficult to understand everything that was part of the request so I tried to 
get even more logs from the application.

By changing the smali classes and recompiling the application I was able to put the Meari SDK in debug and also to enable debug logs
for the HTTP requests. This way the headers and bodies were printed too.

```
diff -r CloudEdge_v4.0.4_apkpure.com._origin/smali_classes4/com/meari/sdk/MeariSdk.smali CloudEdge_v4.0.4_apkpure.com/smali_classes4/com/meari/sdk/MeariSdk.smali
33c33
<     const/4 v0, 0x0
---
>     const/4 v0, 0x1
diff -r CloudEdge_v4.0.4_apkpure.com._origin/smali_classes4/com/meari/sdk/http/OkGo.smali CloudEdge_v4.0.4_apkpure.com/smali_classes4/com/meari/sdk/http/OkGo.smali
50c50
<     .locals 4
---
>     .locals 7
110a111,120
>     .line 66
>     const-string v4, "http"
>
>     sget-object v5, Ljava/util/logging/Level;->INFO:Ljava/util/logging/Level;
>
>     const/4 v6, 0x1
>
>     invoke-virtual {p0, v4, v5, v6}, Lcom/meari/sdk/http/OkGo;->debug(Ljava/lang/String;Ljava/util/logging/Level;Z)Lcom/meari/sdk/http/OkGo;
>
>
```

The application is then rebuilt and executed with the debug logs enabled:
```
# Rebuilding the APK after changing smali classes
apktool b CloudEdge_v4.0.4_apkpure.com -o modified2.apk 
# The app needs to signed again
apksigner sign --ks-key-alias test --ks test.keystore modified2.apk --ks-pass pass:testtest 
# I'm using a simulator to run the app from my laptop directly
adb install modified2.apk
```

Below is the corresponding Postman pre-request script to generate a valid signature:

```
nonce = 643610
ts = Date.now()
key = "XXX"
hashKey = "XXX"
realUrl = "/ppstrongs/pushCtrl.action"
toSign = "api=/ppstrongs/" + realUrl + "|X-Ca-Key=" + key + "|X-Ca-Timestamp=" + ts + "|X-Ca-Nonce=" + nonce

signature = CryptoJS.enc.Base64.stringify(CryptoJS.HmacSHA1(toSign, hashKey))

console.log(signature)

pm.globals.set("sign", signature)
pm.globals.set("key", key)
pm.globals.set("ts", ts)
pm.globals.set("nonce", nonce)
```

Strangely enough I could not actually run the query in Postman directly (it failed with a 10003 error) but it worked with curl though:

```
curl --location --request POST 'https://apis-eu-frankfurt.cloudedge360.com/ppstrongs/pushCtrl.action' \
--header 'X-Ca-Timestamp: 1640885397970' \
--header 'X-Ca-Key: XXX' \
--header 'X-Ca-Sign: WUlX+wjfeCZjcHoyGIGDastehD8=' \
--header 'X-Ca-Nonce: 643610' \
--header 'User-Agent: Mozilla/5.0 (Linux; U; Android 11; en-us; sdk_gphone_x86_arm Build/RSR1.201013.001) AppleWebKit/533.1 (KHTML, like Gecko) Version/5.0 Mobile Safari/533.1' \
--header 'Accept-Language: en-US,en;q=0.8' \
--header 'Content-Length: 211' \
--data-raw 'userToken=XXX&phoneType=a&t=1640885397970&sourceApp=8&countryCode=FR&appVer=4.0.4&lngType=en&phoneCode=33&userID=XXX&deviceID=XXX&closePush=0&appVerCode=404'
```

The information like userToken, device, key can be retrieved from the application logs and could probably be retrieved by login using the API.

Now the main issue is that the application does not allow to be logged in from two different locations, each time you login it invalidates the other sessions.
However by inspecting the application logs in Android Studio (with an original app even), you can retrieve the user token and other info:

```
2022-01-09 12:39:33.745 18381-18381/? I/System.out: userInfoToken:UUID_LIKE
2022-01-09 12:39:33.745 18381-18381/? I/System.out: userInfoID:USER_ID

```

I don't know yet what the validity of the token is, time will tell.

It turns the signature is not really checked, queries like the one below are working:

```
curl --request POST 'https://apis-eu-frankfurt.cloudedge360.com/ppstrongs/pushCtrl.action' \
--data-raw 'userToken=UUID&phoneType=a&t=1641719400597&userID=USER_ID&deviceID=DEVICE_ID&closePush=1'
```

`closePush=1` means to disable the push notifications, use 0 to enable them back.

If the call succeeds it should return something like:

```
{"resultCode":"1001","closePush":0}
```

### iOS

On another attempt with an iPhone this time, it turned out much easier to configure mitm, especially given the application is not pinning certificates.
With it you can see all the API calls made to apis-eu-frankfurt.cloudedge360.com and other domains.
## Accessing the image feed locally

Sadly with the ppsFactoryTool.txt I could not find a way to access the camera images.

In the application logs, when you open the camera we can see it seems to connect locally:

```
2021-12-30 18:39:25.553 4404-4404/com.cloudedge.smarteye I/SdkUtils: --->getConnectString: {"trytimes":3,"udpport":12305,"did":"400f0c-AGIEBE-ci3o9,fa6hagB","initstring":"EFGHFDBJKGICGEJDFJHLFGFEGDMLGIMCHOEIAMCDBKINKELCCDBCCGODHBKBJBKBBENILECMPNNGAO:WeEye2ppStronGer","factory":9,"delaysec":5,"licenceid":"ppsld6b6e68972864d9c","protocolv":2,"username":"admin","password":"a795c196cf60a8e153ec0a4f7406a5e4","mode":5}
```

It uses the removeWake.action API and that ones seems to open a udp port on the camera. However I did not figure out how to use it.