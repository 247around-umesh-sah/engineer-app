package com.around.engineerbuddy;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Base64;

import java.io.File;
import java.io.UnsupportedEncodingException;

@SuppressWarnings("ALL")
public class DeviceInfo {

    Context context;
    TelephonyManager TelephonyMgr;
    BluetoothAdapter m_BluetoothAdapter;
    WifiManager mWifiManager;
    String m_androidId, m_deviceId, m_wlanMacAdd, m_bluetoothAdd, mUniqueId;

    public DeviceInfo(Context context) {
        this.context = context;
    }

    public static boolean findBinary(String binaryName) {

        boolean found = false;

        if (!found) {

            String[] places = {"/sbin/", "/system/bin/", "/system/xbin/", "/data/local/xbin/",
                    "/data/local/bin/", "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/"};

            for (String where : places) {

                if (new File(where + binaryName).exists()) {
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    public String getImeiId() {

        TelephonyMgr = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);

        if (TelephonyMgr != null) {
            //noinspection deprecation
            m_deviceId = TelephonyMgr.getDeviceId();
            return m_deviceId;
        } else {
            return null;
        }
    }

    public String getAndroidId() {

        return Secure.getString(this.context.getContentResolver(), Secure.ANDROID_ID);
    }

    public String getWlanAdd() {

        mWifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);

        if (mWifiManager != null) {
            m_wlanMacAdd = mWifiManager.getConnectionInfo().getMacAddress();
            return m_wlanMacAdd;
        } else {
            return null;
        }
    }

    public String getBluetoothAdd() {

        m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (m_BluetoothAdapter != null) {

            m_bluetoothAdd = m_BluetoothAdapter.getAddress();
            return m_bluetoothAdd;
        } else {
            return null;
        }
    }

    public String bindIds() throws UnsupportedEncodingException {

        mUniqueId = getImeiId() + "-" + getAndroidId() + "-" + getWlanAdd() + "-" + getBluetoothAdd();
        byte[] data = mUniqueId.getBytes("UTF-8");
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    public Boolean isEmulator() {

        String brand = Build.BRAND;
        return brand.compareTo("generic") == 0;
    }

    public boolean isRooted() {

        return findBinary("su");
    }

    public String getOs() {
        return System.getProperty("os.version");
    }

    public String getVersion() {
        return Build.VERSION.RELEASE;
    }

    public String getModel() {
        return Build.MANUFACTURER;
    }

    public String getDevice() {
        return Build.DEVICE;
    }

    public String getAppVersion() {
        return "1.0";
    }
}
