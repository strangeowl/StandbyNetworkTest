# StandbyNetworkTest

Minimal Android/Android TV diagnostic app for testing whether a `PARTIAL_WAKE_LOCK` keeps standby networking reachable without enabling Android's "stay awake while plugged in" setting.

## Test plan
1. Ensure `adb shell svc power stayon false`.
2. Install and launch the app.
3. Press **Start Partial Wake Lock**.
4. Verify with: `adb shell dumpsys power | findstr /i "StandbyNetworkTest"`
5. Leave the TV untouched and let its normal 15-minute standby occur.
6. Keep `ping <TV-IP> -t` running and check whether Home Assistant remains available.
7. Press **Stop Partial Wake Lock** after testing.

This first build deliberately does **not** use WifiLock, boot auto-start, or change any system power setting.
